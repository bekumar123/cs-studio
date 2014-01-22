package org.csstudio.common.trendplotter.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.linearscale.Range;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotSampleCompressor {

    private static final int COMPRESSION_INTERVALS = 1000;
    private static final int TOTAL_SAMPLE_THRESHOLD = 10000;
    private List<PlotSample> _samples;
    private IIntervalProvider _prov;
    
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlotSampleCompressor.class);
    private int _firstVisibleSampleIndex;
    private int _lastVisibleSampleIndex;

    public PlotSampleCompressor(List<PlotSample> samples, IIntervalProvider prov) {
        _samples = samples;
        _prov = prov;
    }

    public void compressSamples() {
        LOG.debug("Timestamp {}", System.currentTimeMillis());
        _firstVisibleSampleIndex = firstVisibleSampleIndex();
        _lastVisibleSampleIndex = lastVisibleSampleIndex();
        TimeDuration interval = getCompressionIntervalLength();
        LOG.debug("first visible sample index {}, time {}", _firstVisibleSampleIndex, _samples.get(_firstVisibleSampleIndex).getTime().toDate().toString());
        LOG.debug("last visible sample index {}, time {}", _lastVisibleSampleIndex, _samples.get(_lastVisibleSampleIndex).getTime().toDate().toString());
        LOG.debug("compression interval {}", interval.getSec());
        if ((_firstVisibleSampleIndex > 0) || (_lastVisibleSampleIndex > 0)) {
            checkAndCleanupIntervals(interval);
        }
        if (_samples.size() > TOTAL_SAMPLE_THRESHOLD) {
            removeSamplesNotVisible();
        }
        LOG.debug("Timestamp {}", System.currentTimeMillis());
    }

    private void removeSamplesNotVisible() {
        if (_lastVisibleSampleIndex < _samples.size()+1) {
            for (int i=_lastVisibleSampleIndex+1; i<_samples.size(); i++) {
                _samples.remove(i);
            }
        }
        //Do not delete the last not visible sample otherwise the trend starts with an empty
        //space until the first visible sample.
        if (_firstVisibleSampleIndex > 0) {
           _samples.subList(0, _firstVisibleSampleIndex-2).clear(); 
        }
        
    }

    private void checkAndCleanupIntervals(TimeDuration interval) {
        int sampleIndex = _firstVisibleSampleIndex;
        Timestamp intervalEnd = _prov.getModelStartTime().plus(interval);
//        Timestamp intervalEnd = _samples.get(sampleIndex).getTime().plus(interval);
        List<PlotSample> samplesToDelete = new ArrayList<>();
        while (sampleIndex < _lastVisibleSampleIndex) {
            sampleIndex = findSamplesToDeleteAndMoveIndex(samplesToDelete, intervalEnd, sampleIndex);
            LOG.trace("sample index {}, Compression Interval End {}, # delete {}", sampleIndex, intervalEnd.toDate().toString(), samplesToDelete.size());
            intervalEnd = intervalEnd.plus(interval);
        }
        _samples.removeAll(samplesToDelete);
        LOG.trace("sample size after deletion {}", _samples.size());
    }
    

    private int findSamplesToDeleteAndMoveIndex(List<PlotSample> samplesToDelete, Timestamp intervalEnd, int sampleIndex) {
        PlotSample plotSample = _samples.get(sampleIndex);
        samplesToDelete.add(plotSample);
        PlotSample highestValue = plotSample;
        PlotSample lowestValue = plotSample;
//        LOG.trace("sind {}, ps y {}, hs y {}, ls y {}", sampleIndex, plotSample.getYValue(), highestValue.getYValue(), lowestValue.getYValue());
        while (plotSample.getTime().compareTo(intervalEnd) != 1) {
            sampleIndex++;
            if (sampleIndex > _lastVisibleSampleIndex) {
                break;
            }
            plotSample = _samples.get(sampleIndex);
            if (plotSample.getYValue() > highestValue.getYValue()) {
                highestValue = plotSample;
            }
            if (plotSample.getYValue() < lowestValue.getYValue()) {
                lowestValue = plotSample;
            }
//            LOG.trace("sind {}-{}, ps y {}, hs y {}, ls y {}", sampleIndex, plotSample.getTime().toDate(), plotSample.getYValue(), highestValue.getYValue(), lowestValue.getYValue());
            samplesToDelete.add(plotSample);
        }
        samplesToDelete.remove(lowestValue);
        samplesToDelete.remove(highestValue);
        return sampleIndex;
    }

    private int lastVisibleSampleIndex() {
        Timestamp modelEndTime = _prov.getModelEndTime();
        for (int i=_samples.size()-1; i>0; i--) {
            Timestamp time = _samples.get(i).getTime();
            if (time.compareTo(modelEndTime) == -1) {
                return i;
            };
        }
        return -1;
    }

    /**
     * find index of first visible sample of in sample list.
     */
    private int firstVisibleSampleIndex() {
        Timestamp modelStartTime = _prov.getModelStartTime();
        for (int i=0; i<_samples.size(); i++) {
            Timestamp time = _samples.get(i).getTime();
            if (time.compareTo(modelStartTime) == 1) {
                return i;
            };
        }
        return -1;
    }

    /**
     * Calculates the interval length in seconds dependent on x-Axis start/end time. 
     * After compression in each interval should be at most two samples (highest and lowest).
     * The interval length should be about 2 pixel.
     * 
     * @return interval length in seconds
     */
    private TimeDuration getCompressionIntervalLength() {
        TimeDuration interval = (_prov.getModelEndTime().durationFrom(_prov.getModelStartTime()));
        LOG.debug("Visible interval length {}", interval.getSec());
        return interval.dividedBy(COMPRESSION_INTERVALS);
    }

}
