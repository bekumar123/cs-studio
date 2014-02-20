package org.csstudio.dal2.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.configuration.DefaultConfiguration;
import gov.aps.jca.event.LatestMonitorOnlyQueuedEventDispatcher;
import gov.aps.jca.event.QueuedEventDispatcher;

import org.csstudio.dal2.epics.mapping.SimpleTypeMapping;
import org.csstudio.dal2.epics.service.EpicsPvAccessFactory;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.servicelocator.ServiceLocator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.cosylab.epics.caj.CAJContext;

public class DalEpicsActivator implements BundleActivator {
    
    private static BundleContext context;
	private static Context _jcaContext;
    
    static BundleContext getContext() {
        return context;
    }
    
    public static Context getJcaContext() {
		return _jcaContext;
	}
    
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        DalEpicsActivator.context = bundleContext;
        
        _jcaContext = newJcaContext();
//        EpicsPvAccessFactory pvAccessFactory = new EpicsPvAccessFactory(_jcaContext, typeMapping);
//		ServiceLocator.registerService(ICsPvAccessFactory.class, pvAccessFactory);
    }
    
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        // TODO dal2 tear down channel access here
        
        DalEpicsActivator.context = null;
    }
    
    private Context newJcaContext() throws CAException {
        setSystemProperties();
        setupLibs();
        
		final DefaultConfiguration edconf = new DefaultConfiguration("event_dispatcher");

		edconf.setAttribute("class", QueuedEventDispatcher.class.getName());
		//		edconf.setAttribute("class", LatestMonitorOnlyQueuedEventDispatcher.class.getName());

		final DefaultConfiguration config = new DefaultConfiguration("EPICSPlugConfig");
	    config.setAttribute("class", JCALibrary.CHANNEL_ACCESS_JAVA);
	    //	    config.setAttribute("class", JCALibrary.JNI_THREAD_SAFE);
		config.addChild(edconf);

        JCALibrary jca = JCALibrary.getInstance();

        final CAJContext context= (CAJContext)jca.createContext(config);

		// force explicit initialization
		context.initialize();

        return context;
    }
    
    private static void setSystemProperties() {
//        System.setProperty("dal.plugs", "EPICS");
//        System.setProperty("dal.plugs.default", "EPICS");
//        System.setProperty("dal.propertyfactory.EPICS",
//                           "org.csstudio.dal.epics.PropertyFactoryImpl");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "epicscpci03 mksherazk");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "131.169.115.234 131.169.115.236");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "NO");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout", "30.0");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period", "15.0");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port", "5065");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port", "6011");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", "5064");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", "6010");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", "16384");
//        
//		System.setProperty(QueuedEventDispatcher.class.getName() + ".queue_limit", "1000000");
//		System.setProperty(QueuedEventDispatcher.class.getName() + ".channel_queue_limit", "10000");
//		System.setProperty(LatestMonitorOnlyQueuedEventDispatcher.class.getName() + ".queue_limit", "1000000");
//		System.setProperty(LatestMonitorOnlyQueuedEventDispatcher.class.getName() + ".channel_queue_limit", "6000");

    }
    
    private void setupLibs() {
        // path to jca.dll is found using java.library.path
        // System.setProperty("java.library.path", "libs/win32/x86"); // ahem, no, I put jca.dll in the root of the project.
        
        // path to Com.dll and ca.dll is hardcoded to windows
        System.setProperty("gov.aps.jca.jni.epics.win32-x86.library.path", "libs/win32/x86");
    }
    

    
}
