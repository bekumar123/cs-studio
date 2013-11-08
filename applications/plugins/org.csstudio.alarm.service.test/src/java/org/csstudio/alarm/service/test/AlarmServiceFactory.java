package org.csstudio.alarm.service.test;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.internal.AlarmConfigurationServiceImpl;
import org.csstudio.alarm.service.internal.AlarmServiceDal2Impl;

public class AlarmServiceFactory {

	public static IAlarmService createDal2Impl() {
		return new AlarmServiceDal2Impl();
	}

	public static IAlarmConfigurationService createAlarmConfigurationService() {
		return new AlarmConfigurationServiceImpl();
		
	}
	
}
