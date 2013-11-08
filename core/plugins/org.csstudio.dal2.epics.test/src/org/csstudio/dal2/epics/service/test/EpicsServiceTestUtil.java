package org.csstudio.dal2.epics.service.test;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.configuration.DefaultConfiguration;
import gov.aps.jca.event.QueuedEventDispatcher;

import org.csstudio.dal2.epics.service.EpicsPvAccessFactory;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IDalServiceFactory;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.dal2.service.test.DalServiceTestUtil;

public class EpicsServiceTestUtil {

	public static ICsPvAccessFactory createEpicsPvAccessFactory(
			Context jcaContext) {
		return new EpicsPvAccessFactory(jcaContext);
	}

	public static IDalService createDalServiceWithLocalJCAContext()
			throws CAException {

		JCALibrary jca = JCALibrary.getInstance();
		Context jcaContext = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
		ICsPvAccessFactory epicsPvAccessFactory = createEpicsPvAccessFactory(jcaContext);

		return DalServiceTestUtil.createService(epicsPvAccessFactory);
	}

	public static IDalServiceFactory createDalServiceFactoryWithLocalJCAContext()
			throws CAException {

		final ICsPvAccessFactory epicsPvAccessFactory = createEpicsPVAccessFactory();
		
		return new IDalServiceFactory() {
			@Override
			public IDalService newDalService() {
				return DalServiceTestUtil.createService(epicsPvAccessFactory);
			}
		};
	}

	/**
	 * @return
	 * @throws CAException
	 */
	private static ICsPvAccessFactory createEpicsPVAccessFactory()
			throws CAException {
		
		Context jcaContext = createJCAContext();
		
		return createEpicsPvAccessFactory(jcaContext);
	}

	/**
	 * Creates a local JCA-Context
	 * 
	 * @return
	 * @throws CAException
	 */
	public static Context createJCAContext(String library) throws CAException {
		
		// path to Com.dll and ca.dll is hardcoded to windows
				System.setProperty("gov.aps.jca.jni.epics.win32-x86.library.path",
						"libs/win32/x86");
				
				System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
				System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "NO");
				System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout", "30.0");
				System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period", "15.0");
				System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port", "5065");
				System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", "5064");
				System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", "16384");
				
				System.setProperty(QueuedEventDispatcher.class.getName() + ".queue_limit", "100000");
				System.setProperty(QueuedEventDispatcher.class.getName() + ".channel_queue_limit", "100");
//				System.setProperty(LatestMonitorOnlyQueuedEventDispatcher.class.getName() + ".queue_limit", "1000000");
//				System.setProperty(LatestMonitorOnlyQueuedEventDispatcher.class.getName() + ".channel_queue_limit", "6000");
				
				JCALibrary jca = JCALibrary.getInstance();

				final DefaultConfiguration config = new DefaultConfiguration("EPICSPlugConfig");

				final DefaultConfiguration eventDispatcherConfig = new DefaultConfiguration("event_dispatcher");
				eventDispatcherConfig.setAttribute("class", QueuedEventDispatcher.class.getName());
//				eventDispatcherConfig.setAttribute("class", LatestMonitorOnlyQueuedEventDispatcher.class.getName());
				config.addChild(eventDispatcherConfig);

				config.setAttribute("class", library);
				
				return jca.createContext(config);
	}
	
	/**
	 * Creates a local JCA-Context with CAJ
	 * 
	 * @return
	 * @throws CAException
	 */
	public static Context createJCAContext() throws CAException {
		return createJCAContext(JCALibrary.CHANNEL_ACCESS_JAVA);
	}
}
