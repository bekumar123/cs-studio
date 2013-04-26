package org.csstudio.dct.model.internal.sync;

import java.util.List;

import org.eclipse.gef.commands.Command;

public interface ISyncModel {
    List<Command> calculateCommands();
}
