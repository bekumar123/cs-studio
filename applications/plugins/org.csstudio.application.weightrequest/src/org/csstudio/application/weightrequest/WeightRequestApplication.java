
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.application.weightrequest;

import org.csstudio.application.weightrequest.internal.PreferenceConstants;
import org.csstudio.application.weightrequest.management.InfoCmd;
import org.csstudio.application.weightrequest.server.CaServer;
import org.csstudio.headless.common.management.IInfoProvider;
import org.csstudio.headless.common.util.ApplicationInfo;
import org.csstudio.headless.common.util.StandardStreams;
import org.csstudio.headless.common.xmpp.XmppCredentials;
import org.csstudio.headless.common.xmpp.XmppSessionException;
import org.csstudio.headless.common.xmpp.XmppSessionHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class WeightRequestApplication implements IApplication, IInfoProvider {

    private static Logger LOG = LoggerFactory.getLogger(WeightRequestApplication.class);

    private CaServer caServer;

    private XmppSessionHandler xmppSessionHandler;

    private ApplicationInfo appInfo;


    public WeightRequestApplication() {
        this.caServer = new CaServer();
        IPreferencesService pref = Platform.getPreferencesService();
        String xmppServer = pref.getString(Activator.PLUGIN_ID,
                                           PreferenceConstants.XMPP_SERVER,
                                           "krynfs.desy.de",
                                           null);
        String xmppUser = pref.getString(Activator.PLUGIN_ID,
                                         PreferenceConstants.XMPP_USER,
                                         "anonymous",
                                         null);
        String xmppPassword = pref.getString(Activator.PLUGIN_ID,
                                             PreferenceConstants.XMPP_PASSWORD,
                                             "anonymous",
                                             null);
        XmppCredentials credentials = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
        xmppSessionHandler = new XmppSessionHandler(Activator.getBundleContext(), credentials);
        String desc = pref.getString(Activator.PLUGIN_ID,
                                     PreferenceConstants.DESCRIPTION,
                                     "",
                                     null);
        appInfo = new ApplicationInfo("WeightRequest", desc);
    }

    @Override
    public Object start(IApplicationContext context) throws Exception {

        LOG.info("Starting WeightRequest application.");

        StandardStreams stdStreams = new StandardStreams("./log");
        stdStreams.redirectStreams();

        InfoCmd.staticInject(this);
        try {
            xmppSessionHandler.connect();
        } catch (XmppSessionException e) {
            LOG.warn("Cannot connect to the XMPP server.");
        }

        context.applicationRunning();
        this.caServer.run();

        LOG.info("Stopping WeightRequest application.");

        xmppSessionHandler.disconnect();

        return IApplication.EXIT_OK;
    }

    @Override
    public void stop() {
      synchronized (this.caServer) {
        this.caServer.stop();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return appInfo.toString();
    }
}
