/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.alarm.dal2jms;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.alarm.dal2jms.preferences.Dal2JmsPreferences;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local service directly used by the alarm handler.
 * 
 * Used for mapping from alarm message to jms message and for sending jms
 * messages.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 09.06.2010
 */
class JmsMessageService {

	private static final Logger LOG = LoggerFactory
			.getLogger(JmsMessageService.class);

	// Given as preference
	private final String _alarmTopicName;

	private final String _beaconTopicName;

	// Given as preference
	private final int _timeToLiveForAlarms;

	private final String _serverId;

	public JmsMessageService() {
		_timeToLiveForAlarms = Dal2JmsPreferences.JMS_TIME_TO_LIVE_ALARMS
				.getValue();
		_alarmTopicName = Dal2JmsPreferences.JMS_ALARM_DESTINATION_TOPIC_NAME
				.getValue();
		_beaconTopicName = Dal2JmsPreferences.JMS_BEACON_DESTINATION_TOPIC_NAME
				.getValue();
		_serverId = Dal2JmsPreferences.JMS_BEACON_MESSAGE_SERVER_ID.getValue();
	}

	public void sendAlarmMessage(@Nonnull final IAlarmMessage alarmMessage) {
		Session session = null;
		try {
			session = newSession();
			MapMessage message = session.createMapMessage();
			copyAlarmMsgToJmsMsg(alarmMessage, message);
			String topic;
			if (alarmMessage.isBeaconMessage()) {
				topic = _beaconTopicName;
				message.setString("ServerId", _serverId);
			}
			else {
				topic = _alarmTopicName;
			}
			sendViaMessageProducer(session, message, topic);
		} catch (final JMSException jmse) {
			LOG.warn("dal2jms could not send alarm message.", jmse);
		} finally {
			tryToCloseSession(session);
		}
	}

	private void sendViaMessageProducer(@Nonnull final Session session,
			@Nonnull final MapMessage message, String topic)
			throws JMSException {
		MessageProducer producer = null;
		try {
			producer = newMessageProducer(session, topic);
			producer.send(message);
		} finally {
			tryToCloseMessageProducer(producer);
		}
	}

	@Nonnull
	private MessageProducer newMessageProducer(@Nonnull final Session session,
			String topic) throws JMSException {
		Destination destination = session.createTopic(topic);
		MessageProducer result = session.createProducer(destination);
		result.setDeliveryMode(DeliveryMode.PERSISTENT);
		result.setTimeToLive(_timeToLiveForAlarms);
		return result;
	}

	private void tryToCloseMessageProducer(
			@CheckForNull final MessageProducer messageProducer) {
		if (messageProducer != null) {
			try {
				messageProducer.close();
			} catch (final JMSException e) {
				LOG.debug("Failed to close message producer", e);
			}
		}
	}

	@Nonnull
	private Session newSession() throws JMSException {
		return SharedJmsConnections.sharedSenderConnection().createSession(
				false, Session.AUTO_ACKNOWLEDGE);
	}

	private void tryToCloseSession(@CheckForNull final Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (final JMSException e) {
				LOG.debug("Failed to close JMS session", e);
			}
		}
	}

	private void copyAlarmMsgToJmsMsg(
			@Nonnull final IAlarmMessage alarmMessage,
			@Nonnull final MapMessage message) throws JMSException {
		for (final AlarmMessageKey key : AlarmMessageKey.values()) {
			message.setString(key.getDefiningName(),
					alarmMessage.getString(key));
		}
	}
}
