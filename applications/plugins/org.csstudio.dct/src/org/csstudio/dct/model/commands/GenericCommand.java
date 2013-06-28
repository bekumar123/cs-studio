package org.csstudio.dct.model.commands;

import org.eclipse.gef.commands.Command;

public final class GenericCommand extends Command {
 
    private Runnable runnable;
    
    /**
     * Constructor.
     * @param project the project
     * @param path the path to the dbd file
     */
    public GenericCommand(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        runnable.run();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        // not yet implemented
    }

}