package org.csstudio.dct.ui.editor.tables.editingsupport;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.ui.editor.GenericContentProposingTextCellEditor;
import org.csstudio.dct.ui.editor.HierarchicalBeanPropertyTableRowAdapter;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;

public class DelegatingColumnEditingSupport extends EditingSupport {

    private TableViewer tableViewer;
    private int columnIndex;
    private CommandStack commandStack;

    public DelegatingColumnEditingSupport(TableViewer viewer, int columnIndex, CommandStack commandStack) {
        super(viewer);
        assert columnIndex >= 0 : "columnIndex>=0";
        assert commandStack != null;
        this.columnIndex = columnIndex;
        this.commandStack = commandStack;
        tableViewer = viewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canEdit(Object element) {
        ITableRow row = (ITableRow) element;
        return row.canModify(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CellEditor getCellEditor(Object element) {
        ITableRow row = (ITableRow) element;
        CellEditor result = null;
        if (element instanceof HierarchicalBeanPropertyTableRowAdapter) {
            HierarchicalBeanPropertyTableRowAdapter rowAdapter = (HierarchicalBeanPropertyTableRowAdapter) row;
            if (rowAdapter.getBeanProperty().equalsIgnoreCase("epicsName")) {
                IElement currentElement = rowAdapter.getDelegate();
                if (currentElement instanceof IRecord) {
                    IRecord currentRecord = (IRecord) currentElement;
                    IContainer container = currentRecord.getContainer();
                    result = new GenericContentProposingTextCellEditor(((TableViewer) getViewer()).getTable(),
                            container);
                    result.getControl().setFont(row.getFont(columnIndex));
                    Color foreGround = CustomMediaFactory.getInstance().getColor(row.getForegroundColor(columnIndex));
                    result.getControl().setForeground(foreGround);
                }
            }
        }
        if (result == null) {
            result = row.getCellEditor(columnIndex, ((TableViewer) getViewer()).getTable());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getValue(Object element) {
        ITableRow row = (ITableRow) element;
        return row.getEditingValue(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setValue(Object element, Object value) {
        ITableRow row = (ITableRow) element;
        row.setValue(columnIndex, value, commandStack);
        getViewer().refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnViewer getViewer() {
        return super.getViewer();
    }

}