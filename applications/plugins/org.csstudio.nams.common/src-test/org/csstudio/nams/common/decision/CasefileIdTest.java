package org.csstudio.nams.common.decision;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class CasefileIdTest extends
		AbstractTestValue<CasefileId> {

	@Test
	public void testContractValueOf() throws Throwable {
		try {
			CasefileId.valueOf(null, "hallo");
			
		} catch (final AssertionError ae) {
			// Ok!
		}

		try {
			CasefileId casefileId = CasefileId.createNew();
			CasefileId extendedId = CasefileId.valueOf(casefileId, "extended");
			CasefileId.valueOf(extendedId, "valueof von extended nicht erlaubt");
			Assert.fail();
		} catch (final AssertionError ae) {
			// Ok!
		}
	}

	@Test
	public void testEquals() {

		final CasefileId kennung1 = CasefileId.createNew();
		final CasefileId kennung2 = CasefileId.valueOf(kennung1, null);
		Assert.assertNotNull(kennung1);
		Assert.assertNotNull(kennung2);
		Assert.assertEquals(kennung1, kennung2);

		final CasefileId kennung3 = CasefileId.createNew();
		Assert.assertNotNull(kennung3);
		Assert.assertFalse(kennung1.equals(kennung3));
		Assert.assertFalse(kennung2.equals(kennung3));

		final CasefileId kennungExtended1 = CasefileId.valueOf(
				kennung1, "Horst Seidel");
		Assert.assertFalse("kennung1.equals(kennung5)", kennung1
				.equals(kennungExtended1));
		final CasefileId kennungExtended2 = CasefileId.valueOf(
				kennung1, "Harry Hirsch");
		Assert.assertFalse("kennung1.equals(kennung6)", kennung1
				.equals(kennungExtended2));
		Assert.assertFalse("kennung5.equals(kennung6)", kennungExtended1
				.equals(kennungExtended2));
		final CasefileId kennungExtended3 = CasefileId.valueOf(
				kennung2, "Horst Seidel");
		Assert.assertTrue("kennung5.equals(kennung7)", kennungExtended1
				.equals(kennungExtended3));
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
	public void testValueOf() {
		final CasefileId kennung = CasefileId.createNew();

		final CasefileId neueKennungBasierendAufAlter = CasefileId
				.valueOf(kennung, "Horst Senkel"); // "12345@127.0.0.1 / Horst
		// Senkel"
		Assert.assertNotNull(neueKennungBasierendAufAlter);
		Assert.assertTrue(neueKennungBasierendAufAlter.hasExtension());

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
		return CasefileId.createNew();
	}

	@Override
	protected CasefileId[] doGetDifferentInstancesOfTypeUnderTest() {
		return new CasefileId[] {
				CasefileId.createNew(),
				CasefileId.createNew(),
				CasefileId.createNew() };
	}
}
