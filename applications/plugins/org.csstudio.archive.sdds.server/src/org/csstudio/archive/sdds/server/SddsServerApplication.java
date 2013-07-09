
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

package org.csstudio.archive.sdds.server;

import javax.annotation.Nonnull;
import org.csstudio.archive.sdds.server.internal.ServerPreferenceKey;
import org.csstudio.archive.sdds.server.io.SddsServer;
import org.csstudio.archive.sdds.server.io.ServerException;
import org.csstudio.archive.sdds.server.management.InfoCmd;
import org.csstudio.archive.sdds.server.management.RestartMgmtCommand;
import org.csstudio.archive.sdds.server.management.StopMgmtCommand;
import org.csstudio.headless.common.management.IInfoProvider;
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
 * @author Markus Moeller
 *
 */
public class SddsServerApplication implements IApplication,
                                              IRemotelyStoppable,
                                              IInfoProvider,
                                              ISignalReceiver {

    /** The logger of this class */
    private static final Logger LOG = LoggerFactory.getLogger(SddsServerApplication.class);

    /** The instance of the server */
    private SddsServer server;

    /** Session service for the XMPP login */
    private final XmppSessionHandler xmppService;

    private final ApplicationInfo appInfo;

    /** Help object for synchronization purposes */
    private final Object lock;

    /** Flag that indicates if the server is running */
    private boolean running;

    /** Flag that indicates if the server has to be restarted */
    private boolean restart;

    /**
     * The standard constructor
     */
    public SddsServerApplication() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String xmppServer = prefs.getString(SddsServerActivator.PLUGIN_ID,
                                                  ServerPreferenceKey.P_XMPP_SERVER,
                                                  "krynfs.desy.de", null);
        final String xmppUser = prefs.getString(SddsServerActivator.PLUGIN_ID,
                                                ServerPreferenceKey.P_XMPP_USER,
                                                "sdds-server", null);
        final String xmppPassword = prefs.getString(SddsServerActivator.PLUGIN_ID,
                                                    ServerPreferenceKey.P_XMPP_PASSWORD,
                                                    "sdds-server", null);
        final XmppCredentials cred = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
        xmppService = new XmppSessionHandler(SddsServerActivator.getBundleContext(), cred, true);
        final String desc =  prefs.getString(SddsServerActivator.PLUGIN_ID,
                                       ServerPreferenceKey.P_DESCRIPTION,
                                       "Not available", null);
        appInfo = new ApplicationInfo("SDDS-Server", desc);
        lock = new Object();
        running = true;
        restart = false;
    }

    @Override
    @Nonnull
    public Object start(@Nonnull final IApplicationContext context) throws Exception {

        LOG.info("Starting {}", SddsServerActivator.PLUGIN_ID);

        final StandardStreams stdStreams = new StandardStreams("./log");
        stdStreams.redirectStreams();

        final IPreferencesService pref = Platform.getPreferencesService();
        final int serverPort = pref.getInt(SddsServerActivator.PLUGIN_ID,
                                           ServerPreferenceKey.P_SERVER_PORT,
                                           4056,
                                           null);
        LOG.info("The server uses port {}", serverPort);

        try {
            final HeadlessSignalHandler signalHandler = new HeadlessSignalHandler(this);
            signalHandler.activateIntSignal();
            signalHandler.activateTermSignal();
        } catch (final SignalException e) {
            LOG.warn("Cannot create the signal handler. The application ignore any signal.");
        }

        StopMgmtCommand.injectStaticObject(this);
        RestartMgmtCommand.injectStaticObject(this);
        InfoCmd.injectStaticObject(this);

        try {
            xmppService.connect();
        } catch (final XmppSessionException e) {
            LOG.warn("Cannot connect to the XMPP server.");
        }

        try {
            server = new SddsServer(serverPort);
            server.start();
        } catch(final ServerException se) {
            LOG.error("Cannot create an instance of the SddsServer class: {}", se.getMessage());
            LOG.error("Stopping application!");
            running = false;
            restart = false;
        }

        context.applicationRunning();

        while (running) {
            synchronized(lock) {
                try {
                    lock.wait();
                } catch(final InterruptedException ie) {
                    LOG.warn("Interrupted");
                }
            }
        }

        if (server != null) {
            server.stopServer();
        }

        if (xmppService != null) {
            xmppService.disconnect();
            LOG.info("XMPP connection disconnected.");
        }

        Integer exitCode = IApplication.EXIT_OK;
        if (restart) {
            LOG.info("Restarting {}", SddsServerActivator.PLUGIN_ID);
            exitCode = IApplication.EXIT_RESTART;
        }

        return exitCode;
    }

    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
        // Nothing to do here
    }

    /**
     *
     * @param setRestart
     */
    @Override
    public void stopApplication(final boolean setRestart) {
        this.running = false;
        this.restart = setRestart;
        synchronized(lock) {
            lock.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        stopApplication(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return appInfo.toString();
    }

    public void nirvana() {
        //sddsReader.readDataPortionSimple("HQCO7L~B", null, -1, startTime, endTime, (short)1, -1, null);
//      running = true;
//      while(running)
//      {
//          synchronized(this)
//          {
//              EpicsRecordData[] data = sddsReader.readData("CMTBVA3V112_ai", 1249120800L, 1249120860L);
//
//              if(data != null)
//              {
//                  logger.info("Anzahl: " + data.length);
//
//                  for(EpicsRecordData p : data)
//                  {
//                      System.out.println(p);
//                  }
//              }
//
//              this.wait(1000);
//          }
//
//          running = false;
//      }

//      URL url = new URL(null, "sdds://krynfs.desy.de:4000", new SddsStreamHandler());
//      System.out.println(url.getProtocol());


//      startTime = TimeConverter.convertToLong("2009-01-10 12:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//      endTime = TimeConverter.convertToLong("2009-01-10 12:10:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//
//      EpicsRecordData[] data = sddsReader.readData("krykWeather_Temp_ai", startTime, endTime);
//      System.out.println("Anzahl der Datenwerte: " + data.length);
//
//      EpicsRecordData erd = data[0];
//
//      System.out.println(erd.getTime());
//
//      TimeInterval ti = new TimeInterval(startTime, endTime);
//      System.out.println("Start month: " + ti.getStartMonthAsString());
//      System.out.println("End month:   " + ti.getEndMonthAsString());
//
//      int[] years = ti.getYears();
//      for(int i : years)
//      {
//          System.out.println(i);
//      }
    }
}
