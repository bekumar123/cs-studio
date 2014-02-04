package org.csstudio.nams.common.material;

import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class Regelwerkskennung_Test extends
		AbstractTestValue<FilterId> {
	@Test
	public void testErzeugen() {
		FilterId.valueOf(10);
	}

	@Override
	protected FilterId doGetAValueOfTypeUnderTest() {
		return FilterId.valueOf(1);
	}

	@Override
	protected FilterId[] doGetDifferentInstancesOfTypeUnderTest() {
		return new FilterId[] { FilterId.valueOf(1),
				FilterId.valueOf(2), FilterId.valueOf(3) };
	}
}
