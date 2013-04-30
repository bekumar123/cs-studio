/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: ControllerTreeLabelProvider.java,v 1.1 2010/09/02 15:47:51 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.ui;

import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for Controller TreeViewer.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 25.08.2010
 */
final class ControllerTreeLabelProvider implements ILabelProvider {

    /** The icon representing a node */
    private static final Image NODE_ICON = PlatformUI.getWorkbench()
            .getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

    /** The icon representing a leaf */
    private static final Image LEAF_ICON = PlatformUI.getWorkbench()
            .getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

    /** Private constructor. Instance available through the static factory method. */
    private ControllerTreeLabelProvider() {
    }

    /**
     * Returns a new instance of {@code ControllerTreeLabelProvider}.
     * @return new {@code ControllerTreeLabelProvider}.
     */
    public static ILabelProvider getProvider() {
        return new ControllerTreeLabelProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final ILabelProviderListener listener) {
        // TODO implementation?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // Do not dispose the images, they are constants.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        // TODO: is the label really never affected?
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final ILabelProviderListener listener) {
        // TODO implementation?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage(final Object element) {
        if (element instanceof IControllerLeaf) {
            return LEAF_ICON;
        }
        return NODE_ICON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(final Object element) {
        return ((IControllerNode) element).toString();
    }
}
