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

import static org.csstudio.utility.ldap.service.util.LdapUtils.equ;

import static org.csstudio.utility.ldap.service.util.LdapUtils.and;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.config.ioconfigurator.activator.Activator;
import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.ui.ControllerTreeViewer;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.eclipse.jface.dialogs.IInputValidator;

import com.google.common.net.InetAddresses;

/**
 * Validators used in this plug-in.
 * 
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 27.08.2010
 */
public enum Validators {

    /**
     * Simple name validator. This validator does not allow forbidden LDAP
     * strings and an expressions starting or ending with whitespace.
     * 
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
                    return "The name must not contain the substring or character '" + forbiddenString + "'.";
                }
            }
            return null;
        }
    }),

    UNIQUE_FACILITY_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {            
            return validateName(newText, LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName(), LdapFieldsAndAttributes.ATTR_VAL_FAC_OBJECT_CLASS,
                    "Error: Facility-Name already in use.");
        }
    }),

    UNIQUE_IOC_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {
            return validateName(newText, LdapEpicsControlsConfiguration.IOC.getNodeTypeName(), LdapFieldsAndAttributes.ATTR_VAL_IOC_OBJECT_CLASS,
                    "Error: IOC-Name already in use.");
        }

    }),

    YES_NO_VALIDATOR(new IInputValidator() {
        @Override
        public String isValid(final String newText) {
            if (newText.equals("YES")) {
                return null;
            }
            if (newText.equals("NO")) {
                return null;
            }
            return "Please enter YES or NO.";
        }
    }),

    TRUE_FALSE_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {
            if (newText.equals("TRUE")) {
                return null;
            }
            if (newText.equals("FALSE")) {
                return null;
            }
            return "Please enter TRUE or FALSE.";
        }
    }),

    /**
     * IP validator.
     */
    IP_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {
            try {

                if (newText.trim().isEmpty()) {
                    return null;
                }

                if (!InetAddresses.isInetAddress(newText)) {
                    return "Please enter a valid IP4-Address.";
                }

                InetAddress.getByName(newText);

                return null;

            } catch (UnknownHostException e) {
                return "Unknown host: " + e.getMessage();
            }
        }
    }),

    /**
     * IP validator.
     */
    EPICS_IP_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {
            return validateIp(ControllerProperty.IP_ADDRESS.getName(), newText);
        }

    }),

    /**
     * IP validator.
     */
    EPICS_IPR_VALIDATOR(new IInputValidator() {

        @Override
        public String isValid(final String newText) {
            return validateIp(ControllerProperty.IP_ADDRESS_REDUNDANT.getName(), newText);
        }

    });

    private final IInputValidator _validator;

    private static ILdapService LDAP_SERVICE = Activator.getDefault().getLdapService();

    private static String validateIp(String attributeName, String newText) {

        try {

            if (newText.trim().isEmpty()) {
                return null;
            }

            if (!InetAddresses.isInetAddress(newText)) {
                return "Please enter a valid IP4-Address.";
            }

            InetAddress.getByName(newText);

            LdapName ldapName = new LdapName(LdapFieldsAndAttributes.ORGANIZATION_UNIT_FIELD_NAME + "="
                    + LdapEpicsControlsConfiguration.VIRTUAL_ROOT.getUnitTypeValue());

            //@formatter:off
            ILdapSearchResult searchResult = LDAP_SERVICE.retrieveSearchResultSynchronously(
                    ldapName,
                    and(
                            equ(attributeName, newText),
                            equ(LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS, LdapFieldsAndAttributes.ATTR_VAL_IOC_OBJECT_CLASS)), 
                    SearchControls.SUBTREE_SCOPE);
                    //@formatter:on

            Set<SearchResult> results = searchResult.getAnswerSet();

            if (searchResult.getAnswerSet().size() == 0) {
                return null;
            } else if (searchResult.getAnswerSet().size() == 1) {
                SearchResult sr = results.iterator().next();
                // make sure that we do not check our own value
                if (ControllerTreeViewer.CURRENT_SELECTION.getLdapName().toString().startsWith(sr.getName())) {
                    return null;
                } else {
                    return "Error: IP-Address already in use.";
                }
            } else {
                return "Error: IP-Address already in use.";
            }

        } catch (UnknownHostException e) {
            return "Unknown host: " + e.getMessage();
        } catch (InvalidNameException e) {
            return e.getMessage();
        }
    }

    private static String validateName(String newText, String nodeTypeName, String objectClassValue, String errorMessage) {

        if (newText.equals("")) {
            return "Please enter a name.";
        } else if (newText.matches("^\\s.*") || newText.matches(".*\\s$")) {
            return "The name cannot begin or end with whitespace.";
        }
        for (final String forbiddenString : LdapFieldsAndAttributes.FORBIDDEN_SUBSTRINGS) {
            if (newText.contains(forbiddenString)) {
                return "The name must not contain the substring or character '" + forbiddenString + "'.";
            }
        }

        try {

            LdapName ldapName = new LdapName(LdapFieldsAndAttributes.ORGANIZATION_UNIT_FIELD_NAME + "="
                    + LdapEpicsControlsConfiguration.VIRTUAL_ROOT.getUnitTypeValue());
            
            //@formatter:off
            ILdapSearchResult searchResult = LDAP_SERVICE.retrieveSearchResultSynchronously(
                    ldapName,
                    and(
                         equ(nodeTypeName, newText),
                         equ(LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS, objectClassValue)),
                    SearchControls.SUBTREE_SCOPE);
                    //@formatter:on

            Set<SearchResult> results = searchResult.getAnswerSet();

            if (searchResult.getAnswerSet().size() == 0) {
                return null;
            } else if (searchResult.getAnswerSet().size() == 1) {
                SearchResult sr = results.iterator().next();
                if (ControllerTreeViewer.CURRENT_SELECTION.getLdapName().toString().startsWith(sr.getName())) {
                    return null;
                } else {
                    return errorMessage;
                }
            } else {
                return errorMessage;
            }

        } catch (InvalidNameException e) {
            return e.getMessage();
        }
        
    }

    /**
     * Constructor.
     * 
     * @param validator
     *            {@code IInputValidator} represented by this constant.
     */
    Validators(@Nonnull final IInputValidator validator) {
        _validator = validator;
    }

    /**
     * Returns the {@code IInputValidator}.
     * 
     * @return the {@code IInputValidator}
     */
    public IInputValidator getValidator() {
        return _validator;
    }
}
