/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
/*
 * $Id: ProfibusHelper.java,v 1.4 2010/08/20 13:33:00 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges.Value;
import org.csstudio.config.ioconfig.view.IOConfigActivatorUI;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Class help handel the Unsign Datatyp from Profibus.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.4 $
 * @since 26.06.2007
 */
public final class ProfibusHelper {

    /**
     * @author hrickens
     * @author $Author: $
     * @since 05.10.2010
     */
    private static final class NumberVerifyListenerImplementation implements
    VerifyListener {
        private final long _min;

        NumberVerifyListenerImplementation(final long min) {
            _min = min;
        }

        @Override
        public void verifyText(@Nonnull final VerifyEvent e) {
            // check != Digit
            final boolean b1 = e.text.matches("\\D+");
            // check is first char
            final boolean b2 = e.start == 0;
            // check is an '-'
            boolean b3;
            if (_min < 0) {
                b3 = e.text.matches("^[\\-]?$");
            } else {
                b3 = false;
            }
            // check is a Digit or is first char an '-'
            if (b1 && !(b2 && b3)) {
                e.doit = false;
            }
        }
    }


    /** Verify Listener Type ID VL_TYP_U08. */
    public static final int VL_TYP_U08 = 8;
    /** Verify Listener Type ID VL_TYP_U16. */
    public static final int VL_TYP_U16 = 16;
    /** Verify Listener Type ID VL_TYP_U32. */
    public static final int VL_TYP_U32 = 32;
    private static final Logger LOG = LoggerFactory.getLogger(ProfibusHelper.class);
    private static final int CHARSIZE = 8;
    /** A VerifyListener to check U8 type. */
    private static VerifyListener _CHECK_OF_U8;
    /** A VerifyListener to check U16 type. */
    private static VerifyListener _CHECK_OF_U16;
    /** A VerifyListener to check U32 type. */
    private static VerifyListener _CHECK_OF_U32;
    /** A TraverseListener to jump per enter to the next field. */
    private static TraverseListener _NE_TL = new TraverseListener() {
        @Override
        public void keyTraversed(@Nonnull final TraverseEvent e) {
            if (e.detail == SWT.TRAVERSE_RETURN) {
                e.detail = SWT.TRAVERSE_TAB_NEXT;
            }
        }
    };

    /** The default Constructor. */
    private ProfibusHelper() {
        // The default Constructor.
    }

    @Nonnull
    public static VerifyListener getDigitVerifyListener() {
        return new VerifyListener() {
            @Override
            public void verifyText(@Nonnull final VerifyEvent e) {
                e.doit = !e.text.matches("\\D+");
            }
        };
    }

    /**
     *
     * @return A TraverseListener to jump per enter to the next field.
     */
    @Nonnull
    public static TraverseListener getNETL() {
        return _NE_TL;
    }

    /**
     * Verify input at Text field is confirm with a Number that is in range Min
     * - Max.
     *
     * @param min
     *            the min Value was accept at Text field.
     * @return a VerifyListener to check a Text field is confirm with a Number
     *         that is in range Min - Max.
     */
    @Nonnull
    public static VerifyListener getNumberVerifyListener(final long min) {
        return new NumberVerifyListenerImplementation(min);
    }

    /**
     *
     * @param parent
     *            The parent composite.
     * @param edit
     *            is true the Text field is editable.
     * @param value
     *            the Value for the Textfield.
     * @param ranges
     * @param verifyListenerTyp
     *            set The Verification Type.
     * @return a Text field whit ranges and verify listener.
     */
    @Nonnull
    public static Text getTextField(@Nonnull final Composite parent,
                                    final boolean edit, @Nullable final String value,
                                    @CheckForNull final Value ranges, final int verifyListenerTyp) {

        final Text textField; 
        
        if (edit) {
            textField = new Text(parent, SWT.SINGLE | SWT.TRAIL | SWT.BORDER);
        } else {
            textField = new Text(parent,  SWT.TRAIL | SWT.BORDER);
            textField.setEnabled(false);
        }
        
        int size = 20;
        if (ranges != null) {
            size = CHARSIZE * Long.toString(ranges.getMax()).length();
            textField.addFocusListener(new CheckNumFocusListener(ranges));
            textField.setToolTipText("The Range is between " + ranges.getMin()
                                     + " and " + ranges.getMax());

        }
        final GridData gdl = GridDataFactory.fillDefaults().grab(true, false)
        .minSize(size, SWT.DEFAULT).create();
        textField.setLayoutData(gdl);
        textField.addTraverseListener(_NE_TL);
        textField.setEditable(edit);
        if (value != null) {
            textField.setText(value);
            textField.setData(value);
        }
        // textField.addKeyListener(_switchFocusAtEnter);
        if (edit) {
            switch (verifyListenerTyp) {
                case VL_TYP_U08:
                    textField.addVerifyListener(ProfibusHelper
                                                .getVerifyListenerCheckOfU8());
                    textField.setTextLimit(3);
                    break;
                case VL_TYP_U16:
                    textField.addVerifyListener(ProfibusHelper
                                                .getVerifyListenerCheckOfU16());
                    textField.setTextLimit(5);
                    break;
                case VL_TYP_U32:
                    textField.addVerifyListener(ProfibusHelper
                                                .getVerifyListenerCheckOfU32());
                    textField.setTextLimit(10);
                    break;
                default:
                    break;
            }
        }
        return textField;
    }

