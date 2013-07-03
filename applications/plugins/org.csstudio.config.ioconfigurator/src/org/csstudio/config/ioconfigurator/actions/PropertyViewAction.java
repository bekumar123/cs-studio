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
 * $Id: PropertyViewAction.java,v 1.2 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.actions;

import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

/**
 * Designed to open the Property View.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 01.09.2010
 */
class PropertyViewAction extends Action {

    private final IWorkbenchPartSite _site;

    /**
     * Private constructor.
     * Instance available through the static factory method.
     * @param site {@code IWorkbenchPartSite} site of the plug-in view.
     */
    private PropertyViewAction(@Nonnull final IWorkbenchPartSite site) {
        _site = site;
    }

    /**
     * Returns the instance of this class.
     * @param site {@code IWorkbenchPartSite} site of the plug-in view.
     * @return the instance of this class.
     */
    public static PropertyViewAction getAction(@Nonnull final IWorkbenchPartSite site) {
        return new PropertyViewAction(site);
    }

    @Override
    public String getDescription() {
        return "Opens a Property View";
    }

    @Override
    public String getText() {
        return "Show Property View";
    }

    @Override
    public String getToolTipText() {
        return "Show Property View";
    }

    @Override
    public void run() {
        try {
            _site.getPage().showView(IPageLayout.ID_PROP_SHEET);
        } catch (final PartInitException e) {
            MessageDialog.openError(_site.getShell(), "Error", e.getMessage());
        }
    }
}
