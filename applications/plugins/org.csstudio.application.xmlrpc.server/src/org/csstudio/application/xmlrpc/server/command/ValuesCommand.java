
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

import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.csstudio.application.xmlrpc.server.ServerCommandException;
import org.csstudio.application.xmlrpc.server.epics.ValueReader;
import org.csstudio.application.xmlrpc.server.epics.ValueType;
import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 28.12.2012
 */
public class ValuesCommand extends AbstractServerCommand {

    private static final Logger LOG = LoggerFactory.getLogger(ValuesCommand.class);

    private IArchiveReaderFacade archiveReader;

    private Map<Integer, IArchiveRequestType> how;

    /** TODO: Have to set with the value in the pluginCustomization, now just false. */
    private boolean askCntrlSystem;

    public ValuesCommand(String name, IArchiveReaderFacade reader, boolean askCntrlSys) {
        super(name);
        archiveReader = reader;
        how = new HashMap<Integer, IArchiveRequestType>();
        ImmutableSet<IArchiveRequestType> types = archiveReader.getRequestTypes();
        int index = 0;
        for (IArchiveRequestType o : types) {
            how.put(Integer.valueOf(index++), o);
        }
        askCntrlSystem = askCntrlSys;
    }

    /**
     * @param name
     */
    public ValuesCommand(String name, IArchiveReaderFacade reader) {
        this(name, reader, false);
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

        String name = (String) params.getParameter("name");
        TimeInstant start = (TimeInstant) params.getParameter("start");
        TimeInstant end = (TimeInstant) params.getParameter("end");

        Hashtable<String, Object> result = new Hashtable<String, Object>();
        try {

            ArchiveChannel channel = (ArchiveChannel) archiveReader.getChannelByName(name);

            Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> samples =
                    archiveReader.readSamples(name, start, end, type);

            result.put("name", name);

            result.put("meta", createMetaData(channel));

            String typeName = channel.getDataType();
            ValueType valueType = null;
            if (typeName != null) {
                valueType = ValueType.getValueTypeByName(typeName);
            }

            if (valueType != null) {
                result.put("type", Integer.valueOf(valueType.getValueTypeNumber()));
            } else {
                // TODO: Poor error handling
                result.put("type", Integer.valueOf(ValueType.STRING.getValueTypeNumber()));
            }
            result.put("count", Integer.valueOf(samples.size()));

            Vector<Object> values = new Vector<Object>();

            Iterator<IArchiveSample<Serializable, ISystemVariable<Serializable>>> iter =
                    samples.iterator();
            while (iter.hasNext()) {
                ArchiveSample<Serializable, ISystemVariable<Serializable>> o
                                   = (ArchiveSample<Serializable, ISystemVariable<Serializable>>) iter.next();

                Hashtable<String, Object> sampleValue = new Hashtable<String, Object>();

                EpicsSystemVariable<Serializable> var = (EpicsSystemVariable<Serializable>) o.getSystemVariable();
                sampleValue.put("stat", var.getAlarm().getStatus());
                sampleValue.put("sevr", var.getAlarm().getSeverity());
                sampleValue.put("secs", var.getTimestamp().getSeconds());
                sampleValue.put("nano", var.getTimestamp().getNanos());
                sampleValue.put("value", new Vector<Serializable>().add(var.getData()));
                values.add(sampleValue);
            }

            result.put("values", values);

        } catch (ArchiveServiceException e) {
            LOG.error("[*** ArchiveServiceException ***]: {}", e.getMessage());
        }

        return new MapResult(result);
    }

    private Map<String, Object> createMetaData(ArchiveChannel channel) {
        Hashtable<String, Object> meta = new Hashtable<String, Object>();
        if (!askCntrlSystem) {
            meta.put("type", new Integer(1));
            meta.put("disp_high", channel.getDisplayLimits().getHigh());
            meta.put("disp_low", channel.getDisplayLimits().getLow());
            meta.put("alarm_high", new Double(0.0));
            meta.put("alarm_low", new Double(0.0));
            meta.put("warn_high", new Double(0.0));
            meta.put("warn_low", new Double(0.0));
            meta.put("prec", new Integer(0));
            meta.put("units", "n/a");
        } else {
            ValueReader valueReader = new ValueReader();
            meta.put("type", new Integer(1));
            meta.put("disp_high", channel.getDisplayLimits().getHigh());
            meta.put("disp_low", channel.getDisplayLimits().getLow());
            meta.put("alarm_high", new Double(0.0));
            meta.put("alarm_low", new Double(0.0));
            meta.put("warn_high", new Double(0.0));
            meta.put("warn_low", new Double(0.0));
            meta.put("prec", valueReader.getPrecision(channel.getName()));
            meta.put("units", valueReader.getEgu(channel.getName()));
        }
        return meta;
    }
}