    @Nonnull
    public static Text getTextField(@Nonnull final Composite parent,
                                    @Nullable final String text) {
        return getTextField(parent, false, text, null, 0);
    }

    /**
     * Verify input at Text field is confirm with a Profibus U16 (0-65535).
     *
     * @return a VerifyListener to check a Text field is confirm with a Profibus
     *         U16 (0-65535)
     */
    @Nonnull
    public static VerifyListener getVerifyListenerCheckOfU16() {
        if (_CHECK_OF_U16 == null) {
            _CHECK_OF_U16 = getNumberVerifyListener(0);
        }
        return _CHECK_OF_U16;
    }

    /**
     * Verify input at Text field is confirm with a Profibus U16 (0-2^32).
     *
     * @return a VerifyListener to check a Text field is confirm with a Profibus
     *         U16 (0-2^32)
     */
    @Nonnull
    public static VerifyListener getVerifyListenerCheckOfU32() {
        if (_CHECK_OF_U32 == null) {
            _CHECK_OF_U32 = getNumberVerifyListener(0);
        }
        return _CHECK_OF_U32;
    }

    /**
     * Verify input at Text field is confirm with a Profibus U8 (0-255).
     *
     * @return a VerifyListener to check a Text field is confirm with a Profibus
     *         U8 (0-255)
     */
    @Nonnull
    public static VerifyListener getVerifyListenerCheckOfU8() {
        if (_CHECK_OF_U8 == null) {
            _CHECK_OF_U8 = getNumberVerifyListener(0);
        }
        return _CHECK_OF_U8;
    }

    /**
     * Open a Error Dialog for a access error to a {@link DBClass}.<br>
     * The Error Msg String for {@link DBClass} get two Parameters (Class Name
     * and DB Id).<br>
     * If the {@link DBClass} a {@link NamedDBClass} then give three Parameters
     * (Class Name, Object name and DB Id).<br>
     *
     * @param parent
     *            the parent shell of the dialog, or <code>null</code> if none
     * @param title
     *            the title to use for this dialog, or <code>null</code> to
     *            indicate that the default title should be used
     * @param status
     *            the error to show to the user
     * @param errMsg
     *            the error message.
     * @param node
     *            the DBClass with error.
     * @param e
     *            the thrown Exception.
     */
    public static void openErrorDialog(@Nullable final Shell shell,
                                       @Nullable final String title, @Nonnull final String errMsg,
                                       @CheckForNull final DBClass node, @Nonnull final Exception e) {
        String format;
        LOG.error("", e);
        if(node == null) {
            format = String.format(errMsg, "N/A", "N/A", "N/A");
        } else if (node instanceof NamedDBClass) {
            final NamedDBClass nameNode = (NamedDBClass) node;
            format = String.format(errMsg,
                                   nameNode.getClass().getSimpleName(),
                                   nameNode.getName(),
                                   nameNode.getId());
        } else {
            format = String.format(errMsg, node.getClass().getSimpleName(),
                                   "N/A", node.getId());
        }
        final OperationStatus status = new OperationStatus(IStatus.ERROR,
                                                           IOConfigActivatorUI.PLUGIN_ID, 3, format, e);
        ErrorDialog.openError(shell, title, null, status);
    }

}
