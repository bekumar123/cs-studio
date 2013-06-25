
package org.csstudio.application.command.server.service;

import java.util.ArrayList;
import java.util.List;

public class CommandParameters {

	private List<String> parameters;

	public CommandParameters() {
		parameters = new ArrayList<String>();
	}

	@Override
	public String toString() {
		return parameters.toString();
	}

	public void setParameters(List<String> params) {
		parameters.clear();
		parameters.addAll(params);
	}

	public List<String> getAllParameters() {
		return parameters;
	}

	public void clear() {
	    parameters.clear();
	}
}
