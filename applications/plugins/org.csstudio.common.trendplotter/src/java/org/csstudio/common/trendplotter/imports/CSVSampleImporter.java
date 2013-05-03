/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.imports;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;

import org.epics.vtype.Alarm;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VDouble;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** {@link SampleImporter} for Command (space, tab) separated value file of time, value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CSVSampleImporter implements SampleImporter
{
    final private Logger logger = Logger.getLogger(getClass().getName());
    final private Alarm ok = ValueFactory.alarmNone();
    final private Display display =ValueFactory.displayNone();
         //   ValueFactory.newDisplay(new Double(0), new Double(0), new Double(0), "", NumberFormats.toStringFormat(), new Double(10), new Double(10),new Double(10), new Double(10), new Double(10));//(0, 10, 0, 0, 0, 0, -1, "");

    /** {@inheritDoc} */
    @Override
    public List<VType> importValues(final InputStream input) throws Exception
    {
        // To be reentrant, need per-call parsers
        final DateFormat date_parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final Pattern pattern = Pattern.compile(
                //    YYYY-MM-DD HH:MM:SS.SSS
                "\\s*([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9][0-9][0-9])[ \\t,]+([-+0-9.e]+)\\s*");

        final List<VType> values = new ArrayList<VType>();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null)
        {
            line = line.trim();
            // Skip empty lines
            if (line.length() <= 0)
                continue;
            // Locate time and value
            final Matcher matcher = pattern.matcher(line);
            if (! matcher.matches())
            {
                logger.log(Level.INFO, "Ignored input: {0}", line);
                continue;
            }
            // Parse
            final Date date = date_parser.parse(matcher.group(1));
            final double number = Double.parseDouble(matcher.group(2));
            // Turn into IValue
            final  Time time =ValueFactory.newTime(Timestamp.of(date));
            final  VDouble value = ValueFactory.newVDouble(number , ok, time, display);
            values.add(value);
        }
        reader.close();

        return values;
    }
}
