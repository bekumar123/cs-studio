package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IFolder;
import org.eclipse.gef.commands.Command;

public class AddFolderAtIndexCommand extends Command {
    
    private IFolder folder;
    private IFolder parentFolder;
    private int index;
    
    /**
     * Constructor.
     * 
     * @param parentFolder
     *            the parent folder
     * @param name
     *            then name of the new folder
     */
    public AddFolderAtIndexCommand(IFolder parentFolder, IFolder folder, int index) {
        assert parentFolder != null;
        assert folder!=null;
        this.parentFolder = parentFolder;
        this.folder = folder;
        this.index= index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        parentFolder.addMember(index, folder);
        folder.setParentFolder(parentFolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        parentFolder.removeMember(folder);
        folder.setParentFolder(null);
    }
}
