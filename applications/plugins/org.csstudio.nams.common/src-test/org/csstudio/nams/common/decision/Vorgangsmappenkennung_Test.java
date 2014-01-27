package org.csstudio.nams.common.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class Vorgangsmappenkennung_Test extends
		AbstractTestValue<CasefileId> {

	@Test
	public void testContractValueOf() throws Throwable {
		final InetAddress hostAdress = InetAddress.getByAddress(new byte[] {
				127, 0, 0, 1 });
		final Date time1 = new Date(123456);

		try {
			CasefileId.valueOf(null, time1);
			Assert.fail();
		} catch (final AssertionError ae) {
			// Ok!
		}

		try {
			CasefileId.valueOf(hostAdress, null);
			Assert.fail();
		} catch (final AssertionError ae) {
			// Ok!
		}
	}

	@Test
	public void testEquals() throws UnknownHostException {
		final InetAddress hostAdress = InetAddress.getByAddress(new byte[] {
				127, 0, 0, 1 });
		final InetAddress hostAdress2 = InetAddress.getByAddress(new byte[] {
				127, 0, 0, 2 });
		final Date time1 = new Date(123456);
		final Date time2 = new Date(8975);
		Assert.assertNotNull(hostAdress);
		Assert.assertNotNull(hostAdress2);

		final CasefileId kennung1 = CasefileId.valueOf(
				hostAdress, time1);
		final CasefileId kennung2 = CasefileId.valueOf(
				hostAdress, time1);
		Assert.assertEquals(kennung1, kennung2);

		final CasefileId kennung3 = CasefileId.valueOf(
				hostAdress, time2);

		Assert.assertNotNull(kennung1);
		Assert.assertNotNull(kennung3);
		Assert.assertFalse("kennung1.equals(kennung3)", kennung1
				.equals(kennung3));

		final CasefileId kennung4 = CasefileId.valueOf(
				hostAdress2, time1);

		Assert.assertFalse(kennung1.equals(kennung4));

		final CasefileId kennung5 = CasefileId.valueOf(
				kennung1, "Horst Seidel");
		Assert.assertFalse("kennung1.equals(kennung5)", kennung1
				.equals(kennung5));
		final CasefileId kennung6 = CasefileId.valueOf(
				kennung1, "Harry Hirsch");
		Assert.assertFalse("kennung1.equals(kennung6)", kennung1
				.equals(kennung6));
		Assert.assertFalse("kennung5.equals(kennung6)", kennung5
				.equals(kennung6));
		final CasefileId kennung7 = CasefileId.valueOf(
				kennung2, "Horst Seidel");
		Assert.assertTrue("kennung5.equals(kennung7)", kennung5
				.equals(kennung7));
	}

	@Test
	public final void testHashCode2() throws Throwable {
		final CasefileId x = this.getAValueOfTypeUnderTest();
		final CasefileId y = this.getAValueOfTypeUnderTest();
		final CasefileId z = CasefileId.valueOf(x,
				"Horst Seidel");
		final CasefileId a = CasefileId.valueOf(y,
				"Horst Seidel");

		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						x);
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						y);

		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application",
						x.hashCode() == x.hashCode());
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result",
						x.equals(y) ? x.hashCode() == y.hashCode() : true);
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application",
						z.hashCode() == z.hashCode());
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result",
						z.equals(a) ? z.hashCode() == a.hashCode() : true);

		// Note (Copied from Java API documentation version JDK 1.5): It is not
		// required that if two objects are unequal according to the
		// equals(java.lang.Object) method, then calling the hashCode method on
		// each of the two objects must produce distinct integer results.
		// However, the programmer should be aware that producing distinct
		// integer results for unequal objects may improve the performance of
		// hashtables.
	}

	@Test
	public final void testToStringLocal() throws Throwable {
		final InetAddress hostAdress = InetAddress.getByAddress(new byte[] {
				127, 0, 0, 1 });
		final Date time1 = new Date(123456);
		final CasefileId ohneErgaenzung = CasefileId
				.valueOf(hostAdress, time1);
		final CasefileId mitErgaenzung = CasefileId
				.valueOf(ohneErgaenzung, "Horst Seidel");

		Assert.assertNotNull(ohneErgaenzung);
		Assert.assertEquals("123456,0@127.0.0.1", ohneErgaenzung.toString());

		Assert.assertNotNull(mitErgaenzung);
		Assert.assertEquals("123456,0@127.0.0.1/Horst Seidel", mitErgaenzung
				.toString());
	}

	@Test
	public void testValueOf() throws UnknownHostException {
		final InetAddress hostAdress = InetAddress.getByAddress(new byte[] {
				127, 0, 0, 1 });
		final Date time = new Date(123456);
		Assert.assertNotNull(hostAdress);

		final CasefileId kennung = CasefileId.valueOf(
				hostAdress, time);
		Assert.assertNotNull(kennung);
		Assert.assertFalse(kennung.hatErgaenzung());

		final CasefileId neueKennungBasierendAufAlter = CasefileId
				.valueOf(kennung, "Horst Senkel"); // "12345@127.0.0.1 / Horst
		// Senkel"
		Assert.assertNotNull(neueKennungBasierendAufAlter);
		Assert.assertTrue(neueKennungBasierendAufAlter.hatErgaenzung());

		try {
			CasefileId.valueOf(neueKennungBasierendAufAlter,
					"Horst Senkel");
			Assert.fail("Vertragsbruch wurde erwartet...");
		} catch (final Throwable t) {
			// Ok, Vorbedingung muss knallen...
		}
	}

	@Override
	protected CasefileId doGetAValueOfTypeUnderTest() {

		InetAddress hostAdress = null;
		try {
			hostAdress = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(hostAdress);

		final Date time = new Date(123456);
		return CasefileId.valueOf(hostAdress, time);
	}

	@Override
	protected CasefileId[] doGetDifferentInstancesOfTypeUnderTest() {
		InetAddress hostAdress = null;
		try {
			hostAdress = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(hostAdress);

		final Date time1 = new Date(123456);
		final Date time2 = new Date(123457);
		final Date time3 = new Date(123458);

		return new CasefileId[] {
				CasefileId.valueOf(hostAdress, time1),
				CasefileId.valueOf(hostAdress, time2),
				CasefileId.valueOf(hostAdress, time3) };
	}
}
