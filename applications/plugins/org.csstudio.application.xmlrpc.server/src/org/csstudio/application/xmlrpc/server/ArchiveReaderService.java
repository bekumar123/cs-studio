
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
import java.util.Hashtable;
import java.util.Vector;
import org.csstudio.application.xmlrpc.server.command.ArchivesCommand;
import org.csstudio.application.xmlrpc.server.command.InfoCommand;
import org.csstudio.application.xmlrpc.server.command.MapResult;
import org.csstudio.application.xmlrpc.server.command.NamesCommand;
import org.csstudio.application.xmlrpc.server.command.ServerCommandParams;
import org.csstudio.application.xmlrpc.server.command.StringCollectionResult;
import org.csstudio.application.xmlrpc.server.command.ValuesCommand;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 21.12.2012
 */
public class ArchiveReaderService implements IArchiveService {
 
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveReaderService.class);

    private IArchiveReaderFacade archiveReader;
    
    public ArchiveReaderService(IArchiveReaderFacade reader) {
        archiveReader = reader;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> info() {
        InfoCommand command = new InfoCommand("info", archiveReader);
        MapResult commandResult = null;
        try {
            commandResult = command.executeCommand(null);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new MapResult();
        }
        Hashtable<String, Object> resultValue = new Hashtable<String, Object>();
        resultValue.putAll(commandResult.getCommandResult());
        Vector<Object> result = new Vector<Object>();
        result.add(0, resultValue);
        return result;
    }

    @Override
    public Collection<Object> archives() {
        ArchivesCommand command = new ArchivesCommand("archives");
        MapResult commandResult = null;
        try {
            commandResult = command.executeCommand(null);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new MapResult();
        }
        Vector<Object> result = new Vector<Object>();
        result.add(0, commandResult.getCommandResult());
        return result;
    }
    
    @Override
    public Collection<Object> names(Integer key, Object pattern) {
        NamesCommand command = new NamesCommand("names", archiveReader);
        StringCollectionResult commandResult = null;
        try {
            ServerCommandParams params = new ServerCommandParams();
            params.addParameter("pattern", pattern);
            commandResult = command.executeCommand(params);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new StringCollectionResult();
        }
        Vector<Object> result = new Vector<Object>();
        result.add(0, commandResult.getCommandResult());
        return result;
    }
    
    @Override
    public Collection<Object> values(Integer key, Object[] name, Integer startSec, Integer startNano,
                                     Integer endSec, Integer endNano, Integer count, Integer how) {
        ValuesCommand command = new ValuesCommand("values", archiveReader);
        MapResult commandResult = null;
        try {
            ServerCommandParams params = new ServerCommandParams();
            String[] names = new String[name.length];
            for (int i = 0;i < name.length;i++) {
                if (name[i].getClass().getSimpleName().equalsIgnoreCase("String")) {
                    names[i] = new String((String) name[i]);
                }
            }
            params.addParameter("name", names);
            params.addParameter("start", TimeInstantBuilder.fromMillis((startSec * 1000L) + startNano));
            params.addParameter("end", TimeInstantBuilder.fromMillis((endSec * 1000L) + endNano));
            params.addParameter("count", count);
            params.addParameter("how", how);
            commandResult = command.executeCommand(params);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new MapResult();
        }
        Vector<Object> result = new Vector<Object>();
        result.add(0, commandResult.getCommandResult());
        return result;
    }
}
