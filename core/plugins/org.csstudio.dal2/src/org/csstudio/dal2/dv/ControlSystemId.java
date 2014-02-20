package org.csstudio.dal2.dv;

public class ControlSystemId extends AbstractStringBasedDV {

	public static ControlSystemId EPICS = getValue("EPICS");
	public static ControlSystemId SIMULATOR = getValue("SIMULATOR");
	
	private ControlSystemId(String value) {
		super(value);
	}
	
	public static ControlSystemId getValue(String value) {
		return new ControlSystemId(value); 
	}

}
