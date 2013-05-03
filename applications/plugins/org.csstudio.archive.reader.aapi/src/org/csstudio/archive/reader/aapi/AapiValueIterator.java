package org.csstudio.archive.reader.aapi;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.reader.ValueIterator;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;
import de.desy.aapi.RequestData;

public abstract class AapiValueIterator implements ValueIterator {

	private final AapiClient _aapiClient;
	private RequestData _requestData = new RequestData();
	
	private List<VType> _result = new ArrayList<VType>();

	public AapiValueIterator(AapiClient aapiClient, int key, String name,
			Timestamp start, Timestamp end) {
		_aapiClient = aapiClient;
		_requestData.setFromTime((int) start.getSec());
		_requestData.setToTime((int) end.getSec());
		_requestData.setPvList(new String[]{name});
	}
	
	public void setCount(int count) {
		_requestData.setNumberOfSamples(count);
	}
	
	public void setConversionParam(double deadbandParam) {
		_requestData.setConversParam(deadbandParam);
	}
	
	public void setConversionMethod(AapiReductionMethod minMaxAverageMethod) {
		_requestData.setConversionMethod(minMaxAverageMethod);
	}
	
	@Override
	public boolean hasNext() {
//		System.out.println(">>>>> AapiValueIterator.hasNext");
		if (_result.size() > 0) {
			return true;
		}
//		System.out.println(">>>>> AapiValueIterator.hasNext no next value");
		return false;
	}

	@Override
	public VType next() throws Exception {
//		System.out.println(">>>>> AapiValueIterator.next");
		if (_result.size() > 0) {
			VType val = _result.remove(0);
//			IMinMaxDoubleValue mmval = (IMinMaxDoubleValue) val;
//			System.out.println(">>>>> " + mmval.getTime() + " " + mmval.getValue());
			//TODO (jhatje): implement vType
			return val;
		}
//		System.out.println(">>>>> AapiValueIterator.next return null");
		return null;
	}
	
	public void getData() {
		dataConversion(_aapiClient.getData(_requestData), _result);
	}

	@Override
	public void close() {
		_result.clear();
		_result = null;
	}

	abstract void dataConversion(AnswerData answerData, List<VType> result);

}
