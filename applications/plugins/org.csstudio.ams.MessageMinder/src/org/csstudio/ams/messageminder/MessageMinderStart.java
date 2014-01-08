
/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: MessageMinderStart.java,v 1.11 2010/04/16 14:07:27 mmoeller Exp $
 */

package org.csstudio.ams.messageminder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import org.csstudio.ams.AMS;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.IRemotelyAccesible;
import org.csstudio.ams.Log;
import org.csstudio.ams.configReplicator.ConfigReplicator;
import org.csstudio.ams.configReplicator.ReplicationException;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.ams.messageminder.management.InfoCmd;
import org.csstudio.ams.messageminder.preference.MessageMinderPreferenceKey;
import org.csstudio.headless.common.util.ApplicationInfo;
import org.csstudio.headless.common.util.StandardStreams;
import org.csstudio.headless.common.xmpp.XmppCredentials;
import org.csstudio.headless.common.xmpp.XmppSessionHandler;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Bundle;

/**
 * @author hrickens
 * @author $Author: mmoeller $
 * @version $Revision: 1.11 $
 * @since 01.11.2007
 */
public final class MessageMinderStart implements IApplication, IRemotelyAccesible {

    public final static boolean CREATE_DURABLE = true;

    private final static long LOOP_WAIT_TIME = 10000L;

    private static MessageMinderStart _instance;

    private MessageGuardCommander _commander;
    private XmppSessionHandler xmppService;
    private ApplicationInfo appInfo;
    private String managementPassword;
    private boolean _restart = false;

    public MessageMinderStart() {
        IPreferencesService pref = Platform.getPreferencesService();
        managementPassword = pref.getString(AmsActivator.PLUGIN_ID,
                                            AmsPreferenceKey.P_AMS_MANAGEMENT_PASSWORD,
                                            "",
                                            null);
        if(managementPassword == null) {
            managementPassword = "";
        }
        String xmppServer = pref.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_SERVER, "krynfs.desy.de", null);
        String xmppUser = pref.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_USER_NAME, "anonymous", null);
        String xmppPassword = pref.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_PASSWORD, "anonymous", null);
        XmppCredentials credentials = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
        xmppService = new XmppSessionHandler(MessageMinderActivator.getBundleContext(), credentials, true);
        String desc = pref.getString(MessageMinderActivator.PLUGIN_ID,
                                     MessageMinderPreferenceKey.P_DESCRIPTION,
                                     "I am a simple but happy application.",
                                     null);
        appInfo = new ApplicationInfo("AMS", AMS.AMS_MAIN_VERSION, "AmsMessageMinder", desc);
    }

    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
    public Object start(IApplicationContext context) throws Exception {

        Log.log(this, Log.INFO, "MessageMinder started...");
        _instance = this;

        MessageMinderPreferenceKey.showPreferences();

        StandardStreams stdStreams = new StandardStreams("./log");
        stdStreams.redirectStreams();

        InfoCmd.staticInject(this);
        xmppService.connect();

        // 2013-06-13: The in-memory cache database cannot be used at the moment,
        //             because it has to be reloaded after a synchronization!!!!

        boolean useCacheDb = false;
//        try {
//            Log.log(this, Log.INFO, "Try to create the cache database.");
//            createCacheDatabase();
//            useCacheDb = true;
//        } catch (ReplicationException e) {
//            Log.log(this, Log.WARN, "[*** ReplicationException ***]: " + e.getMessage());
//            Log.log(this, Log.WARN, "Using application database!!!");
//        }

        _commander = new MessageGuardCommander("MessageMinder", useCacheDb);
        _commander.schedule();

        while (_commander.getState() != Job.NONE){
            Log.log(this, Log.INFO, "Commander state = " + String.valueOf(_commander.getState()));
            Thread.sleep(LOOP_WAIT_TIME);
        }

        _commander.cancel();
        xmppService.disconnect();

        Integer exitCode = IApplication.EXIT_OK;
        if(_restart){
            exitCode = IApplication.EXIT_RESTART;
        }

        return exitCode;
    }

    @SuppressWarnings("unused")
    private void createCacheDatabase() throws ReplicationException {

        Connection localDbCon = null;
        Connection cacheDbCon = null;

        try {
            localDbCon = AmsConnectionFactory.getApplicationDB();
            cacheDbCon = AmsConnectionFactory.getMemoryCacheDB();

            // Get the path for the SQL script
            Bundle amsBundle = Platform.getBundle(AmsActivator.PLUGIN_ID);
            Enumeration<URL> entries = amsBundle.findEntries("resource", "createMemoryCache.sql", true);
            File sqlFile = null;
            if (entries.hasMoreElements()) {

                try {
                    URL fileUrl = FileLocator.toFileURL(entries.nextElement());
                    Log.log(Log.DEBUG, fileUrl.toString());
                    try {
                        URI fileUri = fileUrl.toURI();
                        sqlFile = new File(fileUri);
                    } catch (URISyntaxException uriException) {
                        sqlFile = new File(fileUrl.getPath());
                    }
                } catch (IOException e) {
                    throw new ReplicationException(e);
                }
            }

            // Create the cache db
            ConfigReplicator.createMemoryCacheDb(cacheDbCon, sqlFile);
            ConfigReplicator.replicateConfigurationToHsql(localDbCon, cacheDbCon);
        } catch (SQLException e) {
            throw new ReplicationException(e);
        } finally {
            AmsConnectionFactory.closeConnection(cacheDbCon);
            AmsConnectionFactory.closeConnection(localDbCon);
        }
    }

    /**
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
        // Do nothing here
    }

    public boolean isRestart() {
        return _restart;
    }

    public synchronized void setRestart() {
        _restart = true;
        setRun(false);
    }


    public synchronized void setRun(boolean run) {
        if (_commander != null){
            _commander.setRun(run);
        }
    }

    /**
     *
     * @return The password for remote management
     */
    public synchronized String getPassword() {
        return managementPassword;
    }

    public static MessageMinderStart getInstance() {
        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return appInfo.toString();
    }
}
