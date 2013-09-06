/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.service.internal;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.declaration.IRemoteAcknowledgeService;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IDalServiceFactory;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IResponseListener;
import org.csstudio.remote.jms.command.IRemoteCommandService;
import org.csstudio.remote.jms.command.RemoteCommandException;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for Dal- and Jms-based services.
 * 
 * @author jpenning
 * @since 19.01.2012
 */
public abstract class AbstractAlarmService implements IAlarmService {
	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractAlarmService.class);

	protected int INITIAL_RETRIEVAL_TIMEOUT = 30;
	
	private final List<IAlarmService.IListener> _listeners = new ArrayList<IAlarmService.IListener>();

	private ContentModel<LdapEpicsAlarmcfgConfiguration> _contentModel = null;
	private AtomicBoolean _requestReloadOfContentModel = new AtomicBoolean(true);

	private IDalService _dalService;

	public AbstractAlarmService() {
		this(ServiceLocator.getService(IDalServiceFactory.class)
				.newDalService());
	}

	/**
	 * Constructor
	 * 
	 * @require dalService != null
	 */
	public AbstractAlarmService(@Nonnull IDalService dalService) {

		assert dalService != null : "Precondition: dalService != null";
		this._dalService = dalService;

		if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue()
				|| AlarmPreference.ALARMSERVICE_RUNS_AS_SERVER.getValue()) {
			registerCallbackAtRemoteCommandService();
		}
	}

	private void registerCallbackAtRemoteCommandService() {
		IRemoteCommandService service = ServiceLocator
				.getService(IRemoteCommandService.class);
		IRemoteCommandService.IListener listener = new IRemoteCommandService.IListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void receiveCommand(@Nonnull final String command) {
				for (IAlarmService.IListener updateListener : _listeners) {
					if (IRemoteCommandService.ReloadFromLdapCommand
							.equals(command)) {
						invalidateConfigurationCache();
						updateListener.configurationUpdated();
					} else if (IRemoteCommandService.Dal2JmsReloadedCommand
							.equals(command)) {
						updateListener.alarmServerReloaded();
					} else if (IRemoteCommandService.Dal2JmsStartedCommand
							.equals(command)) {
						updateListener.alarmServerStarted();
					} else if (IRemoteCommandService.Dal2JmsWillStopCommand
							.equals(command)) {
						updateListener.alarmServerWillStop();
					}
				}
			}
		};
		try {
			service.register(AlarmPreference.getClientGroup(), listener);
		} catch (RemoteCommandException e) {
			LOG.error(
					"Could not register alarm service at remote command service. Remote update will not work.",
					e);
		}
	}

	protected IDalService getDalService() {
		return _dalService;
	}

	@Override
	@Nonnull
	public synchronized ContentModel<LdapEpicsAlarmcfgConfiguration> getConfiguration()
			throws AlarmServiceException {
		LOG.trace("getConfiguration entered");
		if (_requestReloadOfContentModel.getAndSet(false)) {
			LOG.trace("getConfiguration will retrieve configuration");
			_contentModel = retrieveConfiguration();
			LOG.trace("getConfiguration finished retrieval of configuration");
		}
		return _contentModel;
	}

	@Override
	public void invalidateConfigurationCache() {
		_requestReloadOfContentModel.set(true);
	}

	@Nonnull
	private ContentModel<LdapEpicsAlarmcfgConfiguration> retrieveConfiguration()
			throws AlarmServiceException {
		final IAlarmConfigurationService configService = ServiceLocator
				.getService(IAlarmConfigurationService.class);

		if (configService == null) {
			String message = "Retrieval of alarm tree configuration failed. Alarm configuration service not available.";
			LOG.error(message);
			throw new AlarmServiceException(message);
		}

		ContentModel<LdapEpicsAlarmcfgConfiguration> model = null;

		try {
			if (AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue()) {
				LOG.trace("retrieve configuration from ldap");
				model = configService
						.retrieveInitialContentModel(AlarmPreference
								.getFacilityNames());
			} else {
				LOG.trace("retrieve configuration from file " + AlarmPreference.getConfigFilename());
				model = configService
						.retrieveInitialContentModelFromFile(AlarmPreference
								.getConfigFilename());
			}
		} catch (FileNotFoundException e) {
			String message = "Opening File!\n"
					+ "Could not properly open the input file stream: "
					+ e.getMessage();
			LOG.error(message, e);
			throw new AlarmServiceException(message, e);
		} catch (CreateContentModelException e) {
			String message = "Building content model!\n"
					+ "Could not properly build the content model from LDAP or XML: "
					+ e.getMessage();
			LOG.error(message, e);
			throw new AlarmServiceException(message, e);
		} catch (LdapServiceException e) {
			String message = "Accessing LDAP!\n" + "Internal service error: "
					+ e.getMessage();
			LOG.error(message, e);
			throw new AlarmServiceException(message, e);
		}

		return model;
	}

	@Override
	@Nonnull
	public Set<String> getPvNames() throws AlarmServiceException {
		return getConfiguration().getSimpleNames(
				LdapEpicsAlarmcfgConfiguration.RECORD);
	}

	@Override
	public void register(@Nonnull final IListener listener) {
		_listeners.add(listener);
	}

	@Override
	public void deregister(@Nonnull final IListener listener) {
		_listeners.remove(listener);
	}

	private static class InitialStateReceiver implements
			IResponseListener<String> {

		private IAlarmInitItem _item;
		private IPvAccess<String> _pvAccess;

		public InitialStateReceiver(IAlarmInitItem item,
				IPvAccess<String> pvAccess, long timeout) {
			_item = item;
			_pvAccess = pvAccess;

			try {
				pvAccess.getValue(timeout, TimeUnit.SECONDS, this);
			} catch (DalException e) {
				_item.notFound(_item.getPVName());
				LOG.error(
						"Error requesting initial state for pv "
								+ _item.getPVName(), e);
			}
		}

		@Override
		public void onFailure(Throwable throwable) {
			_item.notFound(_item.getPVName());
			LOG.error(
					"Error retrieving initial state for pv "
							+ _item.getPVName(), throwable);
		}

		@Override
		public void onSuccess(String response) {
			_item.init(AlarmMessageDAL2Impl.newAlarmMessage(_pvAccess));
		}

		@Override
		public void onTimeout() {
			_item.notFound(_item.getPVName());
			LOG.warn("Timeout retrieving initial state for pv "
					+ _item.getPVName());
		}
	}

	@Override
	public final void retrieveInitialState(
			@Nonnull final List<IAlarmInitItem> initItems)
			throws AlarmServiceException {
		
		LOG.debug("retrieveInitialState for " + initItems.size() + " items");

		// TODO Provide progress monitor + additional logging
		
		for (IAlarmInitItem item : initItems) {
			PvAddress address = PvAddress.getValue(item.getPVName());
			IPvAccess<String> pvAccess = _dalService.getPVAccess(address,
					Type.STRING);
			new InitialStateReceiver(item, pvAccess, INITIAL_RETRIEVAL_TIMEOUT);
		}

		if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue()) {
			retrieveAcknowledgeState(initItems);
		}
	}

	private void retrieveAcknowledgeState(
			@Nonnull final List<IAlarmInitItem> initItems)
			throws AlarmServiceException {
		IRemoteAcknowledgeService acknowledgeService = ServiceLocator
				.getService(IRemoteAcknowledgeService.class);
		if (acknowledgeService != null) {

			try {
				Collection<String> acknowledgedPvs = acknowledgeService
						.getAcknowledgedPvs();
				for (IAlarmInitItem item : initItems) {
					if (acknowledgedPvs.contains(item.getPVName())) {
						item.acknowledge();
					}
				}
			} catch (RemoteException e) {
				LOG.error("Cannot get acknowledged PVs from server", e);
				// Tunneling of remote error
				throw new AlarmServiceException(
						"No connection to acknowledge server", e);
			}
		} else {
			LOG.error("Cannot lookup acknowledge server");
			// Tunneling of remote error
			throw new AlarmServiceException(
					"No connection to acknowledge server");
		}
	}
	
}
