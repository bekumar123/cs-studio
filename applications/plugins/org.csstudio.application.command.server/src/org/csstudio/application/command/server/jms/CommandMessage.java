
/*
 * Copyright (c) 2013 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.application.command.server.jms;

import java.util.Map;

/**
 * @author mmoeller
 * @since 18.06.2013
 */
public class CommandMessage {

    private String cmdLine;

    /**
     * The property COMMAND or NAME may contain the command line.
     *
     * @param msgContent
     */
    public CommandMessage(Map<String, String> msgContent) {
        cmdLine = "";
        if (msgContent != null) {
            if (msgContent.containsKey(CmdMessageProperties.TYPE.toString())) {
                String value = msgContent.get(CmdMessageProperties.TYPE.toString()).trim();
                if (value.compareToIgnoreCase("command") == 0) {
                    if (msgContent.containsKey(CmdMessageProperties.COMMAND.toString())) {
                        cmdLine = msgContent.get(CmdMessageProperties.COMMAND.toString()).trim();
                    } else if (msgContent.containsKey(CmdMessageProperties.NAME.toString())) {
                        cmdLine = msgContent.get(CmdMessageProperties.NAME.toString()).trim();
                    }
                }
            }
        }
    }

    public boolean isCommandMessage() {
        return !cmdLine.isEmpty();
    }

    public String getCommandLine() {
        return cmdLine;
    }
}
