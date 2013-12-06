package org.csstudio.dal2.epics.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.Context;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_String;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TimeStamp;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Timestamp;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class GetValueRequesterTest {

	private static final TimeStamp TIMESTAMP = new TimeStamp();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMonitor() throws Exception {

		PvAddress pv = PvAddress.getValue("MyPv");
		Type<String> type = Type.STRING;

		Channel channel = mock(Channel.class);
		when(channel.getHostName()).thenReturn("HostName");
		when(channel.getName()).thenReturn("MyPv");
		when(channel.getElementCount()).thenReturn(1);

		Context context = mock(Context.class);
		when(
				context.createChannel(eq(pv.getAddress()),
						any(ConnectionListener.class))).thenReturn(channel);

		ICsResponseListener responseListener = mock(ICsResponseListener.class);

		// create requester
		new GetValueRequester(context, pv, type, responseListener);

		ArgumentCaptor<ConnectionListener> connListenerCaptor = ArgumentCaptor
				.forClass(ConnectionListener.class);
		verify(context).createChannel(eq(pv.getAddress()),
				connListenerCaptor.capture());

		// simulate connect event
		when(channel.getConnectionState())
				.thenReturn(ConnectionState.CONNECTED);
		when(channel.getFieldType()).thenReturn(DBRType.STRING);
		ConnectionListener connListener = connListenerCaptor.getValue();
		connListener.connectionChanged(new ConnectionEvent(channel, true));

		ArgumentCaptor<GetListener> getListenerCaptor = ArgumentCaptor.forClass(GetListener.class);
		verify(channel, timeout(1000)).get(eq(DBRType.CTRL_STRING), anyInt(), getListenerCaptor.capture());
		verify(context).flushIO();

		// simulate value change
		GetListener getListener = getListenerCaptor.getValue();
		DBR_CTRL_String dbr = new DBR_CTRL_String(new String[] { "New Value" });
		dbr.setTimeStamp(TIMESTAMP);
		dbr.setSeverity(Severity.MINOR_ALARM);
		dbr.setStatus(Status.HIGH_ALARM);

		getListener.getCompleted(new GetEvent(channel, dbr,	CAStatus.NORMAL));

		ArgumentCaptor<CsPvData> dataCaptor = ArgumentCaptor
				.forClass(CsPvData.class);
		verify(responseListener).onSuccess(dataCaptor.capture());
		CsPvData data = dataCaptor.getValue();
		assertEquals("New Value", data.getValue());
		Characteristics characteristics = data.getCharacteristics();
		assertEquals(EpicsAlarmSeverity.MINOR, characteristics.getSeverity());
		assertEquals(EpicsAlarmStatus.HIGH, characteristics.getStatus());

		Timestamp t = characteristics.get(Characteristic.TIMESTAMP);

		SimpleDateFormat format = new SimpleDateFormat("MM:dd:yy HH:mm:ss.SSS");
		assertEquals(TIMESTAMP.toMMDDYY(),
				format.format(new Date(t.getMilliseconds())));
	}
}
