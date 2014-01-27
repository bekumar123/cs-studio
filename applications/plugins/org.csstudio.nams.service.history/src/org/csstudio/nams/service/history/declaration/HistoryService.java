
package org.csstudio.nams.service.history.declaration;

import org.csstudio.nams.common.decision.MessageCasefile;

public interface HistoryService {
	public void logReceivedReplicationDoneMessage()
			throws HistoryStorageException;

	public void logReceivedStartReplicationMessage()
			throws HistoryStorageException;

	public void logTimeOutForTimeBased(MessageCasefile vorgangsmappe)
			throws HistoryStorageException;
}
