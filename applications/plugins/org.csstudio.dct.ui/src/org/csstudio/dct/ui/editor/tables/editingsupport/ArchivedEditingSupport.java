package org.csstudio.dct.ui.editor.tables.editingsupport;

import org.csstudio.dct.ui.editor.RecordFieldTableRowAdapter;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

public class ArchivedEditingSupport extends EditingSupport {
    
    public static final String CHECKED = "Y";
    public static final String UNCHECKED = "N";
    
    private final TableViewer viewer;
    private final CommandStack commandStack;

    public ArchivedEditingSupport(TableViewer viewer, CommandStack commandStack) {
        super(viewer);
        this.viewer = viewer;
        this.commandStack = commandStack;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);

    }

    @Override
    protected boolean canEdit(Object element) {
        RecordFieldTableRowAdapter adapter = (RecordFieldTableRowAdapter) element;
        return adapter.isArchivable();
    }

    @Override
    protected Object getValue(Object element) {
        RecordFieldTableRowAdapter adapter = (RecordFieldTableRowAdapter) element;
        String value = adapter.getEditingValue(2);
        if (value.equals(CHECKED)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    protected void setValue(Object element, Object value) {
        RecordFieldTableRowAdapter adapter = (RecordFieldTableRowAdapter) element;
        if ((Boolean) value) {
            adapter.setValue(2, CHECKED, commandStack);
        } else {
            adapter.setValue(2, UNCHECKED, commandStack);
        }
        viewer.update(element, null);
    }

}