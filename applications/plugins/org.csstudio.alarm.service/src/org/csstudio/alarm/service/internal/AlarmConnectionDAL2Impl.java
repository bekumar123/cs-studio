/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmConnectionJMSImpl.java,v 1.4
 * 2010/04/28 07:58:00 jpenning Exp $
 */
package org.csstudio.alarm.service.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmResource;
import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.servicelocator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the DAL2 based implementation of the AlarmConnection.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 28.06.2013
 */
public class AlarmConnectionDAL2Impl implements IAlarmConnection {

	private static final Logger LOG = LoggerFactory
			.getLogger(AlarmConnectionDAL2Impl.class);

	private static final String COULD_NOT_CREATE_DAL_CONNECTION = "Could not create DAL connection";

	private final Map<String, ListenerItem> _pv2listenerItem = new HashMap<String, ListenerItem>();

	// The listener is given once at connect
	private IAlarmListener _listener;

	private IDalService _dalService;

	/**
	 * Constructor must be called only from the AlarmService.
	 * 
	 * @param dalService
	 */
	public AlarmConnectionDAL2Impl(@Nonnull IDalService dalService) {
		this._dalService = dalService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandleTopics() {
		return false;
	}

	@Override
	public void connect(
			@Nonnull final IAlarmConnectionMonitor connectionMonitor,
			@Nonnull final IAlarmListener listener,
			@Nonnull final AlarmResource resource)
			throws AlarmConnectionException {
		LOG.info("Connecting to DAL2.");

		_listener = listener;

		Set<String> simpleNames = getPVNamesFromResource();
		LOG.debug("About to connect " + simpleNames.size() + " PVs");
		for (String recordName : simpleNames) {
			LOG.trace("Connecting to " + recordName);
			registerPV(recordName);
		}

		// The DAL implementation sends connect here, because the
		// DynamicValueListenerAdapter will not do so
		connectionMonitor.onConnect();
	}

	@Override
	public void registerPV(@Nonnull final String pvName) {
		// A pv is only registered once
		if (!_pv2listenerItem.containsKey(pvName)) {

			try {
				IPvAccess<String> pvAccess = _dalService.getPVAccess(
						PvAddress.getValue(pvName), Type.STRING,
						ListenerType.ALARM);

				ListenerItem listenerItem = new ListenerItem(_listener,
						pvAccess);
				listenerItem.register();
				_pv2listenerItem.put(pvName, listenerItem);

			} catch (final DalException e) {
				LOG.error(COULD_NOT_CREATE_DAL_CONNECTION, e);
			}
		}
	}

	@Override
	public void deregisterPV(@Nonnull final String pvName) {

		ListenerItem listenerItem = _pv2listenerItem.remove(pvName);
		if (listenerItem != null) {
			try {
				listenerItem.deregister();
			} catch (DalException e) {
				LOG.warn("Trying to deregister a pv named '" + pvName
						+ "' which was not registered.", e);
			}
		}
	}

	@Override
	public void reloadPVsFromResource() throws AlarmConnectionException {
		// calculate change sets: toBeRemoved = current - new, toBeConnected =
		// new - current
		Set<String> currentPVs = new HashSet<String>(_pv2listenerItem.keySet());
		Set<String> newPVs = new HashSet<String>(getPVNamesFromResource());

		Set<String> toBeConnectedPvs = new HashSet<String>(newPVs);
		toBeConnectedPvs.removeAll(currentPVs);

		Set<String> toBeRemovedPvs = new HashSet<String>(currentPVs);
		toBeRemovedPvs.removeAll(newPVs);

		deregister(toBeRemovedPvs);
		register(toBeConnectedPvs);
	}

	@Override
	public void disconnect() {
		LOG.info("Disconnecting from DAL. Deregistering all PVs.");
		for (final ListenerItem item : _pv2listenerItem.values()) {
			try {
				item.deregister();
			} catch (DalException e) {
				LOG.error("Error disconnecting pv {}",
						item._pvAccess.getPVAddress(), e);
			}
		}
		_pv2listenerItem.clear();
		_dalService.disposeAll();
		_listener = null;
	}

	@Override
	public String getStatusAsString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Status of the alarm connection\n");

		builder.append("Connected:\t" + Boolean.toString(_listener != null)
				+ "\n");

		int connectedPvs = 0;
		for (final ListenerItem item : _pv2listenerItem.values()) {
			IPvAccess<String> pvAccess = item._pvAccess;
			if (pvAccess.isConnected()) {
				connectedPvs++;
			}
		}
		builder.append("Number of PVs:\t" + _pv2listenerItem.size() + " (" + connectedPvs + " connected)\n");

		return builder.toString();
	}

	/**
	 * This method enscapsulates retrieval of data from ldap or an xml file. It
	 * is protected so it may be overridden by a test, another option for tests
	 * is to mock the alarm service.
	 * 
	 * @return all the pv names from the initially given resource
	 * @throws AlarmConnectionException
	 */
	@Nonnull
	protected Set<String> getPVNamesFromResource()
			throws AlarmConnectionException {
		IAlarmService alarmService = ServiceLocator
				.getService(IAlarmService.class);
		try {
			return alarmService.getPvNames();
		} catch (AlarmServiceException e) {
			throw new AlarmConnectionException(e.getMessage(), e);
		}
	}

	protected void register(@Nonnull final Set<String> pvSet) {
		LOG.info("Registering " + pvSet.size() + " PVs.");
		for (String pvName : pvSet) {
			registerPV(pvName);
		}
	}

	protected void deregister(@Nonnull final Set<String> pvSet) {
		LOG.info("Deregistering " + pvSet.size() + " PVs.");
		for (String pvName : pvSet) {
			deregisterPV(pvName);
		}
	}

	protected static final class ListenerItem implements IPvListener<String> {

		private static final Logger LOG_INNER = LoggerFactory
				.getLogger(ListenerItem.class);

		private IPvAccess<String> _pvAccess;

		private IAlarmListener _alarmListener;

		public ListenerItem(IAlarmListener alarmListener,
				IPvAccess<String> pvAccess) {
			this._alarmListener = alarmListener;
			this._pvAccess = pvAccess;
		}

		public void register() throws DalException {
			_pvAccess.registerListener(this);
		}

		public void deregister() throws DalException {
			_pvAccess.deregisterListener(this);
		}

		@Override
		public void valueChanged(IPvAccess<String> source, String value) {
			LOG_INNER.trace("received {} for pv {} value {}", new Object[] {
					value, source.getPVAddress().getAddress() });
			_alarmListener.onMessage(AlarmMessageDAL2Impl
					.newAlarmMessage(_pvAccess));
		}

		@Override
		public void connectionChanged(IPvAccess<String> source,
				boolean isConnected) {
			LOG_INNER.trace("received connection change to {} for pv {} ",
					new Object[] { source.getPVAddress().getAddress(),
							isConnected });
		}

	};

}
