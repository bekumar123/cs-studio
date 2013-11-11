import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import junit.framework.Assert;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.csstudio.alarm.dal2jms.Dal2JmsApplication;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAcknowledgeService;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.declaration.IRemoteAcknowledgeService;
import org.csstudio.alarm.service.test.AlarmServiceFactory;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.epics.service.test.TestSoftIOC;
import org.csstudio.dal2.service.IDalServiceFactory;
import org.csstudio.persister.declaration.IPersistenceService;
import org.csstudio.persister.test.PersisterServiceFactory;
import org.csstudio.remote.jms.command.ClientGroup;
import org.csstudio.remote.jms.command.IRemoteCommandService;
import org.csstudio.servicelocator.ServiceLocator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.Bundle;
import org.remotercp.service.connection.session.ISessionService;

public class Dal2JmsIntegrationTest {

	private static final String BROKER_URL = "vm://dal2jmsTest";

	private static Connection _connection;

	private static ExecutorService _singleThreadExecutor;

	private TestSoftIOC _softIOC;

	private MessageListener _listenerMock;

	private Session _consumerSession;

	@BeforeClass
	public static void beforeClass() throws JMSException {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				BROKER_URL);
		_connection = connectionFactory.createConnection();
		_connection.start();

		System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list",
				"localhost");
		System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
				"NO");
	}

	@AfterClass
	public static void afterClass() throws JMSException {
		_connection.close();
	}

	@Before
	public void setup() throws Exception {
		_singleThreadExecutor = Executors.newSingleThreadExecutor();

		Bundle bundle = Platform.getBundle("org.csstudio.alarm.dal2jms.test");
		
		assertNotNull("This test must be executed as plugin test.", bundle);
		
		URL fileURL = bundle.getEntry("res/dal2jmsIntegrationTest.db");
		File file = new File(FileLocator.resolve(fileURL).toURI());

		assertTrue(file.exists());

		_softIOC = new TestSoftIOC(file);
		_softIOC.start();

		_listenerMock = mock(MessageListener.class);

		_consumerSession = _connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		Topic alarmTopic = _consumerSession.createTopic("alarmTopic");
		MessageConsumer consumer = _consumerSession.createConsumer(alarmTopic);
		consumer.setMessageListener(_listenerMock);
	}

	@After
	public void tearDown() throws JMSException {
		_singleThreadExecutor.shutdownNow();

		_consumerSession.close();
	}

	@Test
	public void testPureJMS() throws JMSException {

		Session producerSession = _connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);

		// create consumer
		Topic alarmTopic = producerSession.createTopic("alarmTopic");
		MessageProducer producer = producerSession.createProducer(alarmTopic);

		TextMessage senderMessage = producerSession
				.createTextMessage("TestText");
		producer.send(senderMessage);

		ArgumentCaptor<Message> messageCaptor = ArgumentCaptor
				.forClass(Message.class);
		verify(_listenerMock, timeout(500)).onMessage(messageCaptor.capture());
		TextMessage receiverMessage = (TextMessage) messageCaptor.getValue();

		assertNotSame(senderMessage, receiverMessage);
		assertEquals("TestText", receiverMessage.getText());
	}

	@Test
	public void testApplication() throws Exception {
		// prepare session service
		ISessionService sessionService = mock(ISessionService.class);
		ServiceLocator.registerService(ISessionService.class, sessionService);

		// prepare remote command service
		IRemoteCommandService remoteCommandService = mock(IRemoteCommandService.class);
		ServiceLocator.registerService(IRemoteCommandService.class,
				remoteCommandService);

		// prepare alarm config service
		IAlarmConfigurationService alarmConfigurationService = AlarmServiceFactory
				.createAlarmConfigurationService();
		ServiceLocator.registerService(IAlarmConfigurationService.class,
				alarmConfigurationService);
		// TODO add facilities

		// prepare dal service factory
		IDalServiceFactory dalServiceFactory = EpicsServiceTestUtil
				.createDalServiceFactoryWithLocalJCAContext();
		ServiceLocator.registerService(IDalServiceFactory.class,
				dalServiceFactory);

		// prepare alarm service
		IAlarmService alarmService = AlarmServiceFactory.createDal2Impl();
		ServiceLocator.registerService(IAlarmService.class, alarmService);

		// prepare remote acknowledge service mock
		IRemoteAcknowledgeService remoteAcknowledgeService = mock(IRemoteAcknowledgeService.class);
		ServiceLocator.registerService(IRemoteAcknowledgeService.class,
				remoteAcknowledgeService);

		// prepare acknowledge service mock
		IAcknowledgeService acknowledgeService = mock(IAcknowledgeService.class);
		ServiceLocator.registerService(IAcknowledgeService.class,
				acknowledgeService);

		// prepare persistence service
		IPersistenceService persistenceService = PersisterServiceFactory
				.createService();
		ServiceLocator.registerService(IPersistenceService.class,
				persistenceService);

		final Dal2JmsApplication application = new Dal2JmsApplication();

		// Run application in separate thread
		Future<Object> applicationFuture = _singleThreadExecutor
				.submit(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						application.start(mock(IApplicationContext.class));
						return null;
					}
				});

		// Wait for application to signal application start
		ClientGroup clientGroup = AlarmPreference.getClientGroup();
		verify(remoteCommandService, timeout(5000)).sendCommand(clientGroup,
				IRemoteCommandService.Dal2JmsStartedCommand);

		System.out.println("Application started");

		ArgumentCaptor<Message> messageCaptor = ArgumentCaptor
				.forClass(Message.class);
		verify(_listenerMock, timeout(5000).atLeast(7)).onMessage(
				messageCaptor.capture());

		// Analyze Messages
		Set<String> pvsWithMajorAlarm = listPVsWithMayorAlarm(messageCaptor
				.getAllValues());
		Set<String> expectedPVs = new TreeSet<String>(Arrays.asList(
				"Test:Ramp_calc_0", "Test:Ramp_calc_1", "Test:Ramp_calc_2",
				"Test:Ramp_calc_3", "Test:Ramp_calc_4", "Test:Ramp_calc_5",
				"Test:Ramp_calc_6"));
		assertEquals(expectedPVs, pvsWithMajorAlarm);

		// Stop the application
		application.stop();

		// Wait for application thread to come to end
		applicationFuture.get();
	}

	/**
	 * Lists the pvs with a message with major severity is in the provided list  
	 */
	private Set<String> listPVsWithMayorAlarm(List<Message> list)
			throws JMSException {
		Set<String> pvsWithMajorAlarm = new TreeSet<String>();
		for (Message msg : list) {
			MapMessage mapMessage = (MapMessage) msg;
			String name = mapMessage.getString(AlarmMessageKey.NAME.name());
			String severity = mapMessage.getString(AlarmMessageKey.SEVERITY
					.name());
			if (severity.equals("MAJOR")) {
				pvsWithMajorAlarm.add(name);
			} else {
				Assert.fail("Received unexpected severity [" + severity
						+ "] for pv " + name);
			}
		}
		return pvsWithMajorAlarm;
	}
}
