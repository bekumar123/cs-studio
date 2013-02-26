/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.epics.util.time.Timestamp;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.VType;

/** Helper for transforming samples/values
 *  @author Kay Kasemir
 */
public class ValueButcher
{
    /** Create new value with specific time stamp
     *  @param value Original Value
     *  @param time Desired time stamp
     *  @return New value with given time stamp
     */
    public static VType changeTimestamp(final VType value,
            final Timestamp time)
    {
      /*  final ISeverity severity = value.getSeverity();
        final String status = value.getStatus();
        final Quality quality = value.getQuality();
        final IMetaData meta = value.getMetaData();
        if (value instanceof IDoubleValue)
            return ValueFactory.createDoubleValue(time , severity, status,
                            (INumericMetaData)meta, quality,
                            ((IDoubleValue)value).getValues());
        else if (value instanceof ILongValue)
            return ValueFactory.createLongValue(time, severity, status,
                            (INumericMetaData)meta, quality,
                            ((ILongValue)value).getValues());
        else if (value instanceof IEnumeratedValue)
            return ValueFactory.createEnumeratedValue(time, severity, status,
                            (IEnumeratedMetaData)meta, quality,
                            ((IEnumeratedValue)value).getValues());
        else if (value instanceof IStringValue)
            return ValueFactory.createStringValue(time, severity, status,
                            quality, ((IStringValue)value).getValues());
        // Else: Log unknown data type as text
        return ValueFactory.createStringValue(time, severity, status,
                quality, new String[] { value.toString() });*/
        return null;
    }

    /** Create new value with 'now' as time stamp
     *  @param value Original Value
     *  @return New value with 'now' as time stamp
     */
    public static  VType changeTimestampToNow(final VType value)
    {
        return changeTimestamp(value, Timestamp.now());
    }

    /** Create new sample with 'now' as time stamp
     *  @param value Original sample
     *  @return New sample with 'now' as time stamp
     */
    public static PlotSample changeTimestampToNow(final PlotSample sample)
    {
        return new PlotSample(sample.getSource(), changeTimestampToNow(sample.getValue()));
    }
}
