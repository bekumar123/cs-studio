package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.junit.Test;

public class NichtRegelTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testNichtRegel() {
		Regel falseRegel = createRegel(false);
		NichtRegel notFalseRegel = new NichtRegel(falseRegel);
		Assert.assertFalse(falseRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		Assert.assertTrue(notFalseRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		Regel trueRegel = createRegel(true);
		NichtRegel notTrueRegel = new NichtRegel(trueRegel);
		Assert.assertTrue(trueRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		Assert.assertFalse(notTrueRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
	}

	private Regel createRegel(final boolean testResult) {
		return new Regel() {
			
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht,
					AlarmNachricht vergleichsNachricht) {
				return testResult;
			}
			
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht) {
				return testResult;
			}
		};
	}

}
