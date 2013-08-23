// CHECKSTYLE:OFF
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.alarm.service.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.epics.service.test.TestSoftIOC;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.dal2.service.test.DalServiceTestUtil;
import org.csstudio.remote.jms.command.IRemoteCommandService;
import org.csstudio.servicelocator.ServiceLocator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for retrieval of the initial alarm state of not existing pvs.
 */
public class AlarmServiceJMSImplUnitTest {

	private static final String LOCALHOST = "127.0.0.1";
	private static final String EPICS_CHANNEL_ADDRESS = LOCALHOST;
	private TestSoftIOC _softIOC;

	@Before
	public void setupEnvironment() throws Exception {
		System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list",
				EPICS_CHANNEL_ADDRESS);

		// do not multicast
		System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
				"NO");

		// use common executor
		System.setProperty("EPICSPlug.property.use_common_executor", "TRUE");

		// Channel access answers after 100msec, so we will only wait twice as
		// long
		// System.setProperty(Plugs.INITIAL_CONNECTION_TIMEOUT, "200"); // This
		// is default

		// Service locator
		ServiceLocator.registerService(IRemoteCommandService.class,
				mock(IRemoteCommandService.class));
	}

	@Before
	public void setup() throws Exception {
		File file = new File(AlarmConnectionDALImplUnitTest.class
				.getClassLoader().getResource("EpicsTest.db").toURI());

		_softIOC = new TestSoftIOC(file);
		_softIOC.startJunit();
	}

	@After
	public void after() throws Exception {
		_softIOC.stop();
	}

	/**
	 * This test retrieves a lot of not-existing pvs and checks that they all
	 * have been reported as such.
	 */
	@SuppressWarnings("synthetic-access")
	@Test
	public void testRetrieveInitialState() throws Exception {

		boolean failed = false;

		Context jcaContext = JCALibrary.getInstance().createContext(
				JCALibrary.CHANNEL_ACCESS_JAVA);
		ICsPvAccessFactory epicsPvAccessFactory = EpicsServiceTestUtil
				.createEpicsPvAccessFactory(jcaContext);
		IDalService dalService = DalServiceTestUtil
				.createService(epicsPvAccessFactory);

		IAlarmService service = new AlarmServiceJmsImpl(dalService);
		List<IAlarmInitItem> initItems = new ArrayList<IAlarmInitItem>();
		initItems.add(new TestInitItem("TestDal:ConstantPV1"));
		initItems.add(new TestInitItem("TestDal:ConstantPV2"));
		initItems.add(new TestInitItem("TestDal:ConstantPV3"));
		initItems.add(new TestInitItem("TestDal:ConstantPVYYY")); // n.a.
		initItems.add(new TestInitItem("TestDal:ConstantPV4"));
		initItems.add(new TestInitItem("TestDal:ConstantPV5"));
		initItems.add(new TestInitItem("TestDal:ConstantPV6"));
		initItems.add(new TestInitItem("TestDal:ConstantPVXXX")); // n.a.
		initItems.add(new TestInitItem("TestDal:ConstantPV7"));
		initItems.add(new TestInitItem("TestDal:ConstantPV8"));
		initItems.add(new TestInitItem("TestDal:ConstantPV9"));
		initItems.add(new TestInitItem("TestDal:ConstantPVZZZ")); // n.a.
		initItems.add(new TestInitItem("TestDal:ConstantPV10"));
		initItems.add(new TestInitItem("TestDal:ConstantPV5")); // duplicate

		service.retrieveInitialState(initItems);

		Thread.sleep(3000);

		assertEquals(true, ((TestInitItem) initItems.get(0))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(1))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(2))._wasInitialized);
		assertEquals(false, ((TestInitItem) initItems.get(3))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(4))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(5))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(6))._wasInitialized);
		assertEquals(false, ((TestInitItem) initItems.get(7))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(8))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(9))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(10))._wasInitialized);
		assertEquals(false, ((TestInitItem) initItems.get(11))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(12))._wasInitialized);
		assertEquals(true, ((TestInitItem) initItems.get(13))._wasInitialized);

		assertEquals(false, ((TestInitItem) initItems.get(0))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(1))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(2))._wasNotFound);
		assertEquals(true, ((TestInitItem) initItems.get(3))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(4))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(5))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(6))._wasNotFound);
		assertEquals(true, ((TestInitItem) initItems.get(7))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(8))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(9))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(10))._wasNotFound);
		assertEquals(true, ((TestInitItem) initItems.get(11))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(12))._wasNotFound);
		assertEquals(false, ((TestInitItem) initItems.get(13))._wasNotFound);

		
		for (IAlarmInitItem item : initItems) {
			boolean wasInitialized = ((TestInitItem) item)._wasInitialized;
			boolean wasNotFound = ((TestInitItem) item)._wasNotFound;
			if (!wasInitialized && !wasNotFound) {
				System.err.println(item.getPVName() + " was not processed");
				failed = true;
			}
		}
		Assert.assertFalse(failed);

	}

	/**
	 * init item representing a real pv
	 */
	private static class TestInitItem implements IAlarmInitItem {

		private final String _pvName;
		public boolean _wasInitialized = false;
		public boolean _wasNotFound = false;

		public TestInitItem(@Nonnull final String pvName) {
			_pvName = pvName;
		}

		@Override
		public String getPVName() {
			return _pvName;
		}

		@Override
		public void init(IAlarmMessage alarmMessage) {
			// System.err.println("pv " + _pvName + ": init");
			_wasInitialized = true;
		}

		@Override
		public void acknowledge() {
			// TODO Auto-generated method stub
		}

		@Override
		public void notFound(String pvName) {
			// System.err.println("pv " + _pvName + ": notFound");
			_wasNotFound = true;
		}

	}

}
