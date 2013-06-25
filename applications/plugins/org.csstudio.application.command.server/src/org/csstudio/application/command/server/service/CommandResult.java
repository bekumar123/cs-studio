
package org.csstudio.application.command.server.service;

public class CommandResult {

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
		result.append(resultNumber + "," + resultDescription);
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
