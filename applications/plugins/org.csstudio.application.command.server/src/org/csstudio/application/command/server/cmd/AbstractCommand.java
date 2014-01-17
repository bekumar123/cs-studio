
package org.csstudio.application.command.server.cmd;

import org.csstudio.application.command.server.service.CommandResult;

/**
 * The abstract super class for all supported commands.
 *
 * Common format of a command: cmdName([OPT1=...;OPT2=...;OPTn=...])[: parameter1 parameter2 parameter3 ...]
 *
 * @author mmoeller
 */
public abstract class AbstractCommand {

	protected CommandType commandType;

	protected AbstractCommand(CommandType type) {
	    commandType = type;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("Command {");
		result.append(commandType.toString().toLowerCase() + "}");
		return result.toString();
	}

	public CommandType getCommandType() {
		return commandType;
	}

	public String getOptionList() {
	    return " No options specified.\n";
	}

	public abstract CommandResult execute(String cmdLine);

	public abstract String getCommandDescription();
}
