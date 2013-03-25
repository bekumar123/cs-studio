package org.csstudio.sds.model;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;

public class PlantUnitReferenceProperty extends WidgetProperty {

	public PlantUnitReferenceProperty(String description,
			WidgetPropertyCategory category,
			PlantUnitReferenceContainer defaultValue) {
		super(PropertyTypesEnum.PLANT_UNIT_REFERENCE, description,
				"Contains the referenced PlantUnit in a plant model", category,
				defaultValue, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getCompatibleJavaTypes() {
		return new Class[] { PlantUnitReferenceContainer.class };
	}

	@Override
	public Object checkValue(Object value) {
		if (value instanceof PlantUnitReferenceContainer) {
			return value;
		}
		return null;
	}

}
