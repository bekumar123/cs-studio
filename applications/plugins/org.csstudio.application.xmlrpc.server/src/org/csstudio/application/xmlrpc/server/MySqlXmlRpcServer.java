
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

import java.io.IOException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.csstudio.application.xmlrpc.server.epics.MetaDataCollection;
import org.csstudio.application.xmlrpc.server.internal.PreferenceConstants;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 21.12.2012
 */
public class MySqlXmlRpcServer extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(MySqlXmlRpcServer.class);

    private IArchiveReaderFacade archiveReader;

    private int serverPort;

    public MySqlXmlRpcServer(IArchiveReaderFacade reader, int port) {
        this.setName("MySql-XmlRpc-Server Thread");
        archiveReader = reader;
        serverPort = port;
    }

    @Override
    public void run() {

        if (archiveReader == null) {
            LOG.error("{} cannot work without archive reader service. LEAVING!", this.getName());
            return;
        }

        LOG.info("{} is running.", this.getName());

        IPreferencesService prefs = Platform.getPreferencesService();
        String metaDataPath = prefs.getString(ServerActivator.PLUGIN_ID,
                                              PreferenceConstants.CHANNEL_META_DATA_PATH,
                                              "./channels.xml",
                                              null);
        MetaDataCollection.createInstance(metaDataPath);
        int key = prefs.getInt(ServerActivator.PLUGIN_ID,
                               PreferenceConstants.ARCHIVE_KEY,
                               0,
                               null);
        String name = prefs.getString(ServerActivator.PLUGIN_ID,
                                      PreferenceConstants.ARCHIVE_NAME,
                                      "NONE", null);
        String path = prefs.getString(ServerActivator.PLUGIN_ID,
                                      PreferenceConstants.ARCHIVE_PATH,
                                      "NONE", null);
        boolean askCtrlSystem = prefs.getBoolean(ServerActivator.PLUGIN_ID,
                                                 PreferenceConstants.ASK_CONTROLSYSTEM_FOR_META,
                                                 false,
                                                 null);
        ServerInfo info = new ServerInfo(key, name, path, askCtrlSystem);

        ArchiverRequestProcessorFactoryFactory arpff =
                new ArchiverRequestProcessorFactoryFactory(archiveReader, info);
        WebServer webServer = new WebServer(serverPort);
        XmlRpcServer rpcServer = webServer.getXmlRpcServer();
        rpcServer.setWorkerFactory(new XmlRpcMySqlWorkerFactory(rpcServer));

        try {
            PropertyHandlerMapping phm = new PropertyHandlerMapping();

            phm.setRequestProcessorFactoryFactory(arpff);
            phm.setVoidMethodEnabled(true);
            phm.addHandler("archiver", IArchiveService.class);
            rpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) rpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);
        } catch (XmlRpcException e) {
            LOG.error("[*** XmlRpcException ***]: {}", e);
        }

        LOG.info("Start Server on port {}.", serverPort);

        synchronized (this) {
            try {
                webServer.start();
                this.wait();
            } catch (IOException e) {
                LOG.error("[*** IOException ***]: {}", e.getMessage());
            }  catch (InterruptedException e) {
                LOG.warn("{} has been interrupted.", this.getName());
            }
        }

        LOG.info("Try to stop the web server.");
        webServer.shutdown();
        LOG.info("Web server stopped!?");
    }
}
