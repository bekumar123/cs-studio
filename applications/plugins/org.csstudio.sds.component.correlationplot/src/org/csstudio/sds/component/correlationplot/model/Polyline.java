package org.csstudio.sds.component.correlationplot.model;

import java.util.Arrays;

public class Polyline {
	
	public static final Polyline EMPTY_POLYLINE = new Polyline();
	
	private Coordinate2D[] coordinates;

	// Punkte
	// Linienfarbe
	// Liniendicke
	// Linientyp
	public Polyline(Coordinate2D ... coordinates) {
		this.coordinates = coordinates;
	}
	
	public boolean isEmpty() {
		return coordinates.length < 2;
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

	public Polyline getClosed() {
		Coordinate2D[] resultArray;
		if (coordinates.length > 0 && coordinates[coordinates.length-1] != coordinates[0]) {
			resultArray = new Coordinate2D[coordinates.length+1];
			System.arraycopy(coordinates, 0, resultArray, 0, coordinates.length);
			resultArray[coordinates.length] = coordinates[0];
		} else {
			resultArray = coordinates;
		}
		return new Polyline(resultArray);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coordinates);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Polyline other = (Polyline) obj;
		if (!Arrays.equals(coordinates, other.coordinates))
			return false;
		return true;
	}
}
