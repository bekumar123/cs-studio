package org.csstudio.config.ioconfigurator.view;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowConfiguratorView implements IWorkbenchWindowActionDelegate {

    @Override
    public void run(final IAction action) {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage();
        try {
            page.showView(ConfiguratorView.ID);
        } catch (final PartInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectionChanged(final IAction action, final ISelection selection) {
        // TODO: implementation needed?
    }

    @Override
    public void dispose() {
        // TODO: implementation needed?
    }

    @Override
    public void init(final IWorkbenchWindow window) {
        // TODO: implementation needed?
    }
}
