
/*
 * Copyright (c) 2014 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.application.command.server.cmd;


/**
 * @author mmoeller
 * @since 15.01.2014
 */
public class CommandElements {

    private CommandOptions cmdOptions;

    private CommandParameters cmdParameters;

    public CommandElements() {
        cmdOptions = new CommandOptions();
        cmdParameters = new CommandParameters();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("CommandElements {\n\nOptions:\n");
        result.append(cmdOptions.toString() + "\n\nParameters:\n");
        result.append(cmdParameters.toString() + "\n}");
        return result.toString();
    }

    public String toString(int intend) {
        StringBuffer result = new StringBuffer("CommandElements {\n");
        String spaces = new String(new char[intend]).replace("\0", " ");
        if (!cmdOptions.isEmpty()) {
            result.append(spaces + "Options:\n");
            result.append(cmdOptions.toString(intend + 1));
        }
        if (!cmdParameters.isEmpty()) {
            result.append(spaces + "Parameters:\n");
            result.append(cmdParameters.toString(intend + 1) + "\n");
        }
        result.append("}");
        return result.toString();
    }

    public boolean isEmpty() {
        return cmdOptions.isEmpty() && cmdParameters.isEmpty();
    }

    public void setCommandOptions(CommandOptions o) {
        cmdOptions.setOptions(o.getOptions());
    }

    public void putCommandOption(String name, String value) {
        cmdOptions.putOption(name, value);
    }

    public CommandOptions getCommandOptions() {
        return cmdOptions;
    }

    public void clearCommandOptions() {
        cmdOptions.clear();
    }

    public void addCommandParameter(String o) {
        cmdParameters.addParameter(o);
    }

    public void setCommandParameters(CommandParameters o) {
        cmdParameters.setParameters(o.getAllParameters());
    }

    public CommandParameters getCommandParameters() {
        return cmdParameters;
    }

    public void clearCommandParameters() {
        cmdParameters.clear();
    }
}
