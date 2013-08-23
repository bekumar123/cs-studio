package org.csstudio.dal2.acceptance;

import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.dal2.service.impl.DalService;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

public class Scenario_DAL2JMS {

	public static void main(String[] args) throws Exception {

		int numberOfPVs = 5000;

		Context jcaContext = null;
		try {
			jcaContext = JCALibrary.getInstance().createContext(
					JCALibrary.CHANNEL_ACCESS_JAVA);
			DalService dalService = new DalService(
					EpicsServiceTestUtil.createEpicsPvAccessFactory(jcaContext));

			for (int i = 0; i < numberOfPVs; i++) {

				PvAddress address = PvAddress.getValue("Test:Ramp_calc_" + i);
				IPvAccess<Long> pvAccess = dalService.getPVAccess(address,
						Type.LONG, ListenerType.VALUE);
				pvAccess.registerListener(new IPvListener<Long>() {

					boolean inAlarm = false;

					@Override
					public void valueChanged(IPvAccess<Long> source, Long value) {

						Characteristics characteristics = source
								.getLastKnownCharacteristics();
						EpicsAlarmStatus status = characteristics
								.get(Characteristic.STATUS);
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

		} finally {
			if (jcaContext != null) {
				jcaContext.dispose();
			}
		}

	}

}
