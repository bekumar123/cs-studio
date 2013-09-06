package org.csstudio.dal2.epics.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.softioc.AbstractSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.ISoftIocConfigurator;
import org.csstudio.domain.desy.softioc.SoftIoc;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class EpicsPVAccessTest {

	private static SoftIoc _softIoc;
	private Context _jcaContext;

	@BeforeClass
	public static void beforeClass() {
		setSystemProperties();
		setupLibs();
	}

	@Before
	public void before() throws CAException {
		JCALibrary jca = JCALibrary.getInstance();
		// _jcaContext = jca.createContext(JCALibrary.JNI_THREAD_SAFE);
		_jcaContext = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
	}

	@After
	public void tearDown() {
		if (_jcaContext != null) {
			_jcaContext.dispose();
		}
	}

	private static void setupLibs() {
		// path to jca.dll is found using java.library.path
		// System.setProperty("java.library.path", "libs/win32/x86"); // ahem,
		// no, I put jca.dll in the root of the project.

		// path to Com.dll and ca.dll is hardcoded to windows
		System.setProperty("gov.aps.jca.jni.epics.win32-x86.library.path",
				"libs/win32/x86");
	}

	private static void setSystemProperties() {
		System.setProperty("dal.plugs", "EPICS");
		System.setProperty("dal.plugs.default", "EPICS");
		System.setProperty("dal.propertyfactory.EPICS",
				"org.csstudio.dal.epics.PropertyFactoryImpl");
		System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list",
				"127.0.0.1");
		System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
				"NO");
		System.setProperty(
				"com.cosylab.epics.caj.CAJContext.connection_timeout", "30.0");
		System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period",
				"15.0");
		System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
				"5065");
		System.setProperty("com.cosylab.epics.caj.CAJContext.server_port",
				"5064");
		System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes",
				"16384");
	}

	private static void startUpSoftIoc() throws Exception {
		File file = new File(EpicsPvAccessFactoryTest.class.getClassLoader()
				.getResource("db/EpicsTest.db").toURI());

		final ISoftIocConfigurator cfg = new JUnitSoftIocConfigurator()
				.with(file);

		_softIoc = new SoftIoc(cfg);
		_softIoc.start();
		while (!_softIoc.isStartUpDone()) {
			// wait IOC startup finished
		}
		Thread.sleep(400);
	}

	public static void stopSoftIoc() throws Exception {
		if (_softIoc != null) {
			_softIoc.stop();
		}
		Thread.sleep(1000); // wait until soft ioc comes down
	}

	@Test
	public void testConnectionChangesWhenIocStartsAndStops() throws Exception {
		EpicsPvAccess<Long> pva = new EpicsPvAccess<Long>(_jcaContext,
				PvAddress.getValue("TestDal:ConstantPV"), Type.LONG);

		PvListenerMock<Long> listener = new PvListenerMock<Long>(
				ListenerType.VALUE);

		pva.initMonitor(listener);
		Assert.assertEquals(0, listener.getConnectionChangedCalled());
		Assert.assertFalse(listener.isConnected());

		startUpSoftIoc();
		Assert.assertEquals(1, listener.getConnectionChangedCalled());
		Assert.assertTrue(listener.isConnected());

		stopSoftIoc();
		Assert.assertEquals(2, listener.getConnectionChangedCalled());
		Assert.assertFalse(listener.isConnected());
	}

	@Test
	public void testConnectionChangesAfterPvRegisters() throws Exception {
		startUpSoftIoc();

		EpicsPvAccess<Long> pva = new EpicsPvAccess<Long>(_jcaContext,
				PvAddress.getValue("TestDal:ConstantPV"), Type.LONG);

		PvListenerMock<Long> listener = new PvListenerMock<Long>(
				ListenerType.VALUE);

		pva.initMonitor(listener);

		Thread.sleep(100); // allow soft ioc to answer
		Assert.assertEquals(1, listener.getConnectionChangedCalled());
		Assert.assertTrue(listener.isConnected());

		// here is defined that the listener gets updates on deregister - is
		// this useful?
		// objectUnderTest.deregisterListener(listener);
		stopSoftIoc();

		Thread.sleep(100); // allow soft ioc to answer

		Assert.assertEquals(2, listener.getConnectionChangedCalled());
		Assert.assertFalse(listener.isConnected());
	}

	@Test(timeout = 7000)
	public void testMonitor() throws Exception {
		startUpSoftIoc();

		EpicsPvAccess<Long> pva = new EpicsPvAccess<Long>(_jcaContext,
				PvAddress.getValue("TestDal:Counter"), Type.LONG);

		PvListenerMock2<Long> listener = new PvListenerMock2<Long>(
				ListenerType.VALUE, 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 0L,
				1L, 2L, 3L, 4L, 5L);
		pva.initMonitor(listener);

		while (!listener.isFinished()) {
			Thread.yield();
		}

		Assert.assertFalse(listener.hasError());

		pva.stopMonitor();
		stopSoftIoc();
	}

	@Test(timeout = 10000)
	public void testMonitorAlarm() throws Exception {
		startUpSoftIoc();

		EpicsPvAccess<Long> pva = new EpicsPvAccess<Long>(_jcaContext,
				PvAddress.getValue("TestDal:Counter"), Type.LONG);

		PvListenerMock2<Long> listener = new PvListenerMock2<Long>(
				ListenerType.ALARM, 0L, 6L, 0L, 6L, 0L, 6L, 0L, 6L);
		pva.initMonitor(listener);

		while (!listener.isFinished()) {
			Thread.yield();
		}

		Assert.assertFalse(listener.hasError());

		pva.stopMonitor();
		stopSoftIoc();
	}

	@Test(timeout = 7000)
	public void testAsyncGetValue() throws Exception {
		startUpSoftIoc();

		PvAddress address = PvAddress.getValue("TestDal:Counter");
		EpicsPvAccess<double[]> pva = new EpicsPvAccess<double[]>(_jcaContext,
				address, Type.DOUBLE_SEQ);

		ResponseCallbackHandlerMock<double[]> callbackHandler = new ResponseCallbackHandlerMock<double[]>();
		pva.getValue(callbackHandler);

		while (!callbackHandler.isFinished()) {
			Thread.yield();
		}

		CsPvData<double[]> data = callbackHandler.getValue();

		double[] value = data.getValue();

		Assert.assertTrue(value.length == 1);
		Assert.assertTrue("Unexpected Value: " + Arrays.toString(value),
				value[0] >= 0 && value[0] <= 9);

		stopSoftIoc();
	}

	@Test
	public void testSeverityType() throws Exception {
		startUpSoftIoc();

		PvAddress address = PvAddress.getValue("TestDal:ConstantPV.HSV");
		EpicsPvAccess<EpicsAlarmSeverity> pva = new EpicsPvAccess<EpicsAlarmSeverity>(
				_jcaContext, address, Type.SEVERITY);

		ResponseCallbackHandlerMock<EpicsAlarmSeverity> callbackHandler = new ResponseCallbackHandlerMock<EpicsAlarmSeverity>();
		pva.getValue(callbackHandler);

		while (!callbackHandler.isFinished()) {
			Thread.yield();
		}

		CsPvData<EpicsAlarmSeverity> data = callbackHandler.getValue();

		Assert.assertEquals(EpicsAlarmSeverity.MINOR, data.getValue());

		stopSoftIoc();
	}

	@Test
	public void testNotExistingPvDoesNotConnect() throws Exception {
		startUpSoftIoc();

		EpicsPvAccess<String> pva = new EpicsPvAccess<String>(_jcaContext,
				PvAddress.getValue("TestDal:NotExisting"), Type.STRING);

		PvListenerMock<String> listenerMock = new PvListenerMock<String>(
				ListenerType.VALUE);
		pva.initMonitor(listenerMock);

		Thread.sleep(100); // allow soft ioc to answer
		Assert.assertEquals(0, listenerMock.getConnectionChangedCalled());
		Assert.assertFalse(listenerMock.isConnected());

		stopSoftIoc();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetFieldType() throws Exception {
		startUpSoftIoc();

		EpicsPvAccessFactory factory = new EpicsPvAccessFactory(_jcaContext);

		{
			ICsResponseListener<Type<?>> callback = mock(ICsResponseListener.class);
			factory.requestNativeType(PvAddress.getValue("TestDal:ConstantPV"),
					callback);
			verify(callback, times(0)).onFailure(any(Throwable.class));
			verify(callback, timeout(100)).onSuccess(eq(Type.DOUBLE));
		}

		{
			ICsResponseListener<Type<?>> callback = mock(ICsResponseListener.class);
			ICsOperationHandle operationHandle = factory.requestNativeType(PvAddress.getValue("TestDal:NotExisting"),
					callback);
			Thread.sleep(50);
			verify(callback, times(0)).onFailure(any(Throwable.class));
			verify(callback, times(0)).onSuccess(any(Type.class));
			
			operationHandle.cancel();
		}
		
		stopSoftIoc();
	}
	
	@SuppressWarnings("unchecked")
	@Test @Ignore
	public void testGetFieldTypeForCharacteristic() throws Exception {
		startUpSoftIoc();

		EpicsPvAccessFactory factory = new EpicsPvAccessFactory(_jcaContext);

		{
			ICsResponseListener<Type<?>> callback = mock(ICsResponseListener.class);
			factory.requestNativeType(PvAddress.getValue("TestDal:ConstantPV.HSV"),
					callback);
			Thread.sleep(50);
			verify(callback, times(0)).onFailure(any(Throwable.class));
			verify(callback, timeout(100)).onSuccess(eq(Type.SEVERITY));
		}
		
		stopSoftIoc();
	}

	public static class JUnitSoftIocConfigurator extends
			AbstractSoftIocConfigurator {
		public JUnitSoftIocConfigurator() throws URISyntaxException,
				IOException {
			super(new File(AbstractSoftIocConfigurator.class.getClassLoader()
					.getResource("win/demo.exe").toURI()), new File(
					AbstractSoftIocConfigurator.class.getClassLoader()
							.getResource("st.cmd").toURI()));
		}
	}

}
