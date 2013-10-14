package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.material.AlarmNachricht;

public interface Regel {

	/**
	 * Prüft, ob die Regel auf die übergebene Nachricht zutrifft.
	 * @param nachricht die zu prüfende Nachricht
	 * @return true, gdw. die Regel auf die Nachricht zutrifft.
	 */
	public boolean pruefeNachricht(final AlarmNachricht nachricht);
	
	/**
	 * Prüft eine Nachricht und verwendet zur Prüfung ggf. (je nach konkretem Regel-Typ) Eigenschaften der übergebenen Vergleichsnachricht.
	 * @param nachricht die zu prüfende Nachricht
	 * @param vergleichsNachricht eine Nachricht, die von der Regel für die Prüfung verwendet werden kann.
	 * @return true, gdw. die Regel auf die Nachricht zutrifft.
	 */
	public boolean pruefeNachricht(final AlarmNachricht nachricht, final AlarmNachricht vergleichsNachricht);
}