
package org.csstudio.application.command.server.service;

import java.util.List;

/**
 * The abstract super class for all supported commands.
 *
 * @author mmoeller
 */
public abstract class AbstractCommand {

	protected CommandType commandType;

	protected CommandParameters parameters;

	protected CommandOptions options;

	protected AbstractCommand() {
		parameters = new CommandParameters();
		options = new CommandOptions();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("Command {");
		result.append(commandType.toString() + "," + parameters.toString());
		result.append("}");
		return result.toString();

	}

	public CommandType getCommandType() {
		return commandType;
	}

	public void setParameters(List<String> params) {
		parameters.setParameters(params);
	}

	public String getOptionList() {
	    return "No options specified.\n";
	}

	public void clear() {
	    parameters.clear();
	    options.clear();
	}

	public abstract CommandResult execute();

	public abstract String getCommandDescription();
}
