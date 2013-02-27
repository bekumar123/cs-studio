package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IPrototype;
import org.eclipse.gef.commands.Command;

import static com.google.common.base.Preconditions.checkNotNull;

public class SwitchParametersCommand extends Command {

    private IPrototype prototype;
    private int moveFrom;
    private int moveTo;

    public SwitchParametersCommand(IPrototype prototype, int moveFrom, int moveTo) {
        checkNotNull(prototype);
        this.prototype = prototype;
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        prototype.switchParameters(moveFrom, moveTo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        prototype.switchParameters(moveTo, moveFrom);
    }

}
