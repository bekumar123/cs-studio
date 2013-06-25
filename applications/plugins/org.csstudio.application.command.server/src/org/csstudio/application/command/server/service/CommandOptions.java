
package org.csstudio.application.command.server.service;

import java.util.HashMap;
import java.util.Map;

public class CommandOptions {

	private Map<String, String> options;

	public CommandOptions() {
		options = new HashMap<String, String>();
	}

	public void putOption(String key, String value) {
		options.put(key, value);
	}

	public void setOptions(Map<String, String> opt) {
		options.clear();
		options.putAll(opt);
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public boolean containsKey(String key) {
		return options.containsKey(key);
	}

	public String getOption(String key) {
		return options.get(key);
	}

    public void clear() {
        options.clear();
    }
}
