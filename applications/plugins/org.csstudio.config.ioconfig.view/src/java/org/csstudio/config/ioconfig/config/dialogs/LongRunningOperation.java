package org.csstudio.config.ioconfig.config.dialogs;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Optional;

public class LongRunningOperation implements IRunnableWithProgress {

    private final Runnable runInNewThread;
    private final Optional<Runnable> runInUiThread;

    public LongRunningOperation(final Runnable runInNewThread, final Optional<Runnable> runInUiThread) {
        super();
        this.runInNewThread = runInNewThread;
        this.runInUiThread = runInUiThread;
    }

    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Please wait...", IProgressMonitor.UNKNOWN);
        runInNewThread.run();
        if (runInUiThread.isPresent()) {
            Display.getDefault().syncExec(runInUiThread.get());
        }
    }

    public static void run(final Runnable runInNewThread, final Optional<Runnable> runInUiThread) {
        Shell shell = Display.getDefault().getActiveShell();
        ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
        try {
            pmd.run(true, false, new LongRunningOperation(runInNewThread, runInUiThread));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
