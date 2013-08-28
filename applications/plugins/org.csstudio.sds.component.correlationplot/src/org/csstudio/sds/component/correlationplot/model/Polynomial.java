package org.csstudio.sds.component.correlationplot.model;


public class Polynomial {
	private double[] coefficients;

	// Polynom
	// Linienfarbe
	// Liniendicke
	// Linientyp
	
	public Polynomial(double ... coefficients) {
		this.coefficients = coefficients;
	}
	
	public double[] getCoefficients() {
		return coefficients;
	}
	
	public double getValueForX(double x) {
		double result = 0;
		
		for (int polyIndex = 0; polyIndex < coefficients.length; polyIndex++) {
			double xFactor = coefficients[polyIndex];
			result += xFactor * Math.pow(x, polyIndex);
		}
		
		return result;
	}
	
	public boolean isDrawable() {
		return coefficients != null && coefficients.length > 0;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (int i = coefficients.length - 1; i >= 0; i--) {
			if (coefficients[i] > 0) {
				buf.append(coefficients[i]);
				if (i > 0) {
					buf.append("x");
				}
				if (i > 1) {
					buf.append("^");
					buf.append(i);
				}
				if (i > 0) {
					buf.append(" + ");
				}
			}
		}
		return buf.toString();
	}
}
