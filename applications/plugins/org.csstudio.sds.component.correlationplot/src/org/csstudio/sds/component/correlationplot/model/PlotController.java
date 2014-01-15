package org.csstudio.sds.component.correlationplot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.sds.component.correlationplot.model.PlotValue.Type;

public class PlotController {

	private final Plot plot;
	private FieldOfWork fieldOfWork;
	private double warningDistance;

	private long waitTime1;
	private long waitTime2;
	private long waitTimeSecondValue;

	private double actualXValue;
	private double actualYValue;

	private List<PlotValue> points;
	
	private Timer timer = new Timer(true);
	private SynchronizedTask timerTask1;
	private SynchronizedTask timerTask2;
	private SynchronizedTask timerTaskWaitForSecondValue;
	
	/*
	 *  Zustände
	 *  - WAIT_FOR_DATA
	 *  	Zwei Timer aktiv (Wartezeit 1/2)
	 *  - WAIT_FOR_XDATA	
	 *  	Ein Timer aktiv (200ms)
	 *  - WAIT_FOR_YDATA (200ms)
	 *  	Ein Timer aktiv (200ms)
	 */
	
	private static enum State {
		WAIT_FOR_DATA, WAIT_FOR_XDATA, WAIT_FOR_YDATA
	}

	private State state = State.WAIT_FOR_DATA;
	private int numberOfPoints;
	private String warningTextOutOfBounds;
	private String warningTextNearUpperBound;
	private String warningTextNearLowerBound;
	private PlotWarningListener warningListener;

	
	
	public PlotController(Plot plot,
			List<Polynomial> polynomials, List<Polyline> polylines, FieldOfWork fieldOfWork,
			double minDistance, int numberOfPoints, long waitTimeForSecondValue, long waitTime1,
			long waitTime2, String warningTextNearUpperBound, String warningTextNearLowerBound, String warningTextOutOfBounds) {
		this.plot = plot;
		this.warningTextNearUpperBound = warningTextNearUpperBound;
		this.warningTextNearLowerBound = warningTextNearLowerBound;
		this.warningTextOutOfBounds = warningTextOutOfBounds;
		setFieldOfWork(fieldOfWork, false);
		this.warningDistance = minDistance;
		this.numberOfPoints = numberOfPoints;
		this.waitTimeSecondValue = waitTimeForSecondValue;
		this.waitTime1 = waitTime1;
		this.waitTime2 = waitTime2;

		this.points = new ArrayList<PlotValue>(numberOfPoints);
		this.plot.setPolynomials(polynomials);
		this.plot.setPolylines(polylines);
		
		// At first, timers do nothing
		this.timerTask1 = createEmptyTimerTask();
		this.timerTask2 = createEmptyTimerTask();
		this.timerTaskWaitForSecondValue = createEmptyTimerTask();
	}
	
	public void setNumberOfPoints(int numberOfPoints) {
		synchronized (this) {
			this.numberOfPoints = numberOfPoints;
			while(points.size() > numberOfPoints) {
				points.remove(0);
			}
			plot();
		}
	}
	
	public void setWaitTime1(long waitTime1) {
		this.waitTime1 = waitTime1;
		//TODO hier müssen noch die timer geprüft weden!!!
	}
	
	public void setWaitTime2(long waitTime2) {
		this.waitTime2 = waitTime2;
		//TODO hier müssen noch die timer geprüft weden!!!
	}
	
	public void setWarningDistance(double warningDistance) {
		this.warningDistance = warningDistance;
	}

	public void setFieldOfWork(FieldOfWork fieldOfWork) {
		setFieldOfWork(fieldOfWork, true);
	}

	private void setFieldOfWork(FieldOfWork fieldOfWork, boolean plot) {
		//TODO hier noch nach neuen warnings suchen?
		this.fieldOfWork = fieldOfWork;
		
		this.plot.setFieldOfWorkPolygon(fieldOfWork);
		if(fieldOfWork.isValid()) {
			this.plot.setMask(fieldOfWork.getFieldPolygon());
		} else {
			this.plot.setMask(null);
		}

		if (plot) {
			plot();
		}
	}
	
	public void setWarningTextNearUpperBound(String warningTextNearUpperBound) {
		this.warningTextNearUpperBound = warningTextNearUpperBound;
	}

