
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

import org.csstudio.application.xmlrpc.server.internal.PreferenceConstants;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class ServerApplication implements IApplication,
                                          IGenericServiceListener<ISessionService> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    private MySqlXmlRpcServer xmlrpcServer;
    
    /** The ECF service */
    private ISessionService xmppService;

    /** Object that holds the credentials for XMPP login */
    private XmppInfo xmppInfo;

    private boolean running;
    
    public ServerApplication() {
        IArchiveReaderFacade reader = null;
        try {
            reader = ServerActivator.getBundle().getArchiveEngineService();
            running = true;
        } catch (OsgiServiceUnavailableException e) {
            LOG.error("[*** OsgiServiceUnavailableException ***]: Archive reader service is not available: {}", e.getMessage());
            running = false;
        }
        xmlrpcServer = new MySqlXmlRpcServer(reader, 8080);
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
    public Object start(IApplicationContext context) throws Exception {
		
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String xmppUser = prefs.getString(ServerActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        final String xmppPassword = prefs.getString(ServerActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        final String xmppServer = prefs.getString(ServerActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);

        xmppInfo = new XmppInfo(xmppServer, xmppUser, xmppPassword);
        
        ServerActivator.getBundle().addSessionServiceListener(this);
	    xmlrpcServer.start();
	    context.applicationRunning();
	    
	    LOG.info("XmlRpcServer has been started and initialized.");
	    
	    if (running) {
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
	    
        if (xmppService != null) {
            synchronized (xmppService) {
                try {
                    xmppService.wait(500);
                } catch (InterruptedException ie) {
                    LOG.info("xmppService.wait(500) has been interrupted.");
                }
            }
            xmppService.disconnect();
        }

        LOG.info("MySQL-XmlRpc-Server is stopping.");

		return IApplication.EXIT_OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
    public void stop() {
		LOG.info("I have received the command to stop.");
		synchronized (this) {
		    this.notify();
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindService(ISessionService service) {
        if (xmppInfo == null) {
            LOG.warn("XMPP credentials are not available.");
            return;
        }
        try {
            service.connect(xmppInfo.getXmppUser(), xmppInfo.getXmppPassword(), xmppInfo.getXmppServer());
            xmppService = service;
        } catch (Exception e) {
            LOG.warn("XMPP connection is not available: {}", e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindService(ISessionService service) {
        //  Nothing to do
    }
}
