package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.BasePlantUnitValue;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;

public class ProcessVariableAttribute implements IPlantUnit, Serializable {

	private static final long serialVersionUID = 1364462177894195286L;
	private final PlantUnitId id;
	private String description;
	private PvAttributeNames name = PvAttributeNames.UNDEFINED;

	private IPlantUnitValue<?> attributeValue;

	private ProcessVariableAttribute(PlantUnitId id, String description,
			IPlantUnitValue<?> attributeValue) {
		assert attributeValue != null : "assert  attributeValue != null";

		this.id = id;
		this.description = description;
		this.attributeValue = attributeValue;
		name = PvAttributeNames.UNDEFINED;
	}
	
	public ProcessVariableAttribute(PlantUnitId id, PvAttributeNames name, String description,
			IPlantUnitValue<?> attributeValue) {
		this(id, description, attributeValue);
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public ProcessVariableAttribute copyDeep() {
		ProcessVariableAttribute pvAttributeCopy = new ProcessVariableAttribute(
				id, name, description, attributeValue);
		return pvAttributeCopy;
	}

	public void setDescription(String newDescription) {
		description = newDescription;
	}

	@Override
	public String toString() {
		return "PVAttribute [name=" + getName() + ", value="
				+ getValue() + ", type=" + getDataType() + "]";
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
		ProcessVariableAttribute other = (ProcessVariableAttribute) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		if (attributeValue == null) {
			if (other.getValue() != null)
				return false;
		} else if (!attributeValue.equals(other.attributeValue))
			return false;
		return true;
	}

	public PlantUnitDataTypes getDataType() {
		return attributeValue.getDataType();
	}

	@Override
	public String getDisplayName() {
		return getName().toString();
	}

	public PvAttributeNames getName() {
		return name;
	}

	@Override
	public void setDisplayName(String displayName) {
		PvAttributeNames name = PvAttributeNames.getByName(displayName);
		assert name != null : "assert  name != null";

		this.name = name;
	}
	
	

	@Override
	public PlantUnitId getId() {
		return id;
	}

	@Override
	public void update(IPlantUnit unit) {
		throw new UnsupportedOperationException(
				"Update noch nicht implementiert");
	}

	@Override
	public IPlantUnit createClearCopy() {
		throw new UnsupportedOperationException(
				"Create clearCopy noch nicht implementiert");
	}

	public boolean isValid(Object value) {
		return attributeValue.isValid(value);
	}

	public boolean isWritable() {
		return attributeValue.isWritable();
	}

	public void setDataType(PlantUnitDataTypes dataType) {
		IPlantUnitValue<?> newValue = BasePlantUnitValue.createValue(dataType);
		newValue.setWritable(isWritable());

		this.attributeValue = newValue;
	}

	/**
	 * 
	 * @return
	 */
	public Object getValue() {
		return attributeValue.getData();
	}

	/**
	 * Liefert den Wert als ein Objekt vom z.B. DoubleValue, IntegerValue.
	 * 
	 * @return
	 */
	public IPlantUnitValue<?> getValueAsObject() {
		return attributeValue;
	}

	public boolean hasValue() {
		return attributeValue.getData() != null ? true : false;
	}

	/**
	 * @param value
	 *            der aktuelle Wert des Attributes
	 */
	public void setValue(Object value) {
		assert value != null : "assert  value != null";
		this.attributeValue.setData(value);
	}

	public boolean canSetValue(Object value) {
		assert value != null : "assert  value != null";
		return this.attributeValue.isValid(value);
	}
}
