
package org.csstudio.application.command.server.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CommandFactory {

	private static final Map<CommandType, Class<? extends AbstractCommand>> commandMap =
			new HashMap<CommandType, Class<? extends AbstractCommand>>();

	static {
		commandMap.put(CommandType.UNKNOWN, UnknownCommand.class);
		commandMap.put(CommandType.EXEC, ExecCommand.class);
	}

	public static AbstractCommand getCommand(String command) {
		return parseCommand(command);
	}

	private static AbstractCommand parseCommand(String command) {
		CommandType commandType = CommandType.UNKNOWN;
		AbstractCommand cmd = null;
		try {
			cmd = commandMap.get(commandType).newInstance();
		} catch (Exception e) {
			cmd = new UnknownCommand();
		}
		if (command == null) {
			return cmd;
		}
		if (command.trim().isEmpty()) {
			return cmd;
		}
		String rawCommand = command.trim();
		// exec: java.exe -jar CommandApplication.jar SIGINT 2134
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
}
