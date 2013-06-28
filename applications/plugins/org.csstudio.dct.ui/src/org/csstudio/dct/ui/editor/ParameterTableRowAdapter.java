package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.commands.GenericCommand;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.Activator;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Table adapter for Parameters.
 * 
 * @author Sven Wende
 * 
 */
public final class ParameterTableRowAdapter extends AbstractTableRowAdapter<Parameter> {

    /**
     * Constructor.
     * 
     * @param parameter
     *            a parameter
     */
    public ParameterTableRowAdapter(Parameter parameter) {
        super(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doCanModifyKey(Parameter parameter) {
        return true;
    }

    protected boolean doCanModifyDescription(Parameter parameter) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetKey(Parameter parameter) {
        return parameter.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetValue(Parameter parameter) {
        return parameter.getDefaultValue();
    }

    protected String doGetDescription(Parameter parameter) {
        return parameter.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetValueForDisplay(Parameter parameter) {
        return parameter.getDefaultValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doSetValue(final Parameter parameter, final Object value) {
        Command comand = new GenericCommand(new Runnable() {
            public void run() {
                parameter.setDefaultValue(value.toString());
            }
        });
        return comand;
    }

    protected Command doSetDescriptione(final Parameter parameter, final Object value) {
        Command comand = new GenericCommand(new Runnable() {
            public void run() {
                parameter.setDescription(value.toString());
            }
        });
        return comand;
    }

    public String getDisplayValue(int column) {
        String result = null;

        switch (column) {
        case 0:
            result = doGetKeyDescription(getDelegate());
            break;
        case 1:
            result = doGetValueForDisplay(getDelegate());
            break;
        case 2:
            result = doGetDescription(getDelegate());
            break;
        default:
            break;
        }

        return result;
    }

    public String getEditingValue(int column) {
        String result = null;

        switch (column) {
        case 0:
            result = doGetKey(getDelegate());
            break;
        case 1:
            result = doGetValue(getDelegate());
            break;
        case 2:
            result = doGetDescription(getDelegate());
            break;
        default:
            break;
        }

        return result;
    }

    public final void setValue(int column, Object value, CommandStack commandStack) {
        Command cmd = null;
        switch (column) {
        case 0:
            cmd = doSetKey(getDelegate(), value);
            break;
        case 1:
            cmd = doSetValue(getDelegate(), value);
            break;
        case 2:
            cmd = doSetDescriptione(getDelegate(), value);
            break;
        default:
            break;
        }

        if (cmd != null) {
            commandStack.execute(cmd);
        }
    }

    public boolean canModify(int column) {
        boolean result = false;

        switch (column) {
        case 0:
            result = doCanModifyKey(getDelegate());
            break;
        case 1:
            result = doCanModifyValue(getDelegate());
            break;
        case 2:
            result = doCanModifyDescription(getDelegate());
            break;
        default:
            break;
        }

        return result;
    }

    public CellEditor getCellEditor(int column, Composite parent) {
        CellEditor editor = column > 0 ? doGetValueCellEditor(getDelegate(), parent) : null;
        if (editor != null && editor.getControl() != null) {
            editor.getControl().setFont(getFont(column));
        }
        return editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Image doGetImage(Parameter delegate, int columnIndex) {
        return columnIndex == 0 ? CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID,
                "icons/parameter.png") : null;
    }

}
