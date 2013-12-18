package org.csstudio.common.trendplotter;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Check if given pv is in archives.
 * 
 *  @author jhatje
 */
public class CheckPvInArchives extends AbstractHandler
{
    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        StringBuffer infoText = new StringBuffer();
        infoText.append("\nRecord is in archive(s): ");
        final IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
        CheckPvInArchivesJob job = new CheckPvInArchivesJob("pvSearchJob", pvs[0].getName());
        job.schedule();
        return null;
    }
}
