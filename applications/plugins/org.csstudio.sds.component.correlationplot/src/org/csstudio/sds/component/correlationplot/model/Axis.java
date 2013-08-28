package org.csstudio.sds.component.correlationplot.model;

import java.util.ArrayList;
import java.util.List;


public class Axis {

	private String name;
	private double minValue;
	private double maxValue;
	
	private int mappingMinValue;
	private int mappingMaxValue;

	public Axis(String name, double minValue, double maxValue) {
		if (minValue == maxValue) {
			throw new IllegalArgumentException("Min value: " + minValue + " can not be the same as Max value: " + maxValue);
		}
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.mappingMinValue = 0;
		this.mappingMaxValue = 0;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isValid() {
		return getMinValue() != getMaxValue();
	}
	
	public double getMinValue() {
		return minValue;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	
	public void setMappingMinValue(int mappingMinValue) {
		this.mappingMinValue = mappingMinValue;
	}
	
	public int getMappingMinValue() {
		return mappingMinValue;
	}
	
	public void setMappingMaxValue(int mappingMaxValue) {
		this.mappingMaxValue = mappingMaxValue;
	}
	
	public int getMappingMaxValue() {
		return mappingMaxValue;
	}
	
	@Override
	public String toString() {
		return name + " from " + getMinValue() + " to " + getMaxValue();
	}

	public double getMappingValueFor(double value) throws IllegalStateException {
		if(!isValid()) {
			throw new IllegalStateException("Axis range is not valid! Min value: " + getMinValue() + ", Max value: " + getMaxValue());
		}
		
		double domainWidth = getMaxValue() - getMinValue();
		double relativeValue = (value - getMinValue()) / domainWidth;
		
		double mappingWidth = mappingMaxValue - mappingMinValue;

		double relativeMappedValue = relativeValue * mappingWidth;
		return relativeMappedValue + mappingMinValue;
	}
	
	public double[] getStepsForResolution(int resolution) {
		double[] result = new double[resolution];
		double stepValue = (getMaxValue() - getMinValue()) / resolution;
		for(int i = 0; i < resolution; i++) {
			result[i] = getMinValue() + i * stepValue;
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		Axis axis = new Axis("test", 0, 100);
		axis.setMappingMinValue(0);
		axis.setMappingMaxValue(10);
		
		for(int index = (int) axis.getMinValue(); index <= axis.getMaxValue(); index++)
		System.out.println("Mapping for " + index + ": " + axis.getMappingValueFor(index));
	}

	public void setMinValue(double minValue) {
		if (minValue == getMaxValue()) {
			throw new IllegalArgumentException("Min value: " + minValue + " can not be the same as Max value: " + getMaxValue());
		}
		this.minValue = minValue;
	}

	public void setMaxValue(double maxValue) {
		if (maxValue == getMinValue()) {
			throw new IllegalArgumentException("Max value: " + maxValue + " can not be the same as Min value: " + getMinValue());
		}
		this.maxValue = maxValue;
	}

	public List<Double> getScaleValues() {
		List<Double> result = new ArrayList<Double>();
		
		int numberOfValues = Math.abs((getMappingMaxValue() - getMappingMinValue()) / 40);
		double step = Math.abs((getMaxValue() - getMinValue()) / numberOfValues);
		double niceStep = 1;
		if (step < 0.05) 		niceStep = Math.round(step*1000.)/1000.;
		else if (step < 0.1)	niceStep = 0.1;
		else if (step < 0.2)	niceStep = 0.2;
		else if (step < 0.25)	niceStep = 0.25;
		else if (step < 0.5)	niceStep = 0.5;
		else if (step < 1)		niceStep = 1;
		else if (step < 2)		niceStep = 2;
		else if (step < 2.5)	niceStep = 2.5;
		else if (step < 5)		niceStep = 5;
		else if (step < 10)		niceStep = 10;
		else if (step < 25)		niceStep = 25;
		else if (step < 50)		niceStep = 50;
		else if (step < 100)	niceStep = 100;
		else if (step < 250)	niceStep = 250;
		else if (step < 500)	niceStep = 500;
		else if (step < 1000)	niceStep = 1000;
		else 					niceStep = Math.round(step);
		
		double smallerValue = Math.min(getMinValue(), getMaxValue());
		double largerValue = Math.max(getMinValue(), getMaxValue());
		double index = smallerValue + Math.abs(smallerValue % niceStep);
		result.add(getMinValue());
		while (largerValue - index > -0.001) {
			if (Math.abs(getMinValue() - index) > niceStep/2) {
				result.add(index);
			}
			index += niceStep;
		}
		return result;
	}
}
