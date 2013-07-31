/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.util.List;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

import com.google.common.collect.Lists;

/** Unit-test helper for creating samples
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestSampleBuilder
{
    final public static ISeverity ok = ValueFactory.createOKSeverity();
    // final private static long start = System.currentTimeMillis();

    /** @param i Numeric value as well as pseudo-timestamp
     *  @return IValue sample that has value and time based on input parameter
     */
    public static IValue makeValue(final int i)
    {
        // return ValueFactory.createDoubleValue(TimestampFactory.fromMillisecs(start + i*500),
      /*  return ValueFactory.createDoubleValue(TimestampFactory.fromDouble(i),
               ok, "Test", PlotSample.dummy_meta, IValue.Quality.Original, new double[] { i });*/
        return null;
    }

    /** @param i Pseudo-timestamp
     *  @return IValue sample that has error text with time based on input parameter
     */
    public static IValue makeError(final int i, final String error)
    {
        final ISeverity no_value = new ISeverity()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean hasValue()  { return false; }
            @Override
            public boolean isInvalid() { return true;  }
            @Override
            public boolean isMajor()   { return false; }
            @Override
            public boolean isMinor()   { return false; }
            @Override
            public boolean isOK()      { return false; }
            @Override
            public String toString()   { return "INVALID"; }
        };
        // return ValueFactory.createDoubleValue(TimestampFactory.fromMillisecs(start + i*500),
      /*  return ValueFactory.createDoubleValue(TimestampFactory.fromDouble(i),
                no_value, error, PlotSample.dummy_meta, IValue.Quality.Original,
                new double[] { Double.NaN });*/
        return null;
    }


    /** @param i Numeric value as well as pseudo-timestamp
     *  @return IValue sample that has value and time based on input parameter
     */
    public static PlotSample makePlotSample(final int i)
    {
      //  return new PlotSample("Test", makeValue(i));
        return null;
    }

    /** Create array of samples
     *  @param start First value/time stamp
     *  @param end   Last value/time stamp (exclusive)
     */
    public static PlotSample[] makePlotSamples(final int start, final int end)
    {
        final int N = end - start;
        final PlotSample result[] = new PlotSample[N];
        for (int i=0; i<N; ++i) {
            result[i] = makePlotSample(start + i);
        }
        return result;
    }
    public static List<PlotSample> makePlotSamplesWithStep(final int number,
                                                           final int offset,
                                                           final int stepsize) {
        final List<PlotSample> result = Lists.newArrayListWithCapacity(number);
        for (int i = 0; i < number; ++i) {
            result.add(makePlotSample(offset + i*stepsize));
        }
        return result;

    }
}
