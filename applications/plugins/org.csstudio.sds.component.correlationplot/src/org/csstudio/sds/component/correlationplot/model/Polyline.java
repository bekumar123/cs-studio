package org.csstudio.sds.component.correlationplot.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
//	public double[] toArray() {
//		double[] result = new double[coordinates.length * 2];
//		
//		for (int pointIndex = 0; pointIndex < coordinates.length; pointIndex++) {
//			result[pointIndex*2] = coordinates[pointIndex].getX();
//			result[pointIndex*2 + 1] = coordinates[pointIndex].getY();
//		}
//		
//		return result;
//	}
	
	public boolean containsPoint(Coordinate2D testPoint) {
		boolean result = false;

		for (int indexLineEnd = 0, indexLineStart = coordinates.length - 1; indexLineEnd < coordinates.length; indexLineEnd++) {
			if (((coordinates[indexLineEnd].getY().compareTo(testPoint.getY()) > 0) != (coordinates[indexLineStart].getY().compareTo(testPoint.getY()) > 0))
					&& (testPoint.getX().compareTo((coordinates[indexLineStart].getX().subtract(coordinates[indexLineEnd].getX()))
									.multiply(testPoint.getY().subtract(coordinates[indexLineEnd].getY()))
									.divide(coordinates[indexLineStart].getY().subtract(coordinates[indexLineEnd].getY()))
									.add(coordinates[indexLineEnd].getX()))) < 0) {
//				&& (testPoint.getX() < (coordinates[indexLineStart].getX() - coordinates[indexLineEnd].getX()) * (testPoint.getY() - coordinates[indexLineEnd].getY())
//						/ (coordinates[indexLineStart].getY() - coordinates[indexLineEnd].getY()) + coordinates[indexLineEnd].getX())) {
				
				result = !result;
			}
			indexLineStart = indexLineEnd;
		}

		return result;
	}
	
	public boolean isAbove(Coordinate2D point) {
		int lowerCount = 0;
		List<BigDecimal> yValues = getYValuesForX(point.getX());
		for (BigDecimal yValue : yValues) {
			if(yValue.compareTo(point.getY()) < 0) {
				lowerCount += 1;
			}
		}
		
		return lowerCount % 2 == 1;
	}
	
	public List<BigDecimal> getYValuesForX(BigDecimal x) {
		List<BigDecimal> result = new ArrayList<BigDecimal>();
		
		Coordinate2D[] linePoints = getCoordinates();
		for(int index = 1; index < linePoints.length; index++) {
			Coordinate2D lineStart = linePoints[index - 1];
			Coordinate2D lineEnd = linePoints[index];
			if(lineStart.getX().compareTo(x) < 0 && lineEnd.getX().compareTo(x) >= 0 || lineStart.getX().compareTo(x) > 0 && lineEnd.getX().compareTo(x) <= 0) {
				
				BigDecimal yLength = lineEnd.getY().subtract(lineStart.getY());
				BigDecimal xLength = lineEnd.getX().subtract(lineStart.getX());
				BigDecimal slope = yLength.divide(xLength, 20, RoundingMode.HALF_UP);
				
				BigDecimal lineYAtPoint = (x.subtract(lineStart.getX())).multiply(slope).add(lineStart.getY());

				result.add(lineYAtPoint);
			}
		}
		
		return result;
	}
	
	public double calculateMinDistance(Coordinate2D point) {
		Coordinate2D[] linePoints = getCoordinates();
		double result = Integer.MAX_VALUE;
		
		for(int index = 1; index < linePoints.length; index++) {
			double distancePointToLine = distancePointToLine(point, linePoints[index - 1], linePoints[index]);
			result = Math.min(distancePointToLine, result);
		}
		
		return result;
	}
	
	private double distancePointToLine(Coordinate2D point, Coordinate2D lineStart, Coordinate2D lineEnd) {
		double result = Double.MAX_VALUE;
		
		Coordinate2D lineNormalizedToOrigin = new Coordinate2D(lineEnd.getX().subtract(lineStart.getX()), lineEnd.getY().subtract(lineStart.getY()));
		Coordinate2D pointNormalized = new Coordinate2D(point.getX().subtract(lineStart.getX()), point.getY().subtract(lineStart.getY()));
		double lineLength = Math.sqrt(lineNormalizedToOrigin.getX().pow(2).add(lineNormalizedToOrigin.getY().pow(2)).doubleValue());

		double pi2 = 2*Math.PI;
		double lineAngle = (pi2 + Math.atan2(lineNormalizedToOrigin.getY().doubleValue(), lineNormalizedToOrigin.getX().doubleValue())) % pi2;

		Coordinate2D pointRotated = pointNormalized.rotate(-lineAngle);
		
		// check if point is orthogonal to line (angle difference is less than PI/2)
		if(pointRotated.getX().compareTo(BigDecimal.ZERO) >= 0 && pointRotated.getX().doubleValue() <= lineLength) {
			result = pointRotated.getY().abs().doubleValue();
		}
		// else, use minimum distance to line ends
		else {
			BigDecimal distance = pointNormalized.getX().pow(2).add(pointNormalized.getY().pow(2));
			distance = distance.min(pointNormalized.getX().subtract(lineNormalizedToOrigin.getX()).pow(2).add(pointNormalized.getY().subtract(lineNormalizedToOrigin.getY()).pow(2)));
			result = Math.sqrt(distance.doubleValue());
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
