
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.application.xmlrpc.server;

import org.csstudio.application.xmlrpc.server.command.ServerRequestType;
import org.csstudio.application.xmlrpc.server.internal.PreferenceConstants;
import org.csstudio.application.xmlrpc.server.management.InfoCmd;
import org.csstudio.application.xmlrpc.server.management.MethodsCmd;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.headless.common.signal.HeadlessSignalHandler;
import org.csstudio.headless.common.signal.ISignalReceiver;
import org.csstudio.headless.common.signal.SignalException;
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
public class ServerApplication implements IApplication, RemotelyAccesible, ISignalReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    private MySqlXmlRpcServer xmlrpcServer;

    /** The ECF service */
    private XmppSessionHandler xmppHandler;

    private ApplicationInfo appInfo;

    private HeadlessSignalHandler signalHandler;

    private boolean running;

    public ServerApplication() {

        StandardStreams stdStreams = new StandardStreams("./log");
        stdStreams.redirectStreams();

        try {
            signalHandler = new HeadlessSignalHandler(this);
            signalHandler.activateIntSignal();
            signalHandler.activateTermSignal();
        } catch (SignalException e) {
            LOG.warn("[*** SignalException ***]: {}", e.getMessage());
        }

        IArchiveReaderFacade reader = null;
        try {
            reader = ServerActivator.getBundle().getArchiveEngineService();
            running = true;
        } catch (OsgiServiceUnavailableException e) {
            LOG.error("[*** OsgiServiceUnavailableException ***]: Archive reader service is not available: {}", e.getMessage());
            running = false;
        }

        IPreferencesService prefs = Platform.getPreferencesService();
        final String xmppUser = prefs.getString(ServerActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        final String xmppPassword = prefs.getString(ServerActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        final String xmppServer = prefs.getString(ServerActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);

        XmppCredentials xmppCredentials = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
        xmppHandler = new XmppSessionHandler(ServerActivator.getContext(), xmppCredentials, true);

        int port = prefs.getInt(ServerActivator.PLUGIN_ID,
                                PreferenceConstants.XML_RPC_SERVER_PORT,
                                8080,
                                null);

        xmlrpcServer = new MySqlXmlRpcServer(reader, port);

        String info = prefs.getString(ServerActivator.PLUGIN_ID,
                                      PreferenceConstants.INFO_TEXT,
                                      "I am a simple but happy application.", null);
        appInfo = new ApplicationInfo("XmlRpcServer for MySQL", info + " Used port: " + port);
    }

	/**
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
    public Object start(IApplicationContext context) throws Exception {

	    // -agentpath:"C:\Program Files (x86)\YourKit Java Profiler 11.0.9\bin\win32\yjpagent.dll"

        InfoCmd.staticInject(this);
        MethodsCmd.staticInject(this);
        try {
            xmppHandler.connect();
        } catch (XmppSessionException e) {
            LOG.warn("Cannot connect to the XMPP server: {}", e.getMessage());
        }

	    xmlrpcServer.start();
	    context.applicationRunning();

	    LOG.info("XmlRpcServer has been started and initialized.");

	    while (running) {
	        synchronized (this) {
	            try {
	                this.wait();
	            } catch (InterruptedException e) {
	                LOG.warn("INTERRUPTED");
	            }
	        }
	    }

	    if (xmlrpcServer.isAlive()) {
	        xmlrpcServer.interrupt();
	    }

	    xmppHandler.disconnect();

        LOG.info("MySQL-XmlRpc-Server is stopping.");

		return IApplication.EXIT_OK;
	}

	/**
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
    public void stop() {
		LOG.info("I have received the command to stop.");
		synchronized (this) {
		    running = false;
		    this.notify();
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return appInfo.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMethods() {
        StringBuffer methods = new StringBuffer("Request methods:\n\n");
        ServerRequestType[] srt = ServerRequestType.values();
        for (ServerRequestType o : srt) {
            methods.append(o.toString() + "  (" + o.getRequestTypeNumber() + ")\n");
        }
        return methods.toString().trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        synchronized (this) {
            running = false;
            this.notify();
        }
    }
}
