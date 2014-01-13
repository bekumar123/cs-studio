package org.csstudio.sds.component.correlationplot.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class Axis {

	private String name;
	private BigDecimal minValue;
	private BigDecimal maxValue;
	
	private int mappingMinValue;
	private int mappingMaxValue;
	private int scaleValueWidth;

	public Axis(String name, double minValue, double maxValue) {
		if (minValue == maxValue) {
			throw new IllegalArgumentException("Min value: " + minValue + " can not be the same as Max value: " + maxValue);
		}
		this.name = name;
		
		this.minValue = new BigDecimal(minValue, MathContext.DECIMAL128);
		this.maxValue = new BigDecimal(maxValue, MathContext.DECIMAL128);
		this.mappingMinValue = 0;
		this.mappingMaxValue = 0;
		scaleValueWidth = 40;
	}

	public void setScaleValueWidth(int scaleValueWidth) {
		this.scaleValueWidth = scaleValueWidth;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isValid() {
		return !getMinValue().equals(getMaxValue());
	}
	
	public BigDecimal getMinValue() {
		return minValue;
	}
	
	public BigDecimal getMaxValue() {
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

	public int getMappingValueFor(BigDecimal value) throws IllegalStateException {
		if(!isValid()) {
			throw new IllegalStateException("Axis range is not valid! Min value: " + getMinValue() + ", Max value: " + getMaxValue());
		}
		
		BigDecimal domainWidth = getMaxValue().subtract(getMinValue());
		BigDecimal relativeValue = value.subtract(getMinValue()).divide(domainWidth, 20, RoundingMode.HALF_UP);
		
		BigDecimal mappingWidth = new BigDecimal(mappingMaxValue - mappingMinValue, MathContext.DECIMAL128);

		BigDecimal relativeMappedValue = relativeValue.multiply(mappingWidth).setScale(0, RoundingMode.HALF_UP).add(new BigDecimal(mappingMinValue));
		return getValidIntValue(relativeMappedValue);
	}
	
	private int getValidIntValue(BigDecimal bigDecimal) {
		if(bigDecimal.compareTo(new BigDecimal(Integer.MAX_VALUE - 10)) > 0) {
			return Integer.MAX_VALUE - 10;
		} else if(bigDecimal.compareTo(new BigDecimal(Integer.MIN_VALUE + 10)) < 0) {
			return Integer.MIN_VALUE + 10;
		} else {
			return bigDecimal.intValue();
		}
	}
	
	public BigDecimal[] getStepsForResolution(int resolution) {
		BigDecimal[] result = new BigDecimal[resolution];

		BigDecimal resolutionDecimal = new BigDecimal(resolution);
		
		BigDecimal stepValue = getMaxValue().subtract(getMinValue()).divide(resolutionDecimal, 20, RoundingMode.HALF_UP);
		for(int i = 0; i < resolution; i++) {
			BigDecimal iDecimal = new BigDecimal(i);
			result[i] = getMinValue().add(iDecimal.multiply(stepValue));
		}
		
		return result;
	}
	
	public void setMinValue(double minValue) {
		setMinValue(new BigDecimal(minValue, MathContext.DECIMAL128));
	}

	public void setMinValue(BigDecimal minValue) {
		if (minValue.equals(getMaxValue())) {
			throw new IllegalArgumentException("Min value: " + minValue + " can not be the same as Max value: " + getMaxValue());
		}
		this.minValue = minValue;
	}
	
	public void setMaxValue(double maxValue) {
		setMaxValue(new BigDecimal(maxValue, MathContext.DECIMAL128));
	}

	public void setMaxValue(BigDecimal maxValue) {
		if (maxValue.equals(getMinValue())) {
			throw new IllegalArgumentException("Max value: " + maxValue + " can not be the same as Min value: " + getMinValue());
		}
		this.maxValue = maxValue;
	}

	public List<Double> getScaleValues() {
		List<Double> result = new ArrayList<Double>();
		// minvalue is always a scale value
		result.add(getMinValue().doubleValue());

		int numberOfValues = Math
				.abs((getMappingMaxValue() - getMappingMinValue()) / scaleValueWidth);
		if (numberOfValues > 0) {
			double step = getMaxValue().subtract(getMinValue()).abs().doubleValue() / numberOfValues;
			double niceStep = 1;

			int exponent = 0;
			boolean niceStepFound = false;
			while (!niceStepFound) {
				double factor = Math.pow(10, exponent);

				if (step < 5 * factor) {
					exponent -= 1;
				} else if (step < 10 * factor) {
					niceStep = 10 * factor;
					niceStepFound = true;
				} else if (step < 20 * factor) {
					niceStep = 20 * factor;
					niceStepFound = true;
				} else if (step < 25 * factor) {
					niceStep = 25 * factor;
					niceStepFound = true;
				} else if (step < 50 * factor) {
					niceStep = 50 * factor;
					niceStepFound = true;
				} else {
					exponent += 1;
				}
			}
			double smallerValue = getMinValue().min(getMaxValue()).doubleValue();
			double largerValue = getMinValue().max(getMaxValue()).doubleValue();
			double index = smallerValue - Math.abs(smallerValue % niceStep) + niceStep;

			while (largerValue - index > -0.001) {
				if (Math.abs(getMinValue().doubleValue() - index) >= niceStep / 3) {
					result.add(index);
				}
				index += niceStep;
			}
		}
		return result;
	}

	public int getMappingRange() {
		return Math.abs(getMappingMaxValue()-getMappingMinValue());
	}
}
