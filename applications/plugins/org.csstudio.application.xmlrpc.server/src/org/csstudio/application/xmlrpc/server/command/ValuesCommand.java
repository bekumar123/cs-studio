
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
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.csstudio.application.xmlrpc.server.ServerCommandException;
import org.csstudio.application.xmlrpc.server.epics.MetaData;
import org.csstudio.application.xmlrpc.server.epics.MetaDataCollection;
import org.csstudio.application.xmlrpc.server.epics.ValueReader;
import org.csstudio.application.xmlrpc.server.epics.ValueType;
import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 28.12.2012
 */
public class ValuesCommand extends AbstractServerCommand {

    private static final Logger LOG = LoggerFactory.getLogger(ValuesCommand.class);

    private static final long TIME_DIFF_HALF_YEAR = 259200;

    private static final long TIME_DIFF_MONTH = 43200;

//    private static final long TIME_DIFF_WEEK = 10080;

    private IArchiveReaderFacade archiveReader;

    private Map<Integer, IArchiveRequestType> howArchive;

    private Map<Integer, ServerRequestType> howServer;

    /** TODO: Have to set with the value in the pluginCustomization, now just false. */
    private boolean askCntrlSystem;

    public ValuesCommand(String name, IArchiveReaderFacade reader, boolean askCntrlSys) {
        super(name);
        archiveReader = reader;
        howServer = new HashMap<Integer, ServerRequestType>(ServerRequestType.values().length);
        int index = 0;
        for (ServerRequestType o : ServerRequestType.values()) {
            howServer.put(Integer.valueOf(index++), o);
        }
        ImmutableSet<IArchiveRequestType> types = archiveReader.getRequestTypes();
        howArchive = new HashMap<Integer, IArchiveRequestType>(types.size());
        index = 0;
        for (IArchiveRequestType o : types) {
            howArchive.put(Integer.valueOf(index++), o);
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public MapResult executeCommand(ServerCommandParams params) throws ServerCommandException {

        Integer howNr = (Integer) params.getParameter("how");
        ServerRequestType requestType = howServer.get(howNr);
        if (requestType == null) {
            LOG.warn("Request type was {}, but I do not know such type. I am using AVERAGE instead!", howNr);
            requestType = ServerRequestType.AVERAGE;
        }
        String name = (String) params.getParameter("name");
        TimeInstant start = (TimeInstant) params.getParameter("start");
        TimeInstant end = (TimeInstant) params.getParameter("end");
        int requestedCount = (Integer) params.getParameter("count");
        long timeDiffMin = (end.getSeconds() - start.getSeconds()) / 60;
        IArchiveRequestType archiveRequest = this.getCorrespondedRequestType(requestType, timeDiffMin);

        if (LOG.isDebugEnabled()) {
            LOG.debug("-------------- Value request --------------");
            LOG.debug("Server method:    {}", requestType.toString());
            LOG.debug("Archive method:   {}", archiveRequest.toString());
            LOG.debug("Channel name:     {}", name);
            LOG.debug("Start:            {} ({})", start.toString(), start.getSeconds());
            LOG.debug("End:              {} ({})", end.toString(), end.getSeconds());
            LOG.debug("Time diff (min.): {}", timeDiffMin);
            LOG.debug("Requested Count:  {}", requestedCount);
            LOG.debug("----------- End of value request ----------");
        }

        Map<String, Object> result = new HashMap<String, Object>(5);
        try {

            ArchiveChannel channel = (ArchiveChannel) archiveReader.getChannelByName(name);

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

            Collection<IArchiveSample> samples =
                           (Collection) archiveReader.readSamples(name, start, end, archiveRequest);

            if (samples.isEmpty() && requestType == ServerRequestType.AVERAGE) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No samples found. Reading the last sample before the start time");
                }
                samples = Lists.newArrayList();
                ArchiveSample beforeSample = (ArchiveSample) archiveReader.readLastSampleBefore(name, start);
                EpicsSystemVariable<Serializable> var
                             = (EpicsSystemVariable<Serializable>) beforeSample.getSystemVariable();
                Serializable data = var.getData();
                if (data instanceof Double) {
                    if (((Double) data).doubleValue() != 0.0d) {
                        samples.add(beforeSample);
                    }
                }
            }

            List<Map<String, Object>> values = null;
            if (samples.size() > 0) {
                switch (requestType) {
                    case AVERAGE:
                        values = this.createAverageValues(samples, channel, start, end, requestedCount);
                        break;
                    case RAW:
                        values = this.createRawValues(samples);
                        break;
                    case TAIL_RAW:
                        values = this.createTailRawValues(samples, requestedCount);
                        break;
                    default:
                        values = new ArrayList<Map<String, Object>>();
                }
            } else {
                values = new ArrayList<Map<String, Object>>();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Number of samples: {}", values.size());
            }

            result.put("count", Integer.valueOf(values.size()));
            if (requestType == ServerRequestType.TAIL_RAW) {
                result.put("samplecount", Integer.valueOf(samples.size()));
            }
            result.put("values", values);

        } catch (ArchiveServiceException e) {
            LOG.error("[*** ArchiveServiceException ***]: {}", e.getMessage());
        }

        return new MapResult(result);
    }

