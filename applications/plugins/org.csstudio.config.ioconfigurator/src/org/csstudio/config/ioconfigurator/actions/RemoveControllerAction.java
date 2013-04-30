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
 * $Id: RemoveControllerAction.java,v 1.2 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.actions;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

/**
* Action class designed to remove the selected IOC.
*
* TODO: should this action enable the removal of an arbitrary node,
*       not just IOC?
*
* @author tslamic
* @author $Author: tslamic $
* @version $Revision: 1.2 $
* @since 02.09.2010
*/
class RemoveControllerAction extends Action {

    private final TreeViewer _viewer;
    private final IWorkbenchPartSite _site;

    // Obtained through getters/setters
    private IControllerLeaf _node;

    /**
     * Private constructor.
     * Instance available through the static factory method.
     * @param site {@code IWorkbenchPartSite} site of the plug-in view.
     */
    private RemoveControllerAction(@Nonnull final TreeViewer viewer,
                                   @Nonnull final IWorkbenchPartSite site) {
        _viewer = viewer;
        _site = site;
    }

    /**
     * Returns the instance of this class.
     * @param site {@code IWorkbenchPartSite} site of the plug-in view.
     * @return the instance of this class.
     */
    public static RemoveControllerAction getAction(@Nonnull final TreeViewer viewer,
                                                   @Nonnull final IWorkbenchPartSite site) {
        return new RemoveControllerAction(viewer, site);
    }

    /**
     * Returns this class containing the specified node, emulating Builder pattern.
     * @param node {@code IControllerLeaf} to be set.
     */
    public RemoveControllerAction setNode(final IControllerLeaf node) {
        _node = node;
        return this;
    }

    /**
     * Returns this class node.
     * @return {@code IControllerLeaf} node.
     */
    public IControllerLeaf getNode() {
        return _node;
    }

    @Override
    public String getDescription() {
        return "Removes the selected IOC";
    }

    @Override
    public String getText() {
        return "Remove IOC";
    }

    @Override
    public String getToolTipText() {
        return "Remove the selected IOC";
    }

    @Override
    public void run() {
        try {
            // LdapControllerService.removeController(_node);
            IControllerSubtreeNode parent = _node.getParent();
            if (parent != null) {
                parent.removeChild(_node);
                _viewer.refresh(parent);
            }
        } catch (Exception e) {
            MessageDialog.openError(_site.getShell(), "Rename", e.getMessage());
        }
    }
}