package org.csstudio.ams.distributor;

import java.util.Map;

import org.csstudio.ams.distributor.service.MessageExtensionService;

public class MessageExtender {

	private static final String PV_KEY = "NAME";
	private final MessageExtensionService service;

	public MessageExtender(MessageExtensionService service) {
		this.service = service;
	}
	
	public void extendMessageMap(Map<String, String> messageMap) {
		String pv = messageMap.get(PV_KEY);
		if (pv != null && !pv.isEmpty() && service.hasMessageExtensions(pv)) {
			messageMap.putAll(service.getMessageExtensions(pv));
		}
	}
}
