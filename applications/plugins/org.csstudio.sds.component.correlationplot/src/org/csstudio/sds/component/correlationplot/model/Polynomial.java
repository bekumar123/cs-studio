package org.csstudio.sds.component.correlationplot.model;

import java.math.BigDecimal;
import java.math.MathContext;


public class Polynomial {
	private BigDecimal[] coefficients;

	// Polynom
	// Linienfarbe
	// Liniendicke
	// Linientyp
	
	public Polynomial(double ... coefficients) {
		this.coefficients = new BigDecimal[coefficients.length];
		for (int index = 0; index < coefficients.length; index++) {
			this.coefficients[index] = new BigDecimal(coefficients[index], MathContext.DECIMAL128);
		}
	}
	
	public BigDecimal getValueForX(BigDecimal x) {
		BigDecimal result = new BigDecimal(0, MathContext.DECIMAL128);
		
		for (int polyIndex = 0; polyIndex < coefficients.length; polyIndex++) {
			BigDecimal xFactor = coefficients[polyIndex];
			result = result.add(x.pow(polyIndex).multiply(xFactor));
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
			if (coefficients[i].doubleValue() > 0) {
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
