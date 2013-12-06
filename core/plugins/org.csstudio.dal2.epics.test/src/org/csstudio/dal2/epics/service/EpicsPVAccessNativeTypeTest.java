package org.csstudio.dal2.epics.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.csstudio.dal2.dv.EnumType;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.csstudio.domain.desy.softioc.AbstractSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.ISoftIocConfigurator;
import org.csstudio.domain.desy.softioc.SoftIoc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

public class EpicsPVAccessNativeTypeTest {

	private static SoftIoc _softIoc;
	private Context _jcaContext;
	private File _file1;
	private File _file2;

	@Before
	public void before() throws CAException, URISyntaxException {
		_jcaContext = EpicsServiceTestUtil.createJCAContext();

		_file1 = new File(EpicsPvAccessFactoryTest.class.getClassLoader()
				.getResource("db/EpicsTest.db").toURI());
		_file2 = new File(EpicsPvAccessFactoryTest.class.getClassLoader()
				.getResource("db/EpicsTest_withChangedType.db").toURI());

	}

	@After
	public void tearDown() {
		if (_jcaContext != null) {
			_jcaContext.dispose();
		}
	}

	private static void startUpSoftIoc(File file) throws Exception {

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(timeout = 7000)
	public void testMonitor() throws Exception {

		EpicsPvAccess<Object> pva = new EpicsPvAccess<Object>(_jcaContext,
				PvAddress.getValue("TestDal:Counter"), Type.NATIVE);
		ICsPvListener<Object> listener = mock(ICsPvListener.class);
		when(listener.getType()).thenReturn(ListenerType.VALUE);
		pva.initMonitor(listener);

		startUpSoftIoc(_file1);
		{
			verify(listener, timeout(4000)).connected("TestDal:Counter",
					Type.DOUBLE);

			ArgumentCaptor<CsPvData> captor = ArgumentCaptor
					.forClass(CsPvData.class);
			verify(listener, timeout(1000).atLeast(5)).valueChanged(captor.capture());
			CsPvData data = captor.getValue();
			assertEquals(Type.DOUBLE, data.getNativeType());
			assertThat(data.getValue(), new ArgumentMatcher<Object>() {
				@Override
				public boolean matches(Object argument) {
					return Arrays.asList(0.0, 1.0,
							2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0).contains(argument);
				}
			});

			stopSoftIoc();

			verify(listener, timeout(4000)).disconnected("TestDal:Counter");
		}

		Mockito.reset(listener);
		when(listener.getType()).thenReturn(ListenerType.VALUE);
		
		// in file2 the type of TestDal:Counter has changed to mbbi
		startUpSoftIoc(_file2);

		{
			verify(listener, timeout(4000)).connected("TestDal:Counter",
					Type.ENUM);

			ArgumentCaptor<CsPvData> captor = ArgumentCaptor
					.forClass(CsPvData.class);
			verify(listener, timeout(1000)).valueChanged(captor.capture());
			CsPvData data = captor.getValue();
			assertEquals(Type.ENUM, data.getNativeType());
			
			stopSoftIoc();

			verify(listener, timeout(4000)).disconnected("TestDal:Counter");
		}
		
		pva.stopMonitor();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(timeout = 7000)
	public void testAsyncGetValue() throws Exception {

		EpicsPvAccess<Object> pva = new EpicsPvAccess<Object>(_jcaContext,
				PvAddress.getValue("TestDal:Counter"), Type.NATIVE);

		startUpSoftIoc(_file1);
		{
			ICsResponseListener<CsPvData<Object>> callback = mock(ICsResponseListener.class);
			pva.getValue(callback);
			
			ArgumentCaptor<CsPvData> captor = ArgumentCaptor.forClass(CsPvData.class);
			verify(callback, timeout(5000)).onSuccess(captor.capture());
			CsPvData data = captor.getValue();
			assertThat(data.getValue(), new ArgumentMatcher<Object>() {
				@Override
				public boolean matches(Object argument) {
					return Arrays.asList(0.0, 1.0,
							2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0).contains(argument);
				}
			});
			assertEquals(Type.DOUBLE, data.getNativeType());
			
			stopSoftIoc();
		}

		// in file2 the type of TestDal:Counter has changed to mbbi
		startUpSoftIoc(_file2);

		{
			ICsResponseListener<CsPvData<Object>> callback = mock(ICsResponseListener.class);
			pva.getValue(callback);
			
			ArgumentCaptor<CsPvData> captor = ArgumentCaptor.forClass(CsPvData.class);
			verify(callback, timeout(5000)).onSuccess(captor.capture());
			CsPvData data = captor.getValue();
			assertEquals(4, ((EnumType)data.getValue()).getValue());
			assertEquals(Type.ENUM, data.getNativeType());
			
			stopSoftIoc();
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
