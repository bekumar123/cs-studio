
package org.csstudio.nams.common.service;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * This service is used to execute {@link StepByStepProcessor}s asynchronously
 * instead of usind new {@link Thread}(myRunnable).
 * 
 * TODO interupt aller Thread ermöglichen
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 */
public interface ExecutionService {

	/**
	 * Führt das Runnable asynchron aus.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @param groupId
	 *            Die Gruppenidentifikation, zu der das Runnable gehört
	 *            (vornehmlich zur Identifikation bei Tests); existiert diese
	 *            Gruppe nicht, so wird eine Fehler verursacht.
	 * @param runnable
	 *            Das Runnable, welches ausgeführt werden soll.
	 * @require hasGroupRegistered(groupId)
	 */
	public <GT extends Enum<?> & ThreadType> void executeAsynchronously(
			GT groupId, StepByStepProcessor runnable);

	public <GT extends Enum<?> & ThreadType> void executeAsynchronously(
			GT groupId, StepByStepProcessor runnable, UncaughtExceptionHandler uncaughtExceptionHandler);
}
