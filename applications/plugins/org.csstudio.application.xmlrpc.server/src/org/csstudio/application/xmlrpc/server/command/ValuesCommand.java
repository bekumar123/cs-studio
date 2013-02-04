
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.csstudio.application.xmlrpc.server.ServerCommandException;
import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableSet;

/**
 * @author mmoeller
 * @since 28.12.2012
 */
public class ValuesCommand extends AbstractServerCommand {
    
    private static final Logger LOG = LoggerFactory.getLogger(ValuesCommand.class);
    
    private IArchiveReaderFacade archiveReader;

    private Map<Integer, IArchiveRequestType> how;
    
    /**
     * @param name
     */
    public ValuesCommand(String name, IArchiveReaderFacade reader) {
        super(name);
        archiveReader = reader;
        how = new HashMap<Integer, IArchiveRequestType>();
        ImmutableSet<IArchiveRequestType> types = archiveReader.getRequestTypes();
        int index = 0;
        for (IArchiveRequestType o : types) {
            how.put(Integer.valueOf(index++), o);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapResult executeCommand(ServerCommandParams params) throws ServerCommandException {
        
        Integer howNr = (Integer) params.getParameter("how");
        IArchiveRequestType type = how.get(howNr);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request Type: {}", type);
        }
        
        /*
         * result := { string name,
                       meta,
                       int32 type,
                       int32 count,
                       values
                     } []
         * 
         *   meta := { int32 type;
                       type == 0: string states [],
                       type == 1: double disp high,
                                  double disp low,
                                  double alarm high,
                                  double alarm low,
                                  double warn high,
                                  double warn low,
                                  int prec,
                                  string units
                     }
         * 
         * values := { int32 stat,
                       int32 sevr,
                       int32 secs,
                       int32 nano,
                       <type> value[]
                     } []
         
         */
        
        String[] names = (String[]) params.getParameter("name");
        TimeInstant start = (TimeInstant) params.getParameter("start");
        TimeInstant end = (TimeInstant) params.getParameter("end");
        Limits<?> limits = null;
        try {
            limits = archiveReader.readDisplayLimits(names[0]);
        } catch (ArchiveServiceException e) {
            LOG.error("[*** ArchiveServiceException ***]: {}", e.getMessage());
        }
        
        IArchiveChannel channel = null;
        try {
            channel = archiveReader.getChannelByName(names[0]);
        } catch (ArchiveServiceException e) {
            LOG.error("[*** ArchiveServiceException ***]: {}", e.getMessage());
        }
        
        try {
            // Collection<IArchiveSample<V, T>>
            Object rawValues = archiveReader.readSamples(names[0], start, end, type);
            ArrayList<?> values = (ArrayList<?>) rawValues;
            Object v = values.get(0);
            ArchiveSample<?, ?> sample = (ArchiveSample<?, ?>) v;
            //sample.
        } catch (ArchiveServiceException e) {
            LOG.error("[*** ArchiveServiceException ***]: {}", e.getMessage());
        }
        
        Hashtable<String, Object> result = new Hashtable<String, Object>();
        result.put("name", "krykWeather:Temp_ai");
        result.put("meta", new Hashtable<String, Object>());
        result.put("type", Integer.valueOf(1));
        result.put("count", Integer.valueOf(0));
        Vector<Object> vecValues = new Vector<Object>();
        vecValues.add(new Hashtable<String, Object>()); // Wäre nur ein Wert
        result.put("values", vecValues);
        return new MapResult(result);
    }
}
