package org.csstudio.sds.history.domain;

import org.csstudio.sds.history.domain.listener.IPvChangeListener;
import org.csstudio.sds.history.domain.listener.ITimeChangeListener;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;


public interface IPvUpdater extends ITimeChangeListener {
	
	/**
	 * The given {@link IPvChangeListener} will get value updates of the ProcessVariable that the implementing class handles.
	 * 
	 * @param pvChangeListener the process variable change listener
	 */
	public void addPvChangeListener(IPvChangeListener pvChangeListener);

	
	/**
	 * Removes the listener. The listener will get no more value updates.
	 * 
	 * @param pvChangeListener the listener to be removed from value updates
	 */
	public void removePvChangeListener(IPvChangeListener pvChangeListener);
	
	/**
	 * The {@link ProcessVariable} that this {@link IPvUpdater} is responsible for.
	 * 
	 * @return the {@link ProcessVariable}
	 */
	public ProcessVariable getProcessVariable();
	
}
