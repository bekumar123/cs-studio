package org.csstudio.sds.history.anticorruption.adapter.listener;

import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.sds.history.anticorruption.adapter.ChannelType;
import org.csstudio.sds.history.anticorruption.adapter.anydata.AnyDataChannelFromPV;
import org.csstudio.sds.history.domain.listener.IPvChangeListener;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

/**
 * This is a listener adapter. It routes from {@link IPvChangeListener} to {@link ChannelListener}.
 * 
 * @author Christian Mein
 * 
 */
public class ChannelToPvListener implements IPvChangeListener {

	private ChannelListener _channelListener;
	
	private ProcessVariable _processVariable;
	
	private ChannelType _channelType;

	/**
	 * Constructs the adapter. The instance will route process variable state changes to the given channel listener.
	 * 
	 * @param channelListener
	 *            the channel listener
	 * @param processVariable
	 *            the process variable
	 */
	public ChannelToPvListener(ChannelListener channelListener, ProcessVariable processVariable, ChannelType channelType) {
		assert channelListener != null : "channelListener != null";
		assert processVariable != null : "processVariable != null";
		assert channelType != null : "channelType != null";

		_channelListener = channelListener;
		_processVariable = processVariable;
		_channelType = channelType;
	}

	@Override
	public void pvChanged(ProcessVariable processVariable) {
		assert processVariable != null : "processVariable != null";
		assert processVariable.getControlSystemAddress().equals(_processVariable.getControlSystemAddress()) : "new pv should have same control system addess as before";

		AnyDataChannelFromPV<?, ?> anyDataChannel = new AnyDataChannelFromPV<>(processVariable, _channelType);
		_channelListener.channelDataUpdate(anyDataChannel);
		_channelListener.channelStateUpdate(anyDataChannel);
		_processVariable = processVariable;
	}

	@Override
	public ProcessVariable getProcessVariable() {
		return _processVariable; //TODO CME: defensive copy!?
	}
	
	public ChannelListener getChannelListener() {
		return _channelListener;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Address: ").append(_processVariable.getControlSystemAddress());
		result.append("ChannelListener class: ").append(_channelListener.getClass().getSimpleName());

		return result.toString();
	}
}
