package org.csstudio.sds.history.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.history.IHistoryDataContentEnricher;
import org.csstudio.sds.history.IHistoryDataService;
import org.csstudio.sds.history.domain.IPvUpdater;
import org.csstudio.sds.history.domain.events.UpdateTimeEvent;
import org.csstudio.sds.history.domain.listener.IPvChangeListener;
import org.csstudio.sds.history.domain.listener.ITimeChangeListener;
import org.csstudio.sds.history.domain.service.IPvValueHistoryDataService;
import org.csstudio.sds.history.enricher.HistoryDataContentEnricher;
import org.csstudio.sds.history.enricher.HistoryTimeContentEnricher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

/**
 * This service implementation is the central point for registering {@link IPvChangeListener} that are in history mode.
 * They will get updates from archived data. It will create IPvUpdater for each separate {@link ProcessVariable} and
 * choose the right content enricher for each @link {@link ProcessVariable}.
 * 
 * To work with archived data this service needs an archive service to handle it to {@link PvUpdater} instances.
 * 
 * @author Christian Mein
 * 
 */
// TODO CME: This class should also implement a "DalBroker" equivalent thing.

// TODO CME: separation of concerns? This class handles registration of IPvChangeListener and updates all PvUpdater.
public class HistoryDataService implements IHistoryDataService, ITimeChangeListener {

	private static final Logger LOG = LoggerFactory.getLogger(HistoryDataService.class);

	private Map<String, IPvUpdater> _allPvUpdater = new HashMap<String, IPvUpdater>();

	private IPvValueHistoryDataService _pvValueHistoryDataService;

	private UpdateTimeEvent _lastUpdateTimeEvent;

	@Override
	public void handleTimeIndexChanged(UpdateTimeEvent updateTimeEvent) {
		if (updateTimeEvent.doUpdateData()) {
			for (IPvUpdater pvUpdater : _allPvUpdater.values()) {
				pvUpdater.handleTimeIndexChanged(updateTimeEvent);
			}

			_lastUpdateTimeEvent = updateTimeEvent;
		}
	}

	@Override
	public void addMonitoredPv(IPvChangeListener pvChangeListener) {
		String csAddressString = pvChangeListener.getProcessVariable().getControlSystemAddress();

		if (_allPvUpdater.containsKey(csAddressString)) {
			_allPvUpdater.get(csAddressString).addPvChangeListener(pvChangeListener);
		} else {
			IHistoryDataContentEnricher contentEnricher = getContentEnricherForPv(pvChangeListener.getProcessVariable());
			PvUpdater pvUpdater = new PvUpdater(pvChangeListener.getProcessVariable(), contentEnricher);
			pvUpdater.addPvChangeListener(pvChangeListener);
			_allPvUpdater.put(csAddressString, pvUpdater);

			if (_lastUpdateTimeEvent != null) {
				pvUpdater.handleTimeIndexChanged(_lastUpdateTimeEvent);
			}
		}
	}

	/**
	 * Returns the appropriate {@link IHistoryDataContentEnricher} for the given {@link ProcessVariable}. For example
	 * there are listeners that are only interested in the current time. They should be updated with the current
	 * represented historical time.
	 * 
	 * @param pv
	 *            {@link ProcessVariable} to choose the right content enricher for.
	 * @return The appropriate {@link IHistoryDataContentEnricher} for the given {@link ProcessVariable}.
	 */
	private IHistoryDataContentEnricher getContentEnricherForPv(ProcessVariable pv) {
		if (pv.getControlSystemAddress().endsWith("time_si")) {
			return new HistoryTimeContentEnricher(pv);
		} else {
			return new HistoryDataContentEnricher(pv, _pvValueHistoryDataService);
		}
	}

	@Override
	public void removePvChangeListener(IPvChangeListener pvChangeListner) {
		assert pvChangeListner != null : "pvChangeListener != null";

		String csAddress = pvChangeListner.getProcessVariable().getControlSystemAddress();

		if (_allPvUpdater.containsKey(csAddress)) {
			_allPvUpdater.get(csAddress).removePvChangeListener(pvChangeListner);
		}
	}

	@Override
	public void removePVChangeListeners(List<? extends IPvChangeListener> pvChangeListners) {
		for (IPvChangeListener pvChangeListener : pvChangeListners) {
			String csAddress = pvChangeListener.getProcessVariable().getControlSystemAddress();
			if (_allPvUpdater.containsKey(csAddress)) {
				_allPvUpdater.get(csAddress).removePvChangeListener(pvChangeListener);
			}
		}
	}

	public void bindPvValueHistoryService(IPvValueHistoryDataService pvValueHistoryDataService) {
		LOG.debug("bind IPvValueHistoryDataService");
		_pvValueHistoryDataService = pvValueHistoryDataService;
		// Not dynamic. PvUpdater are not notified.
	}

	public void unbindPvValueHistoryService(IPvValueHistoryDataService pvValueHistoryDataService) {
		_pvValueHistoryDataService = null;
		// Not dynamic. PvUpdater are not notified.
	}
}
