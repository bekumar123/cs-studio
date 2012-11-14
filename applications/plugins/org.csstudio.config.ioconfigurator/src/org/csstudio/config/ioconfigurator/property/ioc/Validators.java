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
 * $Id: Validators.java,v 1.2 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.property.ioc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;

import org.csstudio.utility.ldap.utils.LdapFieldsAndAttributes;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Validators used in this plug-in.
 *
 * TODO: Add some more validators.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 27.08.2010
 */
public enum Validators {

    /**
     * Simple name validator.
     * This validator does not allow forbidden LDAP strings and an expressions
     * starting or ending with whitespace.
     * @see {@link LdapFieldsAndAttributes#FORBIDDEN_SUBSTRINGS}
     */
    NAME_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {
            if (newText.equals("")) {
                return "Please enter a name.";
            } else if (newText.matches("^\\s.*") || newText.matches(".*\\s$")) {
                return "The name cannot begin or end with whitespace.";
            }
            for (final String forbiddenString : LdapFieldsAndAttributes.FORBIDDEN_SUBSTRINGS) {
                if (newText.contains(forbiddenString)) {
                    return "The name must not contain the substring or character '"
                            + forbiddenString + "'.";
                }
            }
            return null;
        }
    }),

    /**
     * IP validator.
     */
    IP_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {
            try {
                InetAddress.getByName(newText);
                return null;
            } catch (UnknownHostException e) {
                return e.getMessage();
            }
        }
    });

    private final IInputValidator _validator;

    /**
     * Constructor.
     * @param validator {@code IInputValidator} represented by this constant.
     */
    Validators(@Nonnull final IInputValidator validator) {
        _validator = validator;
    }

    /**
     * Returns the {@code IInputValidator}.
     * @return the {@code IInputValidator}
     */
    public IInputValidator getValidator() {
        return _validator;
    }
}
