
/**
 * 
 */

package org.csstudio.nams.common.decision;

/**
 * @author Goesta Steen
 * 
 */
public interface Worker {
	/**
	 * Beendet die Arbeit.
	 */
	public void stopWorking();

	/**
	 * Beginnt mit der Arbeit.
	 */
	public void startWorking();

	/**
	 * Ist gerade am arbeiten
	 * 
	 * @return True or False
	 */
	public boolean isWorking();
}
