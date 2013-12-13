
/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.utility.screenshot;

import java.util.Hashtable;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.csstudio.utility.screenshot.util.PaintSurface;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller based on the SWT paint example
 *
 */
public class ScreenshotWorker {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotWorker.class);

    private Hashtable<String, String> messageContent;
    private final Display display;
    private final Composite mainComposite;
    private Color paintColorBlack, paintColorWhite; // alias for paintColors[0] and [1]
    private Color[] paintColors;
    private ImageBundle imageBundle;
    private PaintSurface paintSurface; // paint surface for drawing
    private int restart;

    /** */
    private final int startDelay;

    /** */
    private int timer;

    /** */
    private final boolean restartWithBeep;
    private boolean beep;

    private static final int numPaletteRows = 3;
    private static final int numPaletteCols = 50;

    public ScreenshotWorker(final Composite p) {
        this.mainComposite = p;
        this.display = PlatformUI.getWorkbench().getDisplay();
        messageContent = null;
        imageBundle = null;
        restart = 0;
        startDelay = 0;
        timer = 0;
        restartWithBeep = false;
        beep = false;
        initResources();
        initActions();
        init();
        capture();
        createGUI(p);
    }

    /**
     * Sets the default tool item states.
     */
    public void setDefaults() {
    	// ?
    }

    /**
     * Creates the GUI.
     */
    public void createGUI(final Composite parent) {

        /*** Create principal GUI layout elements ***/
        final Composite displayArea = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        displayArea.setLayout(gridLayout);

        // Creating these elements here avoids the need to instantiate the GUI elements
        // in strict layout order.  The natural layout ordering is an artifact of using
        // SWT layouts, but unfortunately it is not the same order as that required to
        // instantiate all of the non-GUI application elements to satisfy referential
        // dependencies.  It is possible to reorder the initialization to some extent, but
        // this can be very tedious.

        // paint canvas
        final Canvas paintCanvas = new Canvas(displayArea, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
        final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        paintCanvas.setLayoutData(gridData);
        // paintCanvas.setBackground(paintColorWhite);

        // status text
        // final Text statusText = new Text(displayArea, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
        // gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
        // statusText.setLayoutData(gridData);

        /*** Create the remaining application elements inside the principal GUI layout elements ***/
        // paintSurface
        // paintSurface = new SWTPaintSurface(paintCanvas, statusText, paintColorWhite, imageBundle);
        paintSurface = new PaintSurface(paintCanvas, imageBundle);
    }

    /**
     * Disposes of all resources associated with a particular
     * instance of the ScreenshotWorker.
     */
    public void dispose() {
        if (paintSurface != null) {
            paintSurface.dispose();
        }

        if(imageBundle != null) {
            imageBundle.dispose();
            imageBundle = null;
        }

        paintColors = null;
        paintSurface = null;

        freeResources();
    }

    /**
     * Frees the resource bundle resources.
     */
    public void freeResources() {
    	//TODO:
    }

    /**
     * Returns the Display.
     *
     * @return the display we're using
     */
    public Display getDisplay() {
        return mainComposite.getDisplay();
    }

    /**
     * Initialize colors, fonts, and tool settings.
     */
    private void init() {

    	final Display disp = mainComposite.getDisplay();

        paintColorWhite = new Color(disp, 255, 255, 255);
        paintColorBlack = new Color(disp, 0, 0, 0);

        paintColors = new Color[numPaletteCols * numPaletteRows];
        paintColors[0] = paintColorBlack;
        paintColors[1] = paintColorWhite;
        for (int i = 2; i < paintColors.length; i++) {
            paintColors[i] = new Color(disp,
                i * 7 % 255, i * 23 % 255, i * 51 % 255);
        }
    }

    /**
     * Sets the action field of the tools
     */
    private void initActions() {
    	//TODO:
    }

    /**
     * Loads the image resources.
     */

    public void initResources() {
    	//TODO:
    }

    /**
     * Grabs input focus.
     */
    public void setFocus() {
        mainComposite.setFocus();
    }

    public void capture() {
        if(startDelay == 0) {
            processCapture();
            return;
        }

        if(restartWithBeep) {
            timer = 0;
            display.beep();
            display.timerExec(1000, new Runnable() {
                public void run() {
                    beep();
                }
            });
        } else {
            display.timerExec(startDelay, new Runnable() {
                    public void run() {
                        processCapture();
                    }
            });
        }
    }

    public void beep() {
        timer += 1000;

        if(timer < startDelay) {
            display.beep();
            display.timerExec(1000, new Runnable() {
                public void run() {
                    beep();
                }
            });
        } else {
            processCapture();
        }
    }

    public void processCapture() {
        final ImageCreator ic = new ImageCreator(this.display);
        imageBundle = new ImageBundle();
        ic.captureImages(imageBundle);
    }

    public boolean performPrint(final Shell shell, final Image image) {

        final PrintDialog dialog = new PrintDialog(shell);
        final PrinterData pd = dialog.open();
        boolean success = false;

        if(pd != null) {
            final Printer printer = new Printer(pd);

            final Rectangle bounds = image.getBounds();
            final Rectangle area = printer.getClientArea();
            final Point dpi = printer.getDPI();
            final int xScale = dpi.x / 96;
            final int yScale = dpi.y / 96;
            final int width = bounds.width * xScale;
            final int height = bounds.height * yScale;
            final int pWidth = area.width - 5 * dpi.x / 4;
            final int pHeight = area.height - 5 * dpi.x / 4;
            final float factor = Math.min(1.0F,
                                          Math.min((float)pWidth / (float)width,
                                                   (float)pHeight / (float)height));
            final int aWidth = (int)(factor * width);
            final int aHeight = (int)(factor * height);
            final int xoff = (area.width - aWidth) / 2;
            final int yoff = (area.height - aHeight) / 2;

            final String jobName = ScreenshotMessages.getString("ScreenshotPlugin.Screenshot");

            if(printer.startJob(jobName)) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug(" Printer job has been started.");
                }

                if(printer.startPage()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(" Seite gestartet\n");
                        LOG.debug(image.toString() + ", " + bounds.x + ", " + bounds.y + ", " + bounds.width + ", " + bounds.height + ", " + xoff + ", " + yoff + ", " + aWidth + ", " + aHeight);
                    }
                    final GC gc = new GC(printer);
                    gc.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, xoff, yoff, aWidth, aHeight);
                    printer.endPage();
                    gc.dispose();
                }

                printer.endJob();
                success = true;
            }

            printer.dispose();
        }

        return success;
    }

    public void setBeep(final boolean beeping) {
        beep = beeping;
    }

    public boolean getBeep() {
        return beep;
    }

    public Image getSimpleImage() {
        return paintSurface.getImage();
    }

    public Image getDisplayedImage() {
        return imageBundle.getDisplayedImage();
    }

    public void setDisplayedImage(final Image i) {
        imageBundle.setDisplayedImage(i);
        paintSurface.syncScrollBars();
    }

    /*public void setDisplayedImage(ImageData i)
    {
        paintSurface.setCapturedImage(i);
    }*/

    public PaintSurface getPaintSurface() {
        return paintSurface;
    }

    public ImageBundle getAllImages() {
        return imageBundle;
    }

    public void setRestartTime(final int s) {
        restart = s;
    }

    public int getRestartTime() {
        return restart;
    }

    public void setMessageContent(final Hashtable<String, String> m) {
        messageContent = m;
    }

    public Hashtable<String, String> getMessageContent() {
        return messageContent;
    }

    /*
    public String createXmlFromMessageContent() {
        // TODO: ????
        final String  result = null;
        return result;
    }
    */

    public String getNameAndVersion() {
        return ScreenshotPlugin.getDefault().getNameAndVersion();
    }

    public Shell getShell() {
        return ScreenshotPlugin.getDefault().getShell();
    }
}
