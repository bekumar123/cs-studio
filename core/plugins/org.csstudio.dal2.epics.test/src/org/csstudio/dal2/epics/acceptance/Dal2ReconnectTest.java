package org.csstudio.dal2.epics.acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
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

public class Dal2ReconnectTest {

	private static SoftIoc _softIoc;
	private Context _jcaContext;
	private IDalService _dalService;
	private ICsPvAccessFactory _csPvAccessFactory;

	@Before
	public void before() throws CAException {
		_jcaContext = EpicsServiceTestUtil.createJCAContext();
		_csPvAccessFactory = EpicsServiceTestUtil.createEpicsPvAccessFactory(_jcaContext);
		_dalService = DalServiceTestUtil.createService(_csPvAccessFactory);
	}

	@After
	public void tearDown() throws Exception {
		if (_jcaContext != null) {
			_jcaContext.dispose();
		}

		_softIoc.stop();
	}

	@SuppressWarnings("unchecked")
	@Test @Ignore
	public void testAlarmMonitorWithReconnect() throws Exception {

		startPhase("Starting IOC", 1);
		
		startUpSoftIoc();

		startPhase("Create Listener", 2);
		
		final AlarmListener[] listener = new AlarmListener[5000];
		// create pv access
		final IPvAccess<Long>[] access = new IPvAccess[5000];
		for (int i = 0; i < 5000; i++) {
			PvAddress address = PvAddress.getValue("Test:Ramp_calc_" + i);
			access[i] = _dalService.getPVAccess(address, Type.LONG,
					ListenerType.ALARM);
		}

		// create listener
		for (int i = 0; i < 5000; i++) {
			listener[i] = new AlarmListener();
			access[i].registerListener(listener[i]);
		}
		
		for (int round = 0; round < 3; round++) {

			startPhase("Round " + round + ": Expect Alarm on all PVs", 3);
			
			// expect alarm on all pvs within about 5 seconds
			new AssertWithTimeout(5000 + 5000* 1000) {
				@Override
				protected void performCheck() throws Exception {

					int counter = 0;
					for (int i = 0; i < 5000; i++) {
						if (listener[i].receivedAlarm()) {
							counter++;
						}
					}

					System.out.println(counter + "/" + 5000);
					Thread.sleep(500);

					assertEquals(5000, counter);
				}
			};
			
			startPhase("Round " + round + ": Stop IOC", 4);

			stopSoftIoc();

			// wait until no further events arrive
			new AssertWithTimeout(2000) {
				@Override
				protected void performCheck() throws Exception {

					for (int i = 0; i < 5000; i++) {
						listener[i].resetFlag();
					}

					Thread.sleep(500);

					for (int i = 0; i < 5000; i++) {
						assertFalse(listener[i].receivedAlarm());
					}
				}
			};
			
			startPhase("Round " + round + ": Restart IOC", 5);

			// reconnect
			startUpSoftIoc();

			startPhase("Round " + round + ": Expect Alarm on all PVs", 6);
			
			// expect alarm on all pvs within about 5 seconds
			new AssertWithTimeout(5000 + 5000* 1000) {
				@Override
				protected void performCheck() throws Exception {
					int counter = 0;
					for (int i = 0; i < 5000; i++) {
						if (listener[i].receivedAlarm()) {
							counter++;
						}
					}

					System.out.println(counter + "/" + 5000);
					Thread.sleep(500);

					assertEquals(5000, counter);
				}
			};
			
			startPhase("Round " + round + ": Stop IOC", 7);

			stopSoftIoc();

			// wait until no further events arrive
			new AssertWithTimeout(2000) {
				@Override
				protected void performCheck() throws Exception {

					for (int i = 0; i < 5000; i++) {
						listener[i].resetFlag();
					}

					Thread.sleep(500);

					for (int i = 0; i < 5000; i++) {
						assertFalse(listener[i].receivedAlarm());
					}
				}
			};

			int pause = (int) (Math.random() * 5000.0);
			
			System.out.print("Waiting " + pause / 1000.0 + " seconds ... ");
			Thread.sleep(pause);
			System.out.println("done.");

			// reconnect
			startPhase("Round " + round + ": Start IOC", 8);
			startUpSoftIoc();

		}

		// expect alarm on all pvs within about 10 seconds
		new AssertWithTimeout(5000 + 5000 * 1000) {
			@Override
			protected void performCheck() throws Exception {
				int counter = 0;
				for (int i = 0; i < 5000; i++) {
					if (listener[i].receivedAlarm()) {
						counter++;
					}
				}

				System.out.println(counter + "/" + 5000);
				Thread.sleep(500);

				assertEquals(5000, counter);
			}
		};

		stopSoftIoc();
	}

	
	private void startPhase(String string, int phase) {
		System.out.flush();
		System.err.println("Starting Phase: " + string);
		System.err.flush();
	}

	private static void startUpSoftIoc() throws Exception {

		System.out.print("Starting Soft Ioc .");

		File file = new File(Dal2ReconnectTest.class.getClassLoader()
				.getResource("db/EpicsReconnectTest.db").toURI());
		final ISoftIocConfigurator cfg = new JUnitSoftIocConfigurator()
				.with(file);

		System.out.print(".");

		_softIoc = new SoftIoc(cfg);
		_softIoc.start();

		System.out.print(".");

		while (!_softIoc.isStartUpDone()) {
			System.out.print(".");
			// wait IOC startup finished
		}

		System.out.print(".");
		Thread.sleep(400);
		System.out.println(" done");
	}

	private static void stopSoftIoc() throws Exception {
		if (_softIoc != null) {
			_softIoc.stop();
		}
		Thread.sleep(1000); // wait until soft ioc comes down
	}

	private static class AlarmListener implements IPvListener<Long> {

		private boolean receivedAlarm;

		@Override
		public void connectionChanged(IPvAccess<Long> source,
				boolean isConnected) {
			// nothing to do
		}

		@Override
		public synchronized void valueChanged(IPvAccess<Long> source, Long value) {
			if (source.getLastKnownCharacteristics().getStatus().isAlarm()) {
				receivedAlarm = true;
			}
		}

		public synchronized boolean receivedAlarm() {
			return receivedAlarm;
		}

		public synchronized void resetFlag() {
			receivedAlarm = false;
		}
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
