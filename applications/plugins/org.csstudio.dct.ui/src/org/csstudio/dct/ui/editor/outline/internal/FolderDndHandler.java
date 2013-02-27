package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddFolderAtIndexCommand;
import org.csstudio.dct.model.commands.RemoveFolderCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

public class FolderDndHandler extends AbstractDnDHandler<IElement> {

    @Override
    protected Command doCreateCopyCommand(IElement dndSource, IElement dndTarget) {       
        return null;
    }

    @Override
    protected Command doCreateMoveCommand(IElement dndSource, IElement dndTarget) {
        
        if (dndTarget instanceof IRecord) {
            return null;
        }
        
        assert dndSource instanceof IFolder;
        assert (dndTarget instanceof IFolder || (dndTarget instanceof IContainer && ((IContainer) dndTarget).getParentFolder() != null));

        IFolder folder = (IFolder) dndSource;

        // .. determine folder and insertation index
        int index = 0;
        IFolder destFolder = null;

        if (dndTarget instanceof IFolder) {
            destFolder = (IFolder) dndTarget;
        } else {
            destFolder = ((IContainer) dndTarget).getParentFolder();            
            index = destFolder.getMembers().indexOf(dndTarget);
            int tmp = destFolder.getMembers().indexOf(destFolder);
            if(tmp>-1 && tmp<index) {
                index--;
            }
        }

        assert destFolder != null;

        // .. create command
        CompoundCommand cmd = new CompoundCommand();
        cmd.add(new RemoveFolderCommand(folder));
        cmd.add(new AddFolderAtIndexCommand(destFolder, folder, index));
        return cmd;
    }

    @Override
    public int updateDragFeedback(IElement dndSource, IElement dndTarget, DropTargetEvent event) {
        if ((dndSource == dndTarget) || (event.detail == DND.DROP_COPY)) {
            event.feedback = DND.FEEDBACK_NONE;
        } else if (event.detail == DND.DROP_COPY) {
            if (dndTarget instanceof IFolder) {
                event.feedback = DND.FEEDBACK_SELECT;
            } else {
                event.feedback = DND.FEEDBACK_NONE;
            }
        } else if (event.detail == DND.DROP_MOVE) {
            if (dndTarget instanceof IFolder) {
                event.feedback = DND.FEEDBACK_SELECT;
            } else if (dndTarget instanceof IContainer) {
                IContainer container = (IContainer) dndTarget;
                if (container.getParentFolder() != null) {
                    event.feedback = DND.FEEDBACK_INSERT_BEFORE;
                } else {
                    event.feedback = DND.FEEDBACK_NONE;
                }
            } else {
                event.feedback = DND.FEEDBACK_NONE;
            }
        } else {
            event.feedback = DND.FEEDBACK_NONE;
        }

        return 0;
    }

}
