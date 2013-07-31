/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype.trendplotter;


import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VType;
import org.epics.util.time.Timestamp;

/** Base of archive-derived {@link VType} implementations
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveVType implements Alarm, Time, VType
{
    /** Alarm status message for 'OK' */
    final public static String STATUS_OK = "NO_ALARM";
    
	final private Timestamp timestamp;
	final private AlarmSeverity severity;
	final private String status;
    /** The data quality. */
    private final String quality;
	public ArchiveVType(final Timestamp timestamp,
			final AlarmSeverity severity, final String status)
	{
		this(timestamp, severity, status, Quality.Original.toString());
	
	}
	public ArchiveVType(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,String quality)
	{
		this.timestamp = timestamp;
		this.severity = severity;
		this.status = status;
		this.quality=quality;
	}
    /** Describe the data quality.
     *  <p>
     *  Control system data can originate directly from a front-end controller,
     *  or from a data history archive that stored such front-end controller
     *  values.
     *  We consider this the 'original' data.
     *  <p>
     *  Mid-level data servers or history data servers might also offer
     *  processed data, which reduces several 'original' samples to for example
     *  an 'averaged' sample. For those processed values, the time stamp
     *  actually no longer matches one specific instance in time when the
     *  front-end controller obtained a sample.
     *  <p>
     *  While the quality code does not fully describe what happened to the
     *  data, it provides a hint to for example a plotting tool, so that
     *  processed samples can be shown in a different way from original
     *  samples.
     */
    public enum Quality
    {
        /** This is a raw, original sample. */
        Original,

        /** This value is the result of interpolating 'original' samples.
         *  <p>
         *  There are several possible examples:
         *  <ul>
         *  <li>The data type was changed from 'double' to 'integer',
         *      with a possible loss off precision.
         *  <li>This sample results from linear interpolation between two
         *      original samples.
         *  <li>This sample results from averaging over several original
         *      values.
         *  </ul>
         */
        Interpolated
    }

    /** Get the quality of this value.
     *  @see Quality
     *  @return The quality.
     */
    public String getQuality(){
    	  return quality;
    }
	@Override
	public AlarmSeverity getAlarmSeverity() 
	{
		return severity;
	}

	@Override
	public String getAlarmName()
	{
		return status;
	}

	@Override
	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	@Override
	public Integer getTimeUserTag()
	{
		return 0;
	}

	@Override
	public boolean isTimeValid()
	{
		return timestamp.getSec() > 0;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = severity.hashCode();
		result = prime * result + status.hashCode();
		result = prime * result + timestamp.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		
		if (! (obj instanceof VType))
			return false;
		
		if (! (obj instanceof Alarm))
			return false;
		final Alarm alarm = (Alarm) obj;
		if (severity != alarm.getAlarmSeverity())
			return false;
		if (! status.equals(alarm.getAlarmName()))
			return false;
		
		if (! (obj instanceof Time))
			return false;

		final Time time = (Time) obj;
		return timestamp.equals(time.getTimestamp())
		    && getTimeUserTag() == time.getTimeUserTag();
	}
}
