package org.csstudio.dal.simple.compatibility;

import org.csstudio.dal.AccessType;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;

public class MetaDataAdapter implements MetaData {

	private Characteristics _characteristics;

	public MetaDataAdapter(Characteristics characteristics) {
				
		assert characteristics != null;
		
		assert characteristics.isAvailable(Characteristic.GRAPH_MIN);
		assert characteristics.isAvailable(Characteristic.GRAPH_MAX);
		assert characteristics.isAvailable(Characteristic.WARNING_MIN);
		assert characteristics.isAvailable(Characteristic.WARNING_MAX);
		assert characteristics.isAvailable(Characteristic.ALARM_MIN);
		assert characteristics.isAvailable(Characteristic.ALARM_MAX);
		
		_characteristics = characteristics;
	}
	
	@Override
	public double getDisplayLow() {
		return _characteristics.get(Characteristic.GRAPH_MIN);
	}

	@Override
	public double getDisplayHigh() {
		return _characteristics.get(Characteristic.GRAPH_MAX);
	}

	@Override
	public double getWarnLow() {
		return _characteristics.get(Characteristic.WARNING_MIN);
	}

	@Override
	public double getWarnHigh() {
		return _characteristics.get(Characteristic.WARNING_MAX);
	}

	@Override
	public double getAlarmLow() {
		return _characteristics.get(Characteristic.ALARM_MIN);
	}

	@Override
	public double getAlarmHigh() {
		return _characteristics.get(Characteristic.ALARM_MAX);
	}

	@Override
	@Deprecated
	public int getPrecision() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getUnits() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String[] getStates() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getState(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Object[] getStateValues() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Object getStateValue(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getFormat() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public AccessType getAccessType() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getHostname() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getDataType() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getDescription() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int getSequenceLength() {
		throw new UnsupportedOperationException();
	}

}
