package org.csstudio.sds.history;

import java.util.List;

import org.csstudio.sds.history.domain.listener.IPvChangeListener;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

public interface IHistoryDataService {
	
	/**
	 * Add listener for {@link ProcessVariable} state changes.
	 * 
	 * @param pvChangeListener the state change listener
	 */
	public void addMonitoredPv(IPvChangeListener pvChangeListener);
	
	/**
	 * Removes listener for pv state changes.
	 * 
	 * @param pvChangeListner
	 */
	public void removePvChangeListener(IPvChangeListener pvChangeListner);
	
	/**
	 * Removes a list of listeners.
	 * 
	 * @param pvChangeListners
	 */
	public void removePVChangeListeners(List<? extends IPvChangeListener> pvChangeListners);
}
