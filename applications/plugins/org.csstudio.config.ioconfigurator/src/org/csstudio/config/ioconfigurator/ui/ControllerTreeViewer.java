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
 * $Id: ControllerTreeViewer.java,v 1.2 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.ui;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Provides the TreeViewer for this plug-in.
 *
 * TODO: Implement double-click action that opens a Property View when
 *       the selected element is an instance of IControllerLeaf.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 24.08.2010
 */
public final class ControllerTreeViewer {

    private TreeViewer _viewer;

    /**
     * Constructor.
     * @see {@link ControllerTreeViewer#getViewer(Composite, IControllerSubtreeNode, IWorkbenchPartSite)}
     */
    private ControllerTreeViewer(@Nonnull final Composite parent,
                                 @Nonnull final IControllerSubtreeNode root,
                                 @Nonnull final IWorkbenchPartSite site) {
        createTreeViewer(parent, root, site);
    }

    /*
     * Creates a new TreeViewer
     */
    private void createTreeViewer(@Nonnull final Composite parent,
                                  @Nonnull final IControllerSubtreeNode root,
                                  @Nonnull final IWorkbenchPartSite site) {
        _viewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL
                | SWT.V_SCROLL);
        setOutlook();

        _viewer.setInput(root);
        site.setSelectionProvider(_viewer);
    }

    /*
     * Making it pretty
     */
    private void setOutlook() {
        _viewer.setContentProvider(ControllerTreeContentProvider.getProvider());
        _viewer.setLabelProvider(ControllerTreeLabelProvider.getProvider());
        _viewer.setComparator(new ViewerComparator());
    }

    /**
     * Returns this class TreeViewer.
     * @return this class TreeViewer.
     */
    public TreeViewer getTreeViewer() {
        assert _viewer != null;
        return _viewer;
    }

    /**
     * Returns a new TreeViewer.
     * @param parent {@code Composite} parent the viewer should be added to.
     * @param root {@code IControllerSubtreeNode} to serve as the viewer data.
     * @param site {@code IWorkbenchPartSite} to register the viewer to.
     * @return a new TreeViewer.
     */
    public static TreeViewer getViewer(@Nonnull final Composite parent,
                                       @Nonnull final IControllerSubtreeNode root,
                                       @Nonnull final IWorkbenchPartSite site) {
        return new ControllerTreeViewer(parent, root, site).getTreeViewer();
    }
}
