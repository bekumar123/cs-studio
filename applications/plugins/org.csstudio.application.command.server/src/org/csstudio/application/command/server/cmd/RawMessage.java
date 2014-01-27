
/*
 * Copyright (c) 2014 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.application.command.server.cmd;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author mmoeller
 * @version 1.0
 * @since 26.08.2011
 */
public class RawMessage {

    /** Hash table that contains the unprocessed value/key pairs from the JMS message */
    private Map<String, String> content;

    private String propAppId;

    private String propFingerPrint;

    private boolean propEncrypted;

    public RawMessage() {
        content = new Hashtable<String, String>();
        propFingerPrint = "";
        propAppId = "";
        propEncrypted = false;
    }

    public RawMessage(MapMessage message) {
        this();
        try {
            final Enumeration<?> keys = message.getMapNames();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (key != null) {
                    String value = message.getString(key);
                    if (value != null) {
                        content.put(key, value);
                    }
                }
            }
        } catch (final JMSException jmse) {
            content.clear();
        }
        try {
            propEncrypted = message.getBooleanProperty("ENCRYPTED");
            propFingerPrint = message.getStringProperty("FINGERPRINT");
            propAppId = message.getStringProperty("APPLICATION-ID");
        } catch (JMSException e) {
            // Can be ignored
        }
    }

    public boolean isEncryptedMessage() {
        return !propFingerPrint.isEmpty() && !propAppId.isEmpty() && propEncrypted;
    }

    public final boolean itemExists(final String key) {
        return content.containsKey(key);
    }

    public final Iterator<String> getMessagePropertyNames() {
        return content.keySet().iterator();
    }

    public String getPropertyFingerPrint() {
        return propFingerPrint;
    }

    public String getPropertyAppId() {
        return propAppId;
    }

    public Map<String, String> getContent() {
        return new HashMap<String, String>(content);
    }

    /**
     * Return the value of the key.
     *
     * @param key
     * @return The value
     */
    public final String getValue(final String key) {
        if (key == null) {
            return null;
        }
        return content.get(key);
    }
}
