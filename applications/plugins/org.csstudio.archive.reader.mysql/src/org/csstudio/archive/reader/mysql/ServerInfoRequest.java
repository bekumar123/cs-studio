
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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.epics.vtype.AlarmSeverity;

/**
 * This class is responsible for requesting the server info of XMLRPC server that provides
 * the archive data of the MySQL archiver.
 * It is (more or less) a copy of the org.csstudio.archive.reader.channelarchiver.ServerInfoRequest.
 * The two differences are:
 * <ul>
 * <li>It does not use the implementations of List and Map interfaces (Vector, Hashtable)
 *     but only the interfaces itself.</li>
 * <li>The XMLRPC MySQL server returns always List&lt;Object&gt;. The index 0 contains the
 *     actual data.</li>
 * </ul>
 *
 * @author Kay Kasemir
 * @author mmoeller
 * @since 14.03.2013
 */
public class ServerInfoRequest {

    /**
     *  String used for an OK status and severity
     *  (more generic than the EPICS 'NO_ALARM')
     */
    final static String NO_ALARM = "OK";

    private String description;
    private int version;
    private String howStrings[];
    private String statusStrings[];
    private Hashtable<Integer, SeverityImpl> severities;

    /** Read info from data server */
    @SuppressWarnings({ "unchecked" })
    public void read(XmlRpcClient xmlrpc) throws Exception {

        Map<String, Object> result = null;
        try {
            Vector<Object> params = new Vector<Object>();
            Object answer = xmlrpc.execute("archiver.info", params);
            if (answer instanceof Map<?, ?>) {
                result = (Map<String, Object>) answer;
            }
        } catch (XmlRpcException e) {
            throw new Exception("The call of method archiver.info failed.", e);
        }

        if (result == null) {
            result = new HashMap<String, Object>();
        }

        //  { int32             ver,
        //    string            desc,
        //    string            how[],
        //    string            stat[],
        //    { int32 num,
        //      string sevr,
        //      bool has_value,
        //      bool txt_stat
        //    }                 sevr[]
        //  } = archiver.info()
        version = (Integer) result.get("ver");
        description = (String) result.get("desc");
        // Get 'how'. Silly code to copy that into a type-safe vector.
        List<Object> tmp =  (List<Object>) result.get("how");
        howStrings = new String[tmp.size()];
        for (int i = 0;i < tmp.size();++i) {
            howStrings[i] = (String) tmp.get(i);
        }
        // Same silly code for the status strings. Better way?
        tmp = (List<Object>) result.get("stat");
        statusStrings = new String[tmp.size()];
        for (int i = 0;i < tmp.size();++i) {
            statusStrings[i] = (String) tmp.get(i);
            // Patch "NO ALARM" into "OK"
            if (statusStrings[i].equals("NO_ALARM")) {
                statusStrings[i] = NO_ALARM;
            }
        }
        // Same silly code for the severity strings.
        List<Object> sevrInfo = (List<Object>) result.get("sevr");
        severities = new Hashtable<Integer, SeverityImpl>();
        for (Object sio : sevrInfo) {
            Map<String, Object> si = (Map<String, Object>) sio;
            final String txt = (String) si.get("sevr");
            // Patch "NO ALARM" into "OK"
            AlarmSeverity severity;
            if ("NO_ALARM".equals(txt)  ||  NO_ALARM.equals(txt)) {
                severity = AlarmSeverity.NONE;
            } else if ("MINOR".equals(txt)) {
                severity = AlarmSeverity.MINOR;
            } else if ("MAJOR".equals(txt)) {
                severity = AlarmSeverity.MAJOR;
            } else if ("MAJOR".equals(txt)) {
                severity = AlarmSeverity.INVALID;
            } else {
                severity = AlarmSeverity.UNDEFINED;
            }
            severities.put((Integer) si.get("num"),
                           new SeverityImpl(severity,
                                            txt,
                                           (Boolean) si.get("has_value"),
                                           (Boolean) si.get("txt_stat")));
        }
    }

    /** @return Returns the version number. */
    public int getVersion() {
        return version;
    }

    /** @return Returns the description. */
    public String getDescription() {
        return description;
    }

    /** @return Returns the list of supported request types. */
    public String[] getRequestTypes() {
        return howStrings;
    }

    /** @return Returns the status strings. */
    public String[] getStatusStrings() {
        return statusStrings;
    }

    /** @return Returns the severity infos. */
    public SeverityImpl getSeverity(int severity) {
        SeverityImpl sev = severities.get(Integer.valueOf(severity));
        if (sev != null) {
            return sev;
        }
        return new SeverityImpl(AlarmSeverity.UNDEFINED,
                                "<Severity " + severity + "?>",
                                false,
                                false);
    }

    /** @return Returns a more or less useful string for debugging. */
    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append(String.format("Server version : %d\n", version));
        result.append(String.format("Description    :\n%s", description));
        result.append("Available request methods:\n");
        for (int i=0; i<howStrings.length; ++i) {
            result.append(String.format("%d = '%s'\n", i, howStrings[i]));
        }
        return result.toString();
    }
}
