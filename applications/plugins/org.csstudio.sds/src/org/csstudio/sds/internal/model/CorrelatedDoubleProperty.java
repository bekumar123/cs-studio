package org.csstudio.sds.internal.model;

import org.csstudio.sds.model.CorrelationChecker;
import org.csstudio.sds.model.WidgetPropertyCategory;

public class CorrelatedDoubleProperty extends DoubleProperty {

	private final CorrelationChecker<Double> correlationChecker;
	private CorrelatedDoubleProperty otherProperty;
	
	public CorrelatedDoubleProperty(String shortDescription,
			WidgetPropertyCategory category,
			double defaultValue, double min, double max, CorrelationChecker<Double> correlationChecker) {
		super(shortDescription, category, defaultValue, min, max);
		this.correlationChecker = correlationChecker;
	}

	public void setOtherProperty(CorrelatedDoubleProperty otherProperty) {
		this.otherProperty = otherProperty;
	}
	
	@Override
	public Object checkValue(Object value) {
		Object checkValue = super.checkValue(value);
		
		if(checkValue != null && otherProperty != null) {
			// only allow value changes if property values are correlated
			if(!correlationChecker.checkValues((Double)value, (Double)otherProperty.getPropertyValue())) {
				checkValue = null;
			}
		}
		
		return checkValue;
	}
	
}
