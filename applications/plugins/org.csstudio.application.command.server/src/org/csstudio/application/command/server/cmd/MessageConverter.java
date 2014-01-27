
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
 */

package org.csstudio.application.command.server.cmd;

import java.security.PublicKey;
import org.csstudio.application.command.server.jms.CommandMessage;
import org.csstudio.application.command.server.service.CommandMessageListener;
import org.csstudio.application.command.server.service.RawMessageListener;
import org.csstudio.headless.common.cipher.CipherMessages;
import org.csstudio.headless.common.cipher.KeyStores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 23.01.2014
 */
public class MessageConverter implements RawMessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConverter.class);

    private KeyStores keyStores;

    private CipherMessages cipherMsg;

    private CommandMessageListener cmdMsgListener;

    public MessageConverter(KeyStores store, CommandMessageListener listener) {
        keyStores = store;
        cipherMsg = new CipherMessages();
        cmdMsgListener = listener;
    }

    /**
     * Verschlüsselte Nachricht für den Command Server:
     *
     * TYPE = command
     * EVENTTIME = yyyy-MM-dd HH:mm:ss.SSS
     * NAME = Kommando (VERSCHLÜSSELT)
     * APPLICATION-ID = Plugin-Id (VERSCHLÜSSELT)
     * ...
     *
     * Eigenschaften der Nachrichten:
     *
     * ENCRYPTED = true
     * FINGERPRINT = EVENTTIME (VERSCHLÜSSELT)
     * APPLICATION-ID = Plugin-Id (Base64 verschlüsselt)
     *
     */
    private CommandMessage convertRawMessage(RawMessage raw) {
        CommandMessage result = null;
        if (raw.isEncryptedMessage()) {
            String propAppId = cipherMsg.decodeBase64(raw.getPropertyAppId());
            PublicKey publicKey = keyStores.getClientPublicKey(propAppId);
            if (publicKey != null) {
                // First check the finger print and the event time
                String fingerPrint = cipherMsg.decryptString(raw.getPropertyFingerPrint(), publicKey);
                String eventTime = raw.getValue("EVENTTIME");
                String appId = cipherMsg.decryptString(raw.getValue("APPLICATION-ID"), publicKey);
                if (fingerPrint.equals(eventTime) && appId.equals(propAppId)) {
                    String type = raw.getValue("TYPE");
                    String command = raw.getValue("COMMAND");
                    if (command == null) {
                        command = raw.getValue("NAME");
                    }
                    command = cipherMsg.decryptString(command, publicKey);
                    result = new CommandMessage(type, command);
                }
            } else {
                LOG.warn("Cannot find the public key for the client application! Ignoring the message.");
            }
        }
        if (result == null) {
            // An empty message will not be processed
            result = new CommandMessage();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRawMessage(RawMessage message) {
        CommandMessage cmdMsg = convertRawMessage(message);
        if (cmdMsg.isCommandMessage()) {
            if (cmdMsgListener != null) {
                cmdMsgListener.onCommandMessage(cmdMsg);
            }
        }
    }
}
