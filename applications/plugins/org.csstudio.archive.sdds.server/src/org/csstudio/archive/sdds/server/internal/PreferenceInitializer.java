
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
 */

package org.csstudio.archive.sdds.server.internal;

import org.csstudio.archive.sdds.server.SddsServerActivator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Markus Moeller
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {

        final IEclipsePreferences node = DefaultScope.INSTANCE.getNode(SddsServerActivator.PLUGIN_ID);

        // Use JMX instead of XMPP to stop the server
        node.putBoolean(ServerPreferenceKey.P_USE_JMX, false);

        // XMPP server
        node.put(ServerPreferenceKey.P_XMPP_SERVER, "xmpp.where.ever");

        // XMPP user
        node.put(ServerPreferenceKey.P_XMPP_USER, "anonymous");

        node.put(ServerPreferenceKey.P_DESCRIPTION, "I am a simple but happy application.");

        // XMPP password
        node.put(ServerPreferenceKey.P_XMPP_PASSWORD, "anonymous");

        // AAPI default port
        // look at http://www-kryo.desy.de/documents/EPICS/DESY/General/Archiver/AAPI/ArchiveProtocol2.5.htm
        node.putInt(ServerPreferenceKey.P_SERVER_PORT, 4056);

        // Byte order within SDDS files
        node.putBoolean(ServerPreferenceKey.P_SDDS_LITTLE_ENDIAN, false);

        // Use previous record not older than x seconds
        node.putLong(ServerPreferenceKey.P_VALID_RECORD_BEFORE, 3600L);

        // max. number of samples per request
        node.putInt(ServerPreferenceKey.P_MAX_SAMPLES_PER_REQUEST, 10000);

        node.putBoolean(ServerPreferenceKey.P_IGNORE_BIG_FILES, true);

        node.putLong(ServerPreferenceKey.P_MAX_FILE_SIZE, 5242880L);

        node.putBoolean(ServerPreferenceKey.P_USE_COMPRESSED_FILES, true);
    }
}
