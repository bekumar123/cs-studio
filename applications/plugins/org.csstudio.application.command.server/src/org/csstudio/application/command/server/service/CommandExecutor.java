
/*
 * Copyright (c) 2013 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.application.command.server.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.csstudio.application.command.server.cmd.AbstractCommand;
import org.csstudio.application.command.server.cmd.CommandLineParser;
import org.csstudio.application.command.server.cmd.CommandType;
import org.csstudio.application.command.server.cmd.ExecCommand;
import org.csstudio.application.command.server.cmd.UnknownCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 18.06.2013
 */
public class CommandExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(CommandExecutor.class);

    private Map<CommandType, Class<? extends AbstractCommand>> commandMap =
            new HashMap<CommandType, Class<? extends AbstractCommand>>();

    // Is there a better way?
    private StringBuffer description;

    public CommandExecutor() {
        commandMap.put(CommandType.UNKNOWN, UnknownCommand.class);
        commandMap.put(CommandType.EXEC, ExecCommand.class);
        description = new StringBuffer("\n");
        Iterator<CommandType> iter = commandMap.keySet().iterator();
        int index = 1;
        while (iter.hasNext()) {
            CommandType key = iter.next();
            try {
                AbstractCommand command = commandMap.get(key).newInstance();
                if (command.getClass() != UnknownCommand.class) {
                    description.append("\nCommand #" + index++ + "\n");
                    description.append(command.getCommandDescription());
                }
            } catch (Exception e) {
                description.append("\nCommand description not available!\n");
            }
        }
    }

    public CommandResult executeCommand(String cmdStr) {
        String cmdName = CommandLineParser.parseCommandName(cmdStr);
        CommandType commandType = CommandType.getCommandTypeByName(cmdName);
        CommandResult cmdResult = null;
        if (commandType != CommandType.UNKNOWN) {
            try {
                AbstractCommand command = commandMap.get(commandType).newInstance();
                cmdResult = command.execute(cmdStr);
            } catch (Exception e) {
                LOG.error("[*** {} ***]: {}", e.getClass().getSimpleName(), e.getMessage());
            }

        }
        if (cmdResult == null) {
            cmdResult = new CommandResult(0, "");
        }
        return cmdResult;
    }

    public String getCommandDescription() {
        return description.toString();
    }
}
