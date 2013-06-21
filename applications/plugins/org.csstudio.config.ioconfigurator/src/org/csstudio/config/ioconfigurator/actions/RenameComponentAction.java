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

import javax.naming.InvalidNameException;

import org.csstudio.config.ioconfigurator.annotation.CheckForNull;
import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.ldap.LdapControllerService;
import org.csstudio.config.ioconfigurator.ldap.LdapNode;
import org.csstudio.config.ioconfigurator.property.ioc.Validators;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
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
    private final ReloadFromLdapAction _reloadLdap;

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
    private RenameComponentAction(@Nonnull final TreeViewer viewer, @Nonnull final IWorkbenchPartSite site,
            ReloadFromLdapAction reloadLdap) {
        _viewer = viewer;
        _site = site;
        _reloadLdap = reloadLdap;
    }

    /**
     * Returns the instance of this class.
     * 
     * @param site
     *            {@code IWorkbenchPartSite} site of the plug-in view.
     * @return the instance of this class.
     */
    public static RenameComponentAction getAction(@Nonnull final TreeViewer viewer,
            @Nonnull final IWorkbenchPartSite site, ReloadFromLdapAction reloadLdap) {
        return new RenameComponentAction(viewer, site, reloadLdap);
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
        
        try {
            LdapNode ldapNode = new LdapNode(_node.getLdapName());

            IInputValidator validator;
            String prompt;
            
            if (ldapNode.isFacility()) {
                validator = Validators.UNIQUE_FACILITY_VALIDATOR.getValidator();
                prompt = "Please enter the new Facility name";
            } else if (ldapNode.isLeaf()) {
                validator = Validators.UNIQUE_IOC_VALIDATOR.getValidator();
                prompt = "Please enter the new IOC name";
            } else {
                throw new IllegalStateException("Unexpected LDAP node type");
            }

            String newName = renameInputDialog(_site, prompt, _node.getName(), validator);
            
            if (newName != null) {

                if (newName.equals( _node.getName())) {
                    return;
                }

                try {
                    boolean needsReload = LdapControllerService.rename(_node.getLdapName(), newName);
                    if (needsReload) {
                        _reloadLdap.run(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    _viewer.expandToLevel(_node.getLdapName().getRdns().size() + 1);
                                } catch (InvalidNameException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        _node.setName(newName);
                        _viewer.refresh(_node);
                    }
                } catch (Exception e) {
                    MessageDialog.openError(_site.getShell(), "Rename Error", e.getMessage());
                }
            }

        } catch (InvalidNameException e1) {
            MessageDialog.openError(_site.getShell(), "Rename Error", e1.getMessage());
        }

    }

    /*
     * (non-Javadoc) Serves as a helper method. Displaying the input dialog
     * required in the getRenameAction method.
     */
    @CheckForNull
    private static String renameInputDialog(@Nonnull final IWorkbenchPartSite site,
            @Nonnull final String prompt,
            @Nonnull final String currentName,
            @Nonnull final IInputValidator validator) {
        final InputDialog dialog = new InputDialog(site.getShell(), "Rename", prompt, currentName,
               validator);
        if (Window.OK == dialog.open()) {
            return dialog.getValue();
        }
        return null;
    }
}