package org.csstudio.nams.common.material.regelwerk;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmMessage;
import org.junit.Test;

public class OderRegelTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testOderRegel() {
		FilterCondition falseRegel1 = createRegel(false);
		FilterCondition falseRegel2 = createRegel(false);
		FilterCondition falseRegel3 = createRegel(false);
		FilterCondition trueRegel1 = createRegel(true);
		
		OrFilterCondition oderRegel = new OrFilterCondition(Arrays.asList(falseRegel1,falseRegel2,falseRegel3));
		Assert.assertFalse(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		oderRegel = new OrFilterCondition(Arrays.asList(falseRegel1,falseRegel2,falseRegel3, trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));

		oderRegel = new OrFilterCondition(Arrays.asList(falseRegel1,trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		oderRegel = new OrFilterCondition(Arrays.asList(falseRegel1,trueRegel1, falseRegel2));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		oderRegel = new OrFilterCondition(Arrays.asList(trueRegel1, falseRegel1, falseRegel2, falseRegel3));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		oderRegel = new OrFilterCondition(Arrays.asList(trueRegel1, trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		oderRegel = new OrFilterCondition(Arrays.asList(trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		oderRegel = new OrFilterCondition(Arrays.asList(falseRegel1));
		Assert.assertFalse(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
		
		oderRegel = new OrFilterCondition(Collections.<FilterCondition> emptyList());
		Assert.assertFalse(oderRegel.pruefeNachricht(new AlarmMessage("TestNachricht")));
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
