/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.export;

import java.io.PrintStream;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

/** Eclipse Job for exporting data from Model to file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlainExportJob extends ExportJob
{
    final protected ValueFormatter formatter;

    public PlainExportJob(final Model model,
            final Timestamp start, final Timestamp end, final Source source,
            final int optimize_count, final ValueFormatter formatter,
            final String filename,
            final ExportErrorHandler error_handler)
    {
        super("# ", model, start, end, source, optimize_count, filename, error_handler);
        this.formatter = formatter;
    }

    /** {@inheritDoc} */
    @Override
    protected void printExportInfo(final PrintStream out)
    {
        super.printExportInfo(out);
        out.println(comment + "Format     : " + formatter.toString());
        out.println(comment);
        out.println(comment + "Data is in TAB-delimited columns, should import into e.g. Excel");
        out.println();
    }

    /** {@inheritDoc} */
    @Override
    protected void performExport(final IProgressMonitor monitor,
                                 final PrintStream out) throws Exception
    {
        for (int i=0; i<model.getItemCount(); ++i)
        {
            final ModelItem item = model.getItem(i);
            // Item header
            if (i > 0)
                out.println();
            printItemInfo(out, item);
            // Get data
            monitor.subTask(NLS.bind("Fetching data for {0}", item.getName()));
            final ValueIterator values = createValueIterator(item);
            // Dump all values
            out.println(comment + Messages.TimeColumn + Messages.Export_Delimiter + formatter.getHeader());
            long line_count = 0;
            while (values.hasNext()  &&  !monitor.isCanceled())
            {
                //TODO (jhatje): implement vType
                final VType value =values.next();
                out.println(ValueUtil.timeOf(value).getTimestamp().toString() + Messages.Export_Delimiter + formatter.format(value));
                ++line_count;
                if (++line_count % PROGRESS_UPDATE_LINES == 0)
                    monitor.subTask(NLS.bind("{0}: Wrote {1} samples", item.getName(), line_count));
            }
        }
    }
}
