package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmMessage;
import org.junit.Test;

public class NichtRegelTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testNichtRegel() {
		FilterCondition falseRegel = createRegel(false);
		NotFilterCondition notFalseRegel = new NotFilterCondition(falseRegel);
		Assert.assertFalse(falseRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		Assert.assertTrue(notFalseRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		FilterCondition trueRegel = createRegel(true);
		NotFilterCondition notTrueRegel = new NotFilterCondition(trueRegel);
		Assert.assertTrue(trueRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		Assert.assertFalse(notTrueRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
	}

	private FilterCondition createRegel(final boolean testResult) {
		return new FilterCondition() {
			
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
