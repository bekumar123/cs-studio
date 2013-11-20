package org.csstudio.utility.toolbox.framework;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SearchExecutorRunningOperation implements IRunnableWithProgress {

    private final Runnable runInThread;
    private Runnable runInUiThread;
    
    public SearchExecutorRunningOperation(Runnable runInThread, Runnable runInUiThread) {
        super();
        Validate.notNull(runInThread, "runInThread must not be empty");
        Validate.notNull(runInUiThread, "runInUiThread must not be empty");
        this.runInThread = runInThread;
        this.runInUiThread = runInUiThread;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Please wait...", IProgressMonitor.UNKNOWN);
        runInThread.run();
        Display.getDefault().syncExec(runInUiThread);
        monitor.done();
    }

    public static void run(Runnable runnable, Runnable uiRunnable) {
        Shell shell = Display.getDefault().getActiveShell();
        ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
        try {
            pmd.run(true, false, new SearchExecutorRunningOperation(runnable, uiRunnable));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
    }
    
}
