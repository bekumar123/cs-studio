package org.csstudio.sds.history.anticorruption.adapter.anydata;

import java.util.EnumSet;

import javax.annotation.Nullable;

import org.csstudio.dal.AccessType;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.Severity;
import org.csstudio.sds.history.anticorruption.adapter.ChannelFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVConnectionState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVSeverityState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariableAttribute;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PvAttributeNames;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;

/**
 * This class is a adapter for the property construct from dal. It delegates to calls to a stored {@link ProcessVariable}.
 * 
 * @author Christian Mein
 *
 * @param <T>
 * @param <Ts>
 */

//TODO CME: testing !!!! 
public class AnyDataChannelFromPV<T, Ts> extends NumericPropertyImplGenealStub<T, Ts> {

	private Logger LOG = LoggerFactory.getLogger("AnyDataLog");

	private ProcessVariable _processVariable;
	
	private ChannelFieldType _channelType;

	private final AnyDataImpl _anyData;

	private final MetaData _metaData;

	public AnyDataChannelFromPV(ProcessVariable processVariable, ChannelFieldType channelType) {
		_processVariable = processVariable;
		_channelType = channelType;
		_anyData = this.new AnyDataImpl();
		_metaData = this.new MetaDataImpl();
	}
	
	/*
	 * AnyDataChannel start
	 */
	@Override
	public String getUniqueName() {
		LOG.trace("getUniqueName()");
		return _processVariable.getReferenceCode();
	}

	@Override
	public void addListener(ChannelListener listener) {
		LOG.error("AnyDataChannelFromPV.addListener()");
	}

	@Override
	public void removeListener(ChannelListener listener) {
		LOG.error("AnyDataChannelFromPV.removeListener()");
	}

	@Override
	public ChannelListener[] getListeners() {
		LOG.error("AnyDataChannelFromPV.getListeners()");
		return null;
	}

	@Override
	public void start() throws Exception {
		LOG.error("AnyDataChannelFromPV.start()");
	}

	@Override
	public void startSync() throws Exception {
		LOG.error("AnyDataChannelFromPV.startSync()");
	}

	@Override
	public boolean isRunning() {
		LOG.error("AnyDataChannelFromPV.isRunning()");
		return false;
	}

	@Override
	public boolean isConnected() {
		LOG.trace("AnyDataChannelFromPV.isConnected()");
		return _processVariable.getConnectionState() == PVConnectionState.CONNECTED;
	}

	@Override
	public boolean isWriteAllowed() {
		LOG.trace("AnyDataChannelFromPV.isWriteAllowed()");
		return false;
	}

	@Override
	public String getStateInfo() {
		LOG.trace("AnyDataChannelFromPV.getStateInfo()");
		return _processVariable.getConnectionState().name();
	}

	@Override
	public void stop() {
		LOG.error("AnyDataChannelFromPV.stop()");
	}

	@Override
	public AnyData getData() {
		LOG.trace("AnyDataChannelFromPV.getData()");
		return _anyData;
	}

	@Override
	public void setValueAsObject(Object new_value) throws RemoteException {
		LOG.error("AnyDataChannelFromPV.setValueAsObject()");
	}

	@Override
	public DynamicValueProperty<?> getProperty() {
		LOG.trace("AnyDataChannelFromPV.getProperty()");
		return this;
	}

	@Override
	public boolean isMetaDataInitialized() {
		LOG.error("AnyDataChannelFromPV.isMetaDataInitialized()");
		return false;
	}

	/*
	 * AnyDataChannel end
	 */

