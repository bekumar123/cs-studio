package org.csstudio.dal2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Characteristics.Builder;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Timestamp;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.csstudio.dal2.service.cs.ICsPvAccess;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.csstudio.dal2.service.impl.PvAccess;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class PvAccessTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testGetter() throws Exception {
		Type<Integer> type = Type.LONG;
		ICsPvAccess<Integer> csPvAccessMock = mock(ICsPvAccess.class);
		when(csPvAccessMock.getPvAddress()).thenReturn(
				PvAddress.getValue("myPv"));

		PvAccess<Integer> pvAccess = new PvAccess<Integer>(csPvAccessMock, type,
				ListenerType.ALARM);
		assertEquals(PvAddress.getValue("myPv"), pvAccess.getPVAddress());
		assertEquals(Type.LONG, pvAccess.getType());
		assertEquals(ListenerType.ALARM, pvAccess.getListenerType());
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testRegisterAndDeregister() throws Exception {

		// prepare PvAccess
		Type<Integer> type = Type.LONG;
		ICsPvAccess<Integer> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<Integer> objectUnderTest = new PvAccess<Integer>(csPvAccessMock,
				type, ListenerType.VALUE);

		// Register listener 1
		IPvListener<Integer> listenerMock1 = mock(IPvListener.class);
		objectUnderTest.registerListener(listenerMock1);

		// Register listener 2
		IPvListener<Integer> listenerMock2 = mock(IPvListener.class);
		objectUnderTest.registerListener(listenerMock2);

		// check underlying monitor registration
		@SuppressWarnings("rawtypes")
		ArgumentCaptor<ICsPvListener> argument = ArgumentCaptor
				.forClass(ICsPvListener.class);
		verify(csPvAccessMock, times(1)).initMonitor(argument.capture());

		// test event
		ICsPvListener<Integer> registredListener = argument.getValue();
		assertEquals(ListenerType.VALUE, registredListener.getType());
		CsPvData<Integer> csPvData = new CsPvData<Integer>(7,
				createTestCharacteristics(), Type.DOUBLE);
		registredListener.valueChanged(csPvData);

		verify(listenerMock1).valueChanged(objectUnderTest, 7);
		verify(listenerMock2).valueChanged(objectUnderTest, 7);

		// Deregister first listener

		objectUnderTest.deregisterListener(listenerMock1);
		verify(csPvAccessMock, never()).stopMonitor();

		objectUnderTest.deregisterListener(listenerMock2);
		verify(csPvAccessMock, times(1)).stopMonitor();
	}

	private Characteristics createTestCharacteristics() {
		Builder characteristicBuilder = new Characteristics.Builder();
		characteristicBuilder.set(Characteristic.GRAPH_MIN, 0.0);
		characteristicBuilder.set(Characteristic.GRAPH_MAX, 100.0);
		characteristicBuilder.set(Characteristic.WARNING_MIN, 30.0);
		characteristicBuilder.set(Characteristic.WARNING_MAX, 70.0);
		characteristicBuilder.set(Characteristic.ALARM_MIN, 10.0);
		characteristicBuilder.set(Characteristic.ALARM_MAX, 90.0);
		Characteristics characteristics = characteristicBuilder.build();
		return characteristics;
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterNull() throws Exception {
		ICsPvAccess<String> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<String> objectUnderTest = new PvAccess<String>(csPvAccessMock,
				Type.STRING, ListenerType.VALUE);
		objectUnderTest.registerListener(null);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalStateException.class)
	public void testRegisterSameListenerTwice() throws Exception {
		ICsPvAccess<String> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<String> objectUnderTest = new PvAccess<String>(csPvAccessMock,
				Type.STRING, ListenerType.VALUE);

		IPvListener<String> listenerMock = mock(IPvListener.class);
		objectUnderTest.registerListener(listenerMock);
		objectUnderTest.registerListener(listenerMock);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalStateException.class)
	public void testDeregisterNotExisting_1() throws Exception {
		ICsPvAccess<String> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<String> objectUnderTest = new PvAccess<String>(csPvAccessMock,
				Type.STRING, ListenerType.VALUE);

		// deregister unregistred listener
		objectUnderTest.deregisterListener(mock(IPvListener.class));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalStateException.class)
	public void testDeregisterNotExisting_2() throws Exception {
		ICsPvAccess<Integer> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<Integer> objectUnderTest = new PvAccess<Integer>(csPvAccessMock,
				Type.LONG, ListenerType.VALUE);

		// register one listener
		objectUnderTest.registerListener(mock(IPvListener.class));

		// deregister another listener
		objectUnderTest.deregisterListener(mock(IPvListener.class));
	}

	@SuppressWarnings("unchecked")
	@Test()
	public void testDeregisterAll() throws Exception {
		ICsPvAccess<Integer> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<Integer> objectUnderTest = new PvAccess<Integer>(csPvAccessMock,
				Type.LONG, ListenerType.VALUE);

		// register two listener
		objectUnderTest.registerListener(mock(IPvListener.class));
		objectUnderTest.registerListener(mock(IPvListener.class));

		verify(csPvAccessMock, times(1)).initMonitor(any(ICsPvListener.class));
		verify(csPvAccessMock, times(0)).stopMonitor();

		when(csPvAccessMock.hasMonitor()).thenReturn(true);

		// deregister another listener
		objectUnderTest.deregisterAllListener();

		verify(csPvAccessMock, times(1)).stopMonitor();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetValue() throws DalException {

		final ICsPvAccess<Integer> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<Integer> objectUnderTest = new PvAccess<Integer>(csPvAccessMock,
				Type.LONG, ListenerType.VALUE);

		assertNull(objectUnderTest.getLastKnownValue());
		assertNull(objectUnderTest.getLastKnownCharacteristics());

		final Characteristics characteristics = Characteristics.builder()
				.setStatus(EpicsAlarmStatus.LOW).build();

		// Start second thread to perform async callback
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					// wait until main thread has requested the value
					Thread.sleep(100);

					ArgumentCaptor<ICsResponseListener> captor = ArgumentCaptor.forClass(ICsResponseListener.class);
					verify(csPvAccessMock).getValue(captor.capture());
					ICsResponseListener listener = captor.getValue();
					listener.onSuccess(new CsPvData<Integer>(65, characteristics, Type.DOUBLE));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "async stub callback thread").start();

		assertEquals(65L, (long) objectUnderTest.getValue(1, TimeUnit.SECONDS));

		assertEquals(65L, (long) objectUnderTest.getLastKnownValue());
		assertEquals(characteristics,
				objectUnderTest.getLastKnownCharacteristics());
	}

	@SuppressWarnings({ "unchecked" })
	@Test(expected = TimeoutException.class, timeout = 300)
	public void testGetValueWithTimeout() throws DalException {

		final ICsPvAccess<Integer> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<Integer> objectUnderTest = new PvAccess<Integer>(csPvAccessMock,
				Type.LONG, ListenerType.VALUE);

		objectUnderTest.getValue(250, TimeUnit.MILLISECONDS);
	}

	@SuppressWarnings({ "unchecked" })
	@Test(timeout = 50000)
	public void testGetValueWithTimeoutAndCallback() throws DalException {

		ICsOperationHandle operationHandle = mock(ICsOperationHandle.class);

		final ICsPvAccess<Integer> csPvAccessMock = mock(ICsPvAccess.class);
		when(csPvAccessMock.getValue(any(ICsResponseListener.class)))
				.thenReturn(operationHandle);

		PvAccess<Integer> objectUnderTest = new PvAccess<Integer>(csPvAccessMock,
				Type.LONG, ListenerType.VALUE);

		IResponseListener<Integer> listener = mock(IResponseListener.class);
		objectUnderTest.getValue(250, TimeUnit.MILLISECONDS, listener);

		verify(listener, timeout(30000).times(1)).onTimeout();
		verify(operationHandle, times(1)).cancel();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetLastKnown() throws Exception {

		// prepare PvAccess
		Type<String> type = Type.STRING;
		ICsPvAccess<String> csPvAccessMock = mock(ICsPvAccess.class);
		PvAccess<String> objectUnderTest = new PvAccess<String>(csPvAccessMock,
				type, ListenerType.VALUE);

		assertNull(objectUnderTest.getLastKnownValue());
		assertNull(objectUnderTest.getLastKnownCharacteristics());

		// test last known values to be updated by async value request
		{
			objectUnderTest
					.getValue((IResponseListener<String>) mock(IResponseListener.class));

			ArgumentCaptor<ICsResponseListener> captor = ArgumentCaptor
					.forClass(ICsResponseListener.class);
			verify(csPvAccessMock).getValue(captor.capture());

			String value = "A";
			Characteristics characteristics = Characteristics.builder()
					.set(Characteristic.TIMESTAMP, new Timestamp(1000, 0))
					.build();
			captor.getValue().onSuccess(
					new CsPvData<String>(value, characteristics, Type.STRING));

			assertEquals(value, objectUnderTest.getLastKnownValue());
			assertEquals(characteristics,
					objectUnderTest.getLastKnownCharacteristics());
		}

		// test last known values to be updated by monitor
		{
			objectUnderTest.registerListener(mock(IPvListener.class));

			ArgumentCaptor<ICsPvListener> captor = ArgumentCaptor
					.forClass(ICsPvListener.class);
			verify(csPvAccessMock).initMonitor(captor.capture());

			String value = "B";
			Characteristics characteristics = Characteristics.builder()
					.set(Characteristic.TIMESTAMP, new Timestamp(2000, 0))
					.build();
			captor.getValue().valueChanged(new CsPvData<String>(value, characteristics, Type.STRING));

			assertEquals(value, objectUnderTest.getLastKnownValue());
			
			assertEquals(characteristics,
					objectUnderTest.getLastKnownCharacteristics());
			
			objectUnderTest.deregisterAllListener();
		}

	}

}
