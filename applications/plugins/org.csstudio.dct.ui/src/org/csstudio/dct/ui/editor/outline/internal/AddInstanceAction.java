package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.util.CompareUtil;
import org.csstudio.dct.util.ModelValidationUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

/**
 * Action that adds an instance.
 * 
 * @author Sven Wende
 * 
 */
public final class AddInstanceAction extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;
        assert selection.size() == 1;
        assert selection.get(0) instanceof IFolder || selection.get(0) instanceof IContainer;

        IElement container = selection.get(0);

        CompoundCommand result = null;

        InstanceDialog rsd = new InstanceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                getProject(), container instanceof IContainer ? (IContainer) container : null);

        if (rsd.open() == Window.OK) {
            result = new CompoundCommand("Add Instance");

            IPrototype prototype = (IPrototype) rsd.getSelection();

            IInstance instance = new Instance(prototype, UUID.randomUUID());

            if (container instanceof IFolder) {
                result.add(new AddInstanceCommand((IFolder) container, instance));
            } else if (container instanceof IContainer) {
                if (ModelValidationUtil.causesTransitiveLoop((IContainer) container, prototype)) {
                    MessageDialog
                            .openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
                                    "An instance of the selected prototype cannot be inserted because this would cause a transitive relationship.");
                } else {
                    result.add(new AddInstanceCommand((IContainer) container, instance));
                }
            }

            result.add(new SelectInOutlineCommand(getOutlineView(), instance));

        }

        return result;
    }

    @Override
    protected void afterSelectionChanged(List<IElement> selection, IAction action) {
        super.afterSelectionChanged(selection, action);

        if (selection.size() != 1) {
            action.setEnabled(false);
            return;
        }

        boolean prototypesFolder = CompareUtil.containsPrototypesFolder(selection);

        if (prototypesFolder) {
            action.setEnabled(false);
            return;
        }

        boolean childOfPrototypesFolder = CompareUtil.childOfPrototypesFolder(selection);
        boolean isPrototype = selection.get(0) instanceof IPrototype;
        boolean isInstances = selection.get(0) instanceof IInstance;

        if (childOfPrototypesFolder) {
            if (!(isPrototype || isInstances)) {
                action.setEnabled(false);
                return;
            }
        }

        boolean libraryFolder = CompareUtil.containsLibraryFolder(selection);
        boolean childOfLibraryFolder = CompareUtil.childOfLibaryFolder(selection);

        boolean isFolder = selection.get(0) instanceof IFolder;

        //@formatter:off
        action.setEnabled((isFolder || isPrototype || isInstances) && (!(libraryFolder || childOfLibraryFolder)));
        //@formatter:on

    }
}
