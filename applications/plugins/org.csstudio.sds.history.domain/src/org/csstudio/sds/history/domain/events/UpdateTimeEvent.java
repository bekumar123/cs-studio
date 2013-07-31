package org.csstudio.sds.history.domain.events;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class UpdateTimeEvent {
	
	private static final DateTimeFormatter FORMAT = ISODateTimeFormat.basicDateTimeNoMillis();
	
	private final DateTime timeStamp;
	private final Object eventSource;
	private final boolean doUpdateData;
	private final Interval interval;
	
	public UpdateTimeEvent(DateTime timeStamp, Object eventSource, boolean updateData, Interval interval) {
		this.timeStamp = timeStamp;
		this.eventSource = eventSource;
		this.doUpdateData = updateData;
		this.interval = interval;
	}
	
	public DateTime getTimeStamp() {
		return timeStamp;
	}
	public Object getEventSource() {
		return eventSource;
	}
	public boolean doUpdateData() {
		return doUpdateData;
	}
	public Interval getInterval() {
		return interval;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Event source: ").append(eventSource.getClass().getName());
		result.append(" Time: ").append(FORMAT.print(timeStamp));
		result.append(" Update data: ").append(doUpdateData);
		result.append(" Inteval: ").append(FORMAT.print(interval.getStart()));
		result.append(" - ").append(FORMAT.print(interval.getEnd()));
		
		return result.toString();
	}
	
	
	
	

}
