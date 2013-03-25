
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.csstudio.application.xmlrpc.server.command.ArchivesCommand;
import org.csstudio.application.xmlrpc.server.command.InfoCommand;
import org.csstudio.application.xmlrpc.server.command.MapListResult;
import org.csstudio.application.xmlrpc.server.command.MapResult;
import org.csstudio.application.xmlrpc.server.command.NamesCommand;
import org.csstudio.application.xmlrpc.server.command.ServerCommandParams;
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
    public Map<String, Object> info() {
        InfoCommand command = new InfoCommand("info");
        MapResult commandResult = null;
        try {
            commandResult = command.executeCommand(null);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new MapResult();
        }
        Hashtable<String, Object> resultValue = new Hashtable<String, Object>();
        resultValue.putAll(commandResult.getCommandResult());
        return resultValue;
    }

    @Override
    public List<Map<String, Object>> archives() {
        ArchivesCommand command = new ArchivesCommand("archives");
        MapResult commandResult = null;
        try {
            commandResult = command.executeCommand(null);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new MapResult();
        }
        Vector<Map<String, Object>> resultValue = new Vector<Map<String, Object>>();
        resultValue.add(commandResult.getCommandResult());
        return resultValue;
    }

    @Override
    public List<Map<String, Object>> names(Integer key, Object pattern) {
        NamesCommand command = new NamesCommand("names", archiveReader);
        MapListResult commandResult = null;
        try {
            ServerCommandParams params = new ServerCommandParams();
            params.addParameter("pattern", pattern);
            commandResult = command.executeCommand(params);
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
            commandResult = new MapListResult();
        }
        return commandResult.getCommandResult();
    }

    @Override
    public List<Map<String, Object>> values(Integer key, Object[] name,
                                            Integer startSec, Integer startNano,
                                            Integer endSec, Integer endNano,
                                            Integer count, Integer how) {

        ValuesCommand command = new ValuesCommand("values", archiveReader, false);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        try {
            ServerCommandParams params = new ServerCommandParams();
            params.addParameter("start", TimeInstantBuilder.fromMillis(startSec * 1000L + startNano));
            params.addParameter("end", TimeInstantBuilder.fromMillis(endSec * 1000L + endNano));
            params.addParameter("count", count);
            params.addParameter("how", how);
            for (Object element : name) {
                if (element.getClass().getSimpleName().equalsIgnoreCase("String")) {
                    params.addParameter("name", element);
                    MapResult commandResult = command.executeCommand(params);
                    Map<String, Object> resultValue = new HashMap<String, Object>();
                    resultValue.putAll(commandResult.getCommandResult());
                    result.add(resultValue);
                }
            }
        } catch (ServerCommandException e) {
            LOG.error("[*** ServerCommandException ***]: {}", e.getMessage());
        }

        return result;
    }
}
