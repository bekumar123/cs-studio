package org.csstudio.nams.application.department.decision;

import org.csstudio.nams.application.department.decision.office.decision.DecisionDepartment;
import org.csstudio.nams.common.material.SynchronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SynchronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO.ReplicationState;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.messaging.declaration.Pausable;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerksBuilderException;

public class Synchronizer {
	
	private enum SynchronizationState {
		IDLE, SYNCHRONIZING, NEEDS_ANOTHER_SYNCHRONIZE;
	}
	
	private SynchronizationState synchronizationState = SynchronizationState.IDLE;
	private final HistoryService historyService;
	private final ILogger logger;
	private final Pausable alarmConsumer;
	private final Producer amsProducer;
	private final Producer extCommandProducer;
	private final LocalStoreConfigurationService localStoreConfigurationService;
	private final RegelwerkBuilderService regelwerkBuilderService;
	private final DecisionDepartment alarmEntscheidungsBuero;

	public Synchronizer(HistoryService historyService, ILogger logger, Pausable alarmConsumer, Producer amsProducer, Producer extCommandProducer,
			LocalStoreConfigurationService localStoreConfigurationService, RegelwerkBuilderService regelwerkBuilderService, DecisionDepartment alarmEntscheidungsBuero) {
		this.historyService = historyService;
		this.logger = logger;
		this.alarmConsumer = alarmConsumer;
		this.amsProducer = amsProducer;
		this.extCommandProducer = extCommandProducer;
		this.localStoreConfigurationService = localStoreConfigurationService;
		this.regelwerkBuilderService = regelwerkBuilderService;
		this.alarmEntscheidungsBuero = alarmEntscheidungsBuero;
	}

	public void startSynchronization() throws StorageError, StorageException, InconsistentConfigurationException, UnknownConfigurationElementError, MessagingException {
		// nur synchronisieren, wenn noch keine Synchronisation läuft
		if (synchronizationState == SynchronizationState.IDLE) {
			doSynchronize();
		} else if(synchronizationState == SynchronizationState.SYNCHRONIZING) {
			synchronizationState = SynchronizationState.NEEDS_ANOTHER_SYNCHRONIZE;
		}
	}

	public void handleSynchronizationFinishedMessageReceived() throws StorageError, StorageException, InconsistentConfigurationException, UnknownConfigurationElementError, MessagingException, RegelwerksBuilderException {
		if (synchronizationState != SynchronizationState.IDLE) {
			if (synchronizationState == SynchronizationState.NEEDS_ANOTHER_SYNCHRONIZE) {
				// falls zwischenzeitlich weitere Synchronisations-Requests kamen, erneut synchronisieren
				// Falls mehrere Synchronisationen angefragt wurden, reicht auch nur eine weitere.
				doSynchronize();
			} else if(synchronizationState == SynchronizationState.SYNCHRONIZING){
				logger.logInfoMessage(this, "Synchronization done.");

				synchronizationState = SynchronizationState.IDLE;
				
				alarmEntscheidungsBuero.updateRegelwerke(regelwerkBuilderService.gibAlleRegelwerke());

				if (alarmConsumer.isPaused()) {
					alarmConsumer.unpause();
				}

				extCommandProducer.sendeSystemnachricht(new SynchronisationsBestaetigungSystemNachricht());
				logger.logInfoMessage(this, "Synchronize confirm message sent.");
			}
		}
	}

	private void doSynchronize() throws StorageError, StorageException, InconsistentConfigurationException, UnknownConfigurationElementError,
			MessagingException {
		historyService.logReceivedStartReplicationMessage();
		logger.logInfoMessage(this, "Decision department received re-synchronization request, going to be re-initialized...");

		synchronizationState = SynchronizationState.SYNCHRONIZING;

		if (!alarmConsumer.isPaused()) {
			alarmConsumer.pause();
		}
		logger.logInfoMessage(this, "Decision department application orders distributor to synchronize configuration...");

		final ReplicationStateDTO stateDTO = localStoreConfigurationService.getCurrentReplicationState();
		final ReplicationState replicationState = stateDTO.getReplicationState();
		if ((replicationState != ReplicationState.FLAGVALUE_SYNCH_DIST_RPL) && (replicationState != ReplicationState.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR)) {
			stateDTO.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
			localStoreConfigurationService.saveCurrentReplicationState(stateDTO);
		}

		amsProducer.sendeSystemnachricht(new SynchronisationsAufforderungsSystemNachchricht());
	}
}
