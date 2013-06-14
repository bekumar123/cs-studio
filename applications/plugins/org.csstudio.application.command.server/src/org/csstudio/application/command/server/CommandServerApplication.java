
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

package org.csstudio.application.command.server;

import org.csstudio.application.command.server.jms.MessageAcceptor;
import org.csstudio.application.command.server.management.InfoCmd;
import org.csstudio.application.command.server.management.StopCmd;
import org.csstudio.application.command.server.preferences.CommandServerPreferences;
import org.csstudio.headless.common.management.IInfoProvider;
import org.csstudio.headless.common.management.Stoppable;
import org.csstudio.headless.common.util.ApplicationInfo;
import org.csstudio.headless.common.xmpp.XmppCredentials;
import org.csstudio.headless.common.xmpp.XmppSessionException;
import org.csstudio.headless.common.xmpp.XmppSessionHandler;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class CommandServerApplication implements IApplication, IInfoProvider, Stoppable {

    private static final Logger LOG = LoggerFactory.getLogger(CommandServerApplication.class);

    private XmppSessionHandler xmppSession;

    private ApplicationInfo appInfo;

    private MessageAcceptor msgAcceptor;

    private Object lock;

    private boolean running;

    private boolean restart;

    public CommandServerApplication() {
        String desc = CommandServerPreferences.DESCRIPTION.getValue();
        appInfo = new ApplicationInfo("Command Server", desc);
        String xmppServer = CommandServerPreferences.XMPP_SERVER.getValue();
        String xmppUser = CommandServerPreferences.XMPP_USER.getValue();
        String xmppPassword = CommandServerPreferences.XMPP_PASSWORD.getValue();
        XmppCredentials cred = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
        xmppSession = new XmppSessionHandler(ServerActivator.getContext(), cred);
        String[] jmsUrls = CommandServerPreferences.JMS_CONSUMER_URLS.getValue().split(",");
        SharedJmsConnections.staticInjectConsumerUrlAndClientId(jmsUrls[0], jmsUrls[1], "ServerCommandConsumer");
        SharedJmsConnections.staticInjectPublisherUrlAndClientId(CommandServerPreferences.JMS_PUBLISHER_URL.getValue(),
                                                                 "ServerCommandPublisher");
        msgAcceptor = new MessageAcceptor(CommandServerPreferences.JMS_TOPIC.getValue());
        lock = new Object();
        running = true;
        restart = false;
    }

	/**
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
    public Object start(IApplicationContext context) throws Exception {

	    StopCmd.staticInject(this);
	    InfoCmd.staticInject(this);
        try {
            xmppSession.connect();
        } catch (XmppSessionException e) {
            LOG.warn("Cannot connect to the XMPP server.");
        }
        xmppSession.startWatchdog(4000L);

        msgAcceptor.start();
        context.applicationRunning();

        synchronized (lock) {
            while (running) {
                try {
                    lock.wait(5000);
                } catch (InterruptedException e) {
                    LOG.warn("I've been interrupted.");
                }
            }
        }

        msgAcceptor.stopThread();
        xmppSession.disconnect();

        Integer exitCode = IApplication.EXIT_OK;
        if (restart) {
            exitCode = IApplication.EXIT_RESTART;
        }

	    return exitCode;
	}

	/**
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
    public void stop() {
		// nothing to do
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
    public void stopApplication() {
        running = false;
        restart = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restartApplication() {
        running = false;
        restart = true;
        synchronized (lock) {
            lock.notify();
        }
    }
}
