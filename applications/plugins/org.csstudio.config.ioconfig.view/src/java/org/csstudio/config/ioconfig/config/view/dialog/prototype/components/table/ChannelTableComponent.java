package org.csstudio.config.ioconfig.config.view.dialog.prototype.components.table;

import java.util.ArrayList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.component.IComponent;
import org.csstudio.config.ioconfig.config.component.ISelectableAndRefreshable;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.base.Optional;

public class ChannelTableComponent implements IComponent, ISelectableAndRefreshable {

    private final Composite tableParent;
    private ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList;

    private TableViewer tableViewer;
    private Optional<IPropertyChangeListener> propertyChangeListener = Optional.absent();

    //@formatter:off
    public ChannelTableComponent(
            @Nonnull final Composite tableParent,
            @Nonnull final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList) {
            //@formatter:on
        this.tableParent = tableParent;
        this.channelPrototypeModelList = channelPrototypeModelList;
    }

    @Override
    public void buildComponent() {

        final int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
                | SWT.HIDE_SELECTION;

        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd.minimumHeight = 100;

        final Table table = new Table(tableParent, style);
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        addTableColumn(table, SWT.RIGHT, 45, ChannelPrototypConfigColumn.OFFSET.getText());
        addTableColumn(table, SWT.LEFT, 120, ChannelPrototypConfigColumn.NAME.getText());
        addTableColumn(table, SWT.LEFT, 75, ChannelPrototypConfigColumn.TYPE.getText());
        addTableColumn(table, SWT.RIGHT, 45, ChannelPrototypConfigColumn.SIZE.getText());
        addTableColumn(table, SWT.RIGHT, 45, ChannelPrototypConfigColumn.STRUCT.getText());
        addTableColumn(table, SWT.RIGHT, 55, ChannelPrototypConfigColumn.STATUS.getText());
        addTableColumn(table, SWT.RIGHT, 55, ChannelPrototypConfigColumn.MIN.getText());
        addTableColumn(table, SWT.RIGHT, 55, ChannelPrototypConfigColumn.MAX.getText());
        addTableColumn(table, SWT.LEFT, 55, ChannelPrototypConfigColumn.ORDER.getText());

        tableViewer = new TableViewer(table);

        tableViewer.setLabelProvider(new ChannelPrototypeConfigTableLabelProvider());
        tableViewer.setContentProvider(new ChannelTableContentProvider());
        tableViewer.setColumnProperties(ChannelPrototypConfigColumn.getStringValues());

        buildTableCellEditors(tableViewer);
        tableViewer.setCellModifier(new ChannelConfigCellModifier(tableViewer));
        tableViewer.setInput(channelPrototypeModelList);

    }

    public void assignToTablItem(TabItem tabItem) {
        tabItem.setControl(tableViewer.getTable());
    }
    
    public void setData(@Nonnull final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList) {
        this.channelPrototypeModelList = channelPrototypeModelList;
        tableViewer.setInput(channelPrototypeModelList);
        tableViewer.refresh();
    }

    public void closeAllCellEditors() {
        if (tableViewer != null) {
            tableViewer.getTable().setFocus();
            // finish last edit
            try {
                for (final CellEditor editor : tableViewer.getCellEditors()) {
                    if (editor != null) {
                        editor.deactivate();
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public TableViewer getTableViewer() {
        if (tableViewer == null) {
            throw new IllegalStateException("tableViewer must not be null");
        }
        return tableViewer;
    }

    @Override
    public void refresh() {
        tableViewer.refresh();        
    }

    @Override
    public Object getSelection() {
        return tableViewer.getSelection();
    }

    public void assignPropertChangeListener(IPropertyChangeListener listener) {
        propertyChangeListener = Optional.of(listener);
    }

    private void addTableColumn(@Nonnull final Table table, final int style, final int width,
            @Nonnull final String header) {
        final TableColumn tc = new TableColumn(table, style);
        tc.setText(header);
        tc.setResizable(true);
        tc.setWidth(width);
    }

    private void buildTableCellEditors(@Nonnull final TableViewer tableViewer) {
        final ICellEditorValidator cellEditorValidator = new ICellEditorValidator() {

            @Override
            @CheckForNull
            public String isValid(@Nullable final Object value) {
                if (value instanceof String) {
                    final String stringValue = (String) value;
                    try {
                        Integer.parseInt(stringValue);
                        return null;
                    } catch (final Exception e) {
                        return Messages.ChannelConfigDialog_ErrorNoInt;
                    }
                }
                return Messages.ChannelConfigDialog_ErrorNoString;
            }

        };

        final Table table = tableViewer.getTable();
        final CellEditor[] editors = new CellEditor[9];
        // Offset
        editors[0] = buildIntegerEdior(table, cellEditorValidator);
        editors[1] = buildNameEditor(table);
        // Type
        editors[2] = new ComboBoxCellEditor(table, DataType.getNames(), SWT.DROP_DOWN | SWT.READ_ONLY);
        editors[2].activate();
        // Size isn't to edit
        editors[3] = null;
        // Structure
        editors[4] = new CheckboxCellEditor(table, SWT.CHECK);
        editors[4].activate();
        // Status //ehemals Shift
        editors[5] = buildIntegerEdior(table, cellEditorValidator);
        // MIN
        editors[6] = buildIntegerEdior(table, cellEditorValidator);
        // MAX
        editors[7] = buildIntegerEdior(table, cellEditorValidator);
        // Byte Order
        editors[8] = buildIntegerEdior(table, cellEditorValidator);

        tableViewer.setCellEditors(editors);
    }

    @Nonnull
    //@formatter:off
    private CellEditor buildIntegerEdior(
            @Nonnull final Table table,
            @Nullable final ICellEditorValidator cellEditorValidator) {
            //@formatter:on
        final TextCellEditor editor = new TextCellEditor(table);
        editor.setValidator(cellEditorValidator);
        editor.activate();
        return editor;
    }

    @Nonnull
    private CellEditor buildNameEditor(@Nonnull final Table table) {
        final TextCellEditor editor = new TextCellEditor(table);
        editor.activate();
        editor.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(@Nonnull final PropertyChangeEvent event) {
                if (propertyChangeListener.isPresent()) {
                    propertyChangeListener.get().propertyChange(event);
                }
            }
        });
        return editor;
    }

}
