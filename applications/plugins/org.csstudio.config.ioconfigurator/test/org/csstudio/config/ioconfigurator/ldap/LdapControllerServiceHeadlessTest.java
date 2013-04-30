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
 * $Id: LdapControllerServiceHeadlessTest.java,v 1.1 2010/09/02 15:47:50 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.ldap;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;

import org.csstudio.config.ioconfigurator.activator.Activator;
import org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty;
import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.config.ioconfigurator.tree.model.impl.ControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.utils.LdapUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO (tslamic) :
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 01.09.2010
 */
public class LdapControllerServiceHeadlessTest {

    private static ILdapService LDAP_SERVICE;
    private static IControllerSubtreeNode ROOT;

    @BeforeClass
    public static void createLdapConnection() {

        LDAP_SERVICE = Activator.getDefault().getLdapService();
        Assert.assertNotNull(LDAP_SERVICE);

        // Hard-coded properties
        Map<String, String> map = new HashMap<String, String>(5);
        map.put(Context.PROVIDER_URL, "ldap://krynfsc.desy.de:389/o=DESY,c=DE");
        map.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
        map.put(Context.SECURITY_CREDENTIALS, "cssPass");
        map.put(Context.SECURITY_PROTOCOL, "");
        map.put(Context.SECURITY_AUTHENTICATION, "");

        Assert.assertTrue(LDAP_SERVICE.reInitializeLdapConnection(map));

        ROOT = new ControllerSubtreeNode(LdapEpicsControlsConfiguration.ROOT.getRootTypeValue(),
                                         null,
                                         LdapEpicsControlsConfiguration.ROOT);
        Assert.assertNotNull(ROOT);
    }

    @Test
    public void addIoc() {

        IControllerLeaf leaf = new ControllerLeaf("testLeaf",
                                                  ROOT,
                                                  LdapEpicsControlsConfiguration.IOC);
        for (ControllerProperty i : ControllerProperty.values()) {
            leaf.setValue(i, "test");
        }
        try {
            LdapControllerService.addController(leaf);
            LDAP_SERVICE.lookup(LdapUtils.createLdapQuery("efan",
                                                          "Test",
                                                          "ou"));

        } catch (InvalidNameException e) {
            fail("Could not create new Component");
        } catch (NamingException e) {
            fail("LDAP error");
        }
    }

    @Test
    public void testMethod() {
        try {
            LDAP_SERVICE.lookup(LdapUtils.createLdapQuery("efan",
                                                          "Test",
                                                          "ou",
                                                          "EpicsAlarmcfg"));
        } catch (NamingException e) {
            Assert.fail(e.getMessage());
        }
    }
}
