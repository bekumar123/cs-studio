/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.export;

import org.csstudio.archive.vtype.Style;
import org.csstudio.common.trendplotter.Messages;

import org.epics.vtype.VType;
import org.csstudio.archive.vtype.VTypeHelper;


/** Format an IValue to show the value as well as the severity/status
 *  @author Kay Kasemir
 */
public class ValueWithInfoFormatter extends ValueFormatter
{
    /** Initialize
     *  @param format Number format to use
     *  @param precision Precision
     */
    public ValueWithInfoFormatter(final Style style, final int precision)
    {
        super(style, precision);
    }

    /** {@inheritDoc} */
    @Override
    public String getHeader()
    {
        return super.getHeader() + Messages.Export_Delimiter + Messages.SeverityColumn +
            Messages.Export_Delimiter + Messages.StatusColumn;
    }

    /** {@inheritDoc} */
    @Override
    public String format(final VType value)
    {
        if (Double.isNaN(VTypeHelper.toDouble(value)))
            return super.format(null) +
                Messages.Export_Delimiter + Messages.Export_NoValueMarker +
                Messages.Export_Delimiter + Messages.Export_NoValueMarker;
        return super.format(value) + Messages.Export_Delimiter +
            VTypeHelper.getSeverity(value) + Messages.Export_Delimiter +
            VTypeHelper.getMessage(value);
    }
}