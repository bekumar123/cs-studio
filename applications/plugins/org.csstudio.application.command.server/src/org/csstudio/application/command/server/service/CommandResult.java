
package org.csstudio.application.command.server.service;

public class CommandResult {

    public static final int COMMAND_ERROR = -1000;

	private int resultNumber;

	private String resultDescription;

	public CommandResult(int nr, String desc) {
		resultNumber = nr;
		if (desc != null) {
			resultDescription = new String(desc.trim());
		} else {
			resultDescription = "";
		}
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("CommandResult {");
		if (resultNumber == COMMAND_ERROR) {
            result.append("COMMAND_ERROR," + resultDescription);
        } else {
            result.append(resultNumber + "," + resultDescription);
        }
		result.append("}");
		return result.toString();
	}

	public int getCommandResultNumber() {
		return resultNumber;
	}

	public String getCommandResultDescription() {
		return resultDescription;
	}
}
