package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IElement;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;

public final class DisableElements extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;
        return EnableDisableActionHelper.createEnableDisableCommand(selection, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterSelectionChanged(List<IElement> selection, IAction action) {
        action.setEnabled(EnableDisableActionHelper.isValidSelectionForDisable(selection));
    }

}