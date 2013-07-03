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
 * $Id: ControllerPropertyTest.java,v 1.1 2010/09/02 15:47:52 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.property.ioc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * TODO (tslamic) :
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 01.09.2010
 */
public class ControllerPropertyTest {

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty#getName()}.
     */
    @Test
    public final void testGetName() {
        assertEquals("epicsHwName", ControllerProperty.HW_NAME.getName());
        assertEquals("epicsIPAddress", ControllerProperty.IP_ADDRESS.getName());
        assertEquals("epicsHelpPage", ControllerProperty.HELP_PAGE.getName());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty#getDescription()}.
     */
    @Test
    public final void testGetDescription() {
        assertEquals("Hardware name",
                     ControllerProperty.HW_NAME.getDescription());
        assertEquals("IP Address",
                     ControllerProperty.IP_ADDRESS.getDescription());
        assertEquals("Help Page", ControllerProperty.HELP_PAGE.getDescription());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValue() {
        assertEquals("", ControllerProperty.HW_NAME.getDefaultValue());
        assertEquals("", ControllerProperty.IP_ADDRESS.getDefaultValue());
        assertEquals("", ControllerProperty.HELP_PAGE.getDefaultValue());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty#getValidator()}.
     */
    @Test
    public final void testGetValidator() {
        assertEquals(Validators.IP_VALIDATOR.getValidator(),
                     ControllerProperty.HW_NAME.getValidator());
        assertEquals(Validators.IP_VALIDATOR.getValidator(),
                     ControllerProperty.IP_ADDRESS.getValidator());
        assertEquals(Validators.NAME_VALIDATOR.getValidator(),
                     ControllerProperty.HELP_PAGE.getValidator());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty#getProperty(java.lang.String)}.
     */
    @Test
    public final void testGetProperty() {
        assertEquals(ControllerProperty.HW_NAME,
                     ControllerProperty.getProperty("epicsHwName"));
        assertEquals(ControllerProperty.IP_ADDRESS,
                     ControllerProperty.getProperty("epicsIPAddress"));
        assertEquals(ControllerProperty.HELP_PAGE,
                     ControllerProperty.getProperty("epicsHelpPage"));
    }
}
