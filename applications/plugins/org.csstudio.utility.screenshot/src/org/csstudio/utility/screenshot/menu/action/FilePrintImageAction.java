
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

package org.csstudio.utility.screenshot.menu.action;

import org.csstudio.utility.screenshot.ScreenshotPlugin;
import org.csstudio.utility.screenshot.ScreenshotWorker;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.csstudio.utility.screenshot.printing.ImagePrinter;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class FilePrintImageAction extends Action {

    private final ScreenshotWorker worker;
    private final boolean invertImage;

    public FilePrintImageAction(final ScreenshotWorker w, final boolean invert) {
        worker = w;
        invertImage = invert;
        if(invertImage) {
            this.setText(ScreenshotMessages.getString("ScreenshotView.MENU_FILE_PRINTINVERTED"));
            this.setToolTipText(ScreenshotMessages.getString("ScreenshotView.MENU_FILE_PRINTINVERTED_TT"));
        } else {
            this.setText(ScreenshotMessages.getString("ScreenshotView.MENU_FILE_PRINT"));
            this.setToolTipText(ScreenshotMessages.getString("ScreenshotView.MENU_FILE_PRINT_TT"));
        }
    }

    @Override
    public void run() {
        final Display mainDisplay = ScreenshotPlugin.getDefault().getDisplay();
        final Image newImage = new Image(mainDisplay, worker.getDisplayedImage().getImageData());
        final ImagePrinter ip = new ImagePrinter(ScreenshotPlugin.getDefault().getDisplay().getActiveShell(), newImage, invertImage);
        mainDisplay.syncExec(ip);
    }
}
