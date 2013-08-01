package de.c1wps.geneal.desy.domain.plant.listener;


/**
 * Repräsentiert eine Schnittstelle für Objekte, die von außen benachrichtigt
 * werden sollen, dass sie ihren Zustand auf DIRTY überprüfen sollen
 * 
 * @author Andy, Jarig
 * 
 */
public interface IDirtyStateNotifyable {

	/**
	 * Benachrichtigt das Objekt, dass eine Überprüfung des Zustandes
	 * stattfinden soll
	 */
	public void checkDirtyState();

	public void onModified(Object modifiedObject);
}
