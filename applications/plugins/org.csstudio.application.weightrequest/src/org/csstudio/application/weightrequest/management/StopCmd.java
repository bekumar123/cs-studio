
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.application.weightrequest.management;

import java.util.Arrays;
import java.util.List;
import org.csstudio.application.weightrequest.Activator;
import org.csstudio.headless.common.management.CommandResultPrefix;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author mmoeller
 * @version 1.0
 * @since 01.12.2011
 */
public class StopCmd implements IManagementCommand {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public CommandResult execute(CommandParameters parameters) {

        CommandResult result = null;
        ApplicationHandle thisHandle = null;
        BundleContext bundleContext = Activator.getBundleContext();

        String serviceFilter = "(&(objectClass=" +
                ApplicationHandle.class.getName() + ")" +
                "(application.descriptor=" + "org.csstudio.application.weightrequest" + "*))";

        ServiceTracker<?, ?> tracker = null;
        try {
            tracker = new ServiceTracker(bundleContext,
                                         bundleContext.createFilter(serviceFilter),
                                         null);
            tracker.open();

            Object[] allServices = tracker.getServices();
            if (allServices != null) {
                List<Object> services = Arrays.asList(allServices);
                ApplicationHandle[] regApps = services.toArray(new ApplicationHandle[0]);

                for (ApplicationHandle o : regApps) {
                    if (o.getInstanceId().contains("WeightrequestApplication")) {
                        thisHandle = o;
                        break;
                    }
                }
            } else {
                result = CommandResult.createFailureResult(
                                         CommandResultPrefix.getErrorPrefix(1)
                                         + " Cannot get the application entry from the service.");
            }

            tracker.close();
        } catch (InvalidSyntaxException e) {
            result = CommandResult.createFailureResult(e.getMessage());
        }

        if (thisHandle != null) {
            result = CommandResult.createMessageResult(CommandResultPrefix.getOkPrefix()
                                                       + " Stopping WeightRequestApplication...");
            thisHandle.destroy();
        } else {
            result = CommandResult.createFailureResult(
                                          CommandResultPrefix.getErrorPrefix(1)
                                          + " Cannot get the application entry from the service.");
        }

        return result;
    }
}
