/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.CheckForNull;

import org.csstudio.swt.xygraph.linearscale.Range;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Buffer for 'live' samples, i.e. those not originating from
 *  <p>
 *  New samples are always added to the end of a ring buffer.
 *
 *  @author Kay Kasemir
 */
public class LiveSamples extends PlotSamples {

private static final int NEW_SAMPLE_THRESHOLD = 30;

    //    protected LimitedArrayCircularQueue<PlotSample> _samples;
    protected List<PlotSample> _samples;

    /** Waveform index */
    private int waveform_index = 0;
    
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(LiveSamples.class);

    private IIntervalProvider _prov;

    private PlotSampleCompressor _compressor;

    /** Number of samples added after last compression */
    private int _newSampleNumber = 0;
    
    /**
     * Constructor.
     * @param prov 
     * @param _compressor 
     */
    public LiveSamples(final int capacity, IIntervalProvider prov) {
        _prov = prov;
        LOG.trace("Constructor live sample with capacity {}", capacity);
        _samples = new ArrayList<PlotSample>();
        _compressor = new PlotSampleCompressor(_samples, prov);
    }

    /** @param index Waveform index to show */
    synchronized public void setWaveformIndex(int index)
    {
        waveform_index = index;

        // Change the index of all samples in this instance
        for (int i=0; i<_samples.size(); i++) {
            _samples.get(i).setWaveformIndex(waveform_index);
        }
    }
    
    /** @return Maximum number of samples in ring buffer */
    synchronized public int getCapacity() {
        return 5;//_samples.getCapacity();
    }

    /** Set new capacity.
     *  <p>
     *  Tries to preserve the newest samples.
     *  @param new_capacity New sample count capacity
     *  @throws Exception on out-of-memory error
     */
    synchronized public void setCapacity(final int new_capacity) throws Exception {
//        _samples.setCapacity(new_capacity);
    }

    /** @param sample Sample to add to circular buffer */
    protected synchronized void add(final PlotSample sample) {
        sample.setWaveformIndex(waveform_index);
        sample.setDeadband(deadband);
        _samples.add(sample);
        _newSampleNumber++;
        //We can't use _sample.size() to start compression because not the number of 
        //all but of the visible samples is of interest.
        if(_newSampleNumber > NEW_SAMPLE_THRESHOLD) {
            LOG.debug("start compression, {} new samples added", _newSampleNumber);
            _newSampleNumber = 0;
            _compressor.compressSamples();
        }
        have_new_samples = true;
    }

    @Override
    synchronized public int getSize() {
        return _samples.size();
    }

    @Override
    @CheckForNull
    synchronized public PlotSample getSample(final int i) {
        return _samples.get(i);
    }

    /** Delete all samples */
    synchronized public void clear() {
        _samples.clear();
    }
}
