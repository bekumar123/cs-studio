/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.archive.vtype.trendplotter.ArchiveVDisplayType;
import org.csstudio.archive.vtype.trendplotter.ArchiveVEnum;
import org.csstudio.archive.vtype.trendplotter.ArchiveVNumber;
import org.csstudio.archive.vtype.trendplotter.ArchiveVNumberArray;
import org.csstudio.archive.vtype.trendplotter.ArchiveVStatistics;
import org.csstudio.archive.vtype.trendplotter.ArchiveVString;
import org.epics.util.time.Timestamp;
import org.epics.vtype.*;

/** Helper for transforming samples/values
 *  @author Kay Kasemir
 */
public class ValueButcher
{
    /** Create new value with specific time stamp
     *  @param value Original Value
     *  @param time Desired time stamp
     *  @return New value with given time stamp
     */
    public static VType changeTimestamp(final VType value,
            final Timestamp time)
    {
        Display d= getDisplay(value);
       
        if(value instanceof ArchiveVNumber ||value instanceof VDouble  || value instanceof VFloat ||value instanceof VInt || value instanceof VByte  || value instanceof VShort  ){
            VNumber vt=(VNumber)value;
            AlarmSeverity severity = vt.getAlarmSeverity();
            String status = vt.getAlarmName();
            return new ArchiveVNumber(time, severity, status,  d, (Number)value);
        }
        if(value instanceof ArchiveVNumberArray || value instanceof VDoubleArray  || value instanceof VFloatArray ||value instanceof VIntArray || value instanceof VByteArray  || value instanceof VShortArray  ){
            VNumberArray vt=(VNumberArray)value;
            AlarmSeverity severity = vt.getAlarmSeverity();
            String status = vt.getAlarmName();
            return new ArchiveVNumberArray(time, severity, status, d, vt.getData());
        }
        if(value instanceof ArchiveVStatistics ||value instanceof VStatistics){
            VStatistics vt=(VStatistics)value;
            AlarmSeverity severity = vt.getAlarmSeverity();
            String status = vt.getAlarmName();
            return new ArchiveVStatistics(time, severity, status,d, vt.getAverage(),vt.getNSamples(),vt.getMax(),vt.getStdDev(),vt.getNSamples());
        }
        if(value instanceof ArchiveVEnum){
            ArchiveVEnum vt=(ArchiveVEnum)value;
            AlarmSeverity severity = vt.getAlarmSeverity();
            String status = vt.getAlarmName();
            return new ArchiveVEnum(time, severity, status, vt.getLabels(),vt.getIndex());
        }
        if(value instanceof ArchiveVString){
            ArchiveVString vt=(ArchiveVString)value;
            AlarmSeverity severity = vt.getAlarmSeverity();
            String status = vt.getAlarmName();
            return new ArchiveVString(time, severity, status, vt.getValue());
        }
     
    //    ValueFactory
     /*  AlarmSeverity severity = value.getSeverity();
         String status = value.getStatus();
         Quality quality = value.getQuality();
         Display meta = value.getMetaData();*/
      /*  if (value instanceof IDoubleValue)
            return ValueFactory.createDoubleValue(time , severity, status,
                            (INumericMetaData)meta, quality,
                            ((IDoubleValue)value).getValues());
        else if (value instanceof ILongValue)
            return ValueFactory.createLongValue(time, severity, status,
                            (INumericMetaData)meta, quality,
                            ((ILongValue)value).getValues());
        else if (value instanceof IEnumeratedValue)
            return ValueFactory.createEnumeratedValue(time, severity, status,
                            (IEnumeratedMetaData)meta, quality,
                            ((IEnumeratedValue)value).getValues());
        else if (value instanceof IStringValue)
            return ValueFactory.createStringValue(time, severity, status,
                            quality, ((IStringValue)value).getValues());
        // Else: Log unknown data type as text
        return ValueFactory.createStringValue(time, severity, status,
                quality, new String[] { value.toString() });*/
        return ValueFactory.wrapValue(value);
    }

    /** Create new value with 'now' as time stamp
     *  @param value Original Value
     *  @return New value with 'now' as time stamp
     */
    public static  VType changeTimestampToNow(final VType value)
   
    {
       
        return changeTimestamp(value, Timestamp.now());
    }

    /** Create new sample with 'now' as time stamp
     *  @param value Original sample
     *  @return New sample with 'now' as time stamp
     */
    public static PlotSample changeTimestampToNow(final PlotSample sample)
    {
        return new PlotSample(sample.getSource(), VTypeHelper.transformTimestampToNow(sample.getValue()));
    }
    static Display getDisplay(final VType value){
       if(value instanceof ArchiveVDisplayType ) {
           ArchiveVDisplayType vt=(ArchiveVDisplayType)value;
           return ValueFactory.newDisplay(vt.getLowerDisplayLimit(), vt.getLowerAlarmLimit(),
                                          vt.getLowerWarningLimit(), vt.getUnits(), vt.getFormat(), 
                                          vt.getUpperWarningLimit(), vt.getUpperAlarmLimit(), 
                                          vt.getUpperDisplayLimit(), vt.getLowerCtrlLimit(), 
                                          vt.getUpperCtrlLimit());
       } 
        
       return ValueFactory.displayNone(); 
    }
}
