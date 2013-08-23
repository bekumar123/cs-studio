/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.alarm.service.declaration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.csstudio.alarm.service.internal.AlarmMessageDAL2Impl;
import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Characteristics.Builder;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Timestamp;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.junit.Test;

/**
 * Test for the dal-based implementation of the alarm message.
 * 
 * @author jpenning
 * @since 06.08.2012
 */
public class AlarmMessageDAL2ImplTest extends IAlarmMessageTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMessage() {

		IPvAccess<String> pvAccess = mock(IPvAccess.class);
		when(pvAccess.getPVAddress()).thenReturn(
				PvAddress.getValue("MyAddress"));
		when(pvAccess.getLastKnownValue()).thenReturn("a simple value");

		Builder builder = new Characteristics.Builder();
		builder.set(Characteristic.STATUS, EpicsAlarmStatus.HIGH);
		builder.set(Characteristic.SEVERITY, EpicsAlarmSeverity.MINOR);
		builder.set(Characteristic.TIMESTAMP, new Timestamp(1377163396000L, 0));
		when(pvAccess.getLastKnownCharacteristics())
				.thenReturn(builder.build());

		IAlarmMessage objectUnderTest = AlarmMessageDAL2Impl
				.newAlarmMessage(pvAccess);

		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.ACK));
		assertEquals("2013-08-22 11:23:16.000",
				objectUnderTest.getString(AlarmMessageKey.EVENTTIME));
		assertEquals("MyAddress",
				objectUnderTest.getString(AlarmMessageKey.NAME));
		assertEquals("HIGH", objectUnderTest.getString(AlarmMessageKey.STATUS));
		assertEquals("n.a.",
				objectUnderTest.getString(AlarmMessageKey.STATUS_OLD));
		assertEquals("n.a.",
				objectUnderTest.getString(AlarmMessageKey.HOST_PHYS));
		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.HOST));
		assertEquals("n.a.",
				objectUnderTest.getString(AlarmMessageKey.FACILITY));
		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.TEXT));
		assertEquals("event", objectUnderTest.getString(AlarmMessageKey.TYPE));
		assertEquals("a simple value",
				objectUnderTest.getString(AlarmMessageKey.VALUE));
		assertEquals("CSS_AlarmService",
				objectUnderTest.getString(AlarmMessageKey.APPLICATION_ID));
		assertEquals("",
				objectUnderTest.getString(AlarmMessageKey.ALARMUSERGROUP));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void testGetStringFromUnitializedMessage() {
		IPvAccess<String> pvAccess = mock(IPvAccess.class);
		when(pvAccess.getPVAddress()).thenReturn(
				PvAddress.getValue("MyAddress"));
		when(pvAccess.getLastKnownValue()).thenReturn(null);
		when(pvAccess.getLastKnownCharacteristics()).thenReturn(
				new Characteristics());

		IAlarmMessage objectUnderTest = AlarmMessageDAL2Impl
				.newAlarmMessage(pvAccess);

		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.ACK));
		assertEquals("n.a.",
				objectUnderTest.getString(AlarmMessageKey.EVENTTIME));
		assertEquals("MyAddress",
				objectUnderTest.getString(AlarmMessageKey.NAME));
		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.STATUS));
		assertEquals("n.a.",
				objectUnderTest.getString(AlarmMessageKey.STATUS_OLD));
		assertEquals("n.a.",
				objectUnderTest.getString(AlarmMessageKey.HOST_PHYS));
		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.HOST));
		assertEquals("n.a.",
				objectUnderTest.getString(AlarmMessageKey.FACILITY));
		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.TEXT));
		assertEquals("event", objectUnderTest.getString(AlarmMessageKey.TYPE));
		assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.VALUE));
		assertEquals("CSS_AlarmService",
				objectUnderTest.getString(AlarmMessageKey.APPLICATION_ID));
		assertEquals("",
				objectUnderTest.getString(AlarmMessageKey.ALARMUSERGROUP));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void testGetSeverity() {

		IPvAccess<String> pvAccess = mock(IPvAccess.class);
		when(pvAccess.getPVAddress()).thenReturn(
				PvAddress.getValue("MyAddress"));

		// Test high alarm
		{
			Builder builder = new Characteristics.Builder();
			builder.set(Characteristic.STATUS, EpicsAlarmStatus.HIGH);
			builder.set(Characteristic.SEVERITY, EpicsAlarmSeverity.MINOR);
			when(pvAccess.getLastKnownCharacteristics()).thenReturn(
					builder.build());

			IAlarmMessage objectUnderTest = AlarmMessageDAL2Impl
					.newAlarmMessage(pvAccess);
			assertEquals("HIGH",
					objectUnderTest.getString(AlarmMessageKey.STATUS));
			assertEquals("MINOR",
					objectUnderTest.getString(AlarmMessageKey.SEVERITY));
		}

		// Test no alarm
		{
			Builder builder = new Characteristics.Builder();
			builder.set(Characteristic.STATUS, EpicsAlarmStatus.NO_ALARM);
			builder.set(Characteristic.SEVERITY, EpicsAlarmSeverity.NO_ALARM);
			when(pvAccess.getLastKnownCharacteristics()).thenReturn(
					builder.build());

			IAlarmMessage objectUnderTest = AlarmMessageDAL2Impl
					.newAlarmMessage(pvAccess);
			assertEquals("NO_ALARM",
					objectUnderTest.getString(AlarmMessageKey.STATUS));
			assertEquals("NO_ALARM",
					objectUnderTest.getString(AlarmMessageKey.SEVERITY));
		}

		// Test invalid
		{
			Builder builder = new Characteristics.Builder();
			when(pvAccess.getLastKnownCharacteristics()).thenReturn(
					builder.build());

			IAlarmMessage objectUnderTest = AlarmMessageDAL2Impl
					.newAlarmMessage(pvAccess);
			assertEquals("n.a.",
					objectUnderTest.getString(AlarmMessageKey.STATUS));
			assertEquals("n.a.",
					objectUnderTest.getString(AlarmMessageKey.SEVERITY));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void testIsAcknowledgement() throws Exception {

		IPvAccess<String> pvAccess = mock(IPvAccess.class);
		when(pvAccess.getPVAddress()).thenReturn(
				PvAddress.getValue("MyAddress"));
		when(pvAccess.getLastKnownValue()).thenReturn("a simple value");
		when(pvAccess.getLastKnownCharacteristics())
				.thenReturn(new Characteristics());

		IAlarmMessage objectUnderTest = AlarmMessageDAL2Impl
				.newAlarmMessage(pvAccess);

		assertFalse(objectUnderTest.isAcknowledgement());

		// currently acknowledgment is not supported via the dal implementation
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void testGetMap() throws Exception {
		IPvAccess<String> pvAccess = mock(IPvAccess.class);
		when(pvAccess.getPVAddress()).thenReturn(
				PvAddress.getValue("MyAddress"));
		when(pvAccess.getLastKnownValue()).thenReturn("a simple value");
		when(pvAccess.getLastKnownCharacteristics())
				.thenReturn(new Characteristics());

		IAlarmMessage objectUnderTest = AlarmMessageDAL2Impl
				.newAlarmMessage(pvAccess);

		Map<String, String> map = objectUnderTest.getMap();

		// check the count of the keys of the map
		// if uninitialized the alarm user group must not be given, therefore we
		// expect one less than the whole count
		assertEquals("You probably changed the key set of the map.", 14, map
				.keySet().size());
	}

}
