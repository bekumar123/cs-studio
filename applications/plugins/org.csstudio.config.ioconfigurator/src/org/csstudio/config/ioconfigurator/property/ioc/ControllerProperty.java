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
 * $Id: ControllerProperty.java,v 1.1 2010/09/02 15:47:50 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.property.ioc;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.jface.dialogs.IInputValidator;

import com.google.common.collect.Maps;

/**
 * Contains the IOC properties.
 * <p>
 * Every property is defined with the following parameters:
 * <ul>
 *   <li>{@code propertyName} - the name of the property</li>
 *   <li>{@code propertyDescription} - the description of the property</li>
 *   <li>{@code defaultValue} - the default value of the property</li>
 *   <li>{@code validator} - the validator used to check if the property value is valid.</li>
 * </ul>
 * </p>
 *
 * TODO: Since every IOC property is hard-coded as a constant
 *       it has a great flexibility disadvantage as every additional
 *       property has to be added manually. Therefore this solution
 *       works temporarily, but should be changed in the future.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 14.08.2010
 */
public enum ControllerProperty {

    HW_NAME("epicsHwName", "Hardware name", "", Validators.IP_VALIDATOR
            .getValidator()),

    IP_ADDRESS("epicsIPAddress", "IP Address", "", Validators.IP_VALIDATOR
            .getValidator()),

    IP_ADDRESS_REDUNDANT("epicsIPAddressR",
                         "Redundant IP Address",
                         "",
                         Validators.NAME_VALIDATOR.getValidator()),

    SAVE_ENABLED("epicsCaSaveEnabled",
                 "Enabled saving",
                 "",
                 Validators.NAME_VALIDATOR.getValidator()),

    ALARM_DISPLAY("epicsCssAlarmDisplay",
                  "Alarm Display",
                  "",
                  Validators.NAME_VALIDATOR.getValidator()),

    CSS_DISPLAY("epicsCssDisplay",
                "Control System Studio Display",
                "",
                Validators.NAME_VALIDATOR.getValidator()),

    CS_TYPE("epicsCsType", "Cs Type", "", Validators.NAME_VALIDATOR
            .getValidator()),

    CS_VERSION("epicsCsVersion", "Cs Version", "", Validators.NAME_VALIDATOR
            .getValidator()),

    HELP_GUIDANCE("epicsHelpGuidance",
                  "Help Guidance",
                  "",
                  Validators.NAME_VALIDATOR.getValidator()),

    HELP_PAGE("epicsHelpPage", "Help Page", "", Validators.NAME_VALIDATOR
            .getValidator()),

    HOME_DIR("epicsHomeDirectory",
             "Home Directory",
             "",
             Validators.NAME_VALIDATOR.getValidator()),

    HW_TYPE("epicsHwType", "Hardware Type", "", Validators.NAME_VALIDATOR
            .getValidator()),

    HW_VERSION("epicsHwVersion",
               "Hardware Version",
               "",
               Validators.NAME_VALIDATOR.getValidator()),

    LOCATION("epicsLocation",
             "Location of the IOC",
             "",
             Validators.NAME_VALIDATOR.getValidator()),

    NTP_PARAM("epicsNTPParam", "NTP Parameters", "", Validators.NAME_VALIDATOR
            .getValidator()),

    OS_TYPE("epicsOsType",
            "Operational System Type",
            "",
            Validators.NAME_VALIDATOR.getValidator()),

    OS_VERSION("epicsOsVersion",
               "Operational System Version",
               "",
               Validators.NAME_VALIDATOR.getValidator()),

    RESPONSIBLE_NAME("epicsResponsibleName",
                     "The person responsible for this IOC",
                     "",
                     Validators.NAME_VALIDATOR.getValidator()),

    RESPONSIBLE_PHONE("epicsResponsiblePhone",
                      "Telephone of the responsible person",
                      "",
                      Validators.NAME_VALIDATOR.getValidator()),

    SERVICE_NAME("epicsServiceName",
                 "Service name",
                 "",
                 Validators.NAME_VALIDATOR.getValidator()),

    SERVICE_PHONE("epicsServicePhone",
                  " Service telephone",
                  "",
                  Validators.NAME_VALIDATOR.getValidator()),

    SHELL_PROMPT_SET("epicsShellPromptSet",
                     "Shell Prompt",
                     "",
                     Validators.NAME_VALIDATOR.getValidator()),

    SUBNET("epicsSubNet", "Sub Net", "", Validators.NAME_VALIDATOR
            .getValidator());

    /*
     * Creates a map holding this class constants that can be
     * retrieved by its name.
     */
    private static Map<String, ControllerProperty> PROPERTY_HOLDER;
    static {
        ControllerProperty[] values = values();
        PROPERTY_HOLDER = Maps.newHashMapWithExpectedSize(values.length);
        for (ControllerProperty i : values) {
            PROPERTY_HOLDER.put(i.getName(), i);
        }
    }

    private final String _propertyName;
    private final String _description;
    private final String _defaultValue;
    private final IInputValidator _validator;

    /**
    *
    * Constructor.
    * @param propertyName {@code String} name of this property
    * @param description {@code String} description of this property
    */
    ControllerProperty(@Nonnull final String propertyName,
                       @Nonnull final String description,
                       @Nonnull final String defaultValue,
                       @Nonnull final IInputValidator validator) {
        _propertyName = propertyName;
        _description = description;
        _defaultValue = defaultValue;
        _validator = validator;
    }

    /**
     * Returns the name of this property.
     * @return {@code String} name of this property.
     */
    @Nonnull
    public String getName() {
        return _propertyName;
    }

    /**
     * Returns the description of this property.
     * @return {@code String} description of this property.
     */
    @Nonnull
    public String getDescription() {
        return _description;
    }

    /**
     * Returns the default value of this property.
     * @return {@code String} default value of this property.
     */
    @Nonnull
    public String getDefaultValue() {
        return _defaultValue;
    }

    /**
     * Returns the input validator for this property.
     * @return {@code IInputValidator} validator for this property.
     */
    @Nonnull
    public IInputValidator getValidator() {
        return _validator;
    }

    /**
     * Returns the property with the given name or {@code null} if no such
     * property exists.
     * @param name {@code String} description of this property
     * @return the property with the given name or {@code null} if no such property exists.
     */
    @CheckForNull
    public static ControllerProperty getProperty(final String name) {
        return PROPERTY_HOLDER.get(name);
    }
}
