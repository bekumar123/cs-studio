package org.csstudio.sds.history.impl;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.history.IHistoryDataContentEnricher;
import org.csstudio.sds.history.domain.IPvUpdater;
import org.csstudio.sds.history.domain.events.UpdateTimeEvent;
import org.csstudio.sds.history.domain.listener.IPvChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

/**
 * PvUpdater handles a List of {@link IPvChangeListener} that are all interested in value updates for the same
 * {@link ProcessVariable}. When a time change event occurs it updates all registered {@link IPvChangeListener}.
 * 
 * @author Christian
 * 
 */
public class PvUpdater implements IPvUpdater {

	private static final Logger LOG = LoggerFactory.getLogger(PvUpdater.class);
	private List<IPvChangeListener> _pvChangeListener = new ArrayList<IPvChangeListener>();
	private ProcessVariable _processVariable;
	private IHistoryDataContentEnricher _pvContentEnricher;
	private ProcessVariable _lastProcessVariable;

	/**
	 * Construct pv updater with a concrete {@link ProcessVariable} that the returning instance is responsible for. The
	 * given {@link IHistoryDataContentEnricher} updates the {@link ProcessVariable} with the appropriate data.
	 * 
	 * @param pv the {@link ProcessVariable}
	 * @param contentEnricher the appropriate {@link IHistoryDataContentEnricher} for the {@link ProcessVariable}
	 */
	public PvUpdater(ProcessVariable pv, IHistoryDataContentEnricher contentEnricher) {
		assert pv != null : "pv !=null";
		assert contentEnricher != null : "contentEnricher != null";

		_processVariable = pv;
		_pvContentEnricher = contentEnricher;
	}

	@Override
	public void handleTimeIndexChanged(UpdateTimeEvent updateTimeEvent) {
		assert updateTimeEvent != null : "time != null";

		if (updateTimeEvent.doUpdateData()) {

			_lastProcessVariable = _pvContentEnricher.latestPvBefore(updateTimeEvent.getTimeStamp(), updateTimeEvent.getInterval());

			for (IPvChangeListener pvChangeListener : _pvChangeListener) {
				pvChangeListener.pvChanged(_lastProcessVariable);
			}
		}
	}

	@Override
	public void addPvChangeListener(IPvChangeListener pvChangeListener) {
		assert pvChangeListener != null : "pvChangeListener != null";
		// TODO CME: should the following also be checked by an if statement?
		assert pvChangeListener.getProcessVariable().getControlSystemAddress().equals(_processVariable.getControlSystemAddress()) : "listner should have the same control system address";

		_pvChangeListener.add(pvChangeListener);
		if (_lastProcessVariable != null) {
			pvChangeListener.pvChanged(_lastProcessVariable);
		}
		LOG.debug("IPvChangeListener added: " + _pvChangeListener);
	}

	@Override
	public void removePvChangeListener(IPvChangeListener pvChangeListener) {
		assert pvChangeListener != null : "pvChangeListener != null";
		_pvChangeListener.remove(pvChangeListener);
		// TODO CME: empty content enricher cache when there are no more listeners
	}

	@Override
	public ProcessVariable getProcessVariable() {
		return _processVariable;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(_processVariable.getControlSystemAddress());
		result.append(" PVChangeListeners: ");
		result.append(_pvChangeListener.size());

		return result.toString();
	}
}
