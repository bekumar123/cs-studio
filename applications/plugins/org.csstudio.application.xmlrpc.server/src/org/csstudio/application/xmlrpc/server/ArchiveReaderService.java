
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import org.csstudio.application.xmlrpc.server.command.AbstractServerCommand;
import org.csstudio.application.xmlrpc.server.command.InfoCommand;
import org.csstudio.application.xmlrpc.server.command.MapResult;
import org.csstudio.application.xmlrpc.server.command.NamesCommand;
import org.csstudio.application.xmlrpc.server.command.ServerCommandParams;
import org.csstudio.application.xmlrpc.server.command.StringCollectionResult;
import org.csstudio.application.xmlrpc.server.command.ValuesCommand;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 21.12.2012
 */
public class ArchiveReaderService implements IArchiveService {
 
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveReaderService.class);

    private HashMap<String, AbstractServerCommand> commands;
    
    private IArchiveReaderFacade archiveReader;
    
    public ArchiveReaderService(IArchiveReaderFacade reader) {
        archiveReader = reader;
        commands = new HashMap<String, AbstractServerCommand>();
        commands.put("info", new InfoCommand("info", archiveReader));
        commands.put("names", new NamesCommand("names", archiveReader));
        commands.put("values", new ValuesCommand("values", archiveReader));
    }
    
    @Override
    public Map<String, Object> info() {
        InfoCommand command = (InfoCommand) commands.get("info");
        MapResult commandResult = null;
        try {
            commandResult = command.executeCommand(null);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new MapResult();
        }
        Hashtable<String, Object> result = new Hashtable<String, Object>();
        result.putAll(commandResult.getCommandResult());
        return result;
    }

    @Override
    public Collection<Object> names(Object pattern) {
        NamesCommand command = (NamesCommand) commands.get("names");
        StringCollectionResult result = null;
        try {
            ServerCommandParams params = new ServerCommandParams();
            params.addParameter("pattern", pattern);
            result = command.executeCommand(params);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            result = new StringCollectionResult();
        }
        return new Vector<Object>(result.getCommandResult());
    }
    
    @Override
    public Collection<Object> archives() {
        Vector<Object> result = new Vector<Object>();
        Hashtable<String, Object> archive = new Hashtable<String, Object>();
        archive.put("name", "MySQL Archive");
        archive.put("host", "krynfsa.desy.de");
        result.add(0, archive);
        return result;
    }
    
    @Override
    public Collection<Object> values(Object[] name, Integer startSec, Integer startNano,
                                     Integer endSec, Integer endNano, Integer count, Integer how) {
        Vector<Object> result = new Vector<Object>();
        return result;
    }
}
