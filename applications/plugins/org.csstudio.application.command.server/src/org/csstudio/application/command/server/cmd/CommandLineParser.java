
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author mmoeller
 * @since 15.01.2014
 */
public class CommandLineParser {

    private static final Pattern CMD_NAME_PATTERN = Pattern.compile("\\A[a-zA-Z]+");

    private static final Pattern CMD_NAME_OPTIONS = Pattern.compile("\\A[a-zA-Z]+\\(.*\\)\\:?");

    private static final Pattern CMD_OPTIONS = Pattern.compile("\\(.*\\)");

    private static final Pattern CMD_PARAMETERS = Pattern.compile("[^\"\\s]+|\"(\\\\.|[^\\\\\"])*\"");

    public static CommandElements parse(String cmdLine) {
        CommandElements elements = new CommandElements();
        if (cmdLine != null) {
            String rawCommand = cmdLine.trim();
            Matcher mNameOptions = CMD_NAME_OPTIONS.matcher(cmdLine);
            if (mNameOptions.find()) {
                String nameOptions = mNameOptions.group();
                String params = rawCommand.replace(nameOptions, "");
                Matcher mParams = CMD_PARAMETERS.matcher(params);
                while (mParams.find()) {
                    elements.addCommandParameter(mParams.group());
                }
                Matcher mOptions = CMD_OPTIONS.matcher(nameOptions);
                if (mOptions.find()) {
                    String options = mOptions.group().replace("(", "").replace(")", "");
                    String[] parts = options.split(";");
                    for (String o : parts) {
                        String[] op = o.split("=");
                        if (op.length == 2) {
                            elements.putCommandOption(op[0].trim(), op[1].trim());
                        }
                    }
                }
            }
        }
        return elements;
    }

    public static String parseCommandName(String cmdStr) {
        String result = null;
        if (cmdStr != null) {
            String temp = cmdStr.trim();
            if (!temp.isEmpty()) {
                Matcher m = CMD_NAME_PATTERN.matcher(cmdStr);
                if (m.find()) {
                    result = m.group();
                }
            }
        }
        return result;
    }
}