	/*
	 * Linkable start
	 */
	@Override
	public ConnectionState getConnectionState() {
		LOG.trace("AnyDataChannelFromPV.getConnectionState()");

		PVConnectionState pvConnectionState = _processVariable.getConnectionState();
		switch (pvConnectionState) {
		case INITIAL:
			return ConnectionState.INITIAL;
		case CONNECTED:
			return ConnectionState.CONNECTED;
		case CONNECTION_FAILED:
			return ConnectionState.CONNECTION_FAILED;
		case CONNECTION_LOST:
			return ConnectionState.CONNECTION_LOST;
		case DISCONNECTED:
			return ConnectionState.DISCONNECTED;
		case UNKNOWN:
			return ConnectionState.DISCONNECTED;
		default:
			return ConnectionState.DISCONNECTED;
		}
	}

	/*
	 * Linkable end
	 */

	/*
	 * SimplePorperty<T> start
	 */
	@Override
	public DynamicValueCondition getCondition() {
		// TODO CME: check the return statement.
		LOG.trace("AnyDataChannelFromPV.getCondition()");

		// wird von hasValue(final AnyDataChannel anyDataChannel) in AbstractDesyConnectionBehavior aufgerufen.
		EnumSet<DynamicValueState> states = EnumSet.noneOf(DynamicValueState.class);

		if (_processVariable.hasValue()) {
			states.add(DynamicValueState.HAS_LIVE_DATA);
			states.add(DynamicValueState.NORMAL);
		} else {
			states.add(DynamicValueState.TIMEOUT);
			states.add(DynamicValueState.LINK_NOT_AVAILABLE);
		}

		return new DynamicValueCondition(states);
	}

	/*
	 * SimplePorperty<T> end
	 */

