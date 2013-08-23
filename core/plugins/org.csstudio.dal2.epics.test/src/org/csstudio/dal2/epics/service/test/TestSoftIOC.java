package org.csstudio.dal2.epics.service.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.csstudio.domain.desy.softioc.AbstractSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.BasicSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.ISoftIocConfigurator;
import org.csstudio.domain.desy.softioc.SoftIoc;

public class TestSoftIOC {

	private SoftIoc _ioc;
	private File _file;

	public TestSoftIOC(File file) {

		_file = file;

		// path to jca.dll is found using java.library.path
		// System.setProperty("java.library.path", "libs/win32/x86"); // ahem,
		// no, I put jca.dll in the root of the project.

		// path to Com.dll and ca.dll is hardcoded to windows
		System.setProperty("gov.aps.jca.jni.epics.win32-x86.library.path",
				"libs/win32/x86");

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

	public void start() throws Exception {

		System.out.print("Starting Soft Ioc .");

		ISoftIocConfigurator config = new BasicSoftIocConfigurator()
				.with(_file);

		start(config);
	}

	public void startJunit() throws Exception {
		start(new JUnitSoftIocConfigurator().with(_file));
	}

	/**
	 * @param config
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void start(ISoftIocConfigurator config) throws IOException,
			InterruptedException {
		System.out.print(".");

		_ioc = new SoftIoc(config);
		_ioc.start();

		System.out.print(".");

		while (!_ioc.isStartUpDone()) {
			System.out.print(".");
		}

		System.out.print(".");
		Thread.sleep(400);
		System.out.println(" done");
	}

	public void stop() throws Exception {
		_ioc.stop();
		Thread.sleep(1000); // wait until soft ioc comes down
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
