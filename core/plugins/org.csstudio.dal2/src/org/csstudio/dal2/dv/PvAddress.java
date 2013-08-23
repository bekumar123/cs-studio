package org.csstudio.dal2.dv;

/**
 * Address identifying a process variable 
 */
public class PvAddress {

	private String _address;

	private PvAddress(String address) {
		_address = address;
	}
	
	public String getAddress() {
		return _address;
	}

	public static PvAddress getValue(String address) {
		return new PvAddress(address);
	}
	
	@Override
	public int hashCode() {
		return _address.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PvAddress) {
			return _address.equals(((PvAddress)obj)._address);
		}
		return false;
	}
}