    private Map<String, Object> createMetaData(ArchiveChannel channel) {
        Map<String, Object> meta = new HashMap<String, Object>();
        if (!askCntrlSystem) {
            MetaDataCollection mdc = MetaDataCollection.getInstance();
            MetaData metaData = mdc.get(channel.getName());
            meta.put("type", new Integer(1));
            meta.put("disp_high", channel.getDisplayLimits().getHigh());
            meta.put("disp_low", channel.getDisplayLimits().getLow());
            meta.put("alarm_high", new Double(0.0));
            meta.put("alarm_low", new Double(0.0));
            meta.put("warn_high", new Double(0.0));
            meta.put("warn_low", new Double(0.0));
            if (metaData != null) {
                meta.put("prec", new Integer(metaData.getPrec()));
                meta.put("units", metaData.getEgu());
            } else {
                meta.put("prec", new Integer(0));
                meta.put("units", "N/A");
            }
        } else {
            ValueReader valueReader = new ValueReader();
            meta.put("type", new Integer(1));
            meta.put("disp_high", channel.getDisplayLimits().getHigh());
            meta.put("disp_low", channel.getDisplayLimits().getLow());
            meta.put("alarm_high", new Double(0.0));
            meta.put("alarm_low", new Double(0.0));
            meta.put("warn_high", new Double(0.0));
            meta.put("warn_low", new Double(0.0));
            String tmp = valueReader.getPrecision(channel.getName());
            meta.put("prec", convertStringToInteger(tmp));
            meta.put("units", valueReader.getEgu(channel.getName()));
        }
        return meta;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Map<String, Object>> createRawValues(Collection<IArchiveSample> samples) {
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        Iterator<IArchiveSample> iter = samples.iterator();
        while (iter.hasNext()) {
            ArchiveSample o = (ArchiveSample) iter.next();

            Map<String, Object> sampleValue = new HashMap<String, Object>();

            EpicsSystemVariable<Serializable> var = (EpicsSystemVariable<Serializable>) o.getSystemVariable();
            sampleValue.put("stat", Integer.valueOf(var.getAlarm().getStatus().ordinal()));
            sampleValue.put("sevr", Integer.valueOf(var.getAlarm().getSeverity().ordinal()));

            // Datatype Long is not allowed for XMLRPC
            String longStr = String.valueOf(var.getTimestamp().getSeconds());
            sampleValue.put("secs", longStr);
            longStr = String.valueOf(var.getTimestamp().getFractalMillisInNanos());
            sampleValue.put("nano", longStr);
            List<Object> value = new ArrayList<Object>();
            value.add(var.getData());
            sampleValue.put("value", value);
            values.add(sampleValue);
        }
        return values;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Map<String, Object>> createTailRawValues(Collection<IArchiveSample> samples,
                                                          int requestedCount) {
        if (samples.size() <= requestedCount) {
            return createRawValues(samples);
        }
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>(requestedCount);
        IArchiveSample[] sampleArray = new IArchiveSample[samples.size()];
        samples.toArray(sampleArray);
        for (int i = samples.size() - requestedCount;i < sampleArray.length;i++) {
            ArchiveSample o = (ArchiveSample) sampleArray[i];
            Map<String, Object> sampleValue = new HashMap<String, Object>();
            EpicsSystemVariable<Serializable> var = (EpicsSystemVariable<Serializable>) o.getSystemVariable();
            sampleValue.put("stat", Integer.valueOf(var.getAlarm().getStatus().ordinal()));
            sampleValue.put("sevr", Integer.valueOf(var.getAlarm().getSeverity().ordinal()));
            // Datatype Long is not allowed for XMLRPC
            String longStr = String.valueOf(var.getTimestamp().getSeconds());
            sampleValue.put("secs", longStr);
            longStr = String.valueOf(var.getTimestamp().getFractalMillisInNanos());
            sampleValue.put("nano", longStr);
            List<Object> value = new ArrayList<Object>();
            value.add(var.getData());
            sampleValue.put("value", value);
            values.add(sampleValue);
        }
        return values;
    }

    @SuppressWarnings("rawtypes")
    private List<Map<String, Object>> createAverageValues(Collection<IArchiveSample> samples,
                                                          ArchiveChannel channel,
                                                          TimeInstant start,
                                                          TimeInstant end,
                                                          int requestedCount) {

        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        try {
            EquidistantTimeSampleIterator iter =
                    new EquidistantTimeSampleIterator(archiveReader,
                                                      samples,
                                                      channel.getName(),
                                                      start,
                                                      end,
                                                      requestedCount);
            while (iter.hasNext()) {
                VType vType = iter.next();
                if (vType instanceof VStatistics) {
                    VStatistics vs = (VStatistics) vType;

                    Map<String, Object> sampleValue = new HashMap<String, Object>();

                    // TODO: Where is the value of the status???
                    sampleValue.put("stat", Integer.valueOf(0));
                    sampleValue.put("sevr", Integer.valueOf(vs.getAlarmSeverity().ordinal()));

                    // Datatype Long is not allowed for XMLRPC
                    String longStr = String.valueOf(vs.getTimestamp().getSec());
                    sampleValue.put("secs", longStr);
                    sampleValue.put("nano", Integer.valueOf(vs.getTimestamp().getNanoSec()));
                    sampleValue.put("min", vs.getMin());
                    sampleValue.put("max", vs.getMax());
                    List<Object> value = new ArrayList<Object>();
                    value.add(vs.getAverage());
                    sampleValue.put("value", value);
                    values.add(sampleValue);
                }
            }
        } catch (Exception e) {
            LOG.error("[*** Exception ***]: " + e.getMessage());
            values.clear();
        }
        return values;
    }

    private IArchiveRequestType getCorrespondedRequestType(ServerRequestType serverType, long timeDiff) {
        // Default: RAW
        IArchiveRequestType result = howArchive.get(0);
        if (serverType == ServerRequestType.AVERAGE) {
            String method = "RAW";
            if (timeDiff > TIME_DIFF_HALF_YEAR) {
                method = "HOUR";
            } else if (timeDiff > TIME_DIFF_MONTH) {
                method = "MINUTE";
            }
            Iterator<IArchiveRequestType> iter = howArchive.values().iterator();
            while (iter.hasNext()) {
                IArchiveRequestType type = iter.next();
                if (type.getTypeIdentifier().contains(method)) {
                    result = type;
                    break;
                }
            }
        }
        return result;
    }

    private Integer convertStringToInteger(String value) {
        Integer result = null;
        try {
            result = new Integer(value);
        } catch (NumberFormatException e) {
            result = new Integer(0);
        }
        return result;
    }
}
