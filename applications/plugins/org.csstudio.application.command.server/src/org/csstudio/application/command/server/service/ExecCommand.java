
package org.csstudio.application.command.server.service;

public class ExecCommand extends AbstractCommand {

	enum ExecCommandOption {

		/** Directory where to execute the command */
		DIR("Execution directory for the command.", "DIR=<Path>");

		private String optionDesc;

		private String optionUsage;

		private ExecCommandOption(String desc, String usage) {
			optionDesc = desc;
			optionUsage = usage;
		}

		public String getDescription() {
			return optionDesc;
		}

		public String getUsage() {
			return optionUsage;
		}
	}

	public ExecCommand() {
		super();
		commandType = CommandType.EXEC;
	}

	@Override
	public CommandResult execute() {

		ProcessBuilder procBuilder = new ProcessBuilder();
//		Iterator<String> iter = procBuilder.environment().keySet().iterator();
//		while (iter.hasNext()) {
//			String key = iter.next();
//			System.out.println(key + " = " + procBuilder.environment().get(key));
//		}
//		System.out.println("\n------------------------------------------------------------------\n");
		return new CommandResult(0, null);
	}

	@Override
	public String getOptionList() {
		StringBuffer result = new StringBuffer("ExecCommandOptions:\n");
		for (ExecCommandOption o : ExecCommandOption.values()) {
			result.append(" " + o.getUsage() + " - " + o.getDescription() + "\n");
		}
		return result.toString();
	}

	@Override
	public String getCommandDescription() {
		StringBuffer result = new StringBuffer("\n" + this.getClass().getSimpleName() + "\n");
		result.append(" " + commandType.toString().toLowerCase() + "(Options)" + " - Execute a command on the server.\n");
		result.append(getOptionList());
		return result.toString();
	}
}
