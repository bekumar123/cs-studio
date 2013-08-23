package org.csstudio.dal.simple.compatibility;

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.csstudio.dal.DataAccess;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.DynamicValueMonitor;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.EventSystemListener;
import org.csstudio.dal.ExpertMonitor;
import org.csstudio.dal.IllegalViewException;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.Response;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.context.Identifier;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.context.Linkable;
import org.csstudio.dal.context.PropertyContext;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.service.IPvAccess;

/**
 * Minimal Implementation of a dynamic value property used to provide
 * compatibility with old dal
 * 
 * @param <T>
 */
public class DynamicValuePropertyCompatibilityAdapter<T> implements
		DynamicValueProperty<T> {

	private final IPvAccess<T> _pvAccess;

	public DynamicValuePropertyCompatibilityAdapter(IPvAccess<T> pvAccess) {
		_pvAccess = pvAccess;
	}

	@Override
	public Object getCharacteristic(String name) throws DataExchangeException {
		Characteristics characteristics = _pvAccess
				.getLastKnownCharacteristics();
		Characteristic<?> characteristic = CompatibilityMapper
				.toDal2Characteristic(name);
		return characteristics.get(characteristic);
	}

	@Override
	public ConnectionState getConnectionState() {

		if (_pvAccess.isConnected()) {
			return ConnectionState.OPERATIONAL;
		} else {
			return ConnectionState.DISCONNECTED;
		}
	}

	@Override
	public DynamicValueCondition getCondition() {
		return CompatibilityMapper.createDynamicValueCondition(_pvAccess);
	}

	/*
	 * The following methods are not supported by this implementation
	 */

	@Override
	@Deprecated
	public String getUniqueName() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Class<? extends DataAccess<?>>[] getAccessTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public <D extends DataAccess<?>> D getDataAccess(Class<D> type)
			throws IllegalViewException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public DataAccess<T> getDefaultDataAccess() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getDescription() throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isTimeout() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isTimelag() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public <P extends SimpleProperty<T>> void addDynamicValueListener(
			DynamicValueListener<T, P> l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public <P extends SimpleProperty<T>> void removeDynamicValueListener(
			DynamicValueListener<T, P> l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public DynamicValueListener<T, ? extends SimpleProperty<T>>[] getDynamicValueListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean hasDynamicValueListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Class<T> getDataType() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isSettable() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void setValue(T value) throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public T getValue() throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public T getLatestReceivedValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Map<String, Object> getCharacteristics(String[] names)
			throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String[] getCharacteristicNames() throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void addPropertyChangeListener(PropertyChangeListener l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void removePropertyChangeListener(PropertyChangeListener l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public PropertyChangeListener[] getPropertyChangeListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Timestamp getLatestValueChangeTimestamp() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean getLatestValueSuccess() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Timestamp getLatestValueUpdateTimestamp() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public DynamicValueMonitor getDefaultMonitor() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public DynamicValueMonitor[] getMonitors() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public <E extends SimpleProperty<T>> DynamicValueMonitor createNewMonitor(
			DynamicValueListener<T, E> listener) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Identifier getIdentifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isDebug() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void addListener(ChannelListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void removeListener(ChannelListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public ChannelListener[] getListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void start() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void startSync() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isRunning() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isConnected() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isWriteAllowed() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getStateInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void stop() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public AnyData getData() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void setValueAsObject(Object new_value) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public DynamicValueProperty<?> getProperty() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isMetaDataInitialized() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<T> getAsynchronous() throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<T> getAsynchronous(ResponseListener<T> listener)
			throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<T> setAsynchronous(T value) throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<T> setAsynchronous(T value, ResponseListener<T> listener)
			throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void addResponseListener(ResponseListener<?> l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void removeResponseListener(ResponseListener<?> l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public ResponseListener<?>[] getResponseListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<?> getLatestRequest() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Response<?> getLatestResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean getLatestSuccess() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<? extends Object> getCharacteristicsAsynchronously(
			String[] names) throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<? extends Object> getCharacteristicsAsynchronously(
			String[] names, ResponseListener<? extends Object> listener)
			throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<? extends Object> getCharacteristicAsynchronously(String name)
			throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<?> getCharacteristicAsynchronously(String name,
			ResponseListener<?> listener) throws DataExchangeException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Request<T> getLatestValueRequest() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Response<T> getLatestValueResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void addLinkListener(LinkListener<? extends Linkable> l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isOperational() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isDestroyed() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isSuspended() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isConnectionAlive() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isConnectionFailed() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void refresh() throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void removeLinkListener(LinkListener<? extends Linkable> l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void resume() throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void suspend() throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void addEventSystemListener(
			EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l,
			Map<String, Object> parameters) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void addEventSystemListener(
			EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l)
			throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void removeEventSystemListener(
			EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l,
			Map<String, Object> parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void removeEventSystemListener(
			EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>>[] getEventSystemListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Map<String, Object> getSupportedEventSystemParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public PropertyContext getParentContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Map<String, Object> getSupportedExpertMonitorParameters() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("hiding")
	@Override
	@Deprecated
	public <E extends SimpleProperty<T>, M extends ExpertMonitor, DynamicValueMonitor> M createNewExpertMonitor(
			DynamicValueListener<T, E> listener, Map<String, Object> parameters)
			throws RemoteException {
		throw new UnsupportedOperationException();
	}

}
