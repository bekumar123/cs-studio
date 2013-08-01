package org.csstudio.sds.history.domain.listener;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

/**
 * Listener for {@link ProcessVariable} state changes.
 * 
 * @author Christian
 *
 */
public interface IPvChangeListener {
	
	/**
	 * Updates listner to new process variable state.
	 * 
	 * @param processVariable the process variable with the new state.
	 */
	public void pvChanged(ProcessVariable processVariable);
	
	/**
	 * Returns the current process variable.
	 * 
	 * @return
	 */
	public ProcessVariable getProcessVariable();

}
