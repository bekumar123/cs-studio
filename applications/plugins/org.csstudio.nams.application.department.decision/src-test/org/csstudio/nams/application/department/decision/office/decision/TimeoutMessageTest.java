package org.csstudio.nams.application.department.decision.office.decision;

import junit.framework.Assert;

import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class TimeoutMessageTest extends AbstractTestValue<TimeoutMessage> {

	@Test
	public void testCheckContract() throws Throwable {
		FilterId filterId = FilterId.valueOf(1);
		try {
			TimeoutMessage.valueOf(null, Milliseconds.valueOf(100), filterId);
			Assert.fail();
		} catch (final AssertionError ae) {
		}
		try {
			TimeoutMessage.valueOf(CasefileId.createNew(),
					null, filterId);
			Assert.fail();
		} catch (final AssertionError ae) {
		}
	}

	@Test
	public void testEqualsJetztAberRichtig() {
		CasefileId vorgangsmappenkennung1 = null;
		CasefileId vorgangsmappenkennung2 = null;
		vorgangsmappenkennung1 = CasefileId.createNew();
		vorgangsmappenkennung2 = CasefileId.createNew();
		FilterId filterId2 = FilterId.valueOf(2);
		final TimeoutMessage vergleichsTimerMessage = TimeoutMessage.valueOf(
				vorgangsmappenkennung1, Milliseconds.valueOf(5),
				filterId2);
		TimeoutMessage timerMessage1 = TimeoutMessage.valueOf(vorgangsmappenkennung1,
				Milliseconds.valueOf(5), filterId2);

		Assert.assertEquals(vergleichsTimerMessage, timerMessage1);

		timerMessage1 = TimeoutMessage.valueOf(vorgangsmappenkennung1,
				Milliseconds.valueOf(5), FilterId.valueOf(3));
		Assert.assertFalse(vergleichsTimerMessage.equals(timerMessage1));

		timerMessage1 = TimeoutMessage.valueOf(vorgangsmappenkennung1,
				Milliseconds.valueOf(10), filterId2);
		Assert.assertFalse(vergleichsTimerMessage.equals(timerMessage1));

		timerMessage1 = TimeoutMessage.valueOf(vorgangsmappenkennung2,
				Milliseconds.valueOf(5), filterId2);
		Assert.assertFalse(vergleichsTimerMessage.equals(timerMessage1));
	}

	@Override
	protected TimeoutMessage doGetAValueOfTypeUnderTest() throws Throwable {
		CasefileId vorgangsmappenkennung = null;
		vorgangsmappenkennung = CasefileId.createNew();
		final Milliseconds millisekunden = Milliseconds.valueOf(42);
		return TimeoutMessage.valueOf(vorgangsmappenkennung, millisekunden,
				FilterId.valueOf(4));
	}

	@Override
	protected TimeoutMessage[] doGetDifferentInstancesOfTypeUnderTest()
			throws Throwable {
		CasefileId vorgangsmappenkennung1 = null;
		CasefileId vorgangsmappenkennung2 = null;
		vorgangsmappenkennung1 = CasefileId.createNew();
		vorgangsmappenkennung2 = CasefileId.createNew();
		Milliseconds millisekunden = Milliseconds.valueOf(42);
		final TimeoutMessage timerMessage1 = TimeoutMessage.valueOf(
				vorgangsmappenkennung1, millisekunden, FilterId.valueOf(5));
		millisekunden = Milliseconds.valueOf(23);
		final TimeoutMessage timerMessage2 = TimeoutMessage.valueOf(
				vorgangsmappenkennung2, millisekunden, FilterId.valueOf(6));
		millisekunden = Milliseconds.valueOf(666);
		final TimeoutMessage timerMessage3 = TimeoutMessage.valueOf(
				vorgangsmappenkennung2, millisekunden, FilterId.valueOf(7));

		return new TimeoutMessage[] { timerMessage1, timerMessage2, timerMessage3 };
	}
}
