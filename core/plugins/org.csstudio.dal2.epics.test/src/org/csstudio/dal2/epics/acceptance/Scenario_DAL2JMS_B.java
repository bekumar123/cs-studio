package org.csstudio.dal2.epics.acceptance;

import gov.aps.jca.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.service.test.EpicsServiceTestUtil;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.dal2.service.test.DalServiceTestUtil;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

public class Scenario_DAL2JMS_B {

	public static void main(String[] args) throws Exception {

		int numberOfPVs = 20000;

		Context jcaContext = null;
		
		try {
			jcaContext = EpicsServiceTestUtil.createJCAContext();

			ICsPvAccessFactory epicsPvAccessFactory = EpicsServiceTestUtil.createEpicsPvAccessFactory(jcaContext);
			IDalService dalService = DalServiceTestUtil.createService(epicsPvAccessFactory);

			List<IPvAccess<Integer>> accessList = new ArrayList<IPvAccess<Integer>>(
					numberOfPVs);

			for (int i = 0; i < numberOfPVs; i++) {

				PvAddress address = PvAddress.getValue("Test:Ramp_calc_" + i);
				IPvAccess<Integer> pvAccess = dalService.getPVAccess(address,
						Type.LONG, ListenerType.VALUE);
				accessList.add(pvAccess);

				pvAccess.registerListener(new IPvListener<Integer>() {

					boolean inAlarm = false;

					@Override
					public void valueChanged(IPvAccess<Integer> source, Integer value) {

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
					public void connectionChanged(IPvAccess<Integer> source,
							boolean isConnected) {
					}
				});
			}

			new BufferedReader(new InputStreamReader(System.in)).readLine();

			for (IPvAccess<Integer> access : accessList) {
				access.deregisterAllListener();
			}

		} finally {
			if (jcaContext != null) {
				jcaContext.dispose();
			}
		}

	}

}
