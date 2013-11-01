package org.csstudio.alarm.dal2jms;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * This test class provides a simple jms listener for the {@value #ALARM_TOPIC} Topic on {@value #JMS_CONNECTION}
 */
public class Dal2JmsTest {

	private static final String JMS_CONNECTION = "tcp://localhost:61616";
	private static final String ALARM_TOPIC = "LOCAL_ALARM";

	public static void main(String[] args) throws Exception {

		Connection connection = null;
		Session session = null;
		Topic topic = null;
		TopicSubscriber subscrib = null;

		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					JMS_CONNECTION);
			connection = connectionFactory.createConnection();
			connection.start();
			connection.setExceptionListener(new ExceptionListener() {
				@Override
				public void onException(JMSException e) {
					e.printStackTrace();
				}
			});

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			topic = session.createTopic(ALARM_TOPIC);

			MessageConsumer consumer = session.createConsumer(topic);
			consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					System.out.println(message);
					try {
						message.acknowledge();
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});

			System.out.println("Enter to Exit");
			String line = "";

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			while (!line.equalsIgnoreCase("exit")) {
				line = reader.readLine();
			}

		} finally {
			try {
				if (null != subscrib)
					subscrib.close();
			} catch (Exception ex) {/* ok */
			}
			try {
				if (null != session)
					session.close();
			} catch (Exception ex) {/* ok */
			}
			try {
				if (null != connection)
					connection.close();
			} catch (Exception ex) {/* ok */
			}
		}

	}
}
