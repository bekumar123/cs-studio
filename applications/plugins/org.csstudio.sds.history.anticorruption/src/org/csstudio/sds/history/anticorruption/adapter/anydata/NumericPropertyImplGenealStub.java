package org.csstudio.sds.history.anticorruption.adapter.anydata;

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
import org.csstudio.dal.NumericProperty;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumericPropertyImplGenealStub<T,Ts> implements NumericProperty<T, Ts> {
	
	private Logger LOG = LoggerFactory.getLogger("NumDataLog");
	
	@Override
	public PropertyContext getParentContext() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getParentContext()");
		return null;
	}

	@Override
	public Map<String, Object> getSupportedExpertMonitorParameters() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getSupportedExpertMonitorParameters()");
		return null;
	}

	@Override
	public <E extends SimpleProperty<T>, M extends ExpertMonitor, DynamicValueMonitor> M createNewExpertMonitor(
			DynamicValueListener<T, E> listener, Map<String, Object> parameters) throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.createNewExpertMonitor");
		return null;
	}

	@Override
	public String getName() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getName()");
		return null;
	}

	@Override
	public Class<? extends DataAccess<?>>[] getAccessTypes() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getAccessTypes()");
		return null;
	}

	@Override
	public <D extends DataAccess<?>> D getDataAccess(Class<D> type) throws IllegalViewException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getDataAccess(Class<D> type)");
		return null;
	}

	@Override
	public DataAccess<T> getDefaultDataAccess() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getDefaultDataAccess()");
		return null;
	}

	@Override
	public String getDescription() throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getDescription()");
		return null;
	}

	@Override
	public DynamicValueCondition getCondition() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCondition()");
		return null;
	}

	@Override
	public boolean isTimeout() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isTimeout()");
		return false;
	}

	@Override
	public boolean isTimelag() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isTimelag()");
		return false;
	}

	@Override
	public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l) {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.addDynamicValueListener(DynamicValueListener<T, P> l)");
	}

	@Override
	public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T, P> l) {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.removeDynamicValueListener(DynamicValueListener<T, P> l)");
	}

	@Override
	public DynamicValueListener<T, ? extends SimpleProperty<T>>[] getDynamicValueListeners() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getDynamicValueListeners()");
		return null;
	}

	@Override
	public boolean hasDynamicValueListeners() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.hasDynamicValueListeners()");
		return false;
	}

	@Override
	public Class<T> getDataType() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getDataType()");
		return null;
	}

	@Override
	public boolean isSettable() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isSettable()");
		return false;
	}

	@Override
	public void setValue(T value) throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.setValue(T value)");
	}

	@Override
	public T getValue() throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getValue()");
		return null;
	}

	@Override
	public T getLatestReceivedValue() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestReceivedValue()");
		return null;
	}

	@Override
	public Map<String, Object> getCharacteristics(String[] names) throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCharacteristics(String[] names)");
		return null;
	}

	@Override
	public String[] getCharacteristicNames() throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCharacteristicNames()");
		return null;
	}

	@Override
	public Object getCharacteristic(String name) throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCharacteristic(String name) name=" + name);
		return null;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.addPropertyChangeListener(PropertyChangeListener l)");
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.removePropertyChangeListener(PropertyChangeListener l)");
	}

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getPropertyChangeListeners()");
		return null;
	}

	@Override
	public Timestamp getLatestValueChangeTimestamp() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestValueChangeTimestamp()");
		return null;
	}

	@Override
	public boolean getLatestValueSuccess() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestValueSuccess()");
		return false;
	}

	@Override
	public Timestamp getLatestValueUpdateTimestamp() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestValueUpdateTimestamp()");
		return null;
	}

	@Override
	public DynamicValueMonitor getDefaultMonitor() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getDefaultMonitor()");
		return null;
	}

	@Override
	public DynamicValueMonitor[] getMonitors() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getMonitors()");
		return null;
	}

	@Override
	public <E extends SimpleProperty<T>> DynamicValueMonitor createNewMonitor(DynamicValueListener<T, E> listener) throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.createNewMonitor()");
		return null;
	}

	@Override
	public Identifier getIdentifier() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getIdentifier()");
		return null;
	}

	@Override
	public boolean isDebug() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isDebug()");
		return false;
	}
	
	/*
	 * AnyDataChannel start
	 */
	@Override
	public String getUniqueName() {
		LOG.error("not implemented: getUniqueName()");
		return null;
	}
	
	@Override
	public void addListener(ChannelListener listener) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.addListener()");
	}

	@Override
	public void removeListener(ChannelListener listener) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.removeListener()");
		
	}

	@Override
	public ChannelListener[] getListeners() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getListeners()");
		return null;
	}

	@Override
	public void start() throws Exception {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.start()");
	}

	@Override
	public void startSync() throws Exception {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.startSync()");
	}

	@Override
	public boolean isRunning() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isRunning()");
		return false;
	}

	@Override
	public boolean isConnected() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isConnected()");
		return false;
	}

	@Override
	public boolean isWriteAllowed() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isWriteAllowed()");
		return false;
	}

	@Override
	public String getStateInfo() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getStateInfo()");
		return null;
	}

	@Override
	public void stop() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.stop()");
		
	}

	@Override
	public AnyData getData() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getData()");
		return null;
	}

	@Override
	public void setValueAsObject(Object new_value) throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.setValueAsObject()");
		
	}

	@Override
	public DynamicValueProperty<?> getProperty() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getProperty()");
		return null;
	}

	@Override
	public boolean isMetaDataInitialized() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isMetaDataInitialized()");
		return false;
	}
	/*
	 * AnyDataChannel end
	 */
	

	@Override
	public Request<T> getAsynchronous() throws DataExchangeException {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getAsynchronous()");
		return null;
	}

	@Override
	public Request<T> getAsynchronous(ResponseListener<T> listener) throws DataExchangeException {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getAsynchronous()");
		return null;
	}

	@Override
	public Request<T> setAsynchronous(T value) throws DataExchangeException {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.setAsynchronous()");
		return null;
	}

	@Override
	public Request<T> setAsynchronous(T value, ResponseListener<T> listener) throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.setAsynchronous()");
		return null;
	}

	@Override
	public void addResponseListener(ResponseListener<?> l) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.addResponseListener()");
		
	}

	@Override
	public void removeResponseListener(ResponseListener<?> l) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.removeResponseListener()");
	}

	@Override
	public ResponseListener<?>[] getResponseListeners() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getResponseListeners()");
		return null;
	}

	@Override
	public Request<?> getLatestRequest() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestRequest()");
		return null;
	}

	@Override
	public Response<?> getLatestResponse() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestResponse()");
		return null;
	}

	@Override
	public boolean getLatestSuccess() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestSuccess()");
		return false;
	}

	@Override
	public Request<? extends Object> getCharacteristicsAsynchronously(String[] names) throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCharacteristicsAsynchronously()");
		return null;
	}

	@Override
	public Request<? extends Object> getCharacteristicsAsynchronously(String[] names, ResponseListener<? extends Object> listener)
			throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCharacteristicsAsynchronously()");
		return null;
	}

	@Override
	public Request<? extends Object> getCharacteristicAsynchronously(String name) throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCharacteristicAsynchronously()");
		return null;
	}

	@Override
	public Request<?> getCharacteristicAsynchronously(String name, ResponseListener<?> listener) throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getCharacteristicAsynchronously()");
		return null;
	}

	@Override
	public Request<T> getLatestValueRequest() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestValueRequest()");
		return null;
	}

	@Override
	public Response<T> getLatestValueResponse() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getLatestValueResponse()");
		return null;
	}

	@Override
	public void addLinkListener(LinkListener<? extends Linkable> l) {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.addLinkListener()");
	}

	@Override
	public boolean isOperational() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isOperational()");
		return false;
	}

	@Override
	public boolean isDestroyed() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isDestroyed()");
		return false;
	}

	@Override
	public boolean isSuspended() {
		// TODO Auto-generated method stub
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isSuspended()");
		return false;
	}

	@Override
	public boolean isConnectionAlive() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isConnectionAlive()");
		return false;
	}

	@Override
	public boolean isConnectionFailed() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.isConnectionFailed()");
		return false;
	}

	@Override
	public void refresh() throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.refresh()");
	}

	@Override
	public void removeLinkListener(LinkListener<? extends Linkable> l) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.removeLinkListener()");
	}

	@Override
	public void resume() throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.resume()");
	}

	@Override
	public void suspend() throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.suspend()");
	}

	@Override
	public ConnectionState getConnectionState() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getConnectionState()");
		return null;
	}

	@Override
	public void addEventSystemListener(EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l, Map<String, Object> parameters)
			throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.addEventSystemListener()");
	}

	@Override
	public void addEventSystemListener(EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l) throws RemoteException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.addEventSystemListener()");
	}

	@Override
	public void removeEventSystemListener(EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l, Map<String, Object> parameters) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.removeEventSystemListener()");
	}

	@Override
	public void removeEventSystemListener(EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>> l) {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.removeEventSystemListener()");
	}

	@Override
	public EventSystemListener<DynamicValueEvent<T, SimpleProperty<T>>>[] getEventSystemListeners() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getEventSystemListeners()");
		return null;
	}

	@Override
	public Map<String, Object> getSupportedEventSystemParameters() {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getSupportedEventSystemParameters()");
		return null;
	}

	@Override
	public String getUnits() throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getUnits()");
		return null;
	}

	@Override
	public String getFormat() throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getFormat()");
		return null;
	}

	@Override
	public Ts getMinimum() throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getMinimum()");
		return null;
	}

	@Override
	public Ts getMaximum() throws DataExchangeException {
		LOG.error("not implemented: NumericPropertyImpleGenealStub.getMaximum()");
		return null;
	}

}
