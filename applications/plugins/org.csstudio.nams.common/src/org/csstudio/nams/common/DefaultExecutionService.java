package org.csstudio.nams.common;

import java.lang.Thread.UncaughtExceptionHandler;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.common.service.ThreadType;

/**
 * Der Standard execution service, verwendet {@link Thread}s.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is incomplete on handling groups.
 */
public class DefaultExecutionService implements ExecutionService {

	@Override
    public <GT extends Enum<?> & ThreadType> void executeAsynchronously(
			final GT groupId, final StepByStepProcessor runnable) {
		executeAsynchronously(groupId, runnable, null);
	}
	
	@Override
    public <GT extends Enum<?> & ThreadType> void executeAsynchronously(final GT groupId, StepByStepProcessor runnable, UncaughtExceptionHandler uncaughtExceptionHandler) {
		Thread thread = new Thread(runnable, groupId.name());
		
		if(uncaughtExceptionHandler != null) {
			thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		}
		
		thread.start();
	};

}