	public void setWarningTextNearLowerBound(String warningTextNearLowerBound) {
		this.warningTextNearLowerBound = warningTextNearLowerBound;
	}
	
	public void setWarningTextOutOfBounds(String warningTextOutOfBounds) {
		this.warningTextOutOfBounds = warningTextOutOfBounds;
	}
	
	public void setWarningListener(PlotWarningListener warningListener) {
		this.warningListener = warningListener;
	}
	
	public void handleXValue(double xValue) {
		synchronized (this) {
			if (state == State.WAIT_FOR_DATA) {
				actualXValue = xValue;
				changeState(State.WAIT_FOR_YDATA);
				
			} else if (state == State.WAIT_FOR_XDATA) {
				actualXValue = xValue;
				changeState(State.WAIT_FOR_DATA);
				plotNextPoint();
				
			} else if (state == State.WAIT_FOR_YDATA) {
				if (!timerTaskWaitForSecondValue.isCanceled()) {
					timerTaskWaitForSecondValue.cancel();
				}
				plotNextPoint();
				scheduleTimerTaskWaitForSecondValue();
				actualXValue = xValue;
			}
		}
	}

	public void handleYValue(double yValue) {
		synchronized (this) {
			if (state == State.WAIT_FOR_DATA) {
				actualYValue = yValue;
				changeState(State.WAIT_FOR_XDATA);
				
			} else if (state == State.WAIT_FOR_YDATA) {
				actualYValue = yValue;
				changeState(State.WAIT_FOR_DATA);
				plotNextPoint();
				
			} else if (state == State.WAIT_FOR_XDATA) {
				if (!timerTaskWaitForSecondValue.isCanceled()) {
					timerTaskWaitForSecondValue.cancel();
				}
				plotNextPoint();
				scheduleTimerTaskWaitForSecondValue();
				actualYValue = yValue;
			}
		}
	}

	private void plotNextPoint() {
		
		PlotValue point = new PlotValue(actualXValue, actualYValue, Type.NORMAL);

		if(!fieldOfWork.isValid()) {
			plot.setWarning("");
			warningListener.onNoWarning();
		}
		// point out of bounds
		else if(!fieldOfWork.containsPoint(point)) {
			plot.setWarning(warningTextOutOfBounds);
			point.setAlarm(true);
			if (warningListener != null) {
				warningListener.onOutOfBounds();
			}
		} else {
			if (fieldOfWork.getUpperLine().calculateMinDistance(point) < this.warningDistance) {
				// point near upper bound
				plot.setWarning(warningTextNearUpperBound);
				point.setAlarm(true);
				if (warningListener != null) {
					warningListener.onNearUpperBound();
				}
			} else if (fieldOfWork.getLowerLine().calculateMinDistance(point) < this.warningDistance) {
				// point near lower bound
				plot.setWarning(warningTextNearLowerBound);
				point.setAlarm(true);
				if (warningListener != null) {
					warningListener.onNearLowerBound();
				}
			} else {
				// point inside bounds
				plot.setWarning("");
				warningListener.onNoWarning();
			}
		}
		
		points.add(point);
		if(points.size() > numberOfPoints) {
			points.remove(0);
		}
		plot();
	}

//	private double calculateMinDistance(Coordinate2D point, Polyline polyLine) {
//		Coordinate2D[] linePoints = polyLine.getCoordinates();
//		double result = Integer.MAX_VALUE;
//		
//		for(int index = 1; index < linePoints.length; index++) {
//			double distancePointToLine = distancePointToLine(point, linePoints[index - 1], linePoints[index]);
//			result = Math.min(distancePointToLine, result);
//		}
//		
//		return result;
//	}
//	
//	private double distancePointToLine(Coordinate2D point, Coordinate2D lineStart, Coordinate2D lineEnd) {
//		double result = Double.MAX_VALUE;
//		
//		Coordinate2D lineNormalizedToOrigin = new Coordinate2D(lineEnd.getX() - lineStart.getX(), lineEnd.getY() - lineStart.getY());
//		Coordinate2D pointNormalized = new Coordinate2D(point.getX() - lineStart.getX(), point.getY() - lineStart.getY());
//		double lineLength = Math.sqrt(Math.pow(lineNormalizedToOrigin.getX(),2) + Math.pow(lineNormalizedToOrigin.getY(),2));
//		
//		double pi2 = 2*Math.PI;
//		double lineAngle = (pi2 + Math.atan2(lineNormalizedToOrigin.getY(), lineNormalizedToOrigin.getX())) % pi2;
//
//		Coordinate2D pointRotated = rotate(pointNormalized, -lineAngle);
//		
//		// check if point is orthogonal to line (angle difference is less than PI/2)
//		if(pointRotated.getX() >= 0 && pointRotated.getX() <= lineLength) {
//			result = Math.abs(pointRotated.getY());
//		}
//		// else, use minimum distance to line ends
//		else {
//			double distance = Math.pow(pointNormalized.getX(), 2) + Math.pow(pointNormalized.getY(), 2);
//			distance = Math.min(distance, Math.pow(pointNormalized.getX() - lineNormalizedToOrigin.getX(), 2) + Math.pow(pointNormalized.getY() - lineNormalizedToOrigin.getY(), 2));
//			result = Math.sqrt(distance);
//		}
//		return result;
//	}
//	
//	private Coordinate2D rotate(Coordinate2D coordinate, double angle) {
//		double newX = Math.cos(angle) * coordinate.getX() - Math.sin(angle) * coordinate.getY();
//		double newY = Math.sin(angle) * coordinate.getX() + Math.cos(angle) * coordinate.getY();
//		
//		return new Coordinate2D(newX, newY);
//	}
	

