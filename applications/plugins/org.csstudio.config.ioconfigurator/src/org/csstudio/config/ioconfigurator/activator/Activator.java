/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfigurator.activator;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.csstudio.config.ioconfigurator.annotation.CheckForNull;
import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.ILdapService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.common.base.Preconditions;

/**
 *
 * TODO (tslamic) :
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 16.08.2010
 */
public class Activator extends AbstractUIPlugin {

    /** ID */
    public static final String ID = "org.csstudio.config.ioconfigurator";

    /** Instance of this class */
    private static Activator INSTANCE;

    /** The LDAP service */
    private ILdapService ldapService;

    /** Constructor called by the Framework. Do not instantiate. */
    public Activator() {
        if (INSTANCE == null) {
            INSTANCE = this;
        } else {
            throw new IllegalStateException("Activator " + ID
                    + " already exist.");
        }
    }

    /**
     * Returns the instance of this class.
     * @return the instance of this class
     */
    @Nonnull
    public static Activator getDefault() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(@Nonnull final BundleContext context) throws Exception {
    	ldapService = ServiceLocator.getService(ILdapService.class);
        
        Preconditions.checkState(ldapService != null, "LdapService must not be null");
        
        /*
         * The following converts the real LDAP server
         * to test LDAP server;
         * Remove when this plug-in works as expected.
         */
        Map<String, String> map = new HashMap<String, String>(5);
//        map.put(Context.PROVIDER_URL, "ldap://krynfs.desy.de:389/o=DESY,c=DE");
//        map.put(Context.SECURITY_PRINCIPAL, "uid=css_user,ou=people,o=DESY,c=DE");
//        map.put(Context.SECURITY_CREDENTIALS, "cssPass");
//        map.put(Context.SECURITY_PROTOCOL, "");
//        map.put(Context.SECURITY_AUTHENTICATION, "simple");

        map.put(Context.PROVIDER_URL, "ldap://212.1.56.244:389/o=DESY,c=DE");
        map.put(Context.SECURITY_PRINCIPAL, "cn=manager,o=DESY,c=DE");
        map.put(Context.SECURITY_CREDENTIALS, "hal9000");
        map.put(Context.SECURITY_PROTOCOL, "");
        map.put(Context.SECURITY_AUTHENTICATION, "simple");

        boolean success = ldapService.reInitializeLdapConnection(map);
        if (!success) {
            throw new IllegalStateException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(@Nonnull final BundleContext context) throws Exception {
    	super.stop(context);
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     *
     * @param path
     *            the path
     * @return the image descriptor
     */
    @CheckForNull
    public static ImageDescriptor getImageDescriptor(@Nonnull final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }

    /**
     * Returns this class LDAP Service.
     * @return the LDAP Service or {@code null}
     */
    @CheckForNull
    public ILdapService getLdapService() {
        return ldapService;
    }
}
