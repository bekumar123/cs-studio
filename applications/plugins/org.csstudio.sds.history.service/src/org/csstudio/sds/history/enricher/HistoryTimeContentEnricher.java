package org.csstudio.sds.history.enricher;

import org.csstudio.sds.history.IHistoryDataContentEnricher;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVConnectionState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.StringValue;

/**
 * This content enricher handles process variables that are only used to display the current time. 
 * 
 * @author Christian Mein
 *
 */
public class HistoryTimeContentEnricher implements IHistoryDataContentEnricher {

	private ProcessVariable _processVariable;

	public HistoryTimeContentEnricher(ProcessVariable pv) {
		assert pv != null : "pv != null";

		_processVariable = pv;
	}
	
	@Override
	public ProcessVariable latestPvBefore(DateTime timeStamp, Interval interval) {
		assert timeStamp != null : "timestamp != null";
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM d, Y HH:mm:ss");
		IPlantUnitValue<String> stringValue = new StringValue(formatter.print(timeStamp));

		ProcessVariable newPvCopy = _processVariable.copyDeep();
		newPvCopy.setValue(stringValue);
		newPvCopy.setTimeStamp(timeStamp.toDate());
		newPvCopy.setConnectionState(PVConnectionState.CONNECTED);
		
		return newPvCopy;
	}

}
