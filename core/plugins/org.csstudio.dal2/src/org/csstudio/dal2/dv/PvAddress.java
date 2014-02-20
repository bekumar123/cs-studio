package org.csstudio.dal2.dv;

import java.util.regex.Pattern;

/**
 * Address identifying a process variable
 */
public class PvAddress {

	public static final ControlSystemId DEFAULT_CONTROL_SYSTEM = ControlSystemId.EPICS;

	private final String _address;

	private final ControlSystemId _controlSystem;

	private PvAddress(String address, ControlSystemId controlSystemId) {
		_address = address;
		_controlSystem = controlSystemId;
	}

	public final String getAddress() {
		return _address;
	}

	public ControlSystemId getControlSystem() {
		return _controlSystem;
	}

	/**
	 * Creates a pv address for the default control system (epics)
	 */
	public static PvAddress getValue(String address) {

		int separatorIndex = address.indexOf("://");
		if (separatorIndex == -1) {
			return new PvAddress(address, DEFAULT_CONTROL_SYSTEM);
		} else {
			String prefix = address.substring(0, separatorIndex);
			ControlSystemId controlSystemId;
			if (prefix.equalsIgnoreCase("epics")) {
				controlSystemId = ControlSystemId.EPICS;
			} else if (prefix.equalsIgnoreCase("local")) {
				controlSystemId = ControlSystemId.SIMULATOR;
			} else {
				throw new IllegalArgumentException("Unexpected addres prefix: "
						+ prefix);
			}
			String path = address.substring(separatorIndex + 3);
			return new PvAddress(path, controlSystemId);
		}

	}

	public static PvAddress getValue(String address,
			ControlSystemId controlSystemId) {
		return new PvAddress(address, controlSystemId);
	}

	@Override
	public int hashCode() {
		return _address.hashCode() + 31 * _controlSystem.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PvAddress) {
			PvAddress other = (PvAddress) obj;
			return _address.equals(other._address)
					&& _controlSystem.equals(other._controlSystem);
		}
		return false;
	}

	@Override
	public String toString() {
		return "PVAdress[" + _controlSystem + "/" + _address + "]";
	}
}
