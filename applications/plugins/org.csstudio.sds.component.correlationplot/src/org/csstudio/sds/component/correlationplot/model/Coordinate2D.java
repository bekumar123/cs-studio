package org.csstudio.sds.component.correlationplot.model;


public class Coordinate2D extends Tuple<Double, Double>{

	public Coordinate2D(double xValue, double yValue) {
		super(xValue, yValue);
	}
	
	public double getX() {
		return getP1();
	}
		
	public double getY() {
		return getP2();
	}
	
	@Override
	public String toString() {
		return "x: " + getX() + " y: " + getY();
	}
}