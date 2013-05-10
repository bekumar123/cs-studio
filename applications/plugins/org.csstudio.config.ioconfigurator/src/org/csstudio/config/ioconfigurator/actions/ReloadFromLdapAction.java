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
 * $Id: ReloadFromLdapAction.java,v 1.2 2010/09/03 11:52:25 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.actions;

import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.ldap.LdapControllerService;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Reloads the data from the LDAP server.
 *
 * TODO: reload LDAP data only from the selected leaf.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 01.09.2010
 */
class ReloadFromLdapAction extends Action {

    private final IControllerSubtreeNode _root;
    private final TreeViewer _viewer;
    private final IWorkbenchPartSite _site;

    /**
     * Constructor.
     * @param viewer {@code TreeViewer} in the plug-in.
     * @param site {@code IWorkbenchPartSite} site.
     */
    private ReloadFromLdapAction(@Nonnull final IControllerSubtreeNode root,
                                 @Nonnull final TreeViewer viewer,
                                 @Nonnull final IWorkbenchPartSite site) {
        _root = root;
        _viewer = viewer;
        _site = site;
    }

    public static ReloadFromLdapAction getAction(@Nonnull final IControllerSubtreeNode root,
                                                 @Nonnull final TreeViewer viewer,
                                                 @Nonnull final IWorkbenchPartSite site) {
        return new ReloadFromLdapAction(root, viewer, site);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Reloads data from LDAP";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return "Reload from LDAP";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText() {
        return "Reloads data from LDAP";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // Performs this action asynchronously
        _site.getShell().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    LdapControllerService.loadContent(_root);
                    _viewer.setInput(_root);
                    _viewer.refresh();
                } catch (Exception e) {
                    MessageDialog.openError(_site.getShell(),
                                            "Could not reload",
                                            e.getMessage());
                }
            }
        });
    }
}
