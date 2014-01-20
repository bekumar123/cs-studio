package org.csstudio.dct.ui.editor.rowadapter;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.commands.ChangeParameterValueCommand;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.editor.ColorSettings;
import org.csstudio.dct.ui.editor.proposal.GenericContentProposingTextCellEditor;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/**
 * Table adapter for parameter values.
 * 
 * @author Sven Wende
 * 
 */
public final class ParameterValueTableRowAdapter extends AbstractTableRowAdapter<IInstance> {
    private Parameter parameter;

    /**
     * Constructor.
     * 
     * @param instance
     *            an instance
     * @param parameter
     *            a parameter
     */
    public ParameterValueTableRowAdapter(IInstance instance, Parameter parameter) {
        super(instance);
        this.parameter = parameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetKey(IInstance instance) {
        return parameter.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetValue(IInstance instance) {
        return AliasResolutionUtil.getParameterValueFromHierarchy(instance, parameter.getName());
    }

    protected String doGetDescription(IInstance instance) {
        return parameter.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doSetValue(IInstance instance, Object value) {
        return new ChangeParameterValueCommand(instance, parameter.getName(), value != null ? value.toString() : null);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected CellEditor doGetValueCellEditor(IInstance delegate, Composite parent) {
        CellEditor result = new GenericContentProposingTextCellEditor(parent, delegate);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected RGB doGetForegroundColorForValue(IInstance instance) {
        String key = parameter.getName();
        return instance.hasParameterValue(key) ? ColorSettings.OVERRIDDEN_PARAMETER_VALUE
                : ColorSettings.INHERITED_PARAMETER_VALUE;
    }

}
