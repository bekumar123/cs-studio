
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

package org.csstudio.archive.reader.mysql;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.epics.util.time.Timestamp;

/**
 * This class is responsible for requesting the XMLRPC server that provides the archive data
 * of the MySQL archiver. It is (more or less) a copy of the ChannelArchiveReader but uses
 * a newer version of the XMLRPC Java library (too bad using two different versions hidden from
 * all other plug-ins).
 *
 * @author Kay Kasemir
 * @author mmoeller
 * @since 14.03.2013
 */
public class MySqlArchiveReader implements ArchiveReader {

    /** The URL of the XMLRPC server. It contains the prefix xnds:// */
    private String serverUrl;

    /** The client for the server request */
    private XmlRpcClient rpcClient;

    private ServerInfoRequest serverInfoRequest;

    private ArchivesRequest archivesRequest;

    /** Active request. Synchronize on this for access */
    private ValueRequest currentRequest;

    public MySqlArchiveReader(String url) throws Exception {

        currentRequest = null;

        if (url == null) {
            throw new Exception("The server URL must not be null!");
        }

        String httpUrl = url.trim();
        if (httpUrl.isEmpty()) {
            throw new Exception("The server URL must not be empty!");
        }
        if (httpUrl.startsWith("xnds://")) {
            httpUrl = httpUrl.replace("xnds://", "http://");
        }

        try {

            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(httpUrl));
            rpcClient = new XmlRpcClient();
            rpcClient.setConfig(config);

            // Get server info
            serverInfoRequest = new ServerInfoRequest();
            serverInfoRequest.read(rpcClient);

            // .. and archive keys
            archivesRequest = new ArchivesRequest();
            archivesRequest.read(rpcClient);

        } catch (MalformedURLException e) {
            throw new Exception("The URL of the XMLRPC server is not valid: "
                                + httpUrl
                                + " (" + url + ")");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        // TODO: Hard coded name?
        return "XMLRCP MySQL Server";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getURL() {
        return serverUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append(serverInfoRequest.getDescription());
        buf.append("Request Types:\n");
        for (String req : serverInfoRequest.getRequestTypes()) {
            buf.append(req + "\n");
        }
        return buf.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getVersion() {
        return serverInfoRequest.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveInfo[] getArchiveInfos() {
        return archivesRequest.getArchiveInfos();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getNamesByPattern(int key, String globPattern) throws Exception {
        return getNamesByRegExp(key, RegExHelper.fullRegexFromGlob(globPattern));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getNamesByRegExp(int key, String regExp) throws Exception {
        NamesRequest infos = new NamesRequest(key, regExp);
        infos.read(rpcClient);
        return infos.getNameInfos();
    }

    /**
     * Helper for locating a request code by name.
     *
     * @param requestName For example: GET_RAW.
     * @return The 'requestType' ID for a given request type string.
     * @throws Exception when asking for unsupported request type.
     * @see #getRequestTypes()
     */
    public int getRequestCode(String requestName) throws Exception {
        String request_types[] = serverInfoRequest.getRequestTypes();
        for (int i=0; i<request_types.length; ++i) {
            if (request_types[i].equalsIgnoreCase(requestName)) {
                return i;
            }
        }
        throw new Exception("Unsupported request type '" + requestName + "'");
    }

    /**
     * @return Severity for an EPICS severity code.
     */
    public SeverityImpl getSeverity(int severity) {
        return serverInfoRequest.getSeverity(severity);
    }

    /**
     * @return EPICS/ChannelArchiver status string for given code
     */
    public String getStatus(SeverityImpl severity, int status) {
        if (severity.statusIsText()) {
            String[] status_strings = serverInfoRequest.getStatusStrings();
            if (status >= 0  &&  status < status_strings.length) {
                return status_strings[status];
            }
        }
        // return the number as a string
        return severity.getText() + " " + Integer.toString(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueIterator getRawValues(int key, String name, Timestamp start, Timestamp end) throws UnknownChannelException,
                                                                                           Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueIterator getOptimizedValues(int key,
                                            String name,
                                            Timestamp start,
                                            Timestamp end,
                                            int count) throws UnknownChannelException, Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel() {
        if (currentRequest != null) {
            currentRequest.cancel();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        cancel();
    }
}
