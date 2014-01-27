package org.csstudio.nams.common.material.regelwerk;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.junit.Test;

public class PropertyVergleichsRegelTest {

	@Test
	public void testVergleichsRegel() {
		
		MessageKeyEnum messageKey = MessageKeyEnum.AMS_REINSERTED;
		PropertyVergleichsRegel vergleichsRegel = new PropertyVergleichsRegel(StringRegelOperator.OPERATOR_NUMERIC_GT,
				messageKey, null);
		
		Map<MessageKeyEnum, String> map1 = new HashMap<MessageKeyEnum, String>();
		map1.put(messageKey, "5");
		AlarmNachricht ersteAlarmNachricht = new AlarmNachricht(map1);

		Map<MessageKeyEnum, String> map2 = new HashMap<MessageKeyEnum, String>();
		map2.put(messageKey, "5");
		AlarmNachricht zweiteAlarmNachricht = new AlarmNachricht(map2);
		
		Assert.assertFalse(vergleichsRegel.pruefeNachricht(zweiteAlarmNachricht, ersteAlarmNachricht));

		map2 = new HashMap<MessageKeyEnum, String>();
		map2.put(messageKey, "6");
		zweiteAlarmNachricht = new AlarmNachricht(map2);
		
		Assert.assertTrue(vergleichsRegel.pruefeNachricht(zweiteAlarmNachricht, ersteAlarmNachricht));
	}
	
}
