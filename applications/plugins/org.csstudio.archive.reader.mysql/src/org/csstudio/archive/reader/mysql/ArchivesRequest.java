
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
import java.util.Map;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.csstudio.archive.reader.ArchiveInfo;

/**
 * This class is responsible for requesting the server archives of XMLRPC server that provides the archive data
 * of the MySQL archiver.
 * It is (more or less) a copy of the org.csstudio.archive.reader.channelarchiver.ArchivesRequest.
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

public class ArchivesRequest {

    private ArchiveInfo archiveInfos[];

	/** Read info from data server */
	@SuppressWarnings({ "nls", "unchecked" })
    public void read(XmlRpcClient xmlrpc) throws Exception {

	    Map<String, Object> result = null;
		try {
			Vector<Object> params = new Vector<Object>();
			Object answer = xmlrpc.execute("archiver.archives", params);
            if (answer instanceof Map<?, ?>) {
                result = (Map<String, Object>) answer;
            }

		} catch (XmlRpcException e) {
			throw new Exception("The call of method archiver.archives failed.", e);
		}

		if (result == null) {
		    result = new HashMap<String, Object>();
		}

		//	{  int32 key,
		//     string name,
		//     string path }[] = archiver.archives()
        archiveInfos = new ArchiveInfo[result.size()];
		for (int i = 0;i < result.size();++i) {
			Map<String, Object> info = (Map<String, Object>) result.get(i);
            archiveInfos[i] =
                new ArchiveInfo((String) info.get("name"),
                                (String) info.get("path"),
				                (Integer) info.get("key"));
		}
	}

	/** @return Returns all the archive infos obtained in the request. */
    public ArchiveInfo[] getArchiveInfos() {
		return archiveInfos;
	}

    @Override
    public String toString() {
		StringBuffer result = new StringBuffer();
        for (ArchiveInfo o : archiveInfos) {
            result.append(String.format("Key %4d: '%s' (%s)\n",
                o.getKey(),
                o.getName(),
                o.getDescription()));
        }
		return result.toString();
	}
}
