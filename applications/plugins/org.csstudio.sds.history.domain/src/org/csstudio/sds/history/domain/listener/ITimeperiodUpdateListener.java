package org.csstudio.sds.history.domain.listener;

import org.joda.time.DateTime;

public interface ITimeperiodUpdateListener {
	
	public void setTimePeriod(DateTime start, DateTime end);
}
