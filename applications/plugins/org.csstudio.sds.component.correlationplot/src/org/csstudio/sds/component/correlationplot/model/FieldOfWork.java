package org.csstudio.sds.component.correlationplot.model;

import java.util.Arrays;


public class FieldOfWork {

	private Polyline upperLine;
	private Polyline lowerLine;
	
	private Polyline combinedLine;

	public FieldOfWork(Polyline upperLine, Polyline lowerLine) {
		this.upperLine = upperLine;
		this.lowerLine = lowerLine;
		
		this.combinedLine = combinePolylines(upperLine, lowerLine);
		
	}

	public Polyline getUpperLine() {
		return upperLine;
	}
	
	public Polyline getLowerLine() {
		return lowerLine;
	}
	
	public boolean containsPoint(Coordinate2D point) {
		return combinedLine.containsPoint(point);
	}
	
	private Polyline combinePolylines(Polyline first, Polyline second) {
		
		Coordinate2D[] coordinatesFirst = first.getCoordinates();
		Coordinate2D[] coordinatesSecond = second.getCoordinates();

		Coordinate2D[] resultCoordinates = Arrays.copyOf(coordinatesFirst, coordinatesFirst.length + coordinatesSecond.length);
		for (int index = 0; index < coordinatesSecond.length; index++) {
			
			Coordinate2D coordinate2d = coordinatesSecond[coordinatesSecond.length - index - 1];
			resultCoordinates[index + coordinatesFirst.length] = coordinate2d;
		}
		return new Polyline(resultCoordinates);
	}
}
