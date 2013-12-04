package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PVConsistencyState;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;

public class ProcessVariable implements IPlantUnit, IPVAttributeContainer, Serializable {

	private static final long serialVersionUID = 1456163220075693243L;

	private PlantUnitId id;

	private String displayName;
	private String description;

	private ProcessVariableType type;

	private String referenceCode;

	private String address;

	private PVConsistencyState pvConsistencyState;

	private PVConnectionState connectionState;

	private PVSeverityState severityState;
	
	private PVAlarmStatus alarmStatus;
	
	private List<ProcessVariableAttribute> attributes;

	private Date timeStamp;

	private IPlantUnitValue<?> value;

	public ProcessVariable(PlantUnitId id, String displayName) {
		assert id != null : "id != null";
		assert displayName != null : "displayName != null";
		assert displayName.trim().length() > 0 : "displayName.trim().length() > 0";

		this.id = id;
		this.displayName = displayName;
		this.attributes = new ArrayList<ProcessVariableAttribute>();
		this.pvConsistencyState = PVConsistencyState.CONSISTENT;
		this.description = "";
		this.address = "";
		this.type = ProcessVariableType.NULL;
		this.referenceCode = "";
		this.connectionState = PVConnectionState.UNKNOWN;
		this.severityState = PVSeverityState.INVALID;
	}

	public ProcessVariable(PlantUnitId id, String displayName, ProcessVariableType type) {
		assert id != null : "id != null";
		assert displayName != null : "displayName != null";
		assert displayName.trim().length() > 0 : "displayName.trim().length() > 0";
		assert type != null : "type != null";

		this.id = id;
		this.displayName = displayName;
		this.attributes = new ArrayList<ProcessVariableAttribute>();
		this.pvConsistencyState = PVConsistencyState.CONSISTENT;
		this.description = "";
		this.address = "";
		this.type = type;
		this.referenceCode = "";
		this.connectionState = PVConnectionState.UNKNOWN;
		this.severityState = PVSeverityState.INVALID;
	}

