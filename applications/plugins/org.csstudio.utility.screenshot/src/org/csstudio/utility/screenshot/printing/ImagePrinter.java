
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

package org.csstudio.utility.screenshot.printing;

import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagePrinter implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ImagePrinter.class);

    private Shell shell = null;
    private Image image = null;
    private boolean invert = false;

    public ImagePrinter(final Shell s, final Image i, final boolean inv) {
        shell = s;
        image = i;
        invert = inv;
    }

    public void run() {
        final PrintDialog dialog = new PrintDialog(shell);
        final PrinterData pd = dialog.open();

        if(pd != null) {
            final Printer printer = new Printer(pd);

            final String jobName = ScreenshotMessages.getString("ScreenshotPlugin.Screenshot");

            if(printer.startJob(jobName)) {
                if(printImage(printer)) {
                    LOG.info(" OK - Printing");
                } else {
                    LOG.error(" FEHLER - Printing");
                }

                printer.endJob();
            }

            printer.dispose();
        }
    }

    private boolean printImage(final Printer printer) {

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
                                      Math.min((float) pWidth / (float) width,
                                               (float) pHeight / (float) height));
        final int aWidth = (int) (factor * width);
        final int aHeight = (int) (factor * height);
        final int xoff = (area.width - aWidth) / 2;
        final int yoff = (area.height - aHeight) / 2;
        boolean success = true;

        if(invert) {
            image = invertImage(image);
        }

        if(printer.startPage()) {
            final GC gc = new GC(printer);

            gc.drawImage(image,
                         bounds.x,
                         bounds.y,
                         bounds.width,
                         bounds.height,
                         xoff,
                         yoff,
                         aWidth,
                         aHeight);

            gc.dispose();

            printer.endPage();
        } else {
            success = false;
        }

        image.dispose();

        return success;
    }

    private Image invertImage(final Image img) {
        Image inverted = null;

        final ImageData imageData = img.getImageData();

        final byte[] data = imageData.data;

        if(imageData.palette.isDirect) {

            for (int i = 0; i < data.length; i++) {
                imageData.data[i] ^= -1;
            }

            inverted = new Image(shell.getDisplay(), imageData);
        } else {
            final RGB[] rgbs = imageData.palette.getRGBs();

            for (int i = 0; i < rgbs.length; i++) {
                rgbs[i].blue ^= -1;
                rgbs[i].green ^= -1;
                rgbs[i].red ^= -1;
            }

            final PaletteData pd = new PaletteData(rgbs);
            final ImageData id = new ImageData(imageData.width, imageData.height, imageData.depth, pd);
            id.data = data;

            inverted = new Image(shell.getDisplay(), id);
        }

        return inverted;
    }
}
