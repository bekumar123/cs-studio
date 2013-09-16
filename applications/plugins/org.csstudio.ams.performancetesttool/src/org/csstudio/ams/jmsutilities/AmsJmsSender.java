package org.csstudio.ams.jmsutilities;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.csstudio.ams.performancetesttool.MapMessageTemplate;

import com.beust.jcommander.JCommander;

public class AmsJmsSender {
	
	private AmsJmsSenderCommandLineArgs arguments;
	private Connection connection;
	private Session session;
	private MessageProducer producer;

	public AmsJmsSender(AmsJmsSenderCommandLineArgs arguments) throws JMSException {
		this.arguments = arguments;
		initJms();
		initJFrame();
		
	}

	private void initJFrame() {
		final JFrame jFrame = new JFrame("AMS JMS Sender");
		jFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out
						.println("AmsJmsSender.initJFrame().new WindowAdapter() {...}.windowClosed()");
				try {
					producer.close();
					session.close();
					connection.close();
				} catch (JMSException e1) {
					e1.printStackTrace();
				} finally {
					jFrame.dispose();
				}
				super.windowClosing(e);
			}
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				System.exit(0);
			}
		});
		String text = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(arguments.templateFile));
			String buf;
			while ((buf = reader.readLine()) != null) {
				text += buf + "\n";
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		final JTextArea jTextArea = new JTextArea(text);
		jFrame.add(jTextArea);
		JButton jButton = new JButton("SEND");
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(jTextArea.getText());
			}

		});
		jFrame.add(jButton, BorderLayout.SOUTH);
		jFrame.pack();
		jFrame.setVisible(true);		
	}

	private void sendMessage(String text) {
		try {
			MapMessage message = session.createMapMessage();
			MapMessageTemplate mapMessageTemplate = new MapMessageTemplate(text);
			mapMessageTemplate.applyTo(message);
			producer.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void initJms() throws JMSException {
		ConnectionFactory cf = new ActiveMQConnectionFactory(arguments.uri);
        connection = cf.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(arguments.topic);
        producer = session.createProducer(topic);
	}

	public static void main(String[] args) throws JMSException {
		AmsJmsSenderCommandLineArgs arguments = new AmsJmsSenderCommandLineArgs();
		new JCommander(arguments, args);
		new AmsJmsSender(arguments);
	}

}
