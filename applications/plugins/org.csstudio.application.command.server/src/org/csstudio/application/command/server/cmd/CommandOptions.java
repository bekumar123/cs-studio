
package org.csstudio.application.command.server.cmd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandOptions {

    private Map<String, String> options;

    public CommandOptions() {
        options = new HashMap<String, String>();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        Iterator<String> keys = options.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            result.append(key + " = " + options.get(key) + "\n");
        }
        return result.toString().trim();
    }

    public String toString(int intend) {
        StringBuffer result = new StringBuffer();
        String spaces = new String(new char[intend]).replace("\0", " ");
        Iterator<String> keys = options.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            result.append(spaces + key + " = " + options.get(key) + "\n");
        }
        return result.toString();
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

    public boolean isEmpty() {
        return options.isEmpty();
    }

    public void clear() {
        options.clear();
    }
}
