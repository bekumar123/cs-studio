
/*
 * Copyright (c) 2013 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.application.command.server.preferences;

import org.csstudio.application.command.server.ServerActivator;
import org.csstudio.domain.desy.preferences.AbstractPreference;

/**
 * @author mmoeller
 * @since 14.06.2013
 */
public class CommandServerPreferences<T> extends AbstractPreference<T> {

    public static final CommandServerPreferences<String> XMPP_SERVER =
            new CommandServerPreferences<String>("xmppServer", "");

    public static final CommandServerPreferences<String> XMPP_USER =
            new CommandServerPreferences<String>("xmppUser", "");

    public static final CommandServerPreferences<String> XMPP_PASSWORD =
            new CommandServerPreferences<String>("xmppPassword", "");

    public static final CommandServerPreferences<String> JMS_CONSUMER_URLS =
            new CommandServerPreferences<String>("jmsConsumerUrls", "");

    public static final CommandServerPreferences<String> JMS_PUBLISHER_URL =
            new CommandServerPreferences<String>("jmsPublisherUrl", "");

    public static final CommandServerPreferences<String> JMS_TOPIC =
            new CommandServerPreferences<String>("jmsTopic", "");

    public static final CommandServerPreferences<String> DESCRIPTION =
            new CommandServerPreferences<String>("description", "");

    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    protected CommandServerPreferences(String keyAsString, T defaultValue) {
        super(keyAsString, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) CommandServerPreferences.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginID() {
        return ServerActivator.PLUGIN_ID;
    }
}
