package org.csstudio.sds.history.pvvaluehistorydata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.sds.history.domain.service.IPvValueHistoryDataService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Interval;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.DoubleValue;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;

public class PvValueHistoryDataService implements IPvValueHistoryDataService {

	private static Logger LOG = LoggerFactory.getLogger("HistoryLogging");

	private IArchiveReaderFacade _archiveReaderFacade;

	@Nullable
	@Override
	public IPlantUnitValue<?> getLatestValueBefore(String csAddress, DateTime time) {
		assert csAddress != null : "csAddress != null";
		assert time != null : "time != null";
		
		TimeInstant timeInstant = TimeInstantBuilder.fromMillis(time.getMillis());
		IArchiveSample<Serializable, ISystemVariable<Serializable>> lastSample = null;

		try {
			lastSample = _archiveReaderFacade.readLastSampleBefore(csAddress, timeInstant);
			
			// TODO CME: Discuss. This gets the latest sample from the archive. Even though it can be very old and thus not representing the proper
			// value for the given time. For example when the archive is missing samples for a time period.
		} catch (ArchiveServiceException e) {
			LOG.error(e.getMessage());
		}

		Double newValue = null;
		
		if (lastSample != null && lastSample.getValue() instanceof Double) { // TODO CME: type
			newValue = (Double) lastSample.getValue();
		} else {
			if (lastSample == null) {
				LOG.warn("no sample for channel " + csAddress + " and time " + time.toString(ISODateTimeFormat.dateHourMinuteSecond()));
			}
			if (lastSample != null && !(lastSample.getValue() instanceof Double)) {
				LOG.error("sample for channel " + csAddress + " is not of type double");
			}
			return null;
		}
		return new DoubleValue(newValue);
	}
	
	@Override
	public NavigableMap<DateTime, IPlantUnitValue<?>> getSamples(String csAddress, Interval interval) {
		TimeInstant starTimeInstant = TimeInstantBuilder.fromMillis(interval.getStartMillis());
		TimeInstant endTimeInstant = TimeInstantBuilder.fromMillis(interval.getEndMillis());
		
		Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> readSamples = null;
		
		NavigableMap<DateTime, IPlantUnitValue<?>> samples = new TreeMap<DateTime, IPlantUnitValue<?>>(DateTimeComparator.getInstance());
		
		try {
			readSamples = _archiveReaderFacade.readSamples(csAddress, starTimeInstant, endTimeInstant);
			//CME: the mysql service uses minutes values when duration > 1 day and uses hour values when duration > 45 days
		} catch (ArchiveServiceException e) {
			LOG.error(e.getMessage());
		}
		
		if (readSamples == null) {
			readSamples = new ArrayList<IArchiveSample<Serializable, ISystemVariable<Serializable>>>();
		}
		
		for (IArchiveSample<Serializable, ISystemVariable<Serializable>> iArchiveSample : readSamples) {
			TimeInstant timestamp = iArchiveSample.getSystemVariable().getTimestamp();
			DateTime timeStamp = new DateTime(timestamp.getMillis());
			
			DoubleValue doubleValue = new DoubleValue((Double) iArchiveSample.getValue());
			
			samples.put(timeStamp, doubleValue);
		}
		
		return samples;
	}
	

	public void bindArchiveReaderFacade(IArchiveReaderFacade archiveReader) {
		_archiveReaderFacade = archiveReader;
	}

	public void unbindArchiveReaderFacade(IArchiveReaderFacade archiveReader) {
		_archiveReaderFacade = null;
	}
}
