package org.csstudio.sds.history.domain.service;

import java.util.NavigableMap;

import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;

public interface IPvValueHistoryDataService {
	
	@Nullable
	public IPlantUnitValue<?> getLatestValueBefore(String csAddress, DateTime time);
	
	public NavigableMap<DateTime, IPlantUnitValue<?>> getSamples(String csAddress, Interval interval);
	
}
