package org.csstudio.sds.history.pvvaluehistorydata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.sds.history.domain.HistoryArchiveSample;
import org.csstudio.sds.history.domain.service.IPvValueHistoryDataService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Interval;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVAlarmStatus;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVSeverityState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.DoubleValue;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.EnumValue;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IntegerValue;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.StringValue;

public class PvValueHistoryDataService implements IPvValueHistoryDataService {

	private static Logger LOG = LoggerFactory.getLogger("History");

	private IArchiveReaderFacade _archiveReaderFacade;

	@Nullable
	@Override
	public HistoryArchiveSample getLatestValueBefore(String csAddress, DateTime time) {
		assert csAddress != null : "csAddress != null";
		assert time != null : "time != null";

		TimeInstant timeInstant = TimeInstantBuilder.fromMillis(time.getMillis());
		IArchiveSample<Serializable, ISystemVariable<Serializable>> lastSample = null;

		try {
			lastSample = _archiveReaderFacade.readLastSampleBefore(csAddress, timeInstant);
			// TODO CME: Discuss: This gets the latest sample from the archive.
			// Even though it can be very old and thus
			// not representing the proper value for the given time. For example
			// when the archive is missing samples for
			// a time period.
		} catch (ArchiveServiceException e) {
			LOG.debug(e.getMessage());
		}

		if (lastSample != null) {
			return createHistorySample(lastSample);
		} else {
			LOG.warn("no sample for channel " + csAddress + " and time " + time.toString(ISODateTimeFormat.dateHourMinuteSecond()));
			return null;
		}
	}

	@Override
	public NavigableMap<DateTime, HistoryArchiveSample> getSamples(String csAddress, Interval interval) {
		TimeInstant starTimeInstant = TimeInstantBuilder.fromMillis(interval.getStartMillis());
		TimeInstant endTimeInstant = TimeInstantBuilder.fromMillis(interval.getEndMillis());

		Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> readSamples = new ArrayList<>();

		NavigableMap<DateTime, HistoryArchiveSample> samples = new TreeMap<>(DateTimeComparator.getInstance());

		try {
			readSamples = _archiveReaderFacade.readSamples(csAddress, starTimeInstant, endTimeInstant);
			// CME: the mysql service uses minutes values when duration > 1 day
			// and uses hour values when duration > 45
			// days
		} catch (ArchiveServiceException e) {
			LOG.debug(e.getMessage());
		}

		for (IArchiveSample<Serializable, ISystemVariable<Serializable>> iArchiveSample : readSamples) {
			TimeInstant timestamp = iArchiveSample.getSystemVariable().getTimestamp();
			DateTime timeStamp = new DateTime(timestamp.getMillis());

			samples.put(timeStamp, createHistorySample(iArchiveSample));
		}

		return samples;
	}

	private HistoryArchiveSample createHistorySample(IArchiveSample<Serializable, ISystemVariable<Serializable>> archiveSample) {
		TimeInstant timestamp = archiveSample.getSystemVariable().getTimestamp();
		DateTime timeStamp = new DateTime(timestamp.getMillis());

		IPlantUnitValue<?> plantValue = getValueFromArchiveSample(archiveSample);
		PVSeverityState severityState = getSeverityStateFromArchiveSample(archiveSample);
		PVAlarmStatus alarmStatus = getAlarmStatusFromArchiveSample(archiveSample);

		HistoryArchiveSample historySample = HistoryArchiveSample.createHistoryArchiveSample(timeStamp, plantValue, severityState,
				alarmStatus);

		return historySample;
	}

	private PVSeverityState getSeverityStateFromArchiveSample(IArchiveSample<Serializable, ISystemVariable<Serializable>> archiveSample) {
		if (archiveSample instanceof ArchiveSample<?, ?>) {
			ArchiveSample<?, ?> aSample = (ArchiveSample<?, ?>) archiveSample;

			if (aSample.getAlarm() instanceof EpicsAlarm) {
				EpicsAlarm alarm = (EpicsAlarm) aSample.getAlarm();
				return PVSeverityState.parseEpicsAlarmSeverity(alarm.getSeverity().name());
			}
		}
		return PVSeverityState.UNKNOWN;
	}

	private PVAlarmStatus getAlarmStatusFromArchiveSample(IArchiveSample<Serializable, ISystemVariable<Serializable>> archiveSample) {
		if (archiveSample instanceof ArchiveSample<?, ?>) {
			ArchiveSample<?, ?> aSample = (ArchiveSample<?, ?>) archiveSample;

			if (aSample.getAlarm() instanceof EpicsAlarm) {
				EpicsAlarm alarm = (EpicsAlarm) aSample.getAlarm();
				return PVAlarmStatus.valueOf(alarm.getStatus().name());
			}
		}
		return PVAlarmStatus.UNKNOWN;
	}

	public void bindArchiveReaderFacade(IArchiveReaderFacade archiveReader) {
		_archiveReaderFacade = archiveReader;
	}

	public void unbindArchiveReaderFacade(IArchiveReaderFacade archiveReader) {
		_archiveReaderFacade = null;
	}

	private IPlantUnitValue<?> getValueFromArchiveSample(IArchiveSample<Serializable, ISystemVariable<Serializable>> archiveSample) {
		Object value = archiveSample.getValue();
		IPlantUnitValue<?> plantValue = null;

		if (value instanceof Double) {
			plantValue = new DoubleValue((Double) archiveSample.getValue());
		} else if (value instanceof Integer) {
			plantValue = new IntegerValue((Integer) archiveSample.getValue());
		} else if (value instanceof String) {
			plantValue = new StringValue((String) archiveSample.getValue());
		} else if (value instanceof EpicsEnum) {
			EpicsEnum epicsEnumState = (EpicsEnum) value;
			plantValue = new EnumValue(epicsEnumState.getStateIndex(), epicsEnumState.getState());
		} else {
			LOG.error("no type defined for retrieved value from archive sample. Archive type: "
					+ archiveSample.getValue().getClass().getName());
			System.out.println("archive sample class: " + archiveSample.getValue().getClass().getName()); // TODO
																											// CME:
																											// remove
		}
		return plantValue;
	}
}
