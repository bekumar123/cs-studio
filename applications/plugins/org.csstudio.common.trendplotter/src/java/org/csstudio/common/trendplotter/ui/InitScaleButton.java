package org.csstudio.common.trendplotter.ui;

import org.csstudio.common.trendplotter.Messages;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;

/**
 * Button to initialze value axis with HOPR and LOPR from Epics records
 * for all PVs on active plot editor.
 * 
 * @author jhatje
 *
 */
public class InitScaleButton extends Button {
    /** Listener to invoke on button presses */
    private PlotListener listener = null;

    /** Initialize */
    public InitScaleButton() {
        Label text = new Label(Messages.InitScaleBtn);
        setContents(text);
        setToolTip(new Label(Messages.InitScaleButtonTT));
        addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                if (listener != null)
                    listener.initScaleRequested();
            }
        });
    }

    /** Add a listener that will be informed about scroll on/off requests */
    public void addPlotListener(final PlotListener listener)
    {
        if (this.listener != null)
            throw new IllegalStateException();
        this.listener = listener;
    }
}
