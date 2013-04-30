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
 * $Id: ControllerActionCache.java,v 1.2 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.actions;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * This class servers as a cache, instantiating actions from
 * this package and providing methods to populate the menu for
 * this plug-in.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 03.09.2010
 */
public class ControllerActionCache {

    /*
     * All available actions from this package.
     * This is hard-coded and thus not an elegant solution.
     */
    private final AddControllerAction _addController;
    private final PropertyViewAction _propertyView;
    private final ReloadFromLdapAction _reloadLdap;
    private final RemoveControllerAction _removeController;
    private final RenameComponentAction _renameComponent;

    /**
     * Constructor.
     * @param root {@code IControllerSubtreeNode} root of this plug-in.
     * @param viewer {@code TreeViewer} of this plug-in.
     * @param site {@code IWorkbenchPartSite} of this plug-in.
     */
    public ControllerActionCache(@Nonnull final IControllerSubtreeNode root,
                                 @Nonnull final TreeViewer viewer,
                                 @Nonnull final IWorkbenchPartSite site) {

        _addController = AddControllerAction.getAction(site);
        _propertyView = PropertyViewAction.getAction(site);
        _reloadLdap = ReloadFromLdapAction.getAction(root, viewer, site);
        _removeController = RemoveControllerAction.getAction(viewer, site);
        _renameComponent = RenameComponentAction.getAction(viewer, site);
    }

    /**
     * Fills the context menu of this plug-in.
     * @param node {@code IControllerNode} node selected in the tree viewer.
     * @param menuManager {@code IMenuManager} invoked by the framework.
     */
    public void fillContextMenu(@Nonnull final IControllerNode node,
                                @Nonnull final IMenuManager menuManager) {

        LdapEpicsControlsConfiguration configType = node.getConfiguration();

        if (configType == LdapEpicsControlsConfiguration.IOC) {
            menuManager.add(_removeController.setNode((IControllerLeaf) node));
        }
        if (configType == LdapEpicsControlsConfiguration.COMPONENT) {
            menuManager.add(_addController
                    .setNode((IControllerSubtreeNode) node));
        }
        menuManager.add(_propertyView);
        menuManager.add(_reloadLdap);
        menuManager.add(_renameComponent.setNode(node));
    }

}
