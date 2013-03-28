
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.application.xmlrpc.server.internal;

import org.csstudio.application.xmlrpc.server.ServerActivator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author mmoeller
 * @since 21.12.2012
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(ServerActivator.PLUGIN_ID);
        prefs.put(PreferenceConstants.XMPP_USER_NAME, "anonymous");
        prefs.put(PreferenceConstants.XMPP_PASSWORD, "anonymous");
        prefs.put(PreferenceConstants.XMPP_SERVER, "xmppserver.where.ever");
        prefs.put(PreferenceConstants.XMPP_SHUTDOWN_PASSWORD, "");
        prefs.putInt(PreferenceConstants.XML_RCP_SERVER_PORT, 8080);
        prefs.putBoolean(PreferenceConstants.ASK_CONTROLSYSTEM_FOR_META, false);
        prefs.put(PreferenceConstants.INFO_TEXT, "I am a small but happy application.");
        prefs.putInt(PreferenceConstants.ARCHIVE_KEY, 0);
        prefs.put(PreferenceConstants.ARCHIVE_NAME, "");
        prefs.put(PreferenceConstants.ARCHIVE_PATH, "");
    }
}
