
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

package org.csstudio.headless.common.xmpp;

import java.util.Observable;
import java.util.Observer;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 19.03.2013
 */
public class XmppSessionHandler implements Observer, IGenericServiceListener<ISessionService> {

    private static final Logger LOG = LoggerFactory.getLogger(XmppSessionHandler.class);

    class XmppWatchdog extends Observable implements Runnable {

        /** Default value for check interval is 5 minute. */
        public static final long DEFAULT_CHECK_INTERVAL = 300000L;

        private long checkInterval;

        private boolean working;

        public XmppWatchdog(long interval) {
            checkInterval = interval;
            working = false;

        }

        public XmppWatchdog() {
            this(DEFAULT_CHECK_INTERVAL);
        }

        public synchronized void stopThread() {
            working = false;
            this.notify();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            working = true;
            while (working) {
                synchronized (this) {
                    try {
                        this.wait(checkInterval);
                    } catch (InterruptedException e) {
                        // Ignore Me
                    }
                    if (!isConnected()) {
                        setChanged();
                        notifyObservers();
                    }
                }
            }
        }
    }

    /** Service tracker for the XMPP login */
    private GenericServiceTracker<ISessionService> _genericServiceTracker;

    /** The ECF service */
    private ISessionService xmppService;

    private XmppCredentials xmppCredentials;

    private XmppWatchdog watchdog;

    private Thread watchdogThread;

    private boolean startWatchDog;

    public XmppSessionHandler(BundleContext context,
                              XmppCredentials credentials,
                              boolean startWatchdog,
                              long checkInterval) {
        watchdog = new XmppWatchdog(checkInterval);
        watchdogThread = null;
        xmppCredentials = credentials;
        _genericServiceTracker = new GenericServiceTracker<ISessionService>(
                context, ISessionService.class);
        _genericServiceTracker.open();
        startWatchDog = startWatchdog;
    }

    public XmppSessionHandler(BundleContext context, XmppCredentials credentials, boolean startWatchdog) {
        this(context, credentials, startWatchdog, XmppWatchdog.DEFAULT_CHECK_INTERVAL);
    }

    public XmppSessionHandler(BundleContext context, XmppCredentials credentials) {
        this(context, credentials, false, XmppWatchdog.DEFAULT_CHECK_INTERVAL);
    }

    public void connect() throws XmppSessionException {
        if (_genericServiceTracker == null) {
            throw new XmppSessionException("Service tracker must not be null!");
        }
        _genericServiceTracker.addServiceListener(this);
        if (startWatchDog) {
            startWatchdog();
        }
        synchronized (this) {
            try {
                this.wait(2000L);
            } catch (InterruptedException e) {
                // Ignore Me
            }
        }
        // Throw the exception AFTER starting the watchdog
        if (!isConnected()) {
            throw new XmppSessionException("XMPP connection failed.");
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        if (xmppService != null) {
            connected = xmppService.getConnectedID() != null;
        }
        return connected;
    }

    public void disconnect() {
        if (xmppService != null) {
            synchronized (xmppService) {
                try {
                    xmppService.wait(500);
                } catch (InterruptedException ie) {
                    LOG.warn("XMPP service waited and was interrupted.");
                }
            }
            if (watchdogThread != null) {
                stopWatchdog();
            }
            xmppService.disconnect();
            LOG.info("XMPP disconnected.");
        }
    }

    public void reconnect() throws XmppSessionException {
        disconnect();
        connect();
    }

    public ISessionService getXmppSessionService() {
        return xmppService;
    }

    private void startWatchdog() {
        if (watchdogThread == null) {
            watchdog.addObserver(this);
            watchdogThread = new Thread(watchdog, "XMPP Watchdog");
            watchdogThread.start();
        }
    }

    private void stopWatchdog() {
        if (watchdogThread != null) {
            watchdog.stopThread();
            watchdog.deleteObservers();
            try {
                watchdogThread.join(10000L);
                LOG.info("Watchdog Thread has been stopped.");
            } catch (InterruptedException e) {
                // Ignore Me
            } finally {
                watchdogThread = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable o, Object arg) {
        try {
            reconnect();
            LOG.info("Reconnected to the XMPP server.");
        } catch (XmppSessionException e) {
            LOG.warn("Cannot reconnect to the XMPP server.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindService(ISessionService service) {
        String xmppServer = xmppCredentials.getXmppServer();
        String xmppUser = xmppCredentials.getXmppUser();
        String xmppPassword = xmppCredentials.getXmppPassword();
        try {
            service.connect(xmppUser, xmppPassword, xmppServer);
            xmppService = service;
        } catch (final Exception e) {
            LOG.warn("XMPP connection is not available: {}", e.toString());
            xmppService = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindService(ISessionService service) {
        LOG.warn("Unbinding XMPP service.");
    }
}
