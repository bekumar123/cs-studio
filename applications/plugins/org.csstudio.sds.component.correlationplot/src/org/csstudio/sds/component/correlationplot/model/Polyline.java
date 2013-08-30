package org.csstudio.sds.component.correlationplot.model;

import java.util.Arrays;

public class Polyline {
	private Coordinate2D[] coordinates;

	// Punkte
	// Linienfarbe
	// Liniendicke
	// Linientyp
	public Polyline(Coordinate2D ... coordinates) {
		this.coordinates = coordinates;
	}
	
	public Coordinate2D[] getCoordinates() {
		return coordinates;
	}
	
	public double[] toArray() {
		double[] result = new double[coordinates.length * 2];
		
		for (int pointIndex = 0; pointIndex < coordinates.length; pointIndex++) {
			result[pointIndex*2] = coordinates[pointIndex].getX();
			result[pointIndex*2 + 1] = coordinates[pointIndex].getY();
		}
		
		return result;
	}
	
	public boolean containsPoint(Coordinate2D testPoint) {
		boolean result = false;

		for (int indexLineEnd = 0, indexLineStart = coordinates.length - 1; indexLineEnd < coordinates.length; indexLineEnd++) {
			if (((coordinates[indexLineEnd].getY() > testPoint.getY()) != (coordinates[indexLineStart].getY() > testPoint.getY()))
					&& (testPoint.getX() < (coordinates[indexLineStart].getX() - coordinates[indexLineEnd].getX()) * (testPoint.getY() - coordinates[indexLineEnd].getY())
							/ (coordinates[indexLineStart].getY() - coordinates[indexLineEnd].getY()) + coordinates[indexLineEnd].getX())) {
				
				result = !result;
			}
			indexLineStart = indexLineEnd;
		}

		return result;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(coordinates);
	}
}
