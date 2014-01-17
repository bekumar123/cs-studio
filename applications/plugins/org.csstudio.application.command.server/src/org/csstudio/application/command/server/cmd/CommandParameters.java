
package org.csstudio.application.command.server.cmd;

import java.util.ArrayList;
import java.util.List;

public class CommandParameters {

    private List<String> parameters;

    public CommandParameters() {
        parameters = new ArrayList<String>();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (String s : parameters) {
            result.append(s + " ");
        }
        return result.toString().trim();
    }

    public String toString(int intend) {
        String spaces = new String(new char[intend]).replace("\0", " ");
        StringBuffer result = new StringBuffer();
        result.append(spaces);
        for (String s : parameters) {
            result.append(s + " ");
        }
        return result.toString();
    }

    public void setParameters(List<String> params) {
        parameters.clear();
        parameters.addAll(params);
    }

    public void addParameter(String param) {
        parameters.add(param);
    }

    public List<String> getAllParameters() {
        return parameters;
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    public void clear() {
        parameters.clear();
    }
}
