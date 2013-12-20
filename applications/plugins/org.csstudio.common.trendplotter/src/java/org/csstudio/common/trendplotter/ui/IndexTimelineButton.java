/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.ui;

import org.csstudio.common.trendplotter.Activator;
import org.csstudio.common.trendplotter.Messages;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToggleButton;

/** Button (Draw2d) for turning the time index line off/on
 *  @author Christian Mein
 */
public class IndexTimelineButton extends ToggleButton {
    private static final String OFF_ICON = "icons/timeIndexline_off.gif"; //$NON-NLS-1$
    private static final String ON_ICON = "icons/timeIndexline_on.gif"; //$NON-NLS-1$
    
    /** Label that shows one of the ICONs */
    private Label icon;
    
    /** Listener to invoke on button presses */
    private PlotListener listener = null;
    
    /** Used to remember the button state */
    private boolean showTimeIndexLine;
    
    /** Initialize */
    public IndexTimelineButton() {
        icon = new Label(Activator.getDefault().getImage(OFF_ICON));
        showTimeIndexLine = false;
        setContents(icon);
        setToolTip(new Label(Messages.TimeIndexlineBtnTT));
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                setButtonState(isSelected());
                if (listener != null)
                    listener.setIndexTimeline(showTimeIndexLine);
            }
        });
    }
    
    /** Add a listener that will be informed about time index line on/off requests */
    public void addPlotListener(final PlotListener listener) {
        if (this.listener != null)
            throw new IllegalStateException();
        this.listener = listener;
    }
    
    /** Update time index line button to reflect the desired scroll mode
     *  @param on <code>true</code> when scrolling is 'on'
     */
    private void setButtonState(final boolean scroll_on) {
        if (scroll_on)
            icon.setIcon(Activator.getDefault().getImage(ON_ICON));
        else
            icon.setIcon(Activator.getDefault().getImage(OFF_ICON));
        this.showTimeIndexLine = scroll_on;
    }
}
