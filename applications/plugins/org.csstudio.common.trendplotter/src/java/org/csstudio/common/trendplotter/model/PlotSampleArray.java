/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.util.ArrayList;

import org.epics.vtype.VType;

/** Plain array implementation of PlotSamples
 *  @author Kay Kasemir
 */
public class PlotSampleArray extends PlotSamples
{
    private PlotSample samples[];

    /** {@inheritDoc} */
    @Override
    synchronized public int getSize()
    {
        return samples.length;
    }

    /** {@inheritDoc} */
    @Override
    synchronized public PlotSample getSample(int index)
    {
        return samples[index];
    }

    /** @param source Source of the values
     *  @param values Values from which to set the sample array
     */
    synchronized public void set(final String source, final ArrayList<VType> values)
    {
        samples = new PlotSample[values.size()];
        for (int i = 0; i < samples.length; ++i)
            samples[i] = new PlotSample(source, values.get(i));
        have_new_samples = true;
    }
}
