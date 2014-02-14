package org.csstudio.dal2.dv;


public enum ConnectionState {

	/**
	 * @see gov.aps.jca.Channel.ConnectionState.NEVER_CONNECTED
	 */
	NEVER_CONNECTED,
	
	/**
	 * @see gov.aps.jca.Channel.ConnectionState.DISCONNECTED
	 */
    DISCONNECTED,
    
    /**
     * gov.aps.jca.Channel.ConnectionState.CONNECTED
     */
    CONNECTED,
    
    /**
     * gov.aps.jca.Channel.ConnectionState.CLOSED 
     */
    CLOSED,
    
    /**
     * The PvAccess has not created a connection (Channel) to the control system.
     */
    NOT_REQUESTED,
    
    UNDEFINED;
	
	public boolean isConnected() {
		return this == CONNECTED;
	}
	
}
