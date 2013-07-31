package org.csstudio.config.ioconfigurator.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class LongRunningAction implements IRunnableWithProgress {

    private Runnable runnable;
        
    public LongRunningAction(Runnable runnable) {
        super();
        this.runnable = runnable;
    }


    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Executing...", IProgressMonitor.UNKNOWN);
        runnable.run();
        monitor.done();
    }

}