	public ProcessVariable(ProcessVariableTemplate template) {
		assert template != null : "template != null";

		this.id = new PlantUnitId();
		this.displayName = template.getDisplayName();
		this.attributes = new ArrayList<ProcessVariableAttribute>();
		this.pvConsistencyState = PVConsistencyState.CONSISTENT;
		this.description = "";
		this.address = "";
		this.type = template.getType();
		setAttributes(template.getType().getAttributes());
		this.referenceCode = "";
		this.connectionState = PVConnectionState.UNKNOWN;
		this.severityState = PVSeverityState.INVALID;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProcessVariableType getType() {
		return type;
	}

	public void setType(ProcessVariableType type) {
		this.type = type;
	}

	public boolean hasType() {
		return getType() != null;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		assert referenceCode != null : "referenceCode != null";

		this.referenceCode = referenceCode;
	}

	public String getControlSystemAddress() {
		return address;
	}

	public void setControlSystemAddress(String address) {
		this.address = address;
	}

	public PVConsistencyState getConsistencyState() {
		return pvConsistencyState;
	}

	public void setConsistencyState(PVConsistencyState pVConsistencyState) {
		this.pvConsistencyState = pVConsistencyState;
	}

	public void setConnectionState(PVConnectionState connectionState) {
		assert connectionState != null : "assert  connectionState != null";

		this.connectionState = connectionState;
	}

	public PVConnectionState getConnectionState() {
		return connectionState;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public boolean hasAttributes() {
		return !getAttributes().isEmpty();
	}

	public boolean hasValue() {
		return value != null;
	}

	public boolean hasAddressPart(String addressPart) {
		return getControlSystemAddress().startsWith(addressPart);
	}

	public boolean checkDuplicate(ProcessVariable otherPv) {
		boolean isDuplicate = false;
		if (getId() != otherPv.getId() // PV mit gleicher Id ist kein Duplikat,
										// sondern Identität
				&& this.getControlSystemAddress().equalsIgnoreCase(otherPv.getControlSystemAddress())) {
			isDuplicate = true;
		}

		if (isDuplicate) {
			this.pvConsistencyState = PVConsistencyState.INCONSISTENT;
		} else {
			this.pvConsistencyState = PVConsistencyState.CONSISTENT;
		}

		return isDuplicate;
	}

	@Override
	public ProcessVariable copyDeep() {
		//CME: copyDeep is broken!
		
		ProcessVariable copy = null;
		if (hasType()) {
			copy = new ProcessVariable(this.id, this.displayName, this.type);
		} else {
			copy = new ProcessVariable(this.id, this.displayName);
		}

		if (hasValue()) {
			copy.setValue(getValue());
		}
		copy.setControlSystemAddress(this.address);
		copy.setConsistencyState(this.pvConsistencyState);
		copy.setDescription(this.description);
		copy.setReferenceCode(getReferenceCode());
		for (ProcessVariableAttribute attribute : this.attributes) {
			copy.addAttribute(attribute.copyDeep());
		}
		return copy;
	}

	@Override
	public PlantUnitId getId() {
		return id;
	}

	@Override
	public void update(IPlantUnit unit) {
		assert getId().equals(unit.getId()) : "getId().equals(unit.getId())";
		assert unit instanceof ProcessVariable : "unit instanceof ProcessVariable";

		ProcessVariable updatedProcessVariable = (ProcessVariable) unit;

		if (updatedProcessVariable.hasValue()) {
			setValue(updatedProcessVariable.getValue());
		}
		setDisplayName(updatedProcessVariable.getDisplayName());
		setControlSystemAddress(updatedProcessVariable.getControlSystemAddress());
		setType(updatedProcessVariable.getType());
		setReferenceCode(updatedProcessVariable.getReferenceCode());
		setConsistencyState(updatedProcessVariable.getConsistencyState());
		setDescription(updatedProcessVariable.getDescription());
		setAttributes(updatedProcessVariable.getAttributes());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		ProcessVariable other = (ProcessVariable) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;

		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;

		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;

		if (referenceCode == null) {
			if (other.referenceCode != null)
				return false;
		} else if (!referenceCode.equals(other.referenceCode))
			return false;

		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;

		if (pvConsistencyState != other.pvConsistencyState)
			return false;
		
		if (connectionState != other.connectionState)
			return false;
		
		if (severityState != other.severityState)
			return false;

		if (value == null) {
			if (other.hasValue())
				return false;
		} else if (!getValue().equals(other.getValue())) {
			return false;
		}

		return true;
	}

	@Override
	public ProcessVariable createClearCopy() {
		ProcessVariable clearCopy = new ProcessVariable(new PlantUnitId(), getDisplayName(), getType());
		clearCopy.setAttributes(getAttributes());
		clearCopy.setDescription(getDescription());

		return clearCopy;
	}

	@Override
	public void addAttribute(ProcessVariableAttribute attribute) {
		assert attribute != null : "attribute != null";

		attributes.add(attribute);
	}

	public void addAttributes(List<ProcessVariableAttribute> attributes) {
		assert attributes != null : "assert  attributes != null";

		this.attributes.addAll(attributes);
	}

	@Override
	public void removeAttribute(ProcessVariableAttribute pvAttribute) {
		attributes.remove(pvAttribute);

	}

	@Override
	public List<ProcessVariableAttribute> getAttributes() {
		return new ArrayList<ProcessVariableAttribute>(attributes);
	}

	private void setAttributes(List<ProcessVariableAttribute> newAttributes) {
		this.attributes.clear();
		this.attributes.addAll(newAttributes);
	}

	@Override
	public String toString() {
		String type = "Type: ";
		if (hasType()) {
			type += getType().getDisplayName();
		}
		return "DisplayName=" + getDisplayName() + type + ", Address=" + getControlSystemAddress() + ", PVConsistencyState="
				+ getConsistencyState();
	}

	@Override
	public ProcessVariableAttribute getAttributeByName(PvAttributeNames attributeName) {
		for (ProcessVariableAttribute attribute : attributes) {
			if (attribute.getName() == attributeName) {
				return attribute;
			}
		}
		return null;
	}

	public boolean hasAttribute(PvAttributeNames name) {
		return getAttributeByName(name) != null;
	}

	/**
	 * @require {@link #hasValue()}
	 * @return
	 */
	public IPlantUnitValue<?> getValue() {
		assert hasValue() : "assert hasValue()";
		return value;
	}

	public void setValue(IPlantUnitValue<?> value) {
		assert value != null : "assert  value != null";

		this.value = value;
	}

	public void setSeverityState(PVSeverityState severityState) {
		this.severityState = severityState;
	}

	public PVSeverityState getSeverityState() {
		return severityState;
	}

	public void setAlarmStatus(PVAlarmStatus pvAlarmState) {
		this.alarmStatus = pvAlarmState;
	}
	
	public PVAlarmStatus getAlarmStatus() {
		return alarmStatus;
	}
}
