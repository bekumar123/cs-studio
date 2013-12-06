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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.dal2.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.cs.ICsPvAccess;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.servicelocator.ServiceLocator;

/**
 * Implementation of the dal service
 * 
 * @author jpenning, arne scharping
 * @since 06.09.2012
 */
public class DalService implements IDalService {

	private final Logger _logger = Logger.getLogger(getClass().getName());

	private ICsPvAccessFactory dalPluginService;
	private Map<Key, IPvAccess<?>> _accessPool = new HashMap<Key, IPvAccess<?>>();

	public DalService() {

		// currently there is only the epics implementation so we do not collect
		// all available implementations as we should
		dalPluginService = ServiceLocator.getService(ICsPvAccessFactory.class);

		_logger.info("DALService created");
	}

	public DalService(ICsPvAccessFactory pluginService) {
		assert pluginService != null : "Precondition: pluginService != null";
		dalPluginService = pluginService;
	}

	@Override
	public synchronized void disposeAll() {
		Iterator<Entry<Key, IPvAccess<?>>> iterator = _accessPool.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<Key, IPvAccess<?>> entry = iterator.next();
			entry.getValue().deregisterAllListener();
			iterator.remove();
		}
	}

	@Override
	public void dispose(IPvAccess<?> pvAccess) {
		assert pvAccess != null;

		pvAccess.deregisterAllListener();

		Key key = new Key(pvAccess.getPVAddress(), pvAccess.getType(),
				pvAccess.getListenerType());
		if (_accessPool.remove(key) == null) {
			throw new IllegalStateException(
					"pv access is not known to this service");
		}
	}

	@Override
	public synchronized <T> IPvAccess<T> getPVAccess(PvAddress address,
			Type<T> type) {
		
		assert address != null : "Precondition: address != null";
		assert type != null : "Precondition: type != null";
		
		return getPVAccess(address, type, ListenerType.VALUE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> IPvAccess<T> getPVAccess(PvAddress address,
			Type<T> type, ListenerType listenerType) {

		Key key = new Key(address, type, listenerType);
		IPvAccess<T> result = (IPvAccess<T>) _accessPool.get(key);

		if (result == null) {
			result = createPVAccess(address, type, listenerType);
			_accessPool.put(key, result);
		}

		return result;
	}

	@Override
	public synchronized Type<?> getNativeType(PvAddress address)
			throws DalException {
		return getNativeType(address, IDalService.DEFAULT_TIMEOUT,
				TimeUnit.SECONDS);
	}

	@Override
	public synchronized Type<?> getNativeType(PvAddress address, long timeout,
			TimeUnit timeoutUnit) throws DalException {
		assert address != null : "Precondition: address != null";
		assert timeoutUnit != null : "Precondition: timeoutUnit != null";
		assert timeout > 0 : "Precondition: timeout > 0";

		SynchronizingResponseLister<Type<?>> synchronizingListener = new SynchronizingResponseLister<Type<?>>();
		dalPluginService.requestNativeType(address, synchronizingListener);
		return synchronizingListener.getValue(timeout, timeoutUnit);
	}

	private <T> IPvAccess<T> createPVAccess(PvAddress address, Type<T> type,
			ListenerType listenerType) {
		ICsPvAccess<T> pluginPVAccess = dalPluginService.createPVAccess(
				address, type);
		return new PvAccess<T>(pluginPVAccess, type, listenerType);
	}

	private static class Key {

		private final Type<?> _type;
		private final PvAddress _address;
		private final ListenerType _listenerType;

		public Key(PvAddress address, Type<?> type, ListenerType listenerType) {

			if (address == null) {
				throw new IllegalArgumentException("address must not be null");
			}
			if (type == null) {
				throw new IllegalArgumentException("type must not be null");
			}
			if (listenerType == null) {
				throw new IllegalArgumentException(
						"listenerType must not be null");
			}

			_address = address;
			_type = type;
			_listenerType = listenerType;
		}

		@Override
		public int hashCode() {
			return _address.hashCode() + 31 * _type.hashCode() + 17
					* _listenerType.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			return _address.equals(other._address) && _type.equals(other._type)
					&& _listenerType.equals(other._listenerType);
		}

		@Override
		public String toString() {
			return _address.getAddress() + "(" + _type + ", " + _listenerType
					+ ")";
		}
	}
}
