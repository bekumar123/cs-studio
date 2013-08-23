package org.csstudio.dal2.acceptance;

import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.dal2.service.impl.DalService;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

public class Scenario_DAL2JMS_B {

	public static void main(String[] args) throws Exception {

		int numberOfPVs = 20000;

		Context jcaContext = null;
		try {
			setupLibs();

			JCALibrary jca = JCALibrary.getInstance();
//			jcaContext = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
			jcaContext = jca.createContext(JCALibrary.JNI_THREAD_SAFE);

			DalService dalService = new DalService(
					EpicsServiceTestUtil.createEpicsPvAccessFactory(jcaContext));

			List<IPvAccess<Long>> accessList = new ArrayList<IPvAccess<Long>>(
					numberOfPVs);

			for (int i = 0; i < numberOfPVs; i++) {

				PvAddress address = PvAddress.getValue("Test:Ramp_calc_" + i);
				IPvAccess<Long> pvAccess = dalService.getPVAccess(address,
						Type.LONG, ListenerType.VALUE);
				accessList.add(pvAccess);

				pvAccess.registerListener(new IPvListener<Long>() {

					boolean inAlarm = false;

					@Override
					public void valueChanged(IPvAccess<Long> source, Long value) {

						Characteristics characteristics = source
								.getLastKnownCharacteristics();
						EpicsAlarmStatus status = characteristics.getStatus();
						if (status.isAlarm() && !inAlarm) {
							inAlarm = true;
							System.out.println(source.getPVAddress()
									.getAddress() + " -> Alarm");
						} else if (!status.isAlarm() && inAlarm) {
							inAlarm = false;
							System.out.println(source.getPVAddress()
									.getAddress() + " -> No Alarm");
						} else {
							// System.out.print(".");
						}

					}

					@Override
					public void connectionChanged(IPvAccess<Long> source,
							boolean isConnected) {
					}
				});
			}

			new BufferedReader(new InputStreamReader(System.in)).readLine();

			for (IPvAccess<Long> access : accessList) {
				access.deregisterAllListener();
			}

		} finally {
			if (jcaContext != null) {
				jcaContext.dispose();
			}
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

}
