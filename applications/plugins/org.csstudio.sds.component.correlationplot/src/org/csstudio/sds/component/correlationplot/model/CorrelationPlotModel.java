package org.csstudio.sds.component.correlationplot.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.CorrelationChecker;
import org.csstudio.sds.model.WidgetPropertyCategory;

public class CorrelationPlotModel extends AbstractWidgetModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.component.CorrelationPlot"; //$NON-NLS-1$
	public static final String PROP_X_AXIS_NAME = "x_axis_name"; //$NON-NLS-1$
	public static final String PROP_Y_AXIS_NAME = "y_axis_name"; //$NON-NLS-1$
	public static final String PROP_X_AXIS_MIN = "x_axis_minimum"; //$NON-NLS-1$
	public static final String PROP_X_AXIS_MAX = "x_axis_maximum"; //$NON-NLS-1$
	public static final String PROP_Y_AXIS_MIN = "y_axis_minimum"; //$NON-NLS-1$
	public static final String PROP_Y_AXIS_MAX = "y_axis_maximum"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_1 = "polynomial_1"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_2 = "polynomial_2"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_3 = "polynomial_3"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_4 = "polynomial_4"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_5 = "polynomial_5"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_6 = "polynomial_6"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_7 = "polynomial_7"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_8 = "polynomial_8"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_9 = "polynomial_9"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_10 = "polynomial_10"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_LINE_COLOR = "polynomial_line_color"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_LINE_WIDTH = "polynomial_line_width"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_1 = "polyline1"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_2 = "polyline2"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_COLOR = "polyline_color"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_WIDTH = "polyline_width"; //$NON-NLS-1$
	public static final String PROP_NUMBER_OF_POINTS = "number_of_points"; //$NON-NLS-1$
	public static final String PROP_POINT_SIZE = "point_size"; //$NON-NLS-1$
	public static final String PROP_POINT_BRIGHTNESS = "point_brightness"; //$NON-NLS-1$
	public static final String PROP_WAITTIME_1 = "waittime1"; //$NON-NLS-1$
	public static final String PROP_WAITTIME_2 = "waittime2"; //$NON-NLS-1$
	public static final String PROP_WARNING_DISTANCE = "warning_distance"; //$NON-NLS-1$
	public static final String PROP_WARNING_TEXT_NEAR_BOUNDS = "warning_text_near_bounds";
	public static final String PROP_WARNING_TEXT_OUT_OF_BOUNDS = "warning_text_out_of_bounds";
	public static final String PROP_WARNING_TEXT_COLOR = "warning_text_color";
	public static final String PROP_WARNING_TEXT_FONT = "warning_text_font";
	public static final String PROP_WARNING_TEXT_X_POS = "warning_text_x_pos";
	public static final String PROP_WARNING_TEXT_Y_POS = "warning_text_y_pos";
	public static final String PROP_X_VALUE = "x_value";
	public static final String PROP_Y_VALUE = "y_value";

	@Override
	protected void configureProperties() {
		// X und Y Werte
		addDoubleProperty(PROP_X_VALUE, "X Value", WidgetPropertyCategory.MISC, 0, false);
		addDoubleProperty(PROP_Y_VALUE, "Y Value", WidgetPropertyCategory.MISC, 0, false);
		
		// Achsbezeichnung
		addStringProperty(PROP_X_AXIS_NAME, "X-axis name", WidgetPropertyCategory.MISC, "X axis", false);
		addStringProperty(PROP_Y_AXIS_NAME, "Y-axis name", WidgetPropertyCategory.MISC, "Y axis", false);
		// Wertebereich

		CorrelationChecker<Double> unequalValuesChecker = new CorrelationChecker<Double>() {
			@Override
			public boolean checkValues(Double value1, Double value2) {
				return !value1.equals(value2);
			}
		};
		addCorrelatedDoubleProperties(PROP_X_AXIS_MIN, "X-axis minimum", WidgetPropertyCategory.MISC, 0.0,PROP_X_AXIS_MAX, "X-axis maximum", WidgetPropertyCategory.MISC, 10.0, unequalValuesChecker, false);
		addCorrelatedDoubleProperties(PROP_Y_AXIS_MIN, "Y-axis minimum", WidgetPropertyCategory.MISC, 0.0,PROP_Y_AXIS_MAX, "Y-axis maximum", WidgetPropertyCategory.MISC, 10.0, unequalValuesChecker, false);
		
		// Kennlinien (Polynome), bis zu 10, als Polynom 4. Grades, bis zu 5 Koeffizienten
		// TODO Standard Werte??
		addDoubleArrayProperty(PROP_POLYNOMIAL_1, "Polynomial 1  (coefficients)", WidgetPropertyCategory.MISC, new double[]{-1.0, 0.0, 0.6, -0.125}, false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_2, "Polynomial 2  (coefficients)", WidgetPropertyCategory.MISC, new double[]{1.0, 0.0, 0.5, -0.1}, false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_3, "Polynomial 3  (coefficients)", WidgetPropertyCategory.MISC, new double[]{3.0, 0.0, 0.4, -0.075}, false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_4, "Polynomial 4  (coefficients)", WidgetPropertyCategory.MISC, new double[]{5.0, 0.0, 0.3, -0.05}, false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_5, "Polynomial 5  (coefficients)", WidgetPropertyCategory.MISC, new double[]{7.0, 0.0, 0.2, -0.025}, false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_6, "Polynomial 6  (coefficients)", WidgetPropertyCategory.MISC, new double[]{9.0, 0.0, 0.1, -0.01}, false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_7, "Polynomial 7  (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_8, "Polynomial 8  (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_9, "Polynomial 9  (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_10,"Polynomial 10 (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		// Linienfarbe (Linientyp, Liniendicke) zu den Kennlinien
		addColorProperty(PROP_POLYNOMIAL_LINE_COLOR, "Polynomial line color", WidgetPropertyCategory.MISC, "#969696", false);
		addIntegerProperty(PROP_POLYNOMIAL_LINE_WIDTH, "Polynomial line width", WidgetPropertyCategory.MISC, 1, 1, 10, false);
				
		// Arbeitsfeld: 2 Kantenzüge mit je 3-4 Punkten (Linienfarbe, Linientyp, Liniendicke), ggf. Polygon
		// TODO Properties für zwei Linien fehlen noch
		addDoubleArrayProperty(PROP_POLYLINE_1, "Field of work upper limit", WidgetPropertyCategory.MISC, new double[]{0.0, 0.0, 0.0, 2.0, 2.0, 8.0, 4.0, 4.0, 6.0, 15.0, 9.0, 10.0, 10.0, 10.0}, false);
		addDoubleArrayProperty(PROP_POLYLINE_2, "Field of work lower limit", WidgetPropertyCategory.MISC, new double[]{0.0, 0.0, 1.5, 0.0, 5.0, 1.0, 10.0, 8.0, 10.0, 10.0}, false);
		addColorProperty(PROP_POLYLINE_COLOR, "Field of work line color", WidgetPropertyCategory.MISC, "#ff7f00", false);
		addIntegerProperty(PROP_POLYLINE_WIDTH, "Field of work line width", WidgetPropertyCategory.MISC, 1, 1, 10, false);
		
		// Anzahl der dargestellten Punkte, 1-100
		addIntegerProperty(PROP_NUMBER_OF_POINTS, "Number of points", WidgetPropertyCategory.MISC, 100, 1, 100, false);
		// Durchmesser eines Punktes
		addIntegerProperty(PROP_POINT_SIZE, "Point size", WidgetPropertyCategory.MISC, 3, 1, 20, false);
		// Helligkeitswert für den letzten Punkt
		addIntegerProperty(PROP_POINT_BRIGHTNESS, "Last point brightness", WidgetPropertyCategory.MISC, 200, 0, 255, false);
		// Wartezeit 1 und Wartezeit 2 (größer als W1)
		addIntegerProperty(PROP_WAITTIME_1, "Waittime 1", WidgetPropertyCategory.MISC, 5, 1, 1000, false);
		addIntegerProperty(PROP_WAITTIME_2, "Waittime 2", WidgetPropertyCategory.MISC, 60, 2, 1000, false);
		
		// Grenzabstand
		addDoubleProperty(PROP_WARNING_DISTANCE, "Warning distance", WidgetPropertyCategory.MISC, 0.2, 0, Double.MAX_VALUE, false);
		
		// Warnungstext... Position, Information...
		addStringProperty(PROP_WARNING_TEXT_NEAR_BOUNDS, "Warning text near bounds", WidgetPropertyCategory.MISC, "Punkt naehert sich Grenze", false);
		addStringProperty(PROP_WARNING_TEXT_OUT_OF_BOUNDS, "Warning text out of bounds", WidgetPropertyCategory.MISC, "Punkt ausserhalb des Arbeitsfelds", false);
		addColorProperty(PROP_WARNING_TEXT_COLOR, "Warning text color", WidgetPropertyCategory.MISC, "#ff0000", false);
		addFontProperty(PROP_WARNING_TEXT_FONT, "Warning text font", WidgetPropertyCategory.MISC, "", false);
		addIntegerProperty(PROP_WARNING_TEXT_X_POS, "Warning text position x", WidgetPropertyCategory.MISC, 50, false);
		addIntegerProperty(PROP_WARNING_TEXT_Y_POS, "Warning text position y", WidgetPropertyCategory.MISC, 50, false);
	}

	public final String getXAxisName() {
		return getStringProperty(PROP_X_AXIS_NAME);
	}

	public final String getYAxisName() {
		return getStringProperty(PROP_Y_AXIS_NAME);
	}
	
	public final double getXAxisMinimum() {
		return getDoubleProperty(PROP_X_AXIS_MIN);
	}
	
	public final double getXAxisMaximum() {
		return getDoubleProperty(PROP_X_AXIS_MAX);
	}
	
	public final double getYAxisMinimum() {
		return getDoubleProperty(PROP_Y_AXIS_MIN);
	}
	
	public final double getYAxisMaximum() {
		return getDoubleProperty(PROP_Y_AXIS_MAX);
	}
	
	public final double[] getPolynomial1() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_1);
	}
	
	public final double[] getPolynomial2() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_2);
	}
	
	public final double[] getPolynomial3() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_3);
	}
	
	public final double[] getPolynomial4() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_4);
	}
	
	public final double[] getPolynomial5() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_5);
	}
	
	public final double[] getPolynomial6() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_6);
	}
	
	public final double[] getPolynomial7() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_7);
	}
	
	public final double[] getPolynomial8() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_8);
	}
	
	public final double[] getPolynomial9() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_9);
	}
	
	public final double[] getPolynomial10() {
		return getDoubleArrayProperty(PROP_POLYNOMIAL_10);
	}
	
	public final int getPolynomialLineWidth() {
		return getIntegerProperty(PROP_POLYNOMIAL_LINE_WIDTH);
	}
	
	public final String getPolynomialLineColor() {
		return getColor(PROP_POLYNOMIAL_LINE_COLOR);
	}
	
	public final Polyline getUpperPolyline() {
		List<Coordinate2D> coordinates = new ArrayList<Coordinate2D>();
		double[] arrayProperty = getDoubleArrayProperty(PROP_POLYLINE_1);
		for (int i = 0; i < arrayProperty.length-1; i += 2) {
			coordinates.add(new Coordinate2D(arrayProperty[i], arrayProperty[i+1]));
		}
		return new Polyline(coordinates.toArray(new Coordinate2D[coordinates.size()]));
	}
	
	public final Polyline getLowerPolyline() {
		List<Coordinate2D> coordinates = new ArrayList<Coordinate2D>();
		double[] arrayProperty = getDoubleArrayProperty(PROP_POLYLINE_2);
		for (int i = 0; i < arrayProperty.length-1; i += 2) {
			coordinates.add(new Coordinate2D(arrayProperty[i], arrayProperty[i+1]));
		}
		return new Polyline(coordinates.toArray(new Coordinate2D[coordinates.size()]));
	}
	
	public final int getPolylineWidth() {
		return getIntegerProperty(PROP_POLYLINE_WIDTH);
	}
	
	public final String getPolylineColor() {
		return getColor(PROP_POLYLINE_COLOR);
	}
	
	public final int getNumberOfPoints() {
		return getIntegerProperty(PROP_NUMBER_OF_POINTS);
	}
	
	public final int getPointSize() {
		return getIntegerProperty(PROP_POINT_SIZE);
	}
	
	public final int getPointBrightness() {
		return getIntegerProperty(PROP_POINT_BRIGHTNESS);
	}
	
	public final long getWaittime1InMillis() {
		return 1000L * getIntegerProperty(PROP_WAITTIME_1);
	}
	
	public final long getWaittime2InMillis() {
		return 1000L * getIntegerProperty(PROP_WAITTIME_2);
	}
	
	public final double getWarningDistance() {
		return getDoubleProperty(PROP_WARNING_DISTANCE);
	}
	
	public final String getWarningTextNearBounds() {
		return getStringProperty(PROP_WARNING_TEXT_NEAR_BOUNDS);
	}
	
	public final String getWarningTextOutOfBounds() {
		return getStringProperty(PROP_WARNING_TEXT_OUT_OF_BOUNDS);
	}
	
	public int getWarningTextPositionX() {
		return getIntegerProperty(PROP_WARNING_TEXT_X_POS);
	}

	public int getWarningTextPositionY() {
		return getIntegerProperty(PROP_WARNING_TEXT_Y_POS);
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
}
