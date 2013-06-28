package org.csstudio.dct.model.internal.sync;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IInstance;
import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelSync {

    private static final Logger LOG = LoggerFactory.getLogger(ModelSync.class);
    
    List<IInstance> instances;

    public ModelSync(List<IInstance> instances) {
        this.instances = instances;
    }

    /**
     * Calculate a List of Commands that is neccessary to keep the Library and
     * the Project in sync.
     */
    public List<Command> calculateCommands() {

        LOG.info("Calculating commands");
        
        List<Command> commands = new ArrayList<Command>();

        List<ISyncModel> syncCommands = new ArrayList<ISyncModel>();
        syncCommands.add(new RecordSync(instances));

        for (ISyncModel sync : syncCommands) {
            commands.addAll(sync.calculateCommands());
        }

        return commands;
    }

}
