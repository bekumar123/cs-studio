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
 * $Id: RenameComponentAction.java,v 1.2 2010/09/03 11:52:25 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.actions;

import org.csstudio.config.ioconfigurator.annotation.CheckForNull;
import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.ldap.LdapControllerService;
import org.csstudio.config.ioconfigurator.property.ioc.Validators;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Action class designed to rename the chosen component.
 * 
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 02.09.2010
 */
class RenameComponentAction extends Action {
    
    private final TreeViewer _viewer;
    private final IWorkbenchPartSite _site;

    // Obtained through getters/setters
    private IControllerNode _node;

    /**
     * Private constructor. Instance available through the static factory
     * method.
     * 
     * @param viewer
     *            {@code TreeViewer} plug-in tree viewer.
     * @param site
     *            {@code IWorkbenchPartSite} site of the plug-in view.
     */
    private RenameComponentAction(@Nonnull final TreeViewer viewer, @Nonnull final IWorkbenchPartSite site) {
        _viewer = viewer;
        _site = site;
    }

    /**
     * Returns the instance of this class.
     * 
     * @param site
     *            {@code IWorkbenchPartSite} site of the plug-in view.
     * @return the instance of this class.
     */
    public static RenameComponentAction getAction(@Nonnull final TreeViewer viewer,
            @Nonnull final IWorkbenchPartSite site) {
        return new RenameComponentAction(viewer, site);
    }

    /**
     * Sets this class node. Emulates Builder pattern.
     * 
     * @param node
     *            {@code IControllerNode} to be set.
     */
    public RenameComponentAction setNode(final IControllerNode node) {
        _node = node;
        return this;
    }

    /**
     * Returns this class node.
     * 
     * @return {@code IControllerNode} node.
     */
    public IControllerNode getNode() {
        return _node;
    }

    @Override
    public String getDescription() {
        return "Renames the selected Component";
    }

    @Override
    public String getText() {
        return "Rename";
    }

    @Override
    public String getToolTipText() {
        return "Rename the selected Component";
    }

    @Override
    public void run() {
        String name = renameInputDialog(_site, _node.getName());
        if (name != null) {
            try {
                LdapControllerService.rename(_node.getLdapName(), name);
                _node.setName(name);
                _viewer.refresh(_node);
            } catch (Exception e) {
                MessageDialog.openError(_site.getShell(), "Rename Error", e.getMessage());
            }
        }
    }

    /*
     * (non-Javadoc) Serves as a helper method. Displaying the input dialog
     * required in the getRenameAction method.
     */
    @CheckForNull
    private static String renameInputDialog(@Nonnull final IWorkbenchPartSite site, @Nonnull final String currentName) {
        final InputDialog dialog = new InputDialog(site.getShell(), "Rename", "Please input the new name", currentName,
                Validators.NAME_VALIDATOR.getValidator());
        if (Window.OK == dialog.open()) {
            return dialog.getValue();
        }
        return null;
    }
}