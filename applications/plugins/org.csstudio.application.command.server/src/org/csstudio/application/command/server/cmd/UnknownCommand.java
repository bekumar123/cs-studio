
package org.csstudio.application.command.server.cmd;

import org.csstudio.application.command.server.service.CommandResult;

public class UnknownCommand extends AbstractCommand {

	public UnknownCommand() {
		super(CommandType.UNKNOWN);
	}

	@Override
	public CommandResult execute(String cmdLine) {
		return new CommandResult(1, "No command available.");
	}

	@Override
	public String getCommandDescription() {
		StringBuffer result = new StringBuffer(" " + this.getClass().getSimpleName() + "\n");
		result.append("  " + commandType.toString().toLowerCase() + "(Options)" + " - Invalid command.\n");
		result.append(getOptionList());
		return result.toString();

	}
}
