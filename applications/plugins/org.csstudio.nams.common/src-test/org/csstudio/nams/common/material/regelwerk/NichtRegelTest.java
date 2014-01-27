package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmMessage;
import org.junit.Test;

public class NichtRegelTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testNichtRegel() {
		Regel falseRegel = createRegel(false);
		NichtRegel notFalseRegel = new NichtRegel(falseRegel);
		Assert.assertFalse(falseRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		Assert.assertTrue(notFalseRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		Regel trueRegel = createRegel(true);
		NichtRegel notTrueRegel = new NichtRegel(trueRegel);
		Assert.assertTrue(trueRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		Assert.assertFalse(notTrueRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
	}

	private Regel createRegel(final boolean testResult) {
		return new Regel() {
			
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht,
					AlarmMessage vergleichsNachricht) {
				return testResult;
			}
			
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht) {
				return testResult;
			}
		};
	}

}
