package org.csstudio.archive.reader.aapi;

import java.util.List;


import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import de.desy.aapi.AAPI;
import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;

public class MinMaxAapiValueIterator extends AapiValueIterator {

	public MinMaxAapiValueIterator(AapiClient aapiClient, int key, String name,
			Timestamp start, Timestamp end, int count) {
		super(aapiClient, key, name, start, end);
		setCount(count);
		setConversionParam(AAPI.DEADBAND_PARAM);
		setConversionMethod(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD);
	}

	@Override
	void dataConversion(AnswerData answerData, List<VType> result) {
		if(answerData==null)return;
	   Display display=ValueFactory.newDisplay(new Double(answerData.getDisplayLow()) , new Double(answerData.getLowAlarm()),  new Double(answerData.getLowWarning()), "", NumberFormats.toStringFormat(),  new Double(answerData.getHighWarning()), new Double(answerData.getHighAlarm()), new Double(answerData.getDisplayHigh()), new Double(answerData.getDisplayLow()), new Double(answerData.getDisplayHigh()));
		
		for (int i = 0; i+2 < answerData.getData().length; i = i+3) {
			
			Timestamp time = Timestamp.of(
					answerData.getTime()[i],
					answerData.getUTime()[i]);
			double[] value = new double[1];
			value[0] = answerData.getData()[i+2];
			Double min = answerData.getData()[i];
			Double max = answerData.getData()[i+1];
			result.add( new ArchiveVStatistics(time, ValueFactory.newAlarm(value[0], display).getAlarmSeverity() , answerData.getStatus().toString() , display,
             		value[0], min, max, 0.0, 1));
		//	result.add(ValueFactory.newVStatistics(value[0], value[0], min, max, answerData.getData().length, ValueFactory.alarmNone(), ValueFactory.newTime(time), display));
				}	
	}

	
	
}
