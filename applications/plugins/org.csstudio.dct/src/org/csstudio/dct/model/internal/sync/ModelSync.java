package org.csstudio.dct.model.internal.sync;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IInstance;
import org.eclipse.gef.commands.Command;

public class ModelSync {

    List<IInstance> instances;

    public ModelSync(List<IInstance> instances) {
        this.instances = instances;
    }

    /**
     * Calculate a List of Commands that is neccessary to keep the Library and
     * the Project in sync.
     */
    public List<Command> calculateCommands() {

        List<Command> commands = new ArrayList<Command>();

        List<ISyncModel> syncCommands = new ArrayList<ISyncModel>();
        syncCommands.add(new RecordSync(instances));

        for (ISyncModel sync : syncCommands) {
            commands.addAll(sync.calculateCommands());
        }

        return commands;
    }

}
