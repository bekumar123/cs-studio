package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IRecord;
import org.eclipse.gef.commands.Command;

public class ChangeArchivedFlagCommand extends Command {
    
    private IRecord record;
    private String fieldName;
    private boolean value;
    private boolean oldValue;
    
    /**
     * Constructor.
     * @param delegate the object
     * @param propertyName the name of the property
     * @param value the new value
     */
    public ChangeArchivedFlagCommand(IRecord record, String fieldName, Boolean value) {
        super();
        this.record = record;
        this.fieldName = fieldName;
        this.value = value;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        oldValue = record.getArchived(fieldName);
        record.setArchived(fieldName, value);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        record.setArchived(fieldName, oldValue);
    }

}