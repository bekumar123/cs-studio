
/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.jms2ora;

import java.io.File;
import java.io.PrintStream;

import javax.annotation.Nonnull;

import org.csstudio.alarm.jms2ora.management.GetNumberOfMessageFiles;
import org.csstudio.alarm.jms2ora.management.GetQueueSize;
import org.csstudio.alarm.jms2ora.management.InfoCmd;
import org.csstudio.alarm.jms2ora.management.Restart;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.util.CommandLine;
import org.csstudio.alarm.jms2ora.util.Hostname;
import org.csstudio.alarm.jms2ora.util.JmsSender;
import org.csstudio.headless.common.util.ApplicationInfo;
import org.csstudio.headless.common.xmpp.XmppCredentials;
import org.csstudio.headless.common.xmpp.XmppLoginException;
import org.csstudio.headless.common.xmpp.XmppSessionException;
import org.csstudio.headless.common.xmpp.XmppSessionHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The starting class.
 *
 * @author Markus Moeller
 *
 */

public class Jms2OraApplication implements IApplication, Stoppable, RemotelyAccesible {

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(Jms2OraApplication.class);

    /** Time to sleep in ms */
    private static long SLEEPING_TIME = 60000;

    /** Time to wait for the thread MessageProcessor in ms */
    private static final long WAITFORTHREAD = 60000;

    /** The MessageProcessor does all the work on messages */
    private MessageProcessor messageProcessor;

    /**  */
    private final Object lock;

    private XmppSessionHandler xmppSessionHandler;

    private ApplicationInfo appInfo;

    /** Flag that indicates whether or not the application is/should running */
    private boolean running;

    /** Flag that indicates whether or not the application should stop. */
    private boolean shutdown;

