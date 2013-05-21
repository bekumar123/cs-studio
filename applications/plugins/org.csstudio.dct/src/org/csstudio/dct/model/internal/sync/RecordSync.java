package org.csstudio.dct.model.internal.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.commands.RemoveRecordCommand;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.Record;
import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordSync implements ISyncModel {

    private static final Logger LOG = LoggerFactory.getLogger(Project.class);

    private List<IInstance> instances;

    public RecordSync(List<IInstance> instances) {
        this.instances = instances;
    }

    /**
     * 
     * @return all the command that are neccessary to keep the Library and the
     *         Project in sync.
     */
    public List<Command> calculateCommands() {

        LOG.info("Calculating command for : " + instances.size() + " instances.");

        List<Command> commands = new ArrayList<Command>();

        for (IInstance inst : instances) {
            LOG.info("Checking Instance: " + inst);
            if (inst.isFromLibrary()) {
                List<IRecord> newRecords = getRecordsToAdd(inst);
                LOG.info("Found " + newRecords.size() + " new records.");
                for (IRecord record : newRecords) {
                    Command command = new AddRecordCommand(inst, record);
                    commands.add(command);
                }
            }
        }

        return commands;
    }

    /**
     * Get a List of Records for a specific instance that are in the Library but
     * not in the Project.
     */
    private List<IRecord> getRecordsToAdd(IInstance inst) {
        List<IRecord> newRecords = new ArrayList<IRecord>();
        if (inst.getPrototype() != null) {
            List<IRecord> libraryRecords = inst.getPrototype().getRecords();
            for (IRecord libraryRecord : libraryRecords) {
                if (!isInInstance(inst, libraryRecord)) {
                    newRecords.add(new Record(libraryRecord, UUID.randomUUID()));
                }
            }
        }
        return newRecords;
    }

    int count = 0;
    
    /**
     * Check if the given libraryRecord is in the instance.
     */
    private boolean isInInstance(IInstance inst, IRecord libraryRecord) {
        List<IRecord> instanceRecords = inst.getRecords();
        for (IRecord instanceRecord : instanceRecords) {
            if ((instanceRecord.getParentRecord() != null) && instanceRecord.getParentRecord().equals(libraryRecord)) {
                return true;
            }
        }
        return false;
    }

}
