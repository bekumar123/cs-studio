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
package org.csstudio.dal2.epics.service;

import static org.csstudio.dal2.dv.Characteristic.ALARM_MAX;
import static org.csstudio.dal2.dv.Characteristic.ALARM_MIN;
import static org.csstudio.dal2.dv.Characteristic.GRAPH_MAX;
import static org.csstudio.dal2.dv.Characteristic.GRAPH_MIN;
import static org.csstudio.dal2.dv.Characteristic.LABELS;
import static org.csstudio.dal2.dv.Characteristic.MAXIMUM;
import static org.csstudio.dal2.dv.Characteristic.MINIMUM;
import static org.csstudio.dal2.dv.Characteristic.SEVERITY;
import static org.csstudio.dal2.dv.Characteristic.STATUS;
import static org.csstudio.dal2.dv.Characteristic.WARNING_MAX;
import static org.csstudio.dal2.dv.Characteristic.WARNING_MIN;
import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.GR;
import gov.aps.jca.dbr.LABELS;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Timestamp;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

/**
 * Conversion of epics types to characteristics (abstraction for metadata)
 * 
 * @author jpenning
 * @since 10.09.2012
 */
public class CharacteristicsService {

	// TODO dal2 extract interface, add tests

	/** Seconds of epoch start since UTC time start. 
	 * <p><i> taken from {@link org.csstudio.dal.epics.PlugUtilities}*/
	public static long TS_EPOCH_SEC_PAST_1970 = 7305 * 86400;
	
	public Characteristics newCharacteristics(final DBR dbr, final String hostname) {

		Characteristics.Builder builder = new Characteristics.Builder();

		builder.set(Characteristic.HOSTNAME, hostname);
		
		if (dbr instanceof STS) {
			STS dbr_sts = (STS) dbr;
			builder .set(SEVERITY, EpicsAlarmSeverity.valueOf(dbr_sts.getSeverity()))
					.set(STATUS, EpicsAlarmStatus.valueOf(dbr_sts.getStatus()));
			
			if (dbr instanceof TIME) {
				TIME dbr_time = (TIME)dbr;

				TimeStamp timeStamp = dbr_time.getTimeStamp();
				if (timeStamp != null) {
					builder.set(Characteristic.TIMESTAMP, convertTimestamp(timeStamp));
				} else {
					builder.set(Characteristic.TIMESTAMP, new Timestamp());
				}
			
				if (dbr instanceof GR) {
					GR dbr_gr = (GR) dbr;
					builder	.set(GRAPH_MAX, dbr_gr.getUpperDispLimit().doubleValue())
							.set(GRAPH_MIN, dbr_gr.getLowerDispLimit().doubleValue())
							.set(ALARM_MAX, dbr_gr.getUpperAlarmLimit().doubleValue())
							.set(ALARM_MIN, dbr_gr.getLowerAlarmLimit().doubleValue())
							.set(WARNING_MAX, dbr_gr.getUpperWarningLimit().doubleValue())
							.set(WARNING_MIN, dbr_gr.getLowerWarningLimit().doubleValue());
		
					if (dbr instanceof CTRL) {
						CTRL dbr_ctrl = (CTRL) dbr;
						builder	.set(MAXIMUM, dbr_ctrl.getUpperCtrlLimit().doubleValue())
								.set(MINIMUM, dbr_ctrl.getLowerCtrlLimit().doubleValue());
					}
				}
			}
		}
		
		if (dbr instanceof LABELS) {
			LABELS dbr_labels = (LABELS) dbr;
			builder.set(LABELS, dbr_labels.getLabels());
		}

		// TODO weitere ergänzen ...

		return builder.build();
	}

	/**
	 * Converts CA timestamp to DAL timestamp.
	 *
	 * @param ts CA timestamp
	 *
	 * @return DAL timestamp
	 * 
	 * taken from {@link org.csstudio.dal.epics.PlugUtilities}
	 */
	private static Timestamp convertTimestamp(final TimeStamp ts)
	{
		return new Timestamp((ts.secPastEpoch() + TS_EPOCH_SEC_PAST_1970) * 1000, ts.nsec());
	}

}
