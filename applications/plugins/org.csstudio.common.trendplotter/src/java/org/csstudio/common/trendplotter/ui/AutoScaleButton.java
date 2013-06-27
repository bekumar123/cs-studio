package org.csstudio.common.trendplotter.ui;

import org.csstudio.common.trendplotter.Activator;
import org.csstudio.common.trendplotter.Messages;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;

/**
 * Button to set autoscale for all PVs on active plot editor.
 * 
 * @author jhatje
 *
 */
public class AutoScaleButton extends Button {
    /** Listener to invoke on button presses */
    private PlotListener listener = null;

    /** Initialize */
    public AutoScaleButton() {
        Label icon = new Label(Activator.getDefault().getImage("icons/autoscale.ico"));
        setContents(icon);
        setToolTip(new Label(Messages.AutoScaleButtonTT));
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                if (listener != null)
                    listener.autoScaleRequested();
            }
        });
    }

    /** Add a listener that will be informed about scroll on/off requests */
    public void addPlotListener(final PlotListener listener) {
        if (this.listener != null)
            throw new IllegalStateException();
        this.listener = listener;
    }
}
