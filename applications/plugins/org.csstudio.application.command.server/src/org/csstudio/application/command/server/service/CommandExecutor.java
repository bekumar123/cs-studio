
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author mmoeller
 * @since 18.06.2013
 */
public class CommandExecutor {

    private Map<CommandType, Class<? extends AbstractCommand>> commandMap =
            new HashMap<CommandType, Class<? extends AbstractCommand>>();

    // Is there a better way?
    private StringBuffer description;

    public CommandExecutor() {
        commandMap.put(CommandType.UNKNOWN, UnknownCommand.class);
        commandMap.put(CommandType.EXEC, ExecCommand.class);
        description = new StringBuffer();
        Iterator<CommandType> iter = commandMap.keySet().iterator();
        while (iter.hasNext()) {
            CommandType key = iter.next();
            try {
                AbstractCommand command = commandMap.get(key).newInstance();
                description.append(command.getCommandDescription());
            } catch (Exception e) {
                description.append("Command description not available!");
            }
        }
    }

    public CommandResult executeCommand(String cmdStr) {
        AbstractCommand command = parseCommand(cmdStr);
        CommandResult cmdResult = command.execute();
        command.clear();
        command = null;
        return cmdResult;
    }

    private AbstractCommand parseCommand(String cmdStr) {
        CommandType commandType = CommandType.UNKNOWN;
        AbstractCommand cmd = null;
        try {
            cmd = commandMap.get(commandType).newInstance();
        } catch (Exception e) {
            cmd = new UnknownCommand();
        }
        if (cmdStr == null) {
            return cmd;
        }
        if (cmdStr.trim().isEmpty()) {
            return cmd;
        }
        String rawCommand = cmdStr.trim();
        while (rawCommand.indexOf("  ") > -1) {
            rawCommand = rawCommand.replaceAll("  ", " ");
        }
        String[] cmdParts = rawCommand.split(" ");
        if (cmdParts.length > 0) {
            commandType = CommandType.getCommandTypeByName(cmdParts[0]);
            try {
                cmd = commandMap.get(commandType).newInstance();
                List<String> p = new ArrayList<String>();
                Scanner scanner = new Scanner(new StringReader(rawCommand.replace(cmdParts[0], "")));
                Pattern pattern = Pattern.compile("[^\"\\s]+|\"(\\\\.|[^\\\\\"])*\"");
                int index = 0;
                while (scanner.hasNext()) {
                    p.add(index++, scanner.findInLine(pattern));
                }
                cmd.setParameters(p);
            } catch (Exception e) {
                cmd = new UnknownCommand();
            }

        }
        return cmd;
    }

    public String getCommandDescription() {
        return description.toString();
    }
}
