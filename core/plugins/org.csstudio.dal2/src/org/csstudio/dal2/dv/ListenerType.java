package org.csstudio.dal2.dv;

/**
 * A listener type describes the changes of a pv a listener is interested in
 * <p>
 * In EPICS the listener type is known as monitor mask
 * {@link gov.aps.jca.Monitor}
 */
public enum ListenerType {
	/**
	 * Listen to value changes
	 */
	VALUE,

	/**
	 * Listen to changes for logging / archiving
	 */
	LOG,

	/**
	 * Listen to changes of the alarm states
	 */
	ALARM,

	/**
	 * Listen to property changes
	 */
	PROPERTY;
}
