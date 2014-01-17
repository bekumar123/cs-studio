
package org.csstudio.application.command.server.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.csstudio.application.command.server.service.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecCommand extends AbstractCommand {

    private static final Logger LOG = LoggerFactory.getLogger(ExecCommand.class);

    enum ExecCommandOption {

		/** Working directory of the command */
		WORKDIR("Working directory of the command.", "WORKDIR=<Path>"),

		/** Application directory where the app can be found */
        APPDIR("Directory where the application can be found.", "APPDIR=<Path>"),

        /** Redirection of the std streams */
		REDIRECT("Decides if the std streams will be redirected.", "REDIRECT=true|false");

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
		super(CommandType.EXEC);
	}

	@Override
	public CommandResult execute(String cmdLine) {
	    CommandResult result = null;
	    CommandElements elements = CommandLineParser.parse(cmdLine);
	    if (!elements.isEmpty()) {
    		ProcessBuilder procBuilder = new ProcessBuilder(elements.getCommandParameters().getAllParameters());
    		procBuilder.directory(new File(elements.getCommandOptions().getOption("WORKDIR")));
    		try {
    		    Process process = procBuilder.start();
    		    synchronized (process) {
    		        process.wait(5000L);
                }
    		    int exitCode = process.waitFor();
    		    BufferedReader in = null;
    		    try {
        		    in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        		    while (in.ready()) {
        		        String line = in.readLine();
        		        LOG.info(line);
            		}
    		    } catch (IOException e) {
    		        LOG.warn("[*** IOException ***]: Reading process stream: {}", e.getMessage());
    		    } finally {
    		        if (in != null) {try{in.close();}catch(Exception e){/**/}}
    		    }
    		    LOG.info("Die Anwendung gab {} zurueck.", exitCode);
    		    result = new CommandResult(exitCode, "Command has been executed.");
            } catch (IOException e) {
                LOG.error("[*** IOException ***]: {}", e.getMessage());
                result = new CommandResult(CommandResult.COMMAND_ERROR,
                                           "[*** IOException ***]: " + e.getMessage());
            } catch (InterruptedException e) {
                LOG.warn("[*** InterruptedException ***]: {}", e.getMessage());
                result = new CommandResult(CommandResult.COMMAND_ERROR,
                                           "[*** InterruptedException ***]: " + e.getMessage());
            }
	    }
	    if (result == null) {
	        result = new CommandResult(CommandResult.COMMAND_ERROR, null);
	    }
		return result;
	}

	@Override
	public String getOptionList() {
		StringBuffer result = new StringBuffer(" ExecCommandOptions:\n");
		for (ExecCommandOption o : ExecCommandOption.values()) {
			result.append("  " + o.getUsage() + " - " + o.getDescription() + "\n");
		}
		return result.toString();
	}

	@Override
	public String getCommandDescription() {
		StringBuffer result = new StringBuffer(" " + this.getClass().getSimpleName() + "\n");
		result.append("  " + commandType.toString().toLowerCase() + "(Options)" + " - Execute a command on the server.\n");
		result.append(getOptionList());
		return result.toString();
	}
}