	private void plot() {
		plot.setPlotValues(new ArrayList<PlotValue>(points));
	}

	private void changeState(State nextState) {
		if (state == State.WAIT_FOR_DATA
				&& (nextState == State.WAIT_FOR_XDATA || nextState == State.WAIT_FOR_YDATA)) {
			// stoppe Timer1 und Timer2
			if (!timerTask1.isCanceled()) {
				timerTask1.cancel();
			}
			if (!timerTask2.isCanceled()) {
				timerTask2.cancel();
			}
			// starte 200ms Timer
			scheduleTimerTaskWaitForSecondValue();
			state = nextState;
			
		} else if ((state == State.WAIT_FOR_XDATA || state == State.WAIT_FOR_YDATA)
				&& nextState == State.WAIT_FOR_DATA) {
			// stoppe 200ms Timer
			if (!timerTaskWaitForSecondValue.isCanceled()) {
				timerTaskWaitForSecondValue.cancel();
			}
			// starte Timer1 und Timer2
			scheduleTimerTask1();
			scheduleTimerTask2();
			state = nextState;
			
		} else {
			// TODO EXCEPTION
			throw new RuntimeException("state change not possible");
		}
	}

	private SynchronizedTask createEmptyTimerTask() {
		return new SynchronizedTask(this) {
			@Override
			public void synchronizedRun() {
			}
		};
	}

	private void scheduleTimerTask1() {
		timerTask1 = new SynchronizedTask(this) {
			@Override
			public void synchronizedRun() {
				points.get(points.size() - 1).setType(Type.TIME_WARNING_1);
				plot();
			}
		};
		timer.schedule(timerTask1, waitTime1);
	}

	private void scheduleTimerTask2() {
		timerTask2 = new SynchronizedTask(this) {
			@Override
			public void synchronizedRun() {
				points.get(points.size() - 1).setType(Type.TIME_WARNING_2);
				plot();
				
			}
		};
		timer.schedule(timerTask2, waitTime2);
	}

	private void scheduleTimerTaskWaitForSecondValue() {
		timerTaskWaitForSecondValue = new SynchronizedTask(this) {
			@Override
			public void synchronizedRun() {
				plotNextPoint();
				changeState(State.WAIT_FOR_DATA);
			}
		};
		timer.schedule(timerTaskWaitForSecondValue, waitTimeSecondValue);
	}

	private abstract static class SynchronizedTask extends TimerTask {

		private boolean isCanceled;
		private final Object synchronizeToken;

		public SynchronizedTask(Object synchronizeToken) {
			this.synchronizeToken = synchronizeToken;
		}
		
		@Override
		public void run() {
			synchronized (synchronizeToken) {
				if(!isCanceled()) {
					synchronizedRun();
				}
			}
		}
		
		public abstract void synchronizedRun();
		
		@Override
		public boolean cancel() {
			isCanceled = super.cancel();
			return isCanceled;
		}
		
		public boolean isCanceled() {
			return isCanceled;
		}
	}
}
