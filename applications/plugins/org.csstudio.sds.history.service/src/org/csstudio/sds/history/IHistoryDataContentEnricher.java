package org.csstudio.sds.history;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

/**
 * Implementing classes will update a {@link ProcessVariable} with new data.
 * 
 * @author Christian Mein
 *
 */
public interface IHistoryDataContentEnricher {

	/**
	 * Returns a new {@link ProcessVariable} with the latest value for the given time stamp. Also gives the
	 * implementation a chance to preload data for the given time interval.
	 * 
	 * @param timeStamp
	 *            the time stamp
	 * @param interval
	 *            time range that could be a good choice to preload data
	 * @return a new {@link ProcessVariable} with the latest value
	 */
	public ProcessVariable latestPvBefore(DateTime timeStamp, Interval interval);

}
