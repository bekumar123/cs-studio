package org.csstudio.config.ioconfig.view.treeview;

import java.util.Date;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBLoaderJob extends Job {

    protected static final Logger LOG = LoggerFactory.getLogger(DBLoaderJob.class);

    private final TreeViewer treeViewer;   
    private final ILoader loader;

     public DBLoaderJob(@Nonnull TreeViewer treeViewer, ILoader loader, @Nonnull final String name) {
        super(name);
        this.treeViewer = treeViewer;
        this.loader = loader;
    }

    @Override
    @Nonnull
    protected IStatus run(@Nonnull final IProgressMonitor monitor) {
        monitor.beginTask("DBLoaderMonitor", IProgressMonitor.UNKNOWN);
        monitor.setTaskName("Load \t-\tStart Time: " + new Date());
        Repository.close();
        try {
            loader.setLoad(Repository.load(FacilityDBO.class));
        } catch (final PersistenceException e) {
            LOG.error("Can't read from Database!", e);
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    DeviceDatabaseErrorDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Can't read from Database!", e);
                }
            });
            monitor.done();
            return Status.CANCEL_STATUS;
        }
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                treeViewer.setInput(loader.getLoad());
                treeViewer.getTree().setEnabled(true);
            }
        });
        monitor.done();
        return Status.OK_STATUS;
    }
    
}
