package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;

public class ProcessVariableType implements IPlantUnitType, IPVAttributeContainer,
		Serializable {

	private static final long serialVersionUID = -7236468215006727703L;
	private String displayName;
	private final PlantUnitId id;
	private String description;
	private IPlantUnitValue<?> value; //TODO CME: after discussion with E. Reisweich. This field should be removed. 

	private List<ProcessVariableAttribute> attributes;

	public static final ProcessVariableType NULL = new ProcessVariableTypeNull();

	public ProcessVariableType(PlantUnitId id, String displayName) {
		assert id != null : "id != null";
		assert displayName != null : "displayName != null";

		this.id = id;
		this.displayName = displayName;
		this.description = "";
		this.attributes = new ArrayList<ProcessVariableAttribute>();
	}

	@Override
	public PlantUnitId getId() {
		return id;
	}

	public IPlantUnitValue<?> getValue() {
		return value;
	}

	public boolean hasValue() {
		return getValue() != null;
	}

	public void setValue(IPlantUnitValue<?> value) {
		assert value != null : "assert  value != null";
		this.value = value;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		assert displayName != null : "newDisplayName != null";

		this.displayName = displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		assert description != null : "newDescription != null";

		this.description = description;
	}

	public void setAttributes(List<ProcessVariableAttribute> attributes) {
		assert attributes != null : "assert  attributes != null";

		this.attributes.clear();
		this.attributes.addAll(attributes);
	}

	@Override
	public void addAttribute(ProcessVariableAttribute attribute) {
		assert attribute != null : "assert  attribute != null";

		this.attributes.add(attribute);
	}

	@Override
	public void removeAttribute(ProcessVariableAttribute pvAttribute) {
		assert pvAttribute != null : "assert  pvAttribute != null";

		this.attributes.remove(pvAttribute);

	}

	@Override
	public List<ProcessVariableAttribute> getAttributes() {
		return new ArrayList<ProcessVariableAttribute>(attributes);
	}

	@Override
	public void update(IPlantUnit unit) {
		assert unit != null : "unit != null";
		assert unit instanceof ProcessVariableType : "unit instanceof ProcessVariableType";

		ProcessVariableType processVariableType = (ProcessVariableType) unit;

		setValue(processVariableType.getValue());
		setDisplayName(processVariableType.getDisplayName());
		setDescription(processVariableType.getDescription());
		setAttributes(processVariableType.getAttributes());
	}

	@Override
	public ProcessVariableType copyDeep() {
		ProcessVariableType copy = new ProcessVariableType(getId(),
				getDisplayName());
		copy.setDescription(getDescription());

		if (hasValue())
			copy.setValue(getValue());

		for (ProcessVariableAttribute attribute : getAttributes()) {
			copy.addAttribute(attribute.copyDeep());
		}
		return copy;
	}

	@Override
	public ProcessVariableType createClearCopy() {
		ProcessVariableType clearCopy = new ProcessVariableType(
				new PlantUnitId(), getDisplayName());
		clearCopy.setDescription(getDescription());

		return clearCopy;
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

		ProcessVariableType other = (ProcessVariableType) obj;

		if (getValue() == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (!getValue().equals(other.getValue())) {
			return false;
		}

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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

		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DisplayName=" + getDisplayName();
	}

	/**
	 * NullPattern for ProcessVariableType
	 * 
	 * @author reiswich
	 * 
	 */
	private static class ProcessVariableTypeNull extends ProcessVariableType
			implements Serializable {

		private static final long serialVersionUID = -5146498288520283172L;

		public ProcessVariableTypeNull() {
			super(new PlantUnitId(), "undefined");
		}

		@Override
		public void setDisplayName(String newDisplayName) {
			// do nothing
		}

		@Override
		public void setDescription(String newDescription) {
			// do nothing
		}

		@Override
		public void addAttribute(ProcessVariableAttribute attribute) {
			// do nothing
		}

		@Override
		public void removeAttribute(ProcessVariableAttribute pvAttribute) {
			// do nothing
		}

		@Override
		public void setAttributes(List<ProcessVariableAttribute> attributes) {
			// do nothing
		}

		@Override
		public void update(IPlantUnit unit) {
			// do nothing
		}

		@Override
		public ProcessVariableType createClearCopy() {
			return new ProcessVariableTypeNull();
		}

		@Override
		public ProcessVariableType copyDeep() {
			return new ProcessVariableTypeNull();
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}

			return o.getClass().equals(this.getClass());
		}
	}

	@Override
	public ProcessVariableAttribute getAttributeByName(
			PvAttributeNames attributeName) {
		for (ProcessVariableAttribute attribute : attributes) {
			if (attribute.getDisplayName().equals(attributeName.name())) {
				return attribute;
			}
		}
		return null;
	}

	@Override
	public boolean hasAttribute(PvAttributeNames attributeName) {
		return getAttributeByName(attributeName) != null;
	}
}
