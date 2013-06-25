
package org.csstudio.application.command.server.service;

public class UnknownCommand extends AbstractCommand {

	protected UnknownCommand() {
		super();
		commandType = CommandType.UNKNOWN;
	}

	@Override
	public CommandResult execute() {
		return new CommandResult(1, "No command available.");
	}

	@Override
	public String getCommandDescription() {
		StringBuffer result = new StringBuffer("\n" + this.getClass().getSimpleName() + "\n");
		result.append(" " + commandType.toString().toLowerCase() + "(Options)" + " - Invalid command.\n");
		result.append(getOptionList());
		return result.toString();

	}
}
