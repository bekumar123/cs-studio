package org.csstudio.alarm.service.internal;


import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmResource;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Test for the DAL implementation of the alarm connection
 * 
 * @author jpenning
 * @since 08.11.2010
 */
public class AlarmConnectionDALImplUnitTest {
    
	private IDalService _dalServiceMock;

	@Before
	public void setUp() {
		_dalServiceMock = mock(IDalService.class);
	}
	
    @Test
    public void testCreate() throws Exception {
        AlarmConnectionDAL2Impl connectionUnderTest = new AlarmConnectionDAL2Impl(_dalServiceMock);
        assertFalse(connectionUnderTest.canHandleTopics());
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
    public void testConnectDisconnectOne() throws Exception {
        
    	PvAddress pv1 = PvAddress.getValue("mypv");
    	
    	IPvAccess<String> pvAccess = mock(IPvAccess.class);
		when(_dalServiceMock.getPVAccess(eq(pv1), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pvAccess);
    	
    	AlarmConnectionDAL2Impl connectionUnderTest = new TestAlarmConnectionDALImpl(_dalServiceMock);

    	
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);
        verify(connectionMonitor, times(1)).onConnect();
        
        verify(_dalServiceMock).getPVAccess(PvAddress.getValue("mypv"), Type.STRING, ListenerType.ALARM);
        
        ArgumentCaptor<IPvListener> listenerCaptor = ArgumentCaptor.forClass(IPvListener.class);
        verify(pvAccess).registerListener(listenerCaptor.capture());
        
        connectionUnderTest.disconnect();
        
        verify(pvAccess, times(1)).deregisterListener(listenerCaptor.getValue());
        
        // disconnect is robust
        connectionUnderTest.disconnect();
        
        verify(pvAccess, times(1)).deregisterListener(listenerCaptor.getValue());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testConnectAndRegister() throws Exception {
        
    	PvAddress pv0 = PvAddress.getValue("mypv");
    	PvAddress pv1 = PvAddress.getValue("pv1");
    	PvAddress pv2 = PvAddress.getValue("pv2");
    	
    	IPvAccess<String> pv0Access = mock(IPvAccess.class);
    	IPvAccess<String> pv1Access = mock(IPvAccess.class);
    	IPvAccess<String> pv2Access = mock(IPvAccess.class);
		
    	when(_dalServiceMock.getPVAccess(eq(pv0), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv0Access);
    	when(_dalServiceMock.getPVAccess(eq(pv1), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv1Access);
    	when(_dalServiceMock.getPVAccess(eq(pv2), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv2Access);
    	
    	AlarmConnectionDAL2Impl connectionUnderTest = new TestAlarmConnectionDALImpl(_dalServiceMock);
        
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);
        verify(connectionMonitor, times(1)).onConnect();
        
        verify(_dalServiceMock, times(1)).getPVAccess(pv0, Type.STRING, ListenerType.ALARM);
        verify(pv0Access, times(1)).registerListener(any(IPvListener.class));
        
        connectionUnderTest.registerPV("pv1");

        verify(_dalServiceMock, times(1)).getPVAccess(pv1, Type.STRING, ListenerType.ALARM);
        verify(pv1Access, times(1)).registerListener(any(IPvListener.class));

        // Registering the 2nd time is ignored
        connectionUnderTest.registerPV("pv1");

        verify(_dalServiceMock, times(1)).getPVAccess(pv1, Type.STRING, ListenerType.ALARM);
        verify(pv1Access, times(1)).registerListener(any(IPvListener.class));
        
        connectionUnderTest.registerPV("pv2");

        verify(_dalServiceMock, times(1)).getPVAccess(pv0, Type.STRING, ListenerType.ALARM);
        verify(_dalServiceMock, times(1)).getPVAccess(pv1, Type.STRING, ListenerType.ALARM);
        verify(_dalServiceMock, times(1)).getPVAccess(pv2, Type.STRING, ListenerType.ALARM);
        verify(pv0Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv1Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv2Access, times(1)).registerListener(any(IPvListener.class));
        
        connectionUnderTest.deregisterPV("pv1");
        
        verify(pv1Access, times(1)).deregisterListener(any(IPvListener.class));
        
        connectionUnderTest.disconnect();
        
        verify(pv0Access, times(1)).deregisterListener(any(IPvListener.class));
        verify(pv1Access, times(1)).deregisterListener(any(IPvListener.class));
        verify(pv2Access, times(1)).deregisterListener(any(IPvListener.class));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testReloadPVsFromResource() throws Exception {
    	
        Set<String> pvSetStart = new HashSet<String>(Arrays.asList("pv1", "pv2", "pv3"));
        
        PvAddress pv1 = PvAddress.getValue("pv1");
    	PvAddress pv2 = PvAddress.getValue("pv2");
    	PvAddress pv3 = PvAddress.getValue("pv3");
    	PvAddress pv4 = PvAddress.getValue("pv4");
    	PvAddress pv5 = PvAddress.getValue("pv5");
    	
    	IPvAccess<String> pv1Access = mock(IPvAccess.class);
    	IPvAccess<String> pv2Access = mock(IPvAccess.class);
    	IPvAccess<String> pv3Access = mock(IPvAccess.class);
    	IPvAccess<String> pv4Access = mock(IPvAccess.class);
    	IPvAccess<String> pv5Access = mock(IPvAccess.class);
		
    	when(_dalServiceMock.getPVAccess(eq(pv1), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv1Access);
    	when(_dalServiceMock.getPVAccess(eq(pv2), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv2Access);
    	when(_dalServiceMock.getPVAccess(eq(pv3), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv3Access);
    	when(_dalServiceMock.getPVAccess(eq(pv4), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv4Access);
    	when(_dalServiceMock.getPVAccess(eq(pv5), any(Type.class), eq(ListenerType.ALARM))).thenReturn(pv5Access);
        
        TestAlarmConnectionDALImpl connectionUnderTest = new TestAlarmConnectionDALImpl(_dalServiceMock,
                                                                                        pvSetStart);
        connect(connectionUnderTest);
        
        verify(_dalServiceMock, times(1)).getPVAccess(pv1, Type.STRING, ListenerType.ALARM);
        verify(_dalServiceMock, times(1)).getPVAccess(pv2, Type.STRING, ListenerType.ALARM);
        verify(_dalServiceMock, times(1)).getPVAccess(pv3, Type.STRING, ListenerType.ALARM);
        verify(pv1Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv2Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv3Access, times(1)).registerListener(any(IPvListener.class));
        
        Set<String> pvSetReload = new HashSet<String>(Arrays.asList("pv2", "pv3", "pv4", "pv5"));
        connectionUnderTest.setPvNames(pvSetReload);
        
        // the implementation takes care of the difference when de/registering
        connectionUnderTest.reloadPVsFromResource();
        
        verify(pv1Access, times(1)).deregisterListener(any(IPvListener.class));
        verify(pv2Access, times(0)).deregisterListener(any(IPvListener.class));
        verify(pv3Access, times(0)).deregisterListener(any(IPvListener.class));

        verify(pv1Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv2Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv3Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv4Access, times(1)).registerListener(any(IPvListener.class));
        verify(pv5Access, times(1)).registerListener(any(IPvListener.class));
    }
    
    private void connect(@Nonnull final AlarmConnectionDAL2Impl connectionUnderTest,
                         @Nonnull final IAlarmConnectionMonitor connectionMonitor) throws AlarmConnectionException {
        IAlarmListener listener = mock(IAlarmListener.class);
        AlarmResource resource = mock(AlarmResource.class);
        connectionUnderTest.connect(connectionMonitor, listener, resource);
    }
    
    private void connect(@Nonnull final AlarmConnectionDAL2Impl connectionUnderTest) throws AlarmConnectionException {
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);
    }
    
    /**
     * Used for testing only. This subclass overrides the method in the object-under-test, which uses the eclipse
     * framework for retrieval of preferences and access to ldap or file system. This way this test can be run
     * as a simple unit test.
     */
    private static class TestAlarmConnectionDALImpl extends AlarmConnectionDAL2Impl {
        
        private Set<String> _pvSet;
        
        public TestAlarmConnectionDALImpl(@Nonnull final IDalService dalService) {
            this(dalService, Collections.singleton("mypv"));
        }
        
        public TestAlarmConnectionDALImpl(@Nonnull final IDalService dalService,
                                          @Nonnull final Set<String> pvSet) {
            super(dalService);
            _pvSet = pvSet;
        }
        
        protected void setPvNames(@Nonnull final Set<String> pvSet) {
            _pvSet = pvSet;
        }
        
        @Override
        protected Set<String> getPVNamesFromResource() throws AlarmConnectionException {
            return _pvSet;
        }
        
    }
    
}
