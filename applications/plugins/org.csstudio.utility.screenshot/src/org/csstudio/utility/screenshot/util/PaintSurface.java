
/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.utility.screenshot.util;

import java.awt.geom.AffineTransform;
import org.csstudio.utility.screenshot.ImageBundle;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ScrollBar;

/**
 *  Some code is borrowed from the basic image viewer.
 *  http://www.eclipse.org/articles/Article-Image-Viewer/Image_viewer.html
 *  @author Markus Moeller
 *  @author Chengdong Li (some borrowed parts)
 */
public class PaintSurface {

    /* zooming rates in x and y direction are equal.*/
    final float ZOOMIN_RATE = 1.1f; /* zoomin rate */
    final float ZOOMOUT_RATE = 0.9f; /* zoomout rate */

    private Canvas paintCanvas;
    private ImageBundle imageBundle;
    private AffineTransform transform;
    private Image screenImage;

    public PaintSurface(final Canvas paintCanvas, final ImageBundle bundle) {

        this.paintCanvas = paintCanvas;
        this.imageBundle = bundle;
        transform = new AffineTransform();
        screenImage = null;
        if(imageBundle != null) {
            if(imageBundle.getSectionImage() != null) {
                imageBundle.setDisplayedImage(imageBundle.getSectionImage());
            } else if(imageBundle.getWindowImage() != null) {
                imageBundle.setDisplayedImage(imageBundle.getWindowImage());
            } else if(imageBundle.getScreenImage() != null) {
                imageBundle.setDisplayedImage(imageBundle.getScreenImage());
            }
        }

        paintCanvas.addPaintListener(new PaintListener() {
            public void paintControl(final PaintEvent event) {

                Rectangle clientRect = null;

                if(getImageBundle().getDisplayedImage() != null) {

                    final Canvas widget = (Canvas)event.widget;
                    clientRect = widget.getClientArea();

                    Rectangle imageRect=SWT2Dutil.inverseTransformRect(getTransform(), clientRect);
                    final int gap = 2; /* find a better start point to render. */
                    imageRect.x -= gap; imageRect.y -= gap;
                    imageRect.width += 2 * gap; imageRect.height += 2 * gap;

                    final Rectangle imageBound = getImageBundle().getDisplayedImage().getBounds();
                    imageRect = imageRect.intersection(imageBound);
                    final Rectangle destRect = SWT2Dutil.transformRect(getTransform(), imageRect);

                    if(getScreenImage() != null) {
                        getScreenImage().dispose();
                    }
                    setScreenImage(new Image(widget.getDisplay(), clientRect.width, clientRect.height));
                    final GC newGC = new GC(getScreenImage());
                    newGC.setClipping(clientRect);
                    newGC.drawImage(getImageBundle().getDisplayedImage(),
                            imageRect.x,
                            imageRect.y,
                            imageRect.width,
                            imageRect.height,
                            destRect.x,
                            destRect.y,
                            destRect.width,
                            destRect.height);
                    newGC.dispose();

                    event.gc.drawImage(getScreenImage(), 0, 0);
                } else {
                    event.gc.setClipping(clientRect);
                    event.gc.fillRectangle(clientRect);
                    initScrollBars();
                }
            }
        });

        paintCanvas.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(final ControlEvent event)
            {
                syncScrollBars();
            }
        });

        initScrollBars();
    }

    /* Initalize the scrollbar and register listeners. */
    protected void initScrollBars() {
        final ScrollBar horizontal = paintCanvas.getHorizontalBar();
        horizontal.setEnabled(false);
        horizontal.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                scrollHorizontally((ScrollBar) event.widget);
            }
        });

        final ScrollBar vertical = paintCanvas.getVerticalBar();
        vertical.setEnabled(false);
        vertical.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                scrollVertically((ScrollBar) event.widget);
            }
        });
    }

    /**
     * Synchronize the scrollbar with the image. If the transform is out
     * of range, it will correct it. This function considers only following
     * factors :<b> transform, image size, client area</b>.
     */
    public void syncScrollBars() {

        if(imageBundle.getDisplayedImage() == null) {
            redraw();
            return;
        }

        AffineTransform af = transform;
        final double sx = af.getScaleX(), sy = af.getScaleY();
        double tx = af.getTranslateX(), ty = af.getTranslateY();
        if (tx > 0) {
            tx = 0;
        }
        if (ty > 0) {
            ty = 0;
        }

        final ScrollBar horizontal = paintCanvas.getHorizontalBar();
        horizontal.setIncrement(paintCanvas.getClientArea().width / 100);
        horizontal.setPageIncrement(paintCanvas.getClientArea().width);
        final Rectangle imageBound = imageBundle.getDisplayedImage().getBounds();
        final int cw = paintCanvas.getClientArea().width, ch = paintCanvas.getClientArea().height;
        if (imageBound.width * sx > cw) { /* image is wider than client area */
            horizontal.setMaximum((int) (imageBound.width * sx));
            horizontal.setEnabled(true);
            if ((int) - tx > horizontal.getMaximum() - cw) {
                tx = -horizontal.getMaximum() + cw;
            }
        } else { /* image is narrower than client area */
            horizontal.setEnabled(false);
            tx = (cw - imageBound.width * sx) / 2; //center if too small.
        }
        horizontal.setSelection((int) -tx);
        horizontal.setThumb(paintCanvas.getClientArea().width);

        final ScrollBar vertical = paintCanvas.getVerticalBar();
        vertical.setIncrement(paintCanvas.getClientArea().height / 100);
        vertical.setPageIncrement(paintCanvas.getClientArea().height);
        if (imageBound.height * sy > ch) { /* image is higher than client area */
            vertical.setMaximum((int) (imageBound.height * sy));
            vertical.setEnabled(true);
            if ((int) - ty > vertical.getMaximum() - ch) {
                ty = -vertical.getMaximum() + ch;
            }
        } else { /* image is less higher than client area */
            vertical.setEnabled(false);
            ty = (ch - imageBound.height * sy) / 2; //center if too small.
        }
        vertical.setSelection((int) -ty);
        vertical.setThumb(paintCanvas.getClientArea().height);

        /* update transform. */
        af = AffineTransform.getScaleInstance(sx, sy);
        af.preConcatenate(AffineTransform.getTranslateInstance(tx, ty));
        transform = af;

        paintCanvas.redraw();
    }

    /* Scroll horizontally */
    protected void scrollHorizontally(final ScrollBar scrollBar) {
        if (imageBundle.getDisplayedImage() == null) {
            return;
        }

        final AffineTransform af = transform;
        final double tx = af.getTranslateX();
        final double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(select - tx, 0));
        transform = af;
        syncScrollBars();
    }

    /* Scroll vertically */
    protected void scrollVertically(final ScrollBar scrollBar) {
        if (imageBundle.getDisplayedImage() == null) {
            return;
        }

        final AffineTransform af = transform;
        final double ty = af.getTranslateY();
        final double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(0, select - ty));
        transform = af;
        syncScrollBars();
    }

    public void redraw()
    {
        paintCanvas.redraw();
    }

    public void dispose()
    {
        paintCanvas = null;
        imageBundle = null;

        if(screenImage != null)
        {
            if(!screenImage.isDisposed())
            {
                screenImage.dispose();
            }

            screenImage = null;
        }
    }

    /**
     * Perform a zooming operation centered on the given point
     * (dx, dy) and using the given scale factor.
     * The given AffineTransform instance is preconcatenated.
     * @param dx center x
     * @param dy center y
     * @param scale zoom rate
     * @param af original affinetransform
     */
    public void centerZoom(
        final double dx,
        final double dy,
        final double scale,
        final AffineTransform af) {
        af.preConcatenate(AffineTransform.getTranslateInstance(-dx, -dy));
        af.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
        af.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        transform = af;
        syncScrollBars();
    }

    /**
     * Fit the image onto the canvas
     */
    public void fitCanvas() {
        if (imageBundle.getDisplayedImage() == null) {
            return;
        }
        final Rectangle imageBound = imageBundle.getDisplayedImage().getBounds();
        final Rectangle destRect = paintCanvas.getClientArea();
        final double sx = (double) destRect.width / (double) imageBound.width;
        final double sy = (double) destRect.height / (double) imageBound.height;
        final double s = Math.min(sx, sy);
        final double dx = 0.5 * destRect.width;
        final double dy = 0.5 * destRect.height;
        centerZoom(dx, dy, s, new AffineTransform());
    }

    /**
     * Zoom in around the center of client Area.
     */
    public void zoomIn() {
        if (imageBundle.getDisplayedImage() == null) {
            return;
        }
        final Rectangle rect = paintCanvas.getClientArea();
        final int w = rect.width, h = rect.height;
        final double dx = (double) w / 2;
        final double dy = (double) h / 2;
        centerZoom(dx, dy, ZOOMIN_RATE, transform);
    }

    /**
     * Zoom out around the center of client Area.
     */
    public void zoomOut() {
        if (imageBundle.getDisplayedImage() == null) {
            return;
        }
        final Rectangle rect = paintCanvas.getClientArea();
        final int w = rect.width, h = rect.height;
        final double dx = (double) w / 2;
        final double dy = (double) h / 2;
        centerZoom(dx, dy, ZOOMOUT_RATE, transform);
    }

    public ImageBundle getImageBundle() {
        return imageBundle;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public Image getScreenImage() {
        return screenImage;
    }

    public void setScreenImage(final Image image) {
        screenImage = image;
    }

    /**
     * Show the image with the original size
     */
    public void showOriginal() {
        if (imageBundle.getDisplayedImage() == null) {
            return;
        }
        transform = new AffineTransform();
        syncScrollBars();
    }

    public Image getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    public Image getCapturedImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unused")
    public void setCapturedImage(final Image i) {
        // TODO Auto-generated method stub
    }

    public ImageBundle getAllImages() {
        // TODO Auto-generated method stub
        return null;
    }
}
