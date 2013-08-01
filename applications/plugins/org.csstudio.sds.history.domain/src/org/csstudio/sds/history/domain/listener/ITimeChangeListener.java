package org.csstudio.sds.history.domain.listener;

import org.csstudio.sds.history.domain.events.UpdateTimeEvent;


/**
 * Implementing classes are interested in time change events for the history mode.
 * 
 * @author Christian Mein
 *
 */
public interface ITimeChangeListener {
	
	/**
	 * Update history state to the given time in the {@link UpdateTimeEvent}.
	 * 
	 * @param updateTimeEvent the {@link UpdateTimeEvent}s
	 */
	public void handleTimeIndexChanged(UpdateTimeEvent updateTimeEvent);

}
