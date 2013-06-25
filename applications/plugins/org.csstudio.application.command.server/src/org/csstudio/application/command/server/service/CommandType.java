
package org.csstudio.application.command.server.service;

/**
 * Represents the type of a command and defines its behaviour.
 *
 * @author mmoeller
 */
public enum CommandType {

	/** Given command is not supported */
	UNKNOWN,

	/** Execute a command on the host machine */
	EXEC;

	public static CommandType getCommandTypeByName(String name) {
		if (name == null) {
			return CommandType.UNKNOWN;
		}
		String cmdName = name.trim();
		if (cmdName.endsWith(":")) {
			cmdName = cmdName.substring(0, cmdName.length() - 1);
		}
		CommandType result = CommandType.UNKNOWN;
		for (CommandType o : CommandType.values()) {
			if (o.toString().compareToIgnoreCase(cmdName) == 0) {
				result = o;
				break;
			}
		}
		return result;
	}
}
