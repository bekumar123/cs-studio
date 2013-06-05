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
 * $Id: AddControllerAction.java,v 1.2 2010/09/03 11:52:25 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.actions;

import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.property.ioc.wizard.CreateControllerWizard;
import org.csstudio.config.ioconfigurator.property.ioc.wizard.PrimaryWizardPage;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Action class designed to add a new IOC to the chosen
 * {@code IControllerSubtreeNode}, if it is of
 * {@code LdapEpicsControlsConfiguration.COMPONENT} type.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 02.09.2010
 */
class AddControllerAction extends Action {

    private final IWorkbenchPartSite _site;

    // Obtained through getters/setters
    private IControllerSubtreeNode _node;

    /**
     * Private constructor.
     * Instance available through the static factory method.
     * @param site {@code IWorkbenchPartSite} site of the plug-in view.
     */
    private AddControllerAction(@Nonnull final IWorkbenchPartSite site) {
        _site = site;
    }

    /**
     * Returns the instance of this class.
     * @param site {@code IWorkbenchPartSite} site of the plug-in view.
     * @return the instance of this class.
     */
    public static AddControllerAction getAction(@Nonnull final IWorkbenchPartSite site) {
        return new AddControllerAction(site);
    }

    /**
     * Sets this class node, emulating Builder pattern.
     * @param node {@code IControllerLeaf} to be set.
     */
    public AddControllerAction setNode(final IControllerSubtreeNode node) {
        _node = node;
        return this;
    }

    /**
     * Returns this class node.
     * @return {@code IControllerLeaf} node.
     */
    public IControllerSubtreeNode getNode() {
        return _node;
    }

    @Override
    public String getDescription() {
        return "Adds a new IOC";
    }

    @Override
    public String getText() {
        return "Add IOC";
    }

    @Override
    public String getToolTipText() {
        return "Adds a new IOC";
    }

    @Override
    public void run() {
        // TODO:  This Wizard is not yet finished.
        //        Also, the wizard itself is throwing some odd
        //        exceptions

               CreateControllerWizard wizard = new CreateControllerWizard();
               wizard.addPage(new PrimaryWizardPage("Mandatory fields"));
               WizardDialog dialog = new WizardDialog(_site.getShell(), wizard);
               dialog.create();
               dialog.open();
        MessageDialog
                .openInformation(_site.getShell(),
                                 "Info",
                                 "This functionallity is yet to be implemented.");
    }
}
