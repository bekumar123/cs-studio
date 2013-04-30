package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IProject;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that changes the database definition (dbd) reference of a
 * {@link IProject}.
 * 
 * @author Sven Wende
 * 
 */
public final class ChangeLibraryFileCommand extends Command {
    private final IProject project;
    private final String currentPath;
    private final String oldPath;

    /**
     * Constructor.
     * @param project the project
     * @param path the path to the dbd file
     */
    public ChangeLibraryFileCommand(IProject project,  String path) {
        this.project = project;
        this.currentPath = path;
        this.oldPath = project.getDbdPath();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        setPath(currentPath);
    }
        
    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        setPath(oldPath);
    }

    private void setPath(String path) {
        project.setLibraryPath(path);
    }

}
