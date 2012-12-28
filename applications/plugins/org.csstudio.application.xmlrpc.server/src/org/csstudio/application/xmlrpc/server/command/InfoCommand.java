
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

package org.csstudio.application.xmlrpc.server.command;

import java.util.HashMap;
import java.util.Vector;
import org.csstudio.application.xmlrpc.server.ServerActivator;
import org.csstudio.application.xmlrpc.server.ServerCommandException;
import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.osgi.framework.Version;
import com.google.common.collect.ImmutableSet;

/**
 * @author mmoeller
 * @since 21.12.2012
 */
public class InfoCommand extends AbstractServerCommand {
    
    // private static final Logger LOG = LoggerFactory.getLogger(InfoCommand.class);

    private IArchiveReaderFacade archiveReader;

    /**
     * @param name
     */
    public InfoCommand(String name, IArchiveReaderFacade reader) {
        super(name);
        archiveReader = reader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MapResult executeCommand(ServerCommandParams params) throws ServerCommandException {
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        
        Version version = ServerActivator.getContext().getBundle().getVersion();
        result.put("ver", new Integer(1));
        result.put("desc", "XML-RPC-Server Version "
                           + version.getMajor() + "." + version.getMinor()
                           + " for the MySQL archive.");
        
        Vector<Object> how = new Vector<Object>();
        ImmutableSet<IArchiveRequestType> types = archiveReader.getRequestTypes();
        int index = 0;
        for (IArchiveRequestType o : types) {
            how.add(index++, o.getTypeIdentifier());
        }
        result.put("how", how);
        
        Vector<Object> stat = new Vector<Object>();
        stat.add(0, "NO_ALARM");
        stat.add(1, "READ");
        stat.add(2, "WRITE");
        stat.add(3, "HIHI");
        stat.add(4, "HIGH");
        stat.add(5, "LOLO");
        stat.add(6, "LOW");
        stat.add(7, "STATE");
        stat.add(8, "COS");
        stat.add(9, "COMM");
        stat.add(10, "TIMEOUT");
        stat.add(11, "HWLIMIT");
        stat.add(12, "CALC");
        stat.add(13, "SCAN");
        stat.add(14, "LINK");
        stat.add(15, "SOFT");
        stat.add(16, "BAD_SUB");
        stat.add(17, "UDF");
        stat.add(18, "DISABLE");
        stat.add(19, "SIMM");
        stat.add(20, "READ_ACCESS");
        stat.add(21, "WRITE_ACCESS");

        result.put("stat", stat);
        
        Vector<Object> sevr = new Vector<Object>();
        HashMap<String, Object> info = new HashMap<String, Object>();
        info.put("sevr", "NO_ALARM");
        info.put("txt_stat", new Boolean(true));
        info.put("has_value", new Boolean(true));
        info.put("num", new Integer(0));
        sevr.add(0, info);
        
        info = new HashMap<String, Object>();
        info.put("sevr", "MINOR");
        info.put("txt_stat", new Boolean(true));
        info.put("has_value", new Boolean(true));
        info.put("num", new Integer(1));
        sevr.add(1, info);
        
        info = new HashMap<String, Object>();
        info.put("sevr", "MAJOR");
        info.put("txt_stat", new Boolean(true));
        info.put("has_value", new Boolean(true));
        info.put("num", new Integer(2));
        sevr.add(2, info);
        
        info = new HashMap<String, Object>();
        info.put("sevr", "INVALID");
        info.put("txt_stat", new Boolean(true));
        info.put("has_value", new Boolean(true));
        info.put("num", new Integer(3));
        sevr.add(3, info);
        
        info = new HashMap<String, Object>();
        info.put("sevr", "Est_Repeat");
        info.put("txt_stat", new Boolean(false));
        info.put("has_value", new Boolean(true));
        info.put("num", new Integer(3968));
        sevr.add(4, info);
        
        info = new HashMap<String, Object>();
        info.put("sevr", "Repeat");
        info.put("txt_stat", new Boolean(false));
        info.put("has_value", new Boolean(true));
        info.put("num", new Integer(3856));
        sevr.add(5, info);
        
        info = new HashMap<String, Object>();
        info.put("sevr", "Disconnected");
        info.put("txt_stat", new Boolean(true));
        info.put("has_value", new Boolean(false));
        info.put("num", new Integer(3904));
        sevr.add(6, info);
        
        info = new HashMap<String, Object>();
        info.put("sevr", "Archive_Off");
        info.put("txt_stat", new Boolean(true));
        info.put("has_value", new Boolean(false));
        info.put("num", new Integer(3872));
        sevr.add(7, info);

        info = new HashMap<String, Object>();
        info.put("sevr", "Archive_Disabled");
        info.put("txt_stat", new Boolean(true));
        info.put("has_value", new Boolean(false));
        info.put("num", new Integer(3848));
        sevr.add(8, info);

        result.put("sevr", sevr);

        return new MapResult(result);
    }
}
