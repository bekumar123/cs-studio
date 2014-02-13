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
package org.csstudio.dal2.dv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

/**
 * A pv is accompanied by metadata. Here the possible so-called characteristics
 * are defined.
 * 
 * @author jpenning, arne scharping
 * @since 10.09.2012
 */
public class Characteristics {

	private Map<Characteristic<?>, Object> values;

	public Characteristics(Map<Characteristic<?>, Object> values) {

		// perform type check to guarantee consistency
		for (Characteristic<?> c : values.keySet()) {
			c.getType().cast(values.get(c));
		}

		this.values = new HashMap<Characteristic<?>, Object>(values);
	}

	public Characteristics() {
		this.values = Collections.emptyMap();
	}

	public final <T> T get(Characteristic<T> characteristic) {
		Object object = values.get(characteristic);
		return characteristic.getType().cast(object);
	}
	
	/**
	 * Convenience method providing the characteristic {@link Characteristic#STATUS} or null, if status is not available
	 */
	public final EpicsAlarmStatus getStatus() {
		return get(Characteristic.STATUS);
	}
	
	/**
	 * Convenience method providing the characteristic {@link Characteristic#SEVERITY} or null, is severity is not available
	 */
	public final EpicsAlarmSeverity getSeverity() {
		return get(Characteristic.SEVERITY);
	}
	
	public Set<Characteristic<?>> listAvailable() {
		return values.keySet();
	}

	public boolean isAvailable(Characteristic<?> characteristic) {
		return values.containsKey(characteristic);
	}

	/**
	 * Creates a new Characteristics object as a combination of the called and
	 * the given object. The values of the called object will be overwritten by
	 * the parameter object.
	 * 
	 * @param characteristics
	 * @return
	 */
	public Characteristics createUpdate(Characteristics characteristics) {
		
		Map<Characteristic<?>, Object> newValues = new HashMap<Characteristic<?>, Object>();
		newValues.putAll(values);
		newValues.putAll(characteristics.values);
		return new Characteristics(newValues);
	}

	@Override
	public String toString() {
		return values.toString();
	}
	
	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return values.equals(((Characteristics) obj).values);
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {

		private Map<Characteristic<?>, Object> values = new HashMap<Characteristic<?>, Object>();

		public Builder() {
			// nothing to do
		}

		public <T> Builder set(Characteristic<T> characteristic, T value) {
			values.put(characteristic, value);
			return this;
		}

		public Characteristics build() {
			return new Characteristics(values);
		}

		public Builder setStatus(EpicsAlarmStatus status) {
			set(Characteristic.STATUS, status);
			return this;
		}

		public Builder setSeverity(EpicsAlarmSeverity severity) {
			set(Characteristic.SEVERITY, severity);
			return this;
		}
	}

}
