package org.csstudio.sds.history.anticorruption;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.ISimpleDalBroker;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.sds.history.IHistoryDataService;
import org.csstudio.sds.history.anticorruption.adapter.listener.ChannelToPvListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.util.CommonException;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

public class DalBrokerAntiCorruptionLayer implements ISimpleDalBroker {
	
	private static Logger LOG = LoggerFactory.getLogger("DalAnti");

	private Map<String, ProcessVariable> _allProcessVariables;
	
	private List<ChannelToPvListener> _registeredChannelListener;

	private IHistoryDataService _historyService;

	public DalBrokerAntiCorruptionLayer(Map<String, ProcessVariable> allProcessVariables, IHistoryDataService historyDataService) {
		assert allProcessVariables != null : "allProcessVariables != null";
		assert historyDataService != null : "historyDataService != null";
		
		_registeredChannelListener = new ArrayList<ChannelToPvListener>();
		
		_allProcessVariables = allProcessVariables;
		_historyService = historyDataService;
	}

	@Override
	public int getPropertiesMapSize() {
		LOG.error("DalBrokerAntiCorruptionLayer.getPropertiesMapSize()");
		return _allProcessVariables.size();
	}

	@Override
	public Object getValue(ConnectionParameters cparam) throws InstantiationException, CommonException {
		LOG.error("DalBrokerAntiCorruptionLayer.getValue()");
		return null;
	}

	@Override
	public <T> T getValue(RemoteInfo rinfo, Class<T> type) throws InstantiationException, CommonException {
		LOG.error("DalBrokerAntiCorruptionLayer.getValue()");
		return null;
	}

	@Override
	public Object getValue(RemoteInfo rinfo) throws InstantiationException, CommonException {
		LOG.error("DalBrokerAntiCorruptionLayer.getValue()");
		return null;
	}

	@Override
	public Object getValue(String property) throws InstantiationException, CommonException {
		LOG.error("DalBrokerAntiCorruptionLayer.getValue()");
		return null;
	}

	@Override
	public <T> Request<T> getValueAsync(ConnectionParameters cparam, ResponseListener<T> callback) throws InstantiationException,
			CommonException {
		LOG.error("DalBrokerAntiCorruptionLayer.getValueAsync()");
		return null;
	}

	@Override
	public void setValue(RemoteInfo rinfo, Object value) throws InstantiationException, CommonException {
		LOG.error("DalBrokerAntiCorruptionLayer.setValue()");
	}

	@Override
	public <T> Request<T> setValueAsync(ConnectionParameters cparam, Object value, ResponseListener<T> callback) throws Exception {
		LOG.error("DalBrokerAntiCorruptionLayer.setValueAsync()");
		return null;
	}

	@Override
	public synchronized void registerListener(final ConnectionParameters cparam, final ChannelListener listener) {
		String remoteInfoName = cparam.getRemoteInfo().getRemoteName();
		LOG.debug("register listener for: " + remoteInfoName);

		//TODO CME: inspect dataflavor from cparam for more information (e.g. datatype) 
		
		//TODO CME: review
		if (remoteInfoName.endsWith(".SEVR")) {
			remoteInfoName = remoteInfoName.substring(0, remoteInfoName.length() - 5);
		}
		
		if (_allProcessVariables.containsKey(remoteInfoName)) {
			ProcessVariable pv = _allProcessVariables.get(remoteInfoName);
			ChannelToPvListener channelToPvListener = new ChannelToPvListener(listener, pv);
			_historyService.addMonitoredPv(channelToPvListener);
			_registeredChannelListener.add(channelToPvListener);
			
		} else { // fallback if PlantInformation does not have a pv for this channel address.
			LOG.debug("no processvariable for " + remoteInfoName + " in PlantInformationService found");
			
			ProcessVariable newPv = new ProcessVariable(new PlantUnitId(), "workaround pv");
			newPv.setControlSystemAddress(remoteInfoName);
			ChannelToPvListener channelToPvListener = new ChannelToPvListener(listener, newPv);
			_historyService.addMonitoredPv(channelToPvListener);
			_registeredChannelListener.add(channelToPvListener);
		}
	}

	@Override
	public void deregisterListener(ConnectionParameters cparam, ChannelListener listener) throws InstantiationException, CommonException {
		for (ChannelToPvListener pvListener : _registeredChannelListener) {
			if (pvListener.getChannelListener() == listener) {
				_historyService.removePvChangeListener(pvListener);
				break;
			}
		}
		LOG.debug("DalBrokerAntiCorruptionLayer.deregisterListener()");
	}

	@Override
	public void registerListener(ConnectionParameters cparam, DynamicValueListener listener) throws InstantiationException, CommonException {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.registerListener()");
	}

	@Override
	public void deregisterListener(ConnectionParameters cparam, DynamicValueListener listener) throws InstantiationException,
			CommonException {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.deregisterListener()");
	}

	@Override
	public void registerListener(ConnectionParameters cparam, PropertyChangeListener listener) throws InstantiationException,
			CommonException {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.registerListener()");
	}

	@Override
	public void deregisterListener(ConnectionParameters cparam, PropertyChangeListener listener) throws InstantiationException,
			CommonException {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.deregisterListener()");
	}

	@Override
	public void registerListener(ConnectionParameters cparam, DynamicValueListener listener, Map<String, Object> parameters)
			throws InstantiationException, CommonException {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.registerListener()");
	}

	@Override
	public void deregisterListener(ConnectionParameters cparam, DynamicValueListener listener, Map<String, Object> parameters)
			throws InstantiationException, CommonException {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.deregisterListener()");
	}

	@Override
	public String getDefaultPlugType() {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.getDefaultPlugType()");
		return null;
	}

	@Override
	public void setDefaultPlugType(String plugType) {
		// TODO Auto-generated method stub
		LOG.error("DalBrokerAntiCorruptionLayer.setDefaultPlugType()");
	}

	@Override
	public void releaseAll() {
		_historyService.removePVChangeListeners(_registeredChannelListener);
		_historyService = null;
		_allProcessVariables = null;
		_registeredChannelListener = null;
	}
}
