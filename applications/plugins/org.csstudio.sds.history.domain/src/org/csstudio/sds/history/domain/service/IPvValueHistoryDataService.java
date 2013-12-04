package org.csstudio.sds.history.domain.service;

import java.util.NavigableMap;

import javax.annotation.Nullable;

import org.csstudio.sds.history.domain.HistoryArchiveSample;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public interface IPvValueHistoryDataService {
	
	@Nullable
	public HistoryArchiveSample getLatestValueBefore(String csAddress, DateTime time);
	
	public NavigableMap<DateTime, HistoryArchiveSample> getSamples(String csAddress, Interval interval);
	
}
