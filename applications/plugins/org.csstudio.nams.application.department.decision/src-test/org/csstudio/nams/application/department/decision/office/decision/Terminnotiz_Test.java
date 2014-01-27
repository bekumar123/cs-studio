package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import junit.framework.Assert;

import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class Terminnotiz_Test extends AbstractTestValue<Terminnotiz> {

	@Test
	public void testCheckContract() throws Throwable {
		try {
			Terminnotiz.valueOf(null, Milliseconds.valueOf(100), 1);
			Assert.fail();
		} catch (final AssertionError ae) {
		}
		try {
			Terminnotiz.valueOf(CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42)),
					null, 1);
			Assert.fail();
		} catch (final AssertionError ae) {
		}
	}

	@Test
	public void testEqualsJetztAberRichtig() {
		CasefileId vorgangsmappenkennung1 = null;
		CasefileId vorgangsmappenkennung2 = null;
		try {
			vorgangsmappenkennung1 = CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42));
			vorgangsmappenkennung2 = CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 2 }), new Date(23));
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}

		final Terminnotiz vergleichsTerminnotiz = Terminnotiz.valueOf(
				vorgangsmappenkennung1, Milliseconds.valueOf(5),
				2);
		Terminnotiz terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung1,
				Milliseconds.valueOf(5), 2);

		Assert.assertEquals(vergleichsTerminnotiz, terminnotiz1);

		terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung1,
				Milliseconds.valueOf(5), 3);
		Assert.assertFalse(vergleichsTerminnotiz.equals(terminnotiz1));

		terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung1,
				Milliseconds.valueOf(10), 2);
		Assert.assertFalse(vergleichsTerminnotiz.equals(terminnotiz1));

		terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung2,
				Milliseconds.valueOf(5), 2);
		Assert.assertFalse(vergleichsTerminnotiz.equals(terminnotiz1));
	}

	@Override
	protected Terminnotiz doGetAValueOfTypeUnderTest() throws Throwable {
		CasefileId vorgangsmappenkennung = null;
		vorgangsmappenkennung = CasefileId.valueOf(InetAddress
				.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42));
		final Milliseconds millisekunden = Milliseconds.valueOf(42);
		return Terminnotiz.valueOf(vorgangsmappenkennung, millisekunden,
				4);
	}

	@Override
	protected Terminnotiz[] doGetDifferentInstancesOfTypeUnderTest()
			throws Throwable {
		CasefileId vorgangsmappenkennung1 = null;
		CasefileId vorgangsmappenkennung2 = null;
		vorgangsmappenkennung1 = CasefileId.valueOf(InetAddress
				.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42));
		vorgangsmappenkennung2 = CasefileId.valueOf(InetAddress
				.getByAddress(new byte[] { 127, 0, 0, 2 }), new Date(23));
		Milliseconds millisekunden = Milliseconds.valueOf(42);
		final Terminnotiz terminnotiz1 = Terminnotiz.valueOf(
				vorgangsmappenkennung1, millisekunden, 5);
		millisekunden = Milliseconds.valueOf(23);
		final Terminnotiz terminnotiz2 = Terminnotiz.valueOf(
				vorgangsmappenkennung2, millisekunden, 6);
		millisekunden = Milliseconds.valueOf(666);
		final Terminnotiz terminnotiz3 = Terminnotiz.valueOf(
				vorgangsmappenkennung2, millisekunden, 7);

		return new Terminnotiz[] { terminnotiz1, terminnotiz2, terminnotiz3 };
	}
}
