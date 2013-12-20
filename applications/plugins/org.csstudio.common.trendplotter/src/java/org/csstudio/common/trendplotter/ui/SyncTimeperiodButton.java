/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.ui;

import org.csstudio.common.trendplotter.Messages;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;

/** Button to push the actual displayed timeperiod to the history control view.
 *  @author Christian Mein
 */
public class SyncTimeperiodButton extends Button
{
    /** Listener to invoke on button presses */
    private PlotListener plotListener = null;

    /** Initialize */
    public SyncTimeperiodButton()
    {
        Label text = new Label(Messages.SyncTimeperiodBtn);
        setContents(text);
        setToolTip(new Label(Messages.SyncTimeperiodBtnTT));
        addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                if (plotListener != null)
                    plotListener.syncTimeperiodWithHistoryControl();
            }
        });
    }

    /** Add a listener that will be informed about scroll on/off requests */
    public void addPlotListener(final PlotListener listener)
    {
        if (this.plotListener != null)
            throw new IllegalStateException();
        this.plotListener = listener;
    }
}
