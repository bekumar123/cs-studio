package org.csstudio.dal2.epics.acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.dal2.service.IResponseListener;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.dal2.service.test.DalServiceTestUtil;
import org.csstudio.dal2.test.AssertWithTimeout;
import org.csstudio.domain.desy.softioc.AbstractSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.ISoftIocConfigurator;
import org.csstudio.domain.desy.softioc.SoftIoc;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class Dal2WithEpicsTest {

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> result = new ArrayList<Object[]>();
		result.add(new Object[] { JCALibrary.JNI_THREAD_SAFE });
		result.add(new Object[] { JCALibrary.CHANNEL_ACCESS_JAVA });
		return result;
	}

	private String _jcaLibrary;

	public Dal2WithEpicsTest(String jcaLibrary) {
		this._jcaLibrary = jcaLibrary;
	}
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Dal2WithEpicsTest.class);

	private static SoftIoc _softIoc;
	private Context _jcaContext;
	private IDalService _dalService;
	private ICsPvAccessFactory _csPvAccessFactory;

	@Before
	public void before() throws CAException {
		
		_jcaContext = EpicsServiceTestUtil.createJCAContext(_jcaLibrary);
		_csPvAccessFactory = EpicsServiceTestUtil.createEpicsPvAccessFactory(_jcaContext);
		_dalService = DalServiceTestUtil.createService(_csPvAccessFactory);
	}

	@After
	public void tearDown() throws Exception {
		if (_jcaContext != null) {
			_jcaContext.dispose();
		}

		stopSoftIoc();
	}

	@Test(expected = DalException.class, timeout = 900)
	public void testSyncGetValueWithTimeout() throws DalException {

		PvAddress address = PvAddress.getValue("TestDal:ConstantPV");
		IPvAccess<Long> pvAccess = _dalService.getPVAccess(address, Type.LONG,
				ListenerType.VALUE);
		pvAccess.getValue(500, TimeUnit.MILLISECONDS); // This should run on a
														// timeout
	}

	@Test
	public void testSyncGetValue() throws Exception {

		PvAddress address = PvAddress.getValue("TestDal:ConstantPV");
		IPvAccess<Long> pvAccess = _dalService.getPVAccess(address, Type.LONG,
				ListenerType.VALUE);

		assertNull(pvAccess.getLastKnownValue());
		assertNull(pvAccess.getLastKnownCharacteristics());
		assertFalse(pvAccess.isConnected());

		startUpSoftIoc();

		assertNull(pvAccess.getLastKnownValue());
		assertNull(pvAccess.getLastKnownCharacteristics());
		assertFalse(pvAccess.isConnected());

		assertEquals(5L, pvAccess.getValue().longValue());
		assertEquals(5L, pvAccess.getLastKnownValue().longValue());

		Characteristics characteristics = pvAccess
				.getLastKnownCharacteristics();
		assertEquals(0L, characteristics.get(Characteristic.GRAPH_MIN)
				.longValue());
		assertEquals(100L, characteristics.get(Characteristic.GRAPH_MAX)
				.longValue());
		assertEquals(70L, characteristics.get(Characteristic.ALARM_MAX)
				.longValue());

		assertFalse(pvAccess.isConnected());

		stopSoftIoc();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testASyncGetValue() throws Exception {

		PvAddress address = PvAddress.getValue("TestDal:ConstantPV");
		IPvAccess<Long> pvAccess = _dalService.getPVAccess(address, Type.LONG,
				ListenerType.VALUE);

		startUpSoftIoc();

		final IResponseListener<Long> callback = mock(IResponseListener.class);
		pvAccess.getValue(callback);

		new AssertWithTimeout(200) {
			@Override
			protected void performCheck() throws Exception {
				verify(callback, times(1)).onSuccess(5L);
			}
		};
		assertEquals(5L, pvAccess.getLastKnownValue().longValue());

		Characteristics characteristics = pvAccess
				.getLastKnownCharacteristics();
		assertEquals(0L, characteristics.get(Characteristic.GRAPH_MIN)
				.longValue());
		assertEquals(100L, characteristics.get(Characteristic.GRAPH_MAX)
				.longValue());
		assertEquals(70L, characteristics.get(Characteristic.ALARM_MAX)
				.longValue());

		assertFalse(pvAccess.isConnected());

		stopSoftIoc();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConnectionChangesWhenIocStartsAndStops() throws Exception {

		PvAddress address = PvAddress.getValue("TestDal:ConstantPV");

		final IPvAccess<Long> pvAccess = _dalService.getPVAccess(address,
				Type.LONG, ListenerType.VALUE);

		final IPvListener<Long> listener = mock(IPvListener.class);
		pvAccess.registerListener(listener);

		assertNull(pvAccess.getLastKnownValue());
		assertNull(pvAccess.getLastKnownCharacteristics());
		assertFalse(pvAccess.isConnected());

		Thread.sleep(250);

		verify(listener, times(0)).connectionChanged(pvAccess, true);
		verify(listener, times(0)).valueChanged(pvAccess, 7L);

		startUpSoftIoc();

		verify(listener, timeout(1000).times(1)).connectionChanged(pvAccess,
				true);
		verify(listener, timeout(1000).atLeast(1)).valueChanged(pvAccess, 5L);

		assertEquals(5L, pvAccess.getLastKnownValue().longValue());

		Characteristics characteristics = pvAccess
				.getLastKnownCharacteristics();
		assertEquals(0L, characteristics.get(Characteristic.GRAPH_MIN)
				.longValue());
		assertEquals(100L, characteristics.get(Characteristic.GRAPH_MAX)
				.longValue());
		assertEquals(70L, characteristics.get(Characteristic.ALARM_MAX)
				.longValue());
		assertTrue(pvAccess.isConnected());

		stopSoftIoc();

		verify(listener, timeout(10000).times(1)).connectionChanged(pvAccess,
				false);

		assertFalse(pvAccess.isConnected());
		assertEquals(5L, pvAccess.getLastKnownValue().longValue());

		Mockito.reset(listener);

		startUpSoftIoc(); // restart IOC

		verify(listener, timeout(10000).times(1)).connectionChanged(pvAccess,
				true);
		verify(listener, timeout(10000).atLeast(1)).valueChanged(pvAccess, 5L);

		stopSoftIoc();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMonitorCounter() throws Exception {

		startUpSoftIoc();

		PvAddress address = PvAddress.getValue("TestDal:Counter");
		final IPvAccess<String> pvAccess = _dalService.getPVAccess(address,
				Type.STRING, ListenerType.VALUE);

		final IPvListener<String> listener = mock(IPvListener.class);
		pvAccess.registerListener(listener);

		new AssertWithTimeout(2000) {

			@Override
			protected void performCheck() throws Exception {

				verify(listener).connectionChanged(pvAccess, true);

				ArgumentCaptor<String> argument = ArgumentCaptor
						.forClass(String.class);
				verify(listener, atLeast(8)).valueChanged(eq(pvAccess),
						argument.capture());

				List<String> receivedValues = argument.getAllValues();
				assertTrue(receivedValues.contains("0.000000"));
				assertTrue(receivedValues.contains("1.000000"));
				assertTrue(receivedValues.contains("2.000000"));
				assertTrue(receivedValues.contains("3.000000"));
				assertTrue(receivedValues.contains("4.000000"));
				assertTrue(receivedValues.contains("5.000000"));
				assertTrue(receivedValues.contains("6.000000"));
				assertTrue(receivedValues.contains("7.000000"));
				assertTrue(receivedValues.contains("8.000000"));
			}
		};

		Characteristics characteristics = pvAccess
				.getLastKnownCharacteristics();

		// Graph_Min is not set (because Type is String)
		assertNull(characteristics.get(Characteristic.GRAPH_MIN));

		assertTrue(pvAccess.isConnected());

		stopSoftIoc();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMonitorWithReconnect() throws Exception {

		startUpSoftIoc();

		PvAddress address = PvAddress.getValue("TestDal:Counter");
		final IPvAccess<Long> pvAccess = _dalService.getPVAccess(address,
				Type.LONG, ListenerType.VALUE);

		final IPvListener<Long> listener = mock(IPvListener.class);
		pvAccess.registerListener(listener);

		// expect connect
		verify(listener, timeout(1000)).connectionChanged(pvAccess, true);
		verify(listener, timeout(1000).atLeast(1)).valueChanged(eq(pvAccess),
				anyLong());
		assertTrue(pvAccess.isConnected());

		stopSoftIoc();

		// expect disconnect
		verify(listener, timeout(1000)).connectionChanged(pvAccess, false);
		assertFalse(pvAccess.isConnected());

		Mockito.reset(listener);

		startUpSoftIoc(); // reconnect

		// expect connect
		verify(listener, timeout(10000)).connectionChanged(pvAccess, true);
		verify(listener, timeout(10000).atLeast(1)).valueChanged(eq(pvAccess),
				anyLong());
		assertTrue(pvAccess.isConnected());

		// repeat check to ensure new (!) value changed events to occur
		Mockito.reset(listener);
		verify(listener, timeout(10000).atLeast(1)).valueChanged(eq(pvAccess),
				anyLong());

		stopSoftIoc();
	}

	@Test
	@Ignore
	public void testSecondMonitor() throws Exception {
		fail("Test second monitor after connect");
	}

	@Test
	@Ignore
	public void testDisposing() throws Exception {
		fail("Test second monitor after connect");
	}

	private static void startUpSoftIoc() throws Exception {

		LOGGER.info("Soft IOC - Starting ...");

		File file = new File(Dal2WithEpicsTest.class.getClassLoader()
				.getResource("db/EpicsTest.db").toURI());
		final ISoftIocConfigurator cfg = new JUnitSoftIocConfigurator()
				.with(file);

		_softIoc = new SoftIoc(cfg);
		_softIoc.start();

		while (!_softIoc.isStartUpDone()) {
			// wait IOC startup finished
		}

		Thread.sleep(400);

		LOGGER.info("Soft IOC - Running");
	}

	private static void stopSoftIoc() throws Exception {

		LOGGER.info("Soft IOC - Stopping ...");

		if (_softIoc != null) {
			_softIoc.stop();
		}

		Thread.sleep(1000); // wait until soft ioc comes down

		LOGGER.info("Soft IOC - Stopped");
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
