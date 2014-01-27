package org.csstudio.nams.common.material;

import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class Regelwerkskennung_Test extends
		AbstractTestValue<Regelwerkskennung> {
	@Test
	public void testErzeugen() {
		Regelwerkskennung.valueOf(10);
	}

	@Override
	protected Regelwerkskennung doGetAValueOfTypeUnderTest() {
		return Regelwerkskennung.valueOf(1);
	}

	@Override
	protected Regelwerkskennung[] doGetDifferentInstancesOfTypeUnderTest() {
		return new Regelwerkskennung[] { Regelwerkskennung.valueOf(1),
				Regelwerkskennung.valueOf(2), Regelwerkskennung.valueOf(3) };
	}
}
