package org.csstudio.sds.history.enricher;

import java.util.Map.Entry;
import java.util.NavigableMap;

import org.csstudio.sds.history.IHistoryDataContentEnricher;
import org.csstudio.sds.history.domain.service.IPvValueHistoryDataService;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVConnectionState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVSeverityState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;

/**
 * This class handles loading and caching of values for one concrete {@link ProcessVariable}.
 * 
 * @author Christian Mein
 * 
 */
public class HistoryDataContentEnricher implements IHistoryDataContentEnricher {

	private IPvValueHistoryDataService _pvValueService;

	private ProcessVariable _processVariable;

	private Interval _interval; // TODO CME: should time interval be calculated from first and last element of the
								// NavigableMap?

	private NavigableMap<DateTime, IPlantUnitValue<?>> _values;

	private boolean _availableInCache;

	/**
	 * Construct a new content enricher that handles data retrieval and caching for the given {@link ProcessVariable}.
	 * Uses the {@link IPvValueHistoryDataService} to get archived data.
	 * 
	 * @param pv
	 *            {@link ProcessVariable} for which this content enricher is responsible for.
	 * @param pvValueHistoryDataService
	 *            service where to get the archived values from.
	 */
	public HistoryDataContentEnricher(ProcessVariable pv, IPvValueHistoryDataService pvValueHistoryDataService) {
		assert pv != null : "pv != null";
		assert pvValueHistoryDataService != null : "pvValueHistoryDateService != null";

		_processVariable = pv;
		_pvValueService = pvValueHistoryDataService;
	}

	/**
	 * @see IHistoryDataContentEnricher
	 */
	@Override
	public ProcessVariable latestPvBefore(DateTime timeStamp, Interval interval) {
		assert timeStamp != null : "timeStamp != null";
		assert interval != null : "interval != null";

		ProcessVariable newPvCopy = _processVariable.copyDeep();
		String csAddress = _processVariable.getControlSystemAddress();
		IPlantUnitValue<?> value = null;

		if (isInCachedTimeInterval(timeStamp)) {
			Entry<DateTime, IPlantUnitValue<?>> entry = _values.floorEntry(timeStamp);

			if (entry != null) {
				value = entry.getValue();
			} else if (_availableInCache == true) {
//				System.out.println(" +++++++++++++++++  dont do this repeatedly"); // TODO CME: remove
				IPlantUnitValue<?> latestValueBefore = _pvValueService.getLatestValueBefore(csAddress, timeStamp);
				if (latestValueBefore != null) {
					_values.put(timeStamp, latestValueBefore);
					value = latestValueBefore;
					_availableInCache = true;
				} else {
					_availableInCache = false;
				}
			}
		} else {
			// IF interval == null
			// value = _pvValueService.getLatestValueBefore(csAddress, timeStamp);

			_values = _pvValueService.getSamples(csAddress, interval);
			_interval = interval;

			Entry<DateTime, IPlantUnitValue<?>> entry = _values.floorEntry(timeStamp);
			if (entry != null) {
				value = entry.getValue();
			} else {
				IPlantUnitValue<?> latestValueBefore = _pvValueService.getLatestValueBefore(csAddress, timeStamp);
				if (latestValueBefore != null) {
					_values.put(timeStamp, latestValueBefore);
					value = latestValueBefore;
					_availableInCache = true;
				}
			}
		}

		if (value != null) {
			newPvCopy.setValue(value);
			newPvCopy.setTimeStamp(timeStamp.toDate());
			newPvCopy.setConnectionState(PVConnectionState.CONNECTED);
			newPvCopy.setSeverityState(PVSeverityState.OK); //TODO CME: set right severity state from archive sample
		} else {
			newPvCopy.setConnectionState(PVConnectionState.DISCONNECTED);
			newPvCopy.setValue(null);
		}

		return newPvCopy;
	}

	/**
	 * Returns true if the given timestamp is in the range of the cached time interval and there are cached values.
	 * False otherwise. Please note that there may be a proper value in the archive even though this method returned
	 * false (before the stored interval).
	 * 
	 * @param timeStamp
	 * @return true when timeStamp is in range of cached interval. False ohterwise.
	 */
	private boolean isInCachedTimeInterval(DateTime timeStamp) {
		if (_values != null && _interval.contains(timeStamp)) {
			return true;
		} else
			return false;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(" Value enricher ");
		result.append(_processVariable.getControlSystemAddress());
		result.append(" Interval: ").append(_interval);
		result.append(" Values: ").append(_values.size());

		return result.toString();
	}
}
