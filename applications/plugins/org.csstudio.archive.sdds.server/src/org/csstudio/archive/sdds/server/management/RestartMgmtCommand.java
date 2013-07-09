
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.archive.sdds.server.management;

import javax.annotation.Nonnull;
import org.csstudio.archive.sdds.server.IRemotelyStoppable;
import org.csstudio.archive.sdds.server.SddsServerActivator;
import org.csstudio.headless.common.management.CommandResultPrefix;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;

/**
 * @author Markus Moeller
 *
 */
public class RestartMgmtCommand implements IManagementCommand {

    /** Static instance of the Application object. */
    private static IRemotelyStoppable RESTART_ME;

    /* (non-Javadoc)
     * @see org.csstudio.platform.management.IManagementCommand#execute(org.csstudio.platform.management.CommandParameters)
     */
    @Override
    @Nonnull
    public CommandResult execute(@Nonnull final CommandParameters parameters) {

        // The result of this method call.
        CommandResult result = null;

        if(RESTART_ME != null) {
            RESTART_ME.stopApplication(true);
            result = CommandResult.createMessageResult(CommandResultPrefix.getOkPrefix()
                                                       + " " +SddsServerActivator.PLUGIN_ID
                                                       + " is restarting now.");
        } else {
            result = CommandResult.createFailureResult("Do not have a valid reference to the Application object!");
        }

        return result;
    }

    /**
     * Sets the static Application object.
     *
     * @param o
     *
     */
    public static void injectStaticObject(@Nonnull final IRemotelyStoppable o) {
        RESTART_ME = o;
    }
}
