/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmMessageJMSImpl.java,v 1.4
 * 2010/04/28 07:58:00 jpenning Exp $
 */
package org.csstudio.alarm.service.internal;

import static org.csstudio.dal2.dv.Characteristic.HOSTNAME;
import static org.csstudio.dal2.dv.Characteristic.SEVERITY;
import static org.csstudio.dal2.dv.Characteristic.STATUS;
import static org.csstudio.dal2.dv.Characteristic.TIMESTAMP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Timestamp;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAL based implementation of the message abstraction of the AlarmService This
 * is an immutable class.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public final class AlarmMessageDAL2Impl implements IAlarmMessage {

	private static final String NOT_AVAILABLE = "n.a.";

	private static final Logger LOG = LoggerFactory
			.getLogger(AlarmMessageDAL2Impl.class);

	private static final String ERROR_MESSAGE = "Error analyzing DAL message";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * application ID for this application
	 */
	private static final String APPLICATION_ID = "CSS_AlarmService";

	private Characteristics characteristics;

	private String value;

	private PvAddress address;

	/**
	 * Create alarm message with the given pvAccess.
	 * 
	 * @param pvAccess
	 */
	private AlarmMessageDAL2Impl(@Nonnull final IPvAccess<String> pvAccess) {
		assert pvAccess != null : "Precondition: pvAccess != null";
		characteristics = pvAccess.getLastKnownCharacteristics();
		value = pvAccess.getLastKnownValue();
		address = pvAccess.getPVAddress();
	}

	public static boolean canCreateAlarmMessageFrom(
			@Nonnull final IPvAccess<?> pvAccess) {
		return true;
	}

	@Nonnull
	public static IAlarmMessage newAlarmMessage(
			@Nonnull IPvAccess<String> pvAccess) {
		assert canCreateAlarmMessageFrom(pvAccess) : "Alarm message cannot be created for "
				+ pvAccess.getPVAddress().getAddress();
		return new AlarmMessageDAL2Impl(pvAccess);
	}

	@Override
	@Nonnull
	public String getString(@Nonnull final AlarmMessageKey key) {
		String result;
		switch (key) {
		case EVENTTIME:
			result = NOT_AVAILABLE;
			Timestamp timestamp = characteristics.get(TIMESTAMP);
			if (timestamp != null) {
				synchronized (DATE_FORMAT) {
					result = DATE_FORMAT.format(timestamp.getMilliseconds());
				}
			}
			break;
		case NAME:
			result = address.getAddress();
			break;
		case SEVERITY:
			EpicsAlarmSeverity severity = characteristics.get(SEVERITY);
			result = severity != null ? severity.name() : NOT_AVAILABLE;
			break;
		case STATUS:
			EpicsAlarmStatus status = characteristics.get(STATUS);
			result = status != null ? status.name() : NOT_AVAILABLE;
			break;
		case HOST:
			String hostname = characteristics.get(HOSTNAME);
			result = (hostname != null) ? hostname : NOT_AVAILABLE;
			break;
		case VALUE:
			result = (value != null) ? value : NOT_AVAILABLE;
			LOG.trace("getString(VALUE) {}", result);
			break;
		case TYPE:
			// The type is hard coded as an alarm event, because we registered
			// for such a beast.
			result = "event";
			break;
		case APPLICATION_ID:
			result = APPLICATION_ID;
			break;
		case ALARMUSERGROUP:
			result = AlarmPreference.getAlarmGroup().getRepresentation();
			break;
		case ACK:
		case SEVERITY_OLD:
		case STATUS_OLD:
		case HOST_PHYS:
		case FACILITY:
		case TEXT:
			result = NOT_AVAILABLE;
			break;
		default:
			LOG.error(ERROR_MESSAGE + ". getString called for undefined key : "
					+ key);
			result = NOT_AVAILABLE;
		}
		return result;
	}

	@Override
	@Nonnull
	public Map<String, String> getMap() {
		final Map<String, String> result = new HashMap<String, String>();
		for (final AlarmMessageKey key : AlarmMessageKey.values()) {
			// do not enter the alarm user group into the map if none is defined
			if ((key == AlarmMessageKey.ALARMUSERGROUP)
					&& (getString(key).isEmpty())) {
				continue;
			}
			result.put(key.getDefiningName(), getString(key));
		}
		return result;
	}

	@Override
	@Nonnull
	public String toString() {
		return "JMS-AlarmMessage of type " + getString(AlarmMessageKey.TYPE)
				+ " for " + getString(AlarmMessageKey.NAME) + ", Severity "
				+ getSeverity() + ", Status "
				+ getString(AlarmMessageKey.STATUS);
	}

	/**
	 * Maps the condition from the DAL event to the severity enum.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	@Nonnull
	public EpicsAlarmSeverity getSeverity() {
		return characteristics.get(SEVERITY);
	}

	@Override
	@CheckForNull
	public Date getEventtime() {
		final Timestamp timestamp = characteristics.get(TIMESTAMP);
		return timestamp != null ? new Date(timestamp.getMilliseconds()) : null;
	}

	@Override
	@Nonnull
	public Date getEventtimeOrCurrentTime() {
		Date result = getEventtime();
		if (result == null) {
			result = new Date(System.currentTimeMillis());
		}
		return result;
	}

	@Override
	public boolean isAcknowledgement() {
		// The DAL implementation currently does not support alarm
		// acknowledgment
		return false;
	}

	@Override
	public boolean isBeaconMessage() {
		return address.getAddress().contains("beacon");
	}
}
