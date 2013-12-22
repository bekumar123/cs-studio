package org.csstudio.sds.history.enricher;

import java.util.Map.Entry;
import java.util.NavigableMap;

import org.csstudio.sds.history.IHistoryDataContentEnricher;
import org.csstudio.sds.history.domain.HistoryArchiveSample;
import org.csstudio.sds.history.domain.service.IPvValueHistoryDataService;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVConnectionState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

/**
 * This class handles loading and caching of values for one concrete {@link ProcessVariable}.
 * 
 * @author Christian Mein
 * 
 */
public class HistoryDataContentEnricher implements IHistoryDataContentEnricher {

	private IPvValueHistoryDataService _pvValueService;

	private ProcessVariable _processVariable;

	private Interval _cachedInterval;

	private NavigableMap<DateTime, HistoryArchiveSample> _values;

	/**
	 * This variable indicates that this service has once tried to get the oldest value for the cached interval.
	 */
	private boolean _triedOldestValueRetrieval = false;

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
	 * {@inheritDoc}
	 */
	@Override
	public ProcessVariable latestPvBefore(DateTime timeStamp, Interval interval) {
		assert timeStamp != null : "timeStamp != null";
		assert interval != null : "interval != null";

		ProcessVariable newPvCopy = _processVariable.copyDeep();
		String csAddress = _processVariable.getControlSystemAddress();
		HistoryArchiveSample pvHistorySample = null;

		if (isInCachedTimeInterval(timeStamp)) {
			Entry<DateTime, HistoryArchiveSample> entry = _values.floorEntry(timeStamp);

			if (entry != null) {
				pvHistorySample = entry.getValue();
			} else if (!_triedOldestValueRetrieval) {
				HistoryArchiveSample latestValueBefore = _pvValueService.getLatestValueBefore(csAddress, timeStamp);
				_triedOldestValueRetrieval=true;
				if (latestValueBefore != null) {
					_values.put(latestValueBefore.getTimeStamp(), latestValueBefore);
					pvHistorySample = latestValueBefore;
				}
			}
		} else {
			_values = _pvValueService.getSamples(csAddress, interval);
			_cachedInterval = interval;

			latestPvBefore(timeStamp, interval);
		}

		if (pvHistorySample != null) {
			newPvCopy.setValue(pvHistorySample.getValue());
			newPvCopy.setSeverityState(pvHistorySample.getSeverityState());
			newPvCopy.setAlarmStatus(pvHistorySample.getPvAlarmState());
			newPvCopy.setTimeStamp(timeStamp.toDate());
			newPvCopy.setConnectionState(PVConnectionState.CONNECTED);
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
		if (_values != null && _cachedInterval.contains(timeStamp)) {
			return true;
		} else
			return false;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(" Value enricher ");
		result.append(_processVariable.getControlSystemAddress());
		result.append(" Interval: ").append(_cachedInterval);
		result.append(" Values: ").append(_values.size());

		return result.toString();
	}
}
