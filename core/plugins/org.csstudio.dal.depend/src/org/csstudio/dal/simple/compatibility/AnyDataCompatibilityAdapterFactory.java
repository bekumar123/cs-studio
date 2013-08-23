package org.csstudio.dal.simple.compatibility;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.Severity;
import org.csstudio.dal.simple.impl.DataUtil;
import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

public class AnyDataCompatibilityAdapterFactory {

	@SuppressWarnings("unchecked")
	public static <T> AnyData createAnyData(IPvAccess<T> pvAccess) {

		Type<T> type = pvAccess.getType();
		T value = pvAccess.getLastKnownValue();

		if (value == null) {
			return null; // TODO ArS: use another representation of null?
		}

		if (Type.STRING.equals(type)) {
			return new AnyDataCompatibilityStringImpl((IPvAccess<String>)pvAccess);
		} else if (Type.DOUBLE.equals(type)) {
			return new AnyDataCompatibilityDoubleImpl((IPvAccess<Double>)pvAccess);
		}
		throw new RuntimeException("Conversion not supported for type " + value
				+ " (" + type + ")");

	}

	private static abstract class AbstractAnyDataCompatibilityImpl<T>
			implements AnyData {

		private T _value;
		private Characteristics _characteristics;
		private DynamicValueCondition _dynamicValueCondition;
		private IPvAccess<T> _pvAccess;

		public AbstractAnyDataCompatibilityImpl(IPvAccess<T> pvAccess) {
			_pvAccess = pvAccess;
			_value = pvAccess.getLastKnownValue();
			_characteristics = pvAccess.getLastKnownCharacteristics();
			_dynamicValueCondition = CompatibilityMapper.createDynamicValueCondition(pvAccess);
		}

		protected T value() {
			return _value;
		}

		public Object anyValue() {
			return value();
		}

		@Override
		public Object[] anySeqValue() {
			return new Object[] { anyValue() };
		}

		@Override
		public double[] doubleSeqValue() {
			return new double[] { doubleValue() };
		}

		@Override
		public long[] longSeqValue() {
			return new long[] { longValue() };
		}

		@Override
		public String[] stringSeqValue() {
			return new String[] { stringValue() };
		}

		@Override
		public Number[] numberSeqValue() {
			return new Number[] { numberValue() };
		}

		@Override
		public MetaData getMetaData() {
			if (_characteristics == null) {
				return null;
			} else {
				return new MetaDataAdapter(_characteristics);
			}
		}

		@Override
		public boolean isValid() {
			return true; // TODO ArS: Always valid?
		}

		@Override
		public Severity getSeverity() {
			return _dynamicValueCondition;
		}

		@Override
		public Timestamp getTimestamp() {
			org.csstudio.dal2.dv.Timestamp timestamp = _characteristics.get(Characteristic.TIMESTAMP);
			return new Timestamp(timestamp.getMilliseconds(), timestamp.getNanoseconds());
		}

		@Override
		public String getStatus() {
			EpicsAlarmStatus status = _characteristics.get(Characteristic.STATUS);
			return (status != null) ? status.toString() : "N/A";
		}

		@Override
		public Quality getQuality() {
			return Quality.Original;
		}

		@Override
		public AnyDataChannel getParentChannel() {
			return new AnyDataChannelCompatibilityAdapter<T>(_pvAccess);
		}

		@Override
		public DynamicValueProperty<?> getParentProperty() {
			return new DynamicValuePropertyCompatibilityAdapter<T>(_pvAccess);
		}

		@Override
		public long getBeamID() {
			return Long.MIN_VALUE;
		}
	}

	private static class AnyDataCompatibilityStringImpl extends
			AbstractAnyDataCompatibilityImpl<String> {

		public AnyDataCompatibilityStringImpl(IPvAccess<String> pvAccess) {
			super(pvAccess);
		}
		

		public double doubleValue() {
			Double d = Double.NaN;
			try {
				d = DataUtil.castTo(value(), Double.class);
			} catch (Exception e) {
			}
			return d;
		}

		public long longValue() {
			try {
				return DataUtil.castTo(value(), Long.class);
			} catch (Exception e) {
				return Long.MIN_VALUE; // TODO any better idea?
			}
		}

		public Number numberValue() {
			return DataUtil.castToNumber(value());
		}

		public String stringValue() {
			return value();
		}

	}

	private static class AnyDataCompatibilityDoubleImpl extends
			AbstractAnyDataCompatibilityImpl<Double> {

		public AnyDataCompatibilityDoubleImpl(IPvAccess<Double> pvAccess) {
			super(pvAccess);
		}

		public Object[] anySeqValue() {
			return new Object[] { value() };
		}

		public double doubleValue() {
			return value();
		}

		public long longValue() {
			try {
				return DataUtil.castTo(value(), Long.class);
			} catch (Exception e) {
				return Long.MIN_VALUE; // TODO any better idea?
			}
		}

		public Number numberValue() {
			return DataUtil.castToNumber(value());
		}

		public String stringValue() {
			return value().toString();
		}
	}

}
