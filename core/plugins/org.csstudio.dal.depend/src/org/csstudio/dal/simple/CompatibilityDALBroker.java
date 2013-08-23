/**
 *
 */
package org.csstudio.dal.simple;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.simple.compatibility.AsyncRequestAdapter;
import org.csstudio.dal.simple.compatibility.ListenerAdapter;
import org.csstudio.dal.spi.Plugs;
import org.csstudio.dal.spi.PropertyFactoryService;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IDalServiceFactory;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.servicelocator.ServiceLocator;

import com.cosylab.util.CommonException;

/**
 * This is an adapter providing the interface of the simple dal broker based on the dal2 implementation
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CompatibilityDALBroker {

	public static CompatibilityDALBroker newInstance(
			final AbstractApplicationContext ctx) {

		final Object o = ctx
				.getApplicationProperty(Plugs.PROPERTY_FACTORY_SERVICE_IMPLEMENTATION);

		if (o instanceof PropertyFactoryService) {
			return newInstance(ctx, (PropertyFactoryService) o);
		}
		return newInstance(ctx, null);
	}

	public static CompatibilityDALBroker newInstance(
			final AbstractApplicationContext ctx,
			final PropertyFactoryService service) {
		return new CompatibilityDALBroker(ctx);
	}

	private IDalService dalService = null;

	private Map<ChannelListener, IPvListener<?>> channelListenerAdapter = new HashMap<ChannelListener, IPvListener<?>>();
	private Map<PropertyChangeListener, IPvListener<?>> propertyChangeListenerAdapter = new HashMap<PropertyChangeListener, IPvListener<?>>();

	private CompatibilityDALBroker(final AbstractApplicationContext ctx) {
		IDalServiceFactory dalServiceFactory = ServiceLocator
				.getService(IDalServiceFactory.class);
		dalService = dalServiceFactory.newDalService();
	}

	public int getPropertiesMapSize() {
		int size = -1;
		return size;
	}

	public Object getValue(final ConnectionParameters cparam)
			throws InstantiationException, CommonException {
		try {
			return getPVAccess(cparam).getValue();
		} catch (DalException e) {
			throw new CommonException(this, "Error reading value", e);
		}
	}

	public <T> T getValue(final RemoteInfo rinfo, final Class<T> javaType)
			throws InstantiationException, CommonException {
		try {
			IPvAccess<?> pvAccess = getPVAccess(rinfo, javaType,
					ListenerType.VALUE);
			return javaType.cast(pvAccess.getValue());
		} catch (DalException e) {
			throw new CommonException(this, "Error reading value", e);
		}
	}

	public Object getValue(final RemoteInfo rinfo)
			throws InstantiationException, CommonException {
		return getValue(new ConnectionParameters(rinfo));
	}

	public Object getValue(final String property)
			throws InstantiationException, CommonException {
		return getValue(RemoteInfo.fromString(property,
				RemoteInfo.DAL_TYPE_PREFIX + "EPICS"));
	}

	public <T> Request<T> getValueAsync(final ConnectionParameters cparam,
			final ResponseListener<T> callback) throws InstantiationException,
			CommonException {
		try {
			IPvAccess<?> pvAccess = getPVAccess(cparam);

			AsyncRequestAdapter adapter = new AsyncRequestAdapter(pvAccess,
					callback);
			pvAccess.getValue(adapter);

			return adapter.getRequest();
		} catch (DalException e) {
			throw new CommonException(this, "Error reading value", e);
		}
	}

	public void setValue(final RemoteInfo rinfo, final Object value)
			throws InstantiationException, CommonException {
		throw new UnsupportedOperationException("Not implemented");

	}

	public <T> Request<T> setValueAsync(final ConnectionParameters cparam,
			final Object value, final ResponseListener<T> callback)
			throws Exception {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void registerListener(final ConnectionParameters cparam,
			final ChannelListener listener) throws InstantiationException,
			CommonException {
		try {

			IPvAccess<?> pvAccess = getPVAccess(cparam);
			IPvListener pvListener = new ListenerAdapter(pvAccess, listener);

			pvAccess.registerListener(pvListener);

			channelListenerAdapter.put(listener, pvListener);

		} catch (DalException e) {
			throw new CommonException(this, "cannot register listener", e);
		}
	}

	public void deregisterListener(final ConnectionParameters cparam,
			final ChannelListener listener) throws InstantiationException,
			CommonException {
		try {
			IPvListener pvListener = channelListenerAdapter.remove(listener);
			getPVAccess(cparam).deregisterListener(pvListener);
		} catch (DalException e) {
			throw new CommonException(this, "cannot deregister listener", e);
		}
	}

	@Deprecated
	public void registerListener(final ConnectionParameters cparam,
			final DynamicValueListener listener) throws InstantiationException,
			CommonException {
		throw new UnsupportedOperationException(
				"DynamicValueListener is not supported");
	}

	@Deprecated
	public void deregisterListener(final ConnectionParameters cparam,
			final DynamicValueListener listener) throws InstantiationException,
			CommonException {
		throw new UnsupportedOperationException(
				"DynamicValueListener is not supported");
	}

	public void registerListener(final ConnectionParameters cparam,
			final PropertyChangeListener listener)
			throws InstantiationException, CommonException {
		try {
			IPvAccess<?> pvAccess = getPVAccess(cparam);
			IPvListener pvListener = new ListenerAdapter(pvAccess, listener);
			pvAccess.registerListener(pvListener);
			propertyChangeListenerAdapter.put(listener, pvListener);
		} catch (DalException e) {
			throw new CommonException(this, "cannot register listener", e);
		}
	}

	public void deregisterListener(final ConnectionParameters cparam,
			final PropertyChangeListener listener)
			throws InstantiationException, CommonException {

		try {
			IPvListener pvListener = propertyChangeListenerAdapter
					.remove(listener);
			getPVAccess(cparam).deregisterListener(pvListener);
		} catch (DalException e) {
			throw new CommonException(this, "cannot deregister listener", e);
		}
	}

	public void registerListener(final ConnectionParameters cparam,
			final DynamicValueListener listener,
			final Map<String, Object> parameters)
			throws InstantiationException, CommonException {
		throw new UnsupportedOperationException(
				"DynamicValueListener is not supported");
	}

	public void deregisterListener(final ConnectionParameters cparam,
			final DynamicValueListener listener,
			final Map<String, Object> parameters)
			throws InstantiationException, CommonException {
		throw new UnsupportedOperationException(
				"DynamicValueListener is not supported");
	}

	public String getDefaultPlugType() {
		return "EPICS";
	}

	public void setDefaultPlugType(final String plugType) {
		// TODO implement method
		throw new UnsupportedOperationException("Not implemented");
	}

	public void releaseAll() {
		dalService.disposeAll();
		channelListenerAdapter.clear();
		propertyChangeListenerAdapter.clear();
	}

	private <T> IPvAccess<T> getPVAccess(final RemoteInfo rinfo,
			final Class<T> javaType, ListenerType listenerType) {

		if (javaType == null) {
			throw new IllegalArgumentException("javaType must not be null");
		}

		ConnectionParameters cparam = new ConnectionParameters(rinfo);
		PvAddress pvAddress = determinePvAddress(cparam);
		Type<T> type = Type.getType(javaType);

		return dalService.getPVAccess(pvAddress, type, listenerType);
	}

	private IPvAccess<?> getPVAccess(final ConnectionParameters cparam)
			throws DalException {

		if (cparam.getConnectionType() != cparam.getDataType()) {
			throw new IllegalArgumentException(
					"connection parameter with different types are not supported: "
							+ cparam);
		}

		PvAddress pvAddress = determinePvAddress(cparam);

		Type<?> type = determineType(cparam);
		if (type == null) {
			type = dalService.getNativeType(pvAddress);
		}

		return dalService.getPVAccess(pvAddress, type, ListenerType.VALUE);
	}

	private Type<?> determineType(final ConnectionParameters cparam) {

		DataFlavor dataType = cparam.getDataType();
		switch (dataType) {
		case DOUBLE:
			return Type.DOUBLE;
		case DOUBLES:
			return Type.DOUBLE_SEQ;
		case LONG:
			return Type.LONG;
		case LONGS:
			return Type.LONG_SEQ;
		case STRING:
			return Type.STRING;
		case STRINGS:
			return Type.STRING_SEQ;
		case ENUM:
			return Type.ENUM;
		case PATTERN:
		case OBJECT:
		case OBJECTS:
		default:
			throw new IllegalArgumentException("Type not available: "
					+ dataType);
		}

	}

	private PvAddress determinePvAddress(final ConnectionParameters cparam) {
		String remoteName = cparam.getRemoteInfo().getRemoteName();
		return PvAddress.getValue(remoteName);
	}

}
