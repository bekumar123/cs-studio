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
 * $Id: PrimaryWizardPage.java,v 1.1 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.property.ioc.wizard;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfigurator.property.ioc.Validators;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

/**
 * TODO (tslamic) :
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 02.09.2010
 */
public class PrimaryWizardPage extends WizardPage {

    // Title
    private static final String TITLE = "Create a new Controller";

    // Description
    private static final String DESCRIPTION = "Please enter the fields below.";

    // This WizardPage Components
    private Text iocName;
    private Text ipAddress;
    private Text redundantIp;
    private Button isRedundant;

    /**
     * Constructor.
     * @param pageName
     */
    public PrimaryWizardPage(final String pageName) {
        super(pageName);
        super.setTitle(TITLE);
        super.setDescription(DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canFlipToNextPage() {
        // TODO: implement
        return super.canFlipToNextPage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Mandatory input";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPageComplete() {
        // TODO: implement
        return super.isPageComplete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent) {
        Composite cmp = new Composite(parent, SWT.NONE);
        cmp.setLayout(getLayout());

        new Label(cmp, SWT.NONE).setText("Name");
        iocName = getText(cmp, Validators.NAME_VALIDATOR.getValidator());

        new Label(cmp, SWT.NONE).setText("IP Address");
        ipAddress = getText(cmp, Validators.IP_VALIDATOR.getValidator());

        new Label(cmp, SWT.NONE).setText("Is IP redundant");
        isRedundant = new Button(cmp, SWT.CHECK);
        isRedundant.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                redundantIp.setEnabled(isRedundant.getSelection());
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
        });

        new Label(cmp, SWT.NONE).setText("Redundant IP");
        redundantIp = getText(cmp, Validators.IP_VALIDATOR.getValidator());
    }

    private Text getText(@Nonnull final Composite parent,
                         @Nonnull final IInputValidator validator) {
        final Text txt = new Text(parent, SWT.BORDER | SWT.SINGLE);
        txt.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(final FocusEvent e) {
                String err = validator.isValid(txt.getText());
                if (err != null) {
                    setErrorMessage(err);
                }
            }

            @Override
            public void focusGained(final FocusEvent e) {
                // Do nothing
            }
        });
        return txt;
    }

    private Layout getLayout() {
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.verticalSpacing = 10;
        layout.makeColumnsEqualWidth = true;
        layout.numColumns = 2;
        return layout;
    }
}
