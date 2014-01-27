package org.csstudio.nams.service.messaging.declaration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.common.service.ThreadType;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.messaging.declaration.AbstractMultiConsumerMessageHandler.MultiConsumerMessageThreads;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

/**
 * Ein {@link Consumer} der auf mehreren {@link Consumer}n ließt.
 */
public class MultiConsumersConsumer implements Consumer, Pausable {

	private Lock waitLock = new ReentrantLock();
	private Condition waitCondition = waitLock.newCondition();

	public static enum MultiConsumerConsumerThreads implements ThreadType {
		CONSUMER_THREAD
	};

	/**
	 * Queue zum zwischen speichern empfangener Nachrichten. BlockingQueue mit
	 * max groeße 1 damit keine Nachrichten auf Vorrat geholt werden.
	 */
	private final BlockingQueue<NAMSMessage> queue = new ArrayBlockingQueue<NAMSMessage>(1);
	private final List<StepByStepProcessor> processors;
	private boolean isClosed = true;
	
	private final List<MessagingException> messagingExceptions;

	private volatile boolean isPaused = false;
	private Thread receivingThread = null;

	public MultiConsumersConsumer(final ILogger logger, final Consumer[] consumerArray, final ExecutionService executionService) {
		this.processors = new LinkedList<StepByStepProcessor>();
		this.messagingExceptions = new ArrayList<MessagingException>();

		for (final Consumer consumer : consumerArray) {
			final StepByStepProcessor stepByStepProcessor = new StepByStepProcessor() {
				@Override
				protected void doRunOneSingleStep() throws Throwable {
					try {
						NAMSMessage receivedMessage = consumer.receiveMessage();
						if (receivedMessage != null) {
							MultiConsumersConsumer.this.queue.put(receivedMessage);
						}
					} catch (MessagingException me) {
						if (me.getCause() instanceof InterruptedException) {
							// Ok, soll beendet werden....
						} else {
							synchronized(messagingExceptions) {
								messagingExceptions.add(me);
								receivingThread.interrupt();
							}
							logger.logDebugMessage(this, "Exception in multiConsumer while recieving message from: " + consumer.toString(), me);
						}
					}
				}
			};
			executionService.executeAsynchronously(MultiConsumerMessageThreads.CONSUMER_THREAD, stepByStepProcessor);
			this.processors.add(stepByStepProcessor);
		}
		this.isClosed = false;
	}

	@Override
	public void close() {
		for (final StepByStepProcessor processor : this.processors) {
			processor.stopWorking();
		}
		if (isPaused()) {
			unpause();
		}

		this.isClosed = true;
	}

	@Override
	public boolean isClosed() {
		return this.isClosed;
	}

	public void pause() {
		this.isPaused = true;
	}

	public void unpause() {
		waitLock.lock();
		this.isPaused = false;
		waitCondition.signalAll();
		waitLock.unlock();
	}

	public boolean isPaused() {
		return isPaused;
	}

	@Override
	public synchronized NAMSMessage receiveMessage() throws MultiConsumersMessagingException, InterruptedException {
		checkForExceptions();

		receivingThread = Thread.currentThread();
		NAMSMessage message = null;
		try {
			 message = this.queue.take();
		} catch(InterruptedException ie) {
			checkForExceptions();
			throw ie;
		} finally {
			receivingThread = null;
		}
		
		waitLock.lock();
		while (isPaused) {
			waitCondition.await();
		}
		waitLock.unlock();

		return message;
	}

	private void checkForExceptions() throws MultiConsumersMessagingException {
		synchronized(messagingExceptions) {
			if(!messagingExceptions.isEmpty()) {
				MultiConsumersMessagingException multiConsumersMessagingException = new MultiConsumersMessagingException(new ArrayList<MessagingException>(messagingExceptions));
				messagingExceptions.clear();
				throw multiConsumersMessagingException;
			}
		}
	}
	public static class MultiConsumersMessagingException extends MessagingException {
		private static final long serialVersionUID = 1262935432316579328L;
		private List<MessagingException> exceptions;
		
		public MultiConsumersMessagingException(List<MessagingException> messagingExceptions) {
			super("Aggregate MessagingException. To get source exceptions use getExceptions().");
			exceptions = messagingExceptions;
		}
		
		public List<MessagingException> getExceptions() {
			return exceptions;
		}
	}
}

