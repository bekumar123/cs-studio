package de.c1wps.geneal.desy.domain.plant.plantmaterials;

public enum PVConnectionState {

	/**
	 * If state is not a valid DAL-state. Used as initial connection state.
	 */
	UNKNOWN,

	INITIAL,

	/**
	 * If connection is valid and connected.
	 */
	CONNECTED,

	/**
	 * If the connection get lost in case of any problem.
	 */
	CONNECTION_LOST,

	/**
	 * If the connection to the PV failed or failed in re-connect.
	 */
	CONNECTION_FAILED,

	/**
	 * If connection get disposed / disconnected.
	 */
	DISCONNECTED;
	
	public static PVConnectionState getByName(String name){
		for(PVConnectionState state : values()){
			if(state.name().equalsIgnoreCase(name)){
				return state;
			}
		}
		
		return UNKNOWN;
	}
}
