/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.sampleview;

import org.csstudio.archive.vtype.DefaultVTypeFormat;
import org.csstudio.archive.vtype.VTypeFormat;
import org.csstudio.archive.vtype.trendplotter.ArchiveVType;
import org.csstudio.archive.vtype.trendplotter.VTypeHelper;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.editor.DataBrowserAwareView;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.csstudio.common.trendplotter.model.PlotSample;
import org.csstudio.common.trendplotter.ui.TableHelper;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VDouble;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VType;

/** A View that shows all the current Model Samples in a list.
 *
 *  @author Kay Kasemir
 *  @author Helge Rickens contributed to the previous Data Browser SampleView
 *  @author Albert Kagarmanov changed the previous Data Browser's
 *              SampleTableLabelProvider to show numbers with 4 trailing digits.
 *              This implementation uses tooltips to show the Double.toString(number)
 */
public class SampleView extends DataBrowserAwareView
{
    /** View ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.sample_view"; //$NON-NLS-1$

    /** Model of the currently active Data Browser plot or <code>null</code> */
    private Model model;

    /** GUI elements */
    private Combo items;
    private TableViewer sample_table;
    private VTypeFormat format = new DefaultVTypeFormat();
    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        final GridLayout layout = new GridLayout(3, false);
        parent.setLayout(layout);

        // Item: pvs [Refresh]
        Label l = new Label(parent, 0);
        l.setText(Messages.SampleView_Item);
        l.setLayoutData(new GridData());

        items = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        items.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        items.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                widgetDefaultSelected(e);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {   // Configure table to display samples of the selected model item
                if (items.getSelectionIndex() == 0)
                {
                    sample_table.setInput(null);
                    return;
                }
                final ModelItem item = model.getItem(items.getText());
                if (item == null)
                    return;
                sample_table.setInput(item);
            }
        });

        final Button refresh = new Button(parent, SWT.PUSH);
        refresh.setText(Messages.SampleView_Refresh);
        refresh.setToolTipText(Messages.SampleView_RefreshTT);
        refresh.setLayoutData(new GridData());
        refresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {   // Trigger GUI update by switching to current model
                updateModel(model, model);
            }
        });

        // Sample Table
        // TableColumnLayout requires this to be in its own container
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);

        sample_table = new TableViewer(table_parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
        sample_table.setContentProvider(new SampleTableContentProvider());
        final Table table = sample_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        // Time column
        TableViewerColumn col =
            TableHelper.createColumn(table_layout, sample_table, Messages.TimeColumn, 90, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(new TimestampFormat("dd.MM.yyyy' 'HH:mm:ss.NNNNNNNNN").format( sample.getTime()));
            }
        });
        // Value column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.ValueColumn, 50, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
           //     cell.setText(sample.getValue().toString());
                
                cell.setText(format.format(sample.getValue()));
            }

            @Override
            public String getToolTipText(Object element)
            {
                final PlotSample sample = (PlotSample) element;
                final VType value = sample.getValue();
                if (value instanceof VStatistics)
                {
                    final VStatistics mmd = (VStatistics) value;
                    return NLS.bind(Messages.SampleView_MinMaxValueTT,
                        new String[]
                        {
                            Double.toString(mmd.getAverage()),
                            Double.toString(mmd.getMin()),
                            Double.toString(mmd.getMax())
                        });
                }
                else if (value instanceof VDouble)
                {
                    final VDouble dbl = (VDouble) value;
                   // return Double.toString(dbl.getValue());
                    return new TimestampFormat("dd.MM.yyyy' 'HH:mm:ss").format( sample.getTime())+ "  "+ Double.toString(dbl.getValue());
                }
                else
                    return  value.toString();
            }
        });
        // Severity column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.SeverityColumn, 90, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                final VType value = sample.getValue();
                final AlarmSeverity severity = VTypeHelper.getSeverity(value);
                cell.setText(severity.toString());
                if (severity.equals(AlarmSeverity.NONE))
                {
                    cell.setText("OK");
                    cell.setBackground(null);
                    return;
                }
                final Display display = cell.getControl().getDisplay();
                if (severity.equals(AlarmSeverity.MAJOR))
                    cell.setBackground(display.getSystemColor(SWT.COLOR_RED));
                else if (severity.equals(AlarmSeverity.MINOR))
                    cell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
                else
                    cell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
            }
        });
        // Status column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.StatusColumn, 90, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                final VType value = sample.getValue();
                cell.setText(VTypeHelper.getMessage(value));
            }
        });
        // Sample Source column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.SampleView_Source, 90, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getSource());
            }
        });
        // Data Quality column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.SampleView_Quality, 90, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
            //TODO (wenhua xu)  Quality value  
                ArchiveVType at=(ArchiveVType) VTypeHelper.transform(sample.getValue());
             //   ArchiveVType at=(ArchiveVType)(sample.getValue());
                if (at !=null && at.getQuality() != null) {
                    cell.setText(at.getQuality().toString());
                }
            }
        });
        ColumnViewerToolTipSupport.enableFor(sample_table, ToolTip.NO_RECREATE);
    }

    /** {@inheritDoc} */
    @Override
    protected void updateModel(final Model old_model, final Model model)
    {
        this.model = model;
        if (model == null)
        {   // Clear/disable GUI
            items.setItems(new String[] { Messages.SampleView_NoPlot});
            items.select(0);
            items.setEnabled(false);
            sample_table.setInput(null);
            return;
        }

        // Show PV names
        final String names[] = new String[model.getItemCount()+1];
        names[0] = Messages.SampleView_SelectItem;
        for (int i=1; i<names.length; ++i)
            names[i] = model.getItem(i-1).getName();
        if (old_model == model  &&  items.getSelectionIndex() > 0)
        {
            // Is the previously selected item still valid?
            final String old_name = items.getText();
            final ModelItem item = model.getItem(old_name);
            if (item == sample_table.getInput())
            {   // Show same PV name again in combo box
                items.setItems(names);
                items.setText(item.getName());
                // Update sample table size
                sample_table.setItemCount(item.getSamples().getSize());
                sample_table.refresh();
                return;
            }
        }
        // Previously selected item no longer valid.
        // Show new items, clear rest
        items.setItems(names);
        items.select(0);
        items.setEnabled(true);
        sample_table.setInput(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        items.setFocus();
    }
}
