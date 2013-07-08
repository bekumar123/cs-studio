package org.csstudio.archive.reader.aapi;

import java.util.List;

import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import de.desy.aapi.AAPI;
import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;

public class RawAapiValueIterator extends AapiValueIterator {


	public RawAapiValueIterator(AapiClient aapiClient, int key, String name,
			Timestamp start, Timestamp end) {
		super(aapiClient, key, name, start, end);
		setConversionParam(AAPI.DEADBAND_PARAM);
		setConversionMethod(AapiReductionMethod.TAIL_RAW_METHOD);
	}

	@Override
	void dataConversion(AnswerData answerData, List<VType> result) {
		Display display=ValueFactory.newDisplay(new Double(answerData.getDisplayLow()) , new Double(answerData.getLowAlarm()),  new Double(answerData.getLowWarning()), "", NumberFormats.toStringFormat(),  new Double(answerData.getHighWarning()), new Double(answerData.getHighAlarm()), new Double(answerData.getDisplayHigh()), new Double(answerData.getDisplayLow()), new Double(answerData.getDisplayHigh()));
		for (int i = 0; i < answerData.getData().length; i++) {
			
			Timestamp time = Timestamp.of(
					answerData.getTime()[i],
					answerData.getUTime()[i]);
			double[] value = new double[1];
			value[0] = answerData.getData()[i];
		   result.add(ValueFactory.newVDoubleArray(value, ValueFactory.newAlarm(value[0], display), ValueFactory.newTime(time), display));
		}	
	}

	

}
