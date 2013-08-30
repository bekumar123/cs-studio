package org.csstudio.sds.component.correlationplot.model;

public class PlotValue extends Coordinate2D {
	private boolean hasAlarm;
	
	public enum Type {NORMAL, TIME_WARNING_1, TIME_WARNING_2}
	
	private Type type;
	
	public PlotValue(double xValue, double yValue, Type type) {
		super(xValue, yValue);
		this.type = type;
		this.hasAlarm = false;
	}
	
	public void setAlarm(boolean hasAlarm) {
		this.hasAlarm = hasAlarm;
	}
	
	public boolean hasAlarm() {
		return hasAlarm;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type shapeType) {
		this.type = shapeType;
	}
}