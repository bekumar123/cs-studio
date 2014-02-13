
package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.material.SystemMessage;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public interface Producer {
	/**
	 * Indicates weather this producer is closed.
	 */
	public boolean isClosed();

	/**
	 * Send a {@link SystemMessage}
	 * 
	 * @throws MessagingException
	 *             If an send-error/exception occurred.
	 */
	public void sendSystemMessage(SystemMessage systemMessage)
			throws MessagingException;

	/**
	 * Send a {@link MessageCasefile}
	 * 
	 * @throws MessagingException
	 *             If an send-error/exception occurred.
	 */
	public void sendMessageCasefile(MessageCasefile messageCasefile)
			throws MessagingException;

	/**
	 * Tries to close the producer, errors/exceptions during closing will be
	 * ignored.
	 */
	public void tryToClose();
}