    public Jms2OraApplication() {
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppServer = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                                           PreferenceConstants.XMPP_SERVER,
                                           "krynfs.desy.de",
                                           null);
        String xmppUser = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                                          PreferenceConstants.XMPP_USER_NAME,
                                          "anonymous",
                                          null);
        String xmppPassword = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                                              PreferenceConstants.XMPP_PASSWORD,
                                              "anonymous",
                                              null);
        XmppCredentials credentials = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
        xmppSessionHandler = new XmppSessionHandler(Jms2OraActivator.getBundleContext(), credentials);
        lock = new Object();
        running = true;
        shutdown = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object start(@Nonnull final IApplicationContext context) throws Exception {

        IPreferencesService prefs = Platform.getPreferencesService();
        String desc = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                                      PreferenceConstants.DESCRIPTION,
                                      "I am a simple but happy application.", null);

        appInfo = new ApplicationInfo("Jms2Oracle", desc);

        File stdOut = new File("./stdout.txt");
        File stdErr = new File("./stderr.txt");

        long currentTime = System.currentTimeMillis();
        if (stdOut.exists()) {
            stdOut.renameTo(new File("./stdout-" + currentTime + ".txt"));
        }

        if (stdErr.exists()) {
            stdErr.renameTo(new File("./stderr-" + currentTime + ".txt"));
        }

        System.setOut(new PrintStream(new File("./stdout.txt")));
        System.setErr(new PrintStream(new File("./stderr.txt")));

        String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);

        /*
         *  Applikationsoptionen, um den Check zu starten
         *  -check -host krynfs -username archiver
         */
        CommandLine cmd = new CommandLine(args);
        if(cmd.exists("help") || cmd.exists("h") || cmd.exists("?")) {

            System.out.println("Usage: jms2ora [-check] [-stop] [-host <hostname>] [-username <username>] [-help | -h | -?]");
            System.out.println("       -check               - Checks if the application hangs using the XMPP command.");
            System.out.println("       -stop                - Stopps the application using the XMPP command.");
            System.out.println("       -host <hostname>     - Name of host where the application is running.");
            System.out.println("       -username <username> - Name of the user that is running the application.");
            System.out.println("       -help | -h | -?      - This help text.");

            return IApplication.EXIT_OK;
        }

        String host = null;
        String user = null;

        if(cmd.exists("stop")) {

            host = cmd.value("host", Hostname.getInstance().getHostname());
            user = cmd.value("username", "");

            final ApplicationStopper stopper = new ApplicationStopper();
            final boolean success = stopper.stopExternInstance("jms2oracle", host, user);

            if(success) {
                LOG.info("jms2ora stopped.");
            } else {
                LOG.error("jms2ora cannot be stopped.");
            }

            return IApplication.EXIT_OK;
        }

        if(cmd.exists("check")) {

            host = cmd.value("host", Hostname.getInstance().getHostname());
            user = cmd.value("username", "");

            final ApplicationChecker checker = new ApplicationChecker();

            try {
                final boolean success = checker.checkExternInstance("jms2oracle", host, user);
                if(success) {
                    LOG.info("jms2ora is working.");
                } else {
                    LOG.error("jms2ora is NOT working.");
                }
            } catch (XmppLoginException e) {
                LOG.error("[*** XmppLoginException ***]: {}", e.getMessage());
            }

            return IApplication.EXIT_OK;
        }

        long sleep = prefs.getLong(Jms2OraActivator.PLUGIN_ID,
                                   PreferenceConstants.MESSAGE_PROCESSOR_SLEEPING_TIME,
                                   30000L,
                                   null);

        int storageWait = prefs.getInt(Jms2OraActivator.PLUGIN_ID,
                                       PreferenceConstants.TIME_BETWEEN_STORAGE,
                                       60,
                                       null);

        boolean logStatistic = prefs.getBoolean(Jms2OraActivator.PLUGIN_ID,
                                                PreferenceConstants.LOG_STATISTIC,
                                                true,
                                                null);

        ThreadExceptionHandler.initialize(this);

        Restart.staticInject(this);
        GetQueueSize.staticInject(this);
        GetNumberOfMessageFiles.staticInject(this);
        InfoCmd.staticInject(this);

        try {
            xmppSessionHandler.connect();
        } catch (XmppSessionException e) {
            LOG.warn("Cannot connect to the XMPP server.");
        }

        messageProcessor = new MessageProcessor(sleep, storageWait, logStatistic);
        messageProcessor.start();

        context.applicationRunning();

        while (running) {
            synchronized (lock) {
                try {
                    lock.wait(SLEEPING_TIME);
                } catch(final InterruptedException ie) {
                    LOG.info("lock.wait() has been interrupted.");
                }
            }
            // Check XMPP connection
            if (xmppSessionHandler.isConnected()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("XMPP connection is working.");
                }
            } else {
                LOG.warn("XMPP connection is broken! Try to re-connect.");
                try {
                    xmppSessionHandler.reconnect();
                } catch (XmppSessionException e) {
                    LOG.warn("Cannot re-connect to the XMPP server.");
                }
            }
            // TODO: Check the worker...
            if (LOG.isDebugEnabled()) {
                LOG.debug("TODO: Check the worker...");
            }
        }

        if(messageProcessor != null) {

            // Clean stop of the working thread
            messageProcessor.stopWorking();

            int waitCount = 2;
            do {
                try {
                    LOG.info("Waiting for MessageProcessor.");
                    messageProcessor.join(WAITFORTHREAD);
                } catch(final InterruptedException ie) {
                    LOG.info("messageProcessor.join(WAITFORTHREAD) has been interrupted.");
                }
            } while (waitCount-- > 0 && !messageProcessor.stoppedClean());

            LOG.info("Restart/Exit: MessageProcessor stopped clean: {}", messageProcessor.stoppedClean());
        }

        xmppSessionHandler.disconnect();

        Integer exitCode;
        if (shutdown) {
            exitCode = IApplication.EXIT_OK;
            LOG.info("Stopping application.");
        } else {
            exitCode = IApplication.EXIT_RESTART;
            LOG.info("Restarting application.");
        }

        return exitCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopWorking(boolean restart) {
        running = false;
        shutdown = !restart;
        LOG.info("The application will shutdown...");
        synchronized(lock) {
            lock.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendStopNotification() {
        IPreferencesService prefs = Platform.getPreferencesService();
        String url = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                                     PreferenceConstants.JMS_PRODUCER_URL,
                                     "",
                                     null);
        JmsSender jmsSender = new JmsSender(url, "ALARM");
        if (jmsSender.isConnected()) {
            jmsSender.sendStopNotification();
        }
        jmsSender.closeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        running = false;
        shutdown = true;
        LOG.info("The application will shutdown...");
        synchronized(lock) {
            lock.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMessageQueueSize() {
        return messageProcessor.getCompleteQueueSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMessageFiles() {
        return messageProcessor.getNumberOfMessageFiles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return appInfo.toString();
    }
}
