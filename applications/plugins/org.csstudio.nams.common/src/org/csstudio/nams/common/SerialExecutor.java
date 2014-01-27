package org.csstudio.nams.common;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class SerialExecutor implements Executor {
	private final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
	private final Executor executor;
	private volatile Runnable active;
	private Runnable scheduledRunnable;
	
	public SerialExecutor(Executor executor) {
		this.executor = executor;
		scheduledRunnable = createScheduledRunnableWrapper();
	}

	@Override
	public synchronized void execute(final Runnable runnable) {
		tasks.offer(runnable);
		if (active == null) {
			scheduleNext();
		}
	}

	public int getQueueSize() {
		return tasks.size();
	}

	private synchronized void scheduleNext() {
		if ((active = tasks.poll()) != null) {
			executor.execute(scheduledRunnable);
		}
	}

	private Runnable createScheduledRunnableWrapper() {
		return new Runnable() {
			@Override
			public void run() {
				try {
					active.run();
				} finally {
					scheduleNext();
				}
			}
		};
	}
}
