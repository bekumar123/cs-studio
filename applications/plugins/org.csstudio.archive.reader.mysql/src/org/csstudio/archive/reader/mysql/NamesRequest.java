
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

/**
 * This class is responsible for requesting the server archives of XMLRPC server that provides the archive data
 * of the MySQL archiver.
 * It is (more or less) a copy of the org.csstudio.archive.reader.channelarchiver.NamesRequest.
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
public class NamesRequest {

    private int key;
    private String pattern;
    private String names[];

    /** Create a name lookup.
     *   @param pattern Regular expression pattern for the name.
     */
    public NamesRequest(int key, String pattern) {
        this.key = key;
        this.pattern = pattern;
    }

    /** Read info from data server */
    @SuppressWarnings("unchecked")
    public void read(XmlRpcClient xmlrpc) throws Exception {

        List<Object> result = new Vector<Object>();

        try {
            Vector<Object> params = new Vector<Object>();
            params.add(Integer.valueOf(key));
            params.add(pattern);
            Object answer = xmlrpc.execute("archiver.names", params);
            if (answer instanceof Object[]) {
                result.addAll(Arrays.asList((Object[]) answer));
            }
        } catch (XmlRpcException e) {
            throw new Exception("The call of method archiver.names failed!", e);
        }

        //  { string name,
        //    int32 start_sec,  int32 start_nano,
        //    int32 end_sec,    int32 end_nano
        //   }[] = archiver.names(int32 key,  string pattern)
        names = new String[result.size()];
        for (int i = 0;i < result.size();++i)
        {
            Map<String, Object> entry = (Map<String, Object>) result.get(i);
//            ITimestamp start = TimestampFactory.createTimestamp(
//                            (Integer) entry.get("start_sec"),
//                            (Integer) entry.get("start_nano"));
//            ITimestamp end = TimestampFactory.createTimestamp(
//                            (Integer) entry.get("end_sec"),
//                            (Integer) entry.get("end_nano"));
            names[i] =(String) entry.get("name");
        }
    }

    /**
     * @return Returns the name infos that were found.
     */
    public final String[] getNameInfos() {
        return names;
    }

    /** @return Returns a more or less useful string. */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(String.format("Names with key %d matching '%s':\n",
                key, pattern));
        for (int i = 0;i < names.length;++i) {
            if (i > 0) {
                result.append(", ");
            }
            result.append('\'');
            result.append(names[i]);
            result.append('\'');
        }
        return result.toString();
    }
}
