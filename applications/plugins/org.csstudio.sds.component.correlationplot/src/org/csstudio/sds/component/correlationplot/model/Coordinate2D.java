package org.csstudio.sds.component.correlationplot.model;

import java.math.BigDecimal;
import java.math.MathContext;


public class Coordinate2D extends Tuple<BigDecimal, BigDecimal>{

	public Coordinate2D(BigDecimal xValue, BigDecimal yValue) {
		super(xValue, yValue);
	}
	
	public Coordinate2D(double xValue, double yValue) {
		this(new BigDecimal(xValue, MathContext.DECIMAL128),new BigDecimal(yValue, MathContext.DECIMAL128));
	}
	
	public BigDecimal getX() {
		return getP1();
	}
		
	public BigDecimal getY() {
		return getP2();
	}
	
	public Coordinate2D rotate(double angle) {
		double newX = Math.cos(angle) * getX().doubleValue() - Math.sin(angle) * getY().doubleValue();
		double newY = Math.sin(angle) * getX().doubleValue() + Math.cos(angle) * getY().doubleValue();
		
		return new Coordinate2D(newX, newY);
	}
	
	@Override
	public String toString() {
		return "x: " + getX() + " y: " + getY();
	}
}