package org.csstudio.nams.common.fachwert;

import static org.junit.Assert.assertEquals;

import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class Millisekunden_Test extends AbstractTestValue<Milliseconds> {
	@Test
	public void testDifferenz() {
		final Milliseconds millisekunden = Milliseconds.valueOf(2000);
		final Milliseconds millisekunden2 = Milliseconds.valueOf(4000);

		assertEquals(Milliseconds.valueOf(2000), millisekunden
				.differenz(millisekunden2));
		assertEquals(Milliseconds.valueOf(2000), millisekunden2
				.differenz(millisekunden));
	}

	@Test
	public void testEquals() {
		final Milliseconds millisekunden = Milliseconds.valueOf(2000);
		final Milliseconds millisekunden2 = Milliseconds.valueOf(4000);

		// Gleichheit
		assertEquals(Milliseconds.valueOf(2000), millisekunden);
		assertEquals(Milliseconds.valueOf(4000), millisekunden2);
		assertFalse(millisekunden.equals(millisekunden2));
		assertFalse(millisekunden2.equals(millisekunden));
	}

	@Test
	public void testIstNull() {
		final Milliseconds millisekunden = Milliseconds.valueOf(2000);
		final Milliseconds millisekunden2 = Milliseconds.valueOf(4000);
		final Milliseconds millisekunden3 = Milliseconds.valueOf(0);

		assertFalse(millisekunden.istNull());
		assertFalse(millisekunden2.istNull());
		assertTrue(millisekunden3.istNull());
	}

	@Test
	public void testKleinerGroesser() {
		final Milliseconds millisekunden = Milliseconds.valueOf(2000);
		final Milliseconds millisekunden2 = Milliseconds.valueOf(4000);

		assertTrue(millisekunden.istKleiner(millisekunden2));
		assertFalse(millisekunden.istGroesser(millisekunden2));
		assertFalse(millisekunden2.istKleiner(millisekunden));
		assertTrue(millisekunden2.istGroesser(millisekunden));

		assertFalse(millisekunden.istKleiner(millisekunden));
		assertFalse(millisekunden.istGroesser(millisekunden));
	}

	@Test
	public void testValueOf() {
		// Anlegen
		final Milliseconds millisekunden = Milliseconds.valueOf(2000);
		assertNotNull(millisekunden);
		final Milliseconds millisekunden2 = Milliseconds.valueOf(4000);
		assertNotNull(millisekunden2);
		final Milliseconds millisekunden3 = Milliseconds.valueOf(0);
		assertNotNull(millisekunden3);

		try {
			Milliseconds.valueOf(-42);
			fail("Anfrage eines ungueltigen Wertes muss fehlschlagen!");
		} catch (final AssertionError ae) {
			// Ok, call have to fail!
		}
	}

	@Override
	protected Milliseconds doGetAValueOfTypeUnderTest() {
		return Milliseconds.valueOf(42);
	}

	@Override
	protected Milliseconds[] doGetDifferentInstancesOfTypeUnderTest() {
		return new Milliseconds[] { Milliseconds.valueOf(42),
				Milliseconds.valueOf(23), Milliseconds.valueOf(1024) };
	}
}
