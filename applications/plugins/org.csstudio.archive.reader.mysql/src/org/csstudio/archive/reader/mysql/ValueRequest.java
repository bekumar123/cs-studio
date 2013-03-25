
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVString;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/**
 * This class is responsible for requesting the server archives of XMLRPC server that provides the archive data
 * of the MySQL archiver.
 * It is (more or less) a copy of the org.csstudio.archive.reader.channelarchiver.ValueRequest.
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
public class ValueRequest implements AsyncCallback {

    /** Helper for passing the result or an error from the XML-RPC callback
     *  to the ValueRequest that waits for it
     */
    static class Result {

        boolean isSet = false;
        Map<String, Object> xmlRpcResult = null;
        Throwable xmlRpcException = null;

        synchronized void clear() {
            notify(null, null);
        }

        synchronized void setError(final Throwable error) {
            notify(null, error);
        }

        synchronized void setData(final Map<String, Object> data) {
            notify(data, null);
        }

        private void notify(final Map<String, Object> data, final Throwable error) {
            xmlRpcResult = new HashMap<String, Object>(data);
            xmlRpcException = error;
            isSet = true;
            notifyAll();
        }
    }

    final private MySqlArchiveReader reader;
    final private int key;
    final private String channels[];
    final private Timestamp start, end;
    final private int how;
    final private Object parms[];

    // Possible 'type' IDs for the received values.
    final private static int TYPE_STRING = 0;
    final private static int TYPE_ENUM = 1;
    final private static int TYPE_INT = 2;
    final private static int TYPE_DOUBLE = 3;

    final private Result result = new Result();

    /** The result of the query */
    private VType samples[];

    /** Constructor for new value request.
     *  @param reader ChannelArchiverReader
     *  @param key Archive key
     *  @param channel Channel name
     *  @param start Start time for retrieval
     *  @param end  End time for retrieval
     *  @param optimized Get optimized or raw data?
     *  @param count Number of values
     */
    public ValueRequest(final MySqlArchiveReader reader, final int key, final String channel,
                        final Timestamp start, final Timestamp end, final boolean optimized,
                        final int count) throws Exception {

        this.reader = reader;
        this.key = key;
        this.channels = new String[] { channel };
        this.start = start;
        this.end = end;

        // Check parms
        if (optimized) {
            // AVG_PER_MINUTE, AVG_PER_HOUR
            how = reader.getRequestCode("AVG_PER_MINUTE");
            parms = new Object[] { Integer.valueOf(count) };
        } else {
            // RAW
            how = reader.getRequestCode("RAW");
            parms = new Object[] { Integer.valueOf(count) };
        }
    }

    /**
     * @see org.csstudio.archive.channelarchiver.ClientRequest#read()
     */
    @SuppressWarnings({ "unchecked" })
    public void read(final XmlRpcClient xmlrpc) throws Exception {

        final Vector<Object> params = new Vector<Object>(8);
        params.add(Integer.valueOf(key));
        params.add(channels);
        params.add(Integer.valueOf((int)start.getSec()));
        params.add(Integer.valueOf(start.getNanoSec()));
        params.add(Integer.valueOf((int)end.getSec()));
        params.add(Integer.valueOf(end.getNanoSec()));
        params.add(parms[0]);
        params.add(Integer.valueOf(how));
        xmlrpc.executeAsync("archiver.values", params, this);

        // Wait for AsynCallback to set the result
        Map<String, Object> xmlRpcResult;
        synchronized (result) {
            while (! result.isSet) {
                result.wait();
            }
            // Failed?
            if (result.xmlRpcException != null) {
                throw new Exception("The call of method archiver.values failed: "
                                    + result.xmlRpcException.getMessage());
            }
            // Cancelled?
            if (result.xmlRpcResult == null) {
                samples = new VType[0];
                return;
            }
            xmlRpcResult = result.xmlRpcResult;
        }

        // result := { string name,  meta, int32 type,
        //              int32 count,  values }[]
        final int numReturnedChannels = xmlRpcResult.size();
        if (numReturnedChannels != 1) {
            throw new Exception("The method archiver.values returned data for "
                                + numReturnedChannels + " channels?");
        }

        final Map<String, Object> channelData = (Map<String, Object>) xmlRpcResult.get(0);
        final String name = (String) channelData.get("name");
        final int type = (Integer) channelData.get("type");
        final int count = (Integer) channelData.get("count");
        try {
            final Object meta = decodeMetaData(type, (Map<String, Object>) channelData.get("meta"));
            final Display display;
            final List<String> labels;
            if (meta instanceof Display) {
                display = (Display) meta;
                labels = null;
            } else if (meta instanceof List) {
                display = null;
                labels = (List<String>) meta;
            } else {
                display = null;
                labels = null;
            }
            samples = decodeValues(type,
                                   count,
                                   display,
                                   labels,
                                   (Vector<Object>) channelData.get("values"));
        } catch (final Exception e) {
            throw new Exception("Error while decoding values for channel '"
                                + name + "': " + e.getMessage(), e);
        }
    }

    /** Cancel an ongoing read.
     *  <p>
     *  Somewhat fake, because there is no way to stop the underlying
     *  XML-RPC request, but we can abandon the read and pretend
     *  that we didn't receive any data.
     */
    public void cancel() {
        result.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleError(final XmlRpcRequest request, final Throwable error) {
        result.setError(error);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handleResult(final XmlRpcRequest request, final Object data) {
        if (data instanceof Map<?, ?>) {
            result.setData((Map<String, Object>) data);
        }
    }

    /** Parse the MetaData from the received XML-RPC response.
     *  @param valueType Type code of received values
     *  @param metaHash Hash with meta data to decode
     *  @return {@link Display} or List of {@link String}[] depending on data type
     */
    @SuppressWarnings({ "rawtypes" })
    private Object decodeMetaData(final int valueType, final Map<String, Object> metaHash) throws Exception {
        // meta := { int32 type;
        //           type==0: string states[],
        //           type==1: double disp_high,
        //                    double disp_low,
        //                    double alarm_high,
        //                    double alarm_low,
        //                    double warn_high,
        //                    double warn_low,
        //                    int prec,  string units
        //         }
        final int meta_type = (Integer) metaHash.get("type");
        if (meta_type < 0 || meta_type > 1) {
            throw new Exception("Invalid 'meta' type " + meta_type);
        }
        if (meta_type == 1) {
            // The 2.8.1 server will give 'ENUM' type values
            // with Numeric meta data, units = "<No data>"
            // as an error message.
            final NumberFormat format = NumberFormats.format((Integer) metaHash.get("prec"));
            return ValueFactory.newDisplay(
                    (Double) metaHash.get("disp_low"),
                    (Double) metaHash.get("alarm_low"),
                    (Double) metaHash.get("warn_low"),
                    (String) metaHash.get("units"),
                    format,
                    (Double) metaHash.get("warn_high"),
                    (Double) metaHash.get("alarm_high"),
                    (Double) metaHash.get("disp_high"),
                    (Double) metaHash.get("disp_low"),
                    (Double) metaHash.get("disp_high"));
        }
        //  else
        if (!(valueType == TYPE_ENUM || valueType == TYPE_STRING)) {
            throw new Exception(
                    "Received enumerated meta information for value type "
                    + valueType);
        }
        final Vector stateVec = (Vector) metaHash.get("states");
        final int vectorSize = stateVec.size();
        final List<String> states = new ArrayList<String>(vectorSize);
        // Silly loop because of type warnings from state_vec.toArray(states)
        for (int i=0; i < vectorSize; ++i) {
            states.add((String) stateVec.get(i));
        }
        return states;
    }

    /** Parse the values from the received XML-RPC response. */
    @SuppressWarnings({ "rawtypes" })
    private VType[] decodeValues(final int type, final int count, final Display display,
                                 final List<String> labels, final Vector value_vec) throws Exception
    {
        // values := { int32 stat,  int32 sevr,
        //             int32 secs,  int32 nano,
        //             <type> value[] } []
        // [{secs=1137596340, stat=0, nano=344419666, value=[0.79351], sevr=0},
        //  {secs=1137596400, stat=0, nano=330619666, value=[0.79343], sevr=0},..]
        final int num_samples = value_vec.size();
        final VType samp[] = new VType[num_samples];
        for (int si=0; si<num_samples; ++si)
        {
            final Hashtable sample_hash = (Hashtable) value_vec.get(si);
            final long secs = (Integer)sample_hash.get("secs");
            final int nano = (Integer)sample_hash.get("nano");
            final Timestamp time = Timestamp.of(secs, nano);
            final int stat_code = (Integer)sample_hash.get("stat");
            final int sevr_code = (Integer)sample_hash.get("sevr");
            final SeverityImpl sevr = reader.getSeverity(sevr_code);
            final String stat = reader.getStatus(sevr, stat_code);
            final Vector vv = (Vector)sample_hash.get("value");
            final AlarmSeverity severity = sevr.getSeverity();

            if (type == TYPE_DOUBLE)
            {
                final double values[] = new double[count];
                for (int vi=0; vi<count; ++vi) {
                    values[vi] = (Double)vv.get(vi);
                }
                // Check for "min", "max".
                // Only handles min/max for double, but that's OK
                // since for now that's all that the server does as well.
                if (sample_hash.containsKey("min") &&
                    sample_hash.containsKey("max"))
                {   // It's a min/max double, certainly interpolated
                    final double min = (Double)sample_hash.get("min");
                    final double max = (Double)sample_hash.get("max");
                    samp[si] = new ArchiveVStatistics(time, severity, stat, display,
                            values[0], min, max, 0.0, 1);
                }
                else
                {   // Was this from a min/max/avg request?
                    // Yes: Then we ran into a raw value.
                    // No: Then it's whatever quality we expected in general
                    if (values.length == 1) {
                        samp[si] = new ArchiveVNumber(time, severity, stat, display, values[0]);
                    } else {
                        samp[si] = new ArchiveVNumberArray(time, severity, stat, display, values);
                    }
                }
            }
            else if (type == TYPE_ENUM)
            {
                // The 2.8.1 server will give 'ENUM' type values
                // with Numeric meta data, units = "<No data>".
                // as an error message -> Handle it by returning
                // the data as long with the numeric meta that we have.
                if (labels != null)
                {
                    if (count < 0) {
                        throw new Exception("No values");
                    }
                    final int index = (Integer)vv.get(0);
                    samp[si] = new ArchiveVEnum(time, severity, stat, labels, index);
                }
                else
                {
                    if (count == 1) {
                        samp[si] = new ArchiveVNumber(time, severity, stat, display, (Integer)vv.get(0));
                    } else
                    {
                        final int values[] = new int[count];
                        for (int vi=0; vi<count; ++vi) {
                            values[vi] = (Integer)vv.get(vi);
                        }
                        samp[si] = new ArchiveVNumberArray(time, severity, stat, display, values);
                    }
                }
            }
            else if (type == TYPE_STRING)
            {
                final String value = (String)vv.get(0);
                samp[si] = new ArchiveVString(time, severity, stat, value);
            }
            else if (type == TYPE_INT)
            {
                if (count == 1)
                {
                    final int value = (Integer)vv.get(0);
                    samp[si] = new ArchiveVNumber(time, severity, stat, display, value);
                }
                else
                {
                    final int values[] = new int[count];
                    for (int vi=0; vi<count; ++vi) {
                        values[vi] = (Integer)vv.get(vi);
                    }
                    samp[si] = new ArchiveVNumberArray(time, severity, stat, display, values);
                }
            } else {
                throw new Exception("Unknown value type " + type);
            }
        }
        return samp;
    }

    /**
     * @return Samples
     */
    public VType[] getSamples() {
        return samples;
    }
}
