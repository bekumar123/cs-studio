package org.csstudio.common.trendplotter.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.linearscale.Range;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotSampleCompressor {

    private static final int COMPRESSION_INTERVALS = 10;
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
        TimeDuration interval = getCompressionIntervalLength();
        _firstVisibleSampleIndex = firstVisibleSampleIndex();
        _lastVisibleSampleIndex = lastVisibleSampleIndex();
        LOG.debug("first visible sample index {}, time {}", _firstVisibleSampleIndex, _samples.get(_firstVisibleSampleIndex).getTime().toDate().toString());
        LOG.debug("last visible sample index {}, time {}", _lastVisibleSampleIndex, _samples.get(_lastVisibleSampleIndex).getTime().toDate().toString());
        LOG.debug("compression interval {}", interval.getSec());
        if ((_firstVisibleSampleIndex > 0) || (_lastVisibleSampleIndex > 0)) {
            checkAndCleanupIntervals(interval);
        }
        if (_samples.size() > TOTAL_SAMPLE_THRESHOLD) {
            removeSamplesNotVisible();
        }
//        LOG.trace("sample size {}", _samples.size());
//        long lowerSec = (long) (sampleTimeRange.getLower()/1000.0);
//        Timestamp lower = Timestamp.of(lowerSec, 0);
//        Date dateLower = lower.toDate();
//        long upperSec = (long) (sampleTimeRange.getUpper()/1000.0);
//        Timestamp upper = Timestamp.of(upperSec, 0);
//        Date dateUpper = upper.toDate();
//        LOG.trace("range lower {}, upper {}", dateLower.toString(), dateUpper.toString());
//        LOG.trace("range time axis lower {}, upper {}", _prov.getModelStartTime().toDate().toString(), _prov.getModelEndTime().toDate().toString());
//        sampleTimeRange.getUpper();
    }

    private void removeSamplesNotVisible() {
        if (_lastVisibleSampleIndex < _samples.size()+1) {
            for (int i=_lastVisibleSampleIndex+1; i<_samples.size(); i++) {
                _samples.remove(i);
            }
        }
        if (_firstVisibleSampleIndex > 0) {
           _samples.subList(0, _firstVisibleSampleIndex-1).clear(); 
        }
        
    }

    private void checkAndCleanupIntervals(TimeDuration interval) {
        Timestamp intervalEnd = _samples.get(_firstVisibleSampleIndex).getTime().plus(interval);
        int sampleIndex = _firstVisibleSampleIndex;
        List<PlotSample> samplesToDelete = new ArrayList<>();
        while (sampleIndex < _samples.size()) {
//            //The next interval for compression can start with the next sample. (If it starts at the end
//            //of the last interval there can be intervals with no samples)
//            Timestamp intervalEnd = _samples.get(sampleIndex).getTime().plus(interval);
            intervalEnd = intervalEnd.plus(interval);
            if (intervalEnd.compareTo(_samples.get(_lastVisibleSampleIndex).getTime()) >= 0) {
                LOG.trace("end Interval");
                intervalEnd = _samples.get(_lastVisibleSampleIndex).getTime();
                sampleIndex = findSamplesToDeleteAndMoveIndex(samplesToDelete, intervalEnd, sampleIndex);
                break;
            }
            LOG.trace("sample index {}, Compression Interval End {}, sec {}", sampleIndex, intervalEnd.toDate().toString(), intervalEnd.getSec());
            sampleIndex = findSamplesToDeleteAndMoveIndex(samplesToDelete, intervalEnd, sampleIndex);
            if (sampleIndex > _lastVisibleSampleIndex) {
                break;
            }
            sampleIndex++;
        }
        _samples.removeAll(samplesToDelete);
    }
    

    private int findSamplesToDeleteAndMoveIndex(List<PlotSample> samplesToDelete, Timestamp intervalEnd, int sampleIndex) {
        PlotSample plotSample = _samples.get(sampleIndex);
        PlotSample highestValue = plotSample;
        PlotSample lowestValue = plotSample;
        boolean firstValueIsHighest = true;
        boolean firstValueIsLowest = true;
        boolean highestLowestSameValue = true;
        while (plotSample.getTime().compareTo(intervalEnd) != 1) {
            sampleIndex++;
            if (sampleIndex > _lastVisibleSampleIndex) {
                break;
            }
            plotSample = _samples.get(sampleIndex);
            if (plotSample.getYValue() > highestValue.getYValue()) {
                LOG.trace("samples index {}, highest", sampleIndex);
                if (highestLowestSameValue == false) {
                    samplesToDelete.add(highestValue);
                }
                highestLowestSameValue = false;
                highestValue = plotSample;
            }
            if (plotSample.getYValue() < lowestValue.getYValue()) {
                LOG.trace("samples index {}, lowest", sampleIndex);
                if (highestLowestSameValue == false) {
                    samplesToDelete.add(lowestValue);
                }
                highestLowestSameValue = false;
                lowestValue = plotSample;
            }
        }
        LOG.trace("sample.size {}, samples index {}, size samples to delete {}", _samples.size(), sampleIndex, samplesToDelete.size());
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
