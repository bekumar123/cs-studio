package org.csstudio.nams.common.decision;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

abstract public class AbstractTestAblagekorb<T extends Document, KT>
		extends AbstractTestObject<KT> {
	@Test(timeout = 2000)
	public void testAblegen() throws InterruptedException {
		final Outbox<T> eingangskorb = this.gibNeuesExemplar();

		final T object1 = this.gibNeuesAblagefaehigesExemplar();
		final T object2 = this.gibNeuesAblagefaehigesExemplar();

		// Objects ablegen:
		eingangskorb.put(object1);
		eingangskorb.put(object2);

		// Objects in richtiger Reihenfolge abholbar?
		Assert.assertTrue("Das zu erst hineingelegte ist enthalten", this
				.pruefeObEnthalten(eingangskorb, object1));
		Assert.assertTrue("Das zu yweit hineingelegte ist enthalten", this
				.pruefeObEnthalten(eingangskorb, object2));
	}

	protected abstract T gibNeuesAblagefaehigesExemplar();

	protected abstract Outbox<T> gibNeuesExemplar();

	protected abstract boolean pruefeObEnthalten(Outbox<T> korb, T element);
}
