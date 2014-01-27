
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

package org.csstudio.application.command.server.jms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.csstudio.application.command.server.cmd.MessageConverter;
import org.csstudio.application.command.server.cmd.RawMessage;
import org.csstudio.application.command.server.service.CommandMessageListener;
import org.csstudio.application.command.server.service.RawMessageListener;
import org.csstudio.headless.common.cipher.KeyStores;
import org.csstudio.utility.jms.consumer.AsyncJmsConsumer;
import org.csstudio.utility.jms.sharedconnection.ClientConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 14.06.2013
 */
public class MessageAcceptor extends Thread implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageAcceptor.class);

    private AsyncJmsConsumer consumer;

    private String topicName;

    private RawMessageListener msgConverter;

    private boolean working;

    public MessageAcceptor(KeyStores store, String topic, CommandMessageListener cml) {
        topicName = topic;
        msgConverter = new MessageConverter(store, cml);
        try {
            consumer = new AsyncJmsConsumer();
        } catch (ClientConnectionException e) {
            LOG.error("[*** ClientConnectionException ***]: Cannot create JMS connection: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        working = false;
        try {
            consumer.createMessageConsumer(topicName, false, "CommandServerConsumer");
            consumer.addMessageListener(this);
            working = true;
        } catch (JMSException e) {
            LOG.error("[*** JMSException ***]: {}", e.getMessage());
        } catch (ClientConnectionException e) {
            LOG.error("[*** ClientConnectionException ***]: Cannot create JMS connection: " + e.getMessage());
        }

        while (working) {
            synchronized (consumer) {
                try {
                    consumer.wait();
                } catch (InterruptedException e) {
                    LOG.warn("I've been interrupted.");
                }
            }
        }

        try {
            consumer.removeMessageListener();
        } catch (JMSException e) {
            LOG.warn("[*** JMSException ***]: {}", e.getMessage());
        }

        consumer.close();
        LOG.info("MessageAcceptor is leaving.");
    }

    public void stopThread() {
        working = false;
        synchronized (consumer) {
            consumer.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            MapMessage mapMsg = (MapMessage) message;
            if (LOG.isDebugEnabled()) {
                LOG.debug(mapMsg.toString());
            }
            RawMessage rawMessage = new RawMessage(mapMsg);
            if (msgConverter != null) {
                msgConverter.onRawMessage(rawMessage);
            }
        }
    }
}
