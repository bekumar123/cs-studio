package org.csstudio.dal2.epics.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.EnumType;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.softioc.AbstractSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.ISoftIocConfigurator;
import org.csstudio.domain.desy.softioc.SoftIoc;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class EpicsPVAccessTest {

	private static SoftIoc _softIoc;
	private Context _jcaContext;

	@Before
	public void before() throws CAException {
		_jcaContext = EpicsServiceTestUtil.createJCAContext();
	}

	@After
	public void tearDown() {
		if (_jcaContext != null) {
			_jcaContext.dispose();
		}
	}

	private static void startUpSoftIoc() throws Exception {
		File file = new File(EpicsPvAccessFactoryTest.class.getClassLoader()
				.getResource("db/EpicsTest.db").toURI());

		System.out.println(file.getAbsolutePath());
		assert file.exists();

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testConnectionChangesAfterPvRegisters() throws Exception {
		startUpSoftIoc();

		String PV = "TestDal:ConstantPV";

		EpicsPvAccess<Long> pva = new EpicsPvAccess<Long>(_jcaContext,
				PvAddress.getValue(PV), Type.LONG);

		ICsPvListener listenerMock = mock(ICsPvListener.class);
		System.out.println(listenerMock);

		when(listenerMock.getType()).thenReturn(ListenerType.VALUE);

		pva.initMonitor(listenerMock);

		verify(listenerMock, timeout(5000)).connectionChanged(PV, true);
		stopSoftIoc();

		verify(listenerMock, timeout(5000)).connectionChanged(PV, false);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test()
	public void testMonitorAlarm() throws Exception {
		startUpSoftIoc();

		EpicsPvAccess<Long> pva = new EpicsPvAccess<Long>(_jcaContext,
				PvAddress.getValue("TestDal:AlarmCounter"), Type.LONG);

		ICsPvListener<Long> listener = mock(ICsPvListener.class);
		when(listener.getType()).thenReturn(ListenerType.ALARM);

		pva.initMonitor(listener);

		verify(listener, timeout(5000)).connectionChanged(
				"TestDal:AlarmCounter", true);

		ArgumentCaptor<CsPvData> captor = ArgumentCaptor
				.forClass(CsPvData.class);
		verify(listener, timeout(10000).atLeast(20)).valueChanged(
				captor.capture());

		List<CsPvData> values = captor.getAllValues();
		
		assertTrue(values.size() >= 20);
		
		EpicsAlarmSeverity lastSeverity = null;
		for (int i = 0; i < values.size(); i++) {
			EpicsAlarmSeverity severity = values.get(i).getCharacteristics()
					.getSeverity();
			if (lastSeverity != null) {
				switch (lastSeverity) {
				case NO_ALARM:
					assertEquals(EpicsAlarmSeverity.MINOR, severity);
					break;
				case MINOR:
					assertEquals(EpicsAlarmSeverity.MAJOR, severity);
					break;
				case MAJOR:
					assertEquals(EpicsAlarmSeverity.NO_ALARM, severity);
					break;
				default:
					Assert.fail("Unexpected severity: " + severity);
				}
			}
			lastSeverity = severity;
		}

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(timeout = 7000)
	public void testAsyncGetEnumValue() throws Exception {
		startUpSoftIoc();

		PvAddress address = PvAddress.getValue("TestDal:STATE_mbbi");

		// retrieve as long
		{
			EpicsPvAccess<Long> pva = new EpicsPvAccess<Long>(_jcaContext,
					address, Type.LONG);

			ICsResponseListener<CsPvData<Long>> callback = mock(ICsResponseListener.class);
			pva.getValue(callback);

			ArgumentCaptor<CsPvData> captor = ArgumentCaptor
					.forClass(CsPvData.class);
			verify(callback, timeout(500)).onSuccess(captor.capture());
			CsPvData csPvData = captor.getValue();

			assertEquals(4L, csPvData.getValue());
		}

		// retrieve as enum
		{
			EpicsPvAccess<EnumType> pva = new EpicsPvAccess<EnumType>(
					_jcaContext, address, Type.ENUM);

			ICsResponseListener<CsPvData<EnumType>> callback = mock(ICsResponseListener.class);
			pva.getValue(callback);

			ArgumentCaptor<CsPvData> captor = ArgumentCaptor
					.forClass(CsPvData.class);
			verify(callback, timeout(100)).onSuccess(captor.capture());
			CsPvData<EnumType> csPvData = captor.getValue();

			assertEquals(4, csPvData.getValue().getValue());
			assertEquals("laeuft", csPvData.getValue().getName());

			List<String> expectedLabels = Arrays.asList("", "gestoppt",
					"bereit", "startbereit", "laeuft", "auto geregelt",
					"man drosseln", "man oeffnen", "soft Stop", "hard Stop",
					"Hand", "Auto-Start", "Druck abbauen", "mit CV400 abbauen",
					"mit CV408 abbauen");
			assertEquals(
					expectedLabels,
					Arrays.asList(csPvData.getCharacteristics().get(
							Characteristic.LABELS)));
		}

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
			ICsOperationHandle operationHandle = factory.requestNativeType(
					PvAddress.getValue("TestDal:NotExisting"), callback);
			Thread.sleep(50);
			verify(callback, times(0)).onFailure(any(Throwable.class));
			verify(callback, times(0)).onSuccess(any(Type.class));

			operationHandle.cancel();
		}

		stopSoftIoc();
	}

	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void testGetFieldTypeForCharacteristic() throws Exception {
		startUpSoftIoc();

		EpicsPvAccessFactory factory = new EpicsPvAccessFactory(_jcaContext);

		{
			ICsResponseListener<Type<?>> callback = mock(ICsResponseListener.class);
			factory.requestNativeType(
					PvAddress.getValue("TestDal:ConstantPV.HSV"), callback);
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