	/*
	 * CharacteristicContext start
	 */
	@Override
	@Nullable
	public Object getCharacteristic(String name) {
		LOG.trace("AnyDataChannelFromPV.getCharacteristic() " + name);

		ProcessVariableAttribute pva = null;

		// TODO CME: type???
		if (CharacteristicInfo.C_ALARM_MAX.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.HIHI);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_ALARM_MIN.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.LOLO);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_WARNING_MAX.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.HI);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_WARNING_MIN.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.LO);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_GRAPH_MAX.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.MAX);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_GRAPH_MIN.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.MIN);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_MAXIMUM.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.MAX);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_MINIMUM.getName().equals(name)) {
			pva = _processVariable.getAttributeByName(PvAttributeNames.MIN);
			return pva != null ? pva.getValue() : null;
		} else if (CharacteristicInfo.C_SEVERITY.getName().equals(name)) {
			return _anyData.getPvSeverityAsLong();
		} else {
			LOG.error("no characteristic for '" + name + "' defined");
			return null;
		}
	}

	/*
	 * CharacteristicContext end
	 */

	private class AnyDataImpl implements AnyData {

		private Severity _severity = new SeverityImpl();

		@Override
		public Timestamp getTimestamp() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.getTimestamp()");
			return new Timestamp(_processVariable.getTimeStamp().getTime(), 0);
		}

		@Override
		public Severity getSeverity() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.getSeverity()");
			return _severity;
		}

		@Override
		public String getStatus() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.getStatus()");
			return null;
		}

		@Override
		public Quality getQuality() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.getQuality()");
			return null;
		}

		@Override
		public MetaData getMetaData() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.getMetaData()");
			return _metaData;
		}

		@Override
		public String stringValue() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.stringValue()");

			if (_processVariable.hasValue()) {
				return _processVariable.getValue().getStringValue();
			}
			LOG.error("no data data available for type String " + _processVariable.getControlSystemAddress());
			return "no Data";
		}
		

		@Override
		public double doubleValue() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.doubleValue()");
			if (_processVariable.hasValue() && _processVariable.getValue().getData() instanceof Double) {
				return (Double) _processVariable.getValue().getData();
			} else {
				LOG.error("no data available for type double " + _processVariable.getControlSystemAddress());
				return 0; // TODO CME: who is calling doubleValue() when the channel is marked that it has no data???
			}
		}

		@Override
		public long longValue() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.longValue()");
			switch (_channelType) {
			case VAL:
				return getPvValueAsLong();
			case SEVR:
				return getPvSeverityAsLong();
			default:
				return getPvValueAsLong();
			}
		}
		
		private long getPvValueAsLong() {
			if (_processVariable.hasValue() && _processVariable.getValue().getData() instanceof Integer) {
				return ((Integer) _processVariable.getValue().getData()).longValue();
			} else {
				LOG.error("no data available for type long " + _processVariable.getControlSystemAddress());
				return 0;
			}
		}
		
		private long getPvSeverityAsLong() {
			if (_processVariable.getSeverityState() != null) {
				PVSeverityState severityState = _processVariable.getSeverityState();
				switch (severityState) {
				case OK:
					return 0;
				case MINOR:
					return 1;
				case MAJOR:
					return 2;
				case INVALID:
					return 3;
				default:
					return 3;
				}				
			} else {
				return 3;
			}
		}

		@Override
		public String[] stringSeqValue() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.stringSeqValue()");
			return null;
		}

		@Override
		public double[] doubleSeqValue() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.doubleSeqValue()");
			return null;
		}

		@Override
		public long[] longSeqValue() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.longSeqValue()");
			return null;
		}

		@Override
		public Number numberValue() {
			//Used when channel type is EpicsEnum.
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.numberValue()");
			
			if (_processVariable.hasValue() && _processVariable.getValue().getData() instanceof Number) {
				return (Number) _processVariable.getValue().getData();
			} else {
				LOG.error("no data available for type Number " + _processVariable.getControlSystemAddress());
				return null;
			}
			
		}

		@Override
		public Number[] numberSeqValue() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.numberSeqValue()");
			return null;
		}

		@Override
		public Object anyValue() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.anyValue()");
			return null;
		}

		@Override
		public Object[] anySeqValue() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.anySeqValue()");
			return null;
		}

		@Override
		public boolean isValid() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.isValid()");
			// Wird vom SinlgePropertyReadConenctor aufgerufen wenn characteristic==null. Wenn true, dann wird der Wert
			// im Bargraph aktualisiert.
			return _processVariable.hasValue();
		}

		@Override
		public AnyDataChannel getParentChannel() {
			LOG.trace("AnyDataChannelFromPV.AnyDataImpl.getParentChannel()");
			return AnyDataChannelFromPV.this;
		}

		@Override
		public DynamicValueProperty<?> getParentProperty() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.getParentProperty()");
			return null;
		}

		@Override
		public long getBeamID() {
			LOG.error("AnyDataChannelFromPV.AnyDataImpl.getBeamID()");
			return 0;
		}
	}

	private class MetaDataImpl implements MetaData {

		@Override
		public double getDisplayLow() {
			LOG.trace("AnyDataChannelFromPV.MetaDataImpl.getDisplayLow()");
			if (_processVariable.hasAttribute(PvAttributeNames.MIN)) {
				return (Double) _processVariable.getAttributeByName(PvAttributeNames.MIN).getValue();
			} else {
				return 0;
			}
		}

		@Override
		public double getDisplayHigh() {
			LOG.trace("AnyDataChannelFromPV.MetaDataImpl.getDisplayHigh()");
			if (_processVariable.hasAttribute(PvAttributeNames.MAX)) {
				return (Double) _processVariable.getAttributeByName(PvAttributeNames.MAX).getValue();
			} else {
				return 0;
			}
		}

		@Override
		public double getWarnLow() {
			LOG.trace("AnyDataChannelFromPV.MetaDataImpl.getWarnLow()");
			if (_processVariable.hasAttribute(PvAttributeNames.LO)) {
				return (Double) _processVariable.getAttributeByName(PvAttributeNames.LO).getValue();
			} else {
				return 0;
			}
		}

		@Override
		public double getWarnHigh() {
			LOG.trace("AnyDataChannelFromPV.MetaDataImpl.getWarnHigh()");
			if (_processVariable.hasAttribute(PvAttributeNames.HI)) {
				return (Double) _processVariable.getAttributeByName(PvAttributeNames.HI).getValue();
			} else {
				return 0;
			}
		}

		@Override
		public double getAlarmLow() {
			LOG.trace("AnyDataChannelFromPV.MetaDataImpl.getAlarmLow()");
			if (_processVariable.hasAttribute(PvAttributeNames.LOLO)) {
				return (Double) _processVariable.getAttributeByName(PvAttributeNames.LOLO).getValue();
			} else {
				return 0;
			}
		}

		@Override
		public double getAlarmHigh() {
			LOG.trace("AnyDataChannelFromPV.MetaDataImpl.getAlarmHigh()");
			if (_processVariable.hasAttribute(PvAttributeNames.MIN)) {
				return (Double) _processVariable.getAttributeByName(PvAttributeNames.HIHI).getValue();
			} else {
				return 0;
			}
		}

		@Override
		public int getPrecision() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getPrecision()");
			return 0;
		}

		@Override
		public String getUnits() {
			LOG.trace("AnyDataChannelFromPV.MetaDataImpl.getUnits()");
			if (_processVariable.hasAttribute(PvAttributeNames.UNIT)) {
				IPlantUnitValue<?> unitValue = _processVariable.getAttributeByName(PvAttributeNames.UNIT).getValueAsObject();
				if (unitValue.getDataType() == PlantUnitDataTypes.STRING) {
					return (String) unitValue.getData();
				}
			}
			return null;
		}

		@Override
		public String[] getStates() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getStates()");
			return null;
		}

		@Override
		public String getState(int index) {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getState()");
			return null;
		}

		@Override
		public Object[] getStateValues() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getStateValues()");
			return null;
		}

		@Override
		public Object getStateValue(int index) {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getStateValue()");
			return null;
		}

		@Override
		public String getFormat() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getFormat()");
			return null;
		}

		@Override
		public AccessType getAccessType() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getAccessType()");
			return AccessType.READ;
		}

		@Override
		public String getHostname() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getHostname()");
			return null;
		}

		@Override
		public String getDataType() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getDataType()");
			return null;
		}

		@Override
		public String getDescription() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getDescription()");
			return null;
		}

		@Override
		public String getName() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getName()");
			return null;
		}

		@Override
		public int getSequenceLength() {
			LOG.error("AnyDataChannelFromPV.MetaDataImpl.getSequenceLength()");
			return 0;
		}
	}

	private class SeverityImpl implements Severity {

		@Override
		public String getSeverityInfo() {
			LOG.error("AnyDataChannelFromPV.SeverityImpl.getSeverityInfo()");
			return null;
		}

		@Override
		public String descriptionToString() {
			LOG.trace("AnyDataChannelFromPV.SeverityImpl.descriptionToString()");
			return _processVariable.getSeverityState().name();
		}

		@Override
		public boolean isOK() {
			LOG.trace("AnyDataChannelFromPV.SeverityImpl.isOK()");
			return _processVariable.getSeverityState() == PVSeverityState.OK;
		}

		@Override
		public boolean isMinor() {
			LOG.trace("AnyDataChannelFromPV.SeverityImpl.isMinor()");
			return _processVariable.getSeverityState() == PVSeverityState.MINOR;
		}

		@Override
		public boolean isMajor() {
			LOG.trace("AnyDataChannelFromPV.SeverityImpl.isMajor()");
			return _processVariable.getSeverityState() == PVSeverityState.MAJOR;
		}

		@Override
		public boolean isInvalid() {
			LOG.trace("AnyDataChannelFromPV.SeverityImpl.isInvalid()");
			return _processVariable.getSeverityState() == PVSeverityState.INVALID;
		}

		@Override
		public boolean hasValue() {
			LOG.trace("AnyDataChannelFromPV.SeverityImpl.hasValue()");
			return _processVariable.getSeverityState() != null;
		}

	}
}
