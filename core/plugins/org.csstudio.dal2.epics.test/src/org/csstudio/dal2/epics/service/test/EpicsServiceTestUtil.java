package org.csstudio.dal2.epics.service.test;

import gov.aps.jca.Context;

import org.csstudio.dal2.epics.service.EpicsPvAccessFactory;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;

public class EpicsServiceTestUtil {

	public static ICsPvAccessFactory createEpicsPvAccessFactory(
			Context jcaContext) {
		return new EpicsPvAccessFactory(jcaContext);
	}
}
