package org.csstudio.nams.common.material.regelwerk;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.junit.Test;

public class OderRegelTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testOderRegel() {
		Regel falseRegel1 = createRegel(false);
		Regel falseRegel2 = createRegel(false);
		Regel falseRegel3 = createRegel(false);
		Regel trueRegel1 = createRegel(true);
		
		OderRegel oderRegel = new OderRegel(Arrays.asList(falseRegel1,falseRegel2,falseRegel3));
		Assert.assertFalse(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		oderRegel = new OderRegel(Arrays.asList(falseRegel1,falseRegel2,falseRegel3, trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));

		oderRegel = new OderRegel(Arrays.asList(falseRegel1,trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		oderRegel = new OderRegel(Arrays.asList(falseRegel1,trueRegel1, falseRegel2));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		oderRegel = new OderRegel(Arrays.asList(trueRegel1, falseRegel1, falseRegel2, falseRegel3));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		oderRegel = new OderRegel(Arrays.asList(trueRegel1, trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		oderRegel = new OderRegel(Arrays.asList(trueRegel1));
		Assert.assertTrue(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		oderRegel = new OderRegel(Arrays.asList(falseRegel1));
		Assert.assertFalse(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
		
		oderRegel = new OderRegel(Collections.<Regel> emptyList());
		Assert.assertFalse(oderRegel.pruefeNachricht(new AlarmNachricht("TestNachricht")));
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
