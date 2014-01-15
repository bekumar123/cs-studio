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
	public static final String PROP_POLYNOMIAL_1_LABEL = "polynomial_1_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_2_LABEL = "polynomial_2_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_3_LABEL = "polynomial_3_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_4_LABEL = "polynomial_4_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_5_LABEL = "polynomial_5_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_6_LABEL = "polynomial_6_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_7_LABEL = "polynomial_7_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_8_LABEL = "polynomial_8_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_9_LABEL = "polynomial_9_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_10_LABEL = "polynomial_10_label"; //$NON-NLS-1$
	public static final String PROP_POLYNOMIAL_LABEL_COLOR = "polynomial_label_color"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_1 = "polyline_1"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_2 = "polyline_2"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_3 = "polyline_3"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_4 = "polyline_4"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_5 = "polyline_5"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_6 = "polyline_6"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_7 = "polyline_7"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_8 = "polyline_8"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_9 = "polyline_9"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_10 = "polyline_10"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_LINE_COLOR = "polyline_line_color"; //$NON-NLS-1$
	public static final String PROP_POLYLINE_LINE_WIDTH = "polyline_line_width"; //$NON-NLS-1$
	public static final String PROP_FIELD_OF_WORK_UPPER = "field_of_work_upper"; //$NON-NLS-1$
	public static final String PROP_FIELD_OF_WORK_LOWER = "field_of_work_lower"; //$NON-NLS-1$
	public static final String PROP_FIELD_OF_WORK_COLOR = "field_of_work_color"; //$NON-NLS-1$
	public static final String PROP_FIELD_OF_WORK_WIDTH = "field_of_work_width"; //$NON-NLS-1$
	public static final String PROP_NUMBER_OF_POINTS = "number_of_points"; //$NON-NLS-1$
	public static final String PROP_POINT_SIZE = "point_size"; //$NON-NLS-1$
	public static final String PROP_POINT_BRIGHTNESS = "point_brightness"; //$NON-NLS-1$
	public static final String PROP_WAITTIME_1 = "waittime1"; //$NON-NLS-1$
	public static final String PROP_WAITTIME_2 = "waittime2"; //$NON-NLS-1$
	public static final String PROP_WARNING_DISTANCE = "warning_distance"; //$NON-NLS-1$
	public static final String PROP_WARNING_TEXT_NEAR_UPPER_BOUND = "warning_text_near_upper_bound";
	public static final String PROP_WARNING_TEXT_NEAR_LOWER_BOUND = "warning_text_near_lower_bound";
	public static final String PROP_WARNING_TEXT_OUT_OF_BOUNDS = "warning_text_out_of_bounds";
	public static final String PROP_WARNING_TEXT_COLOR = "warning_text_color";
	public static final String PROP_WARNING_TEXT_FONT = "warning_text_font";
	public static final String PROP_WARNING_TEXT_X_POS = "warning_text_x_pos";
	public static final String PROP_WARNING_TEXT_Y_POS = "warning_text_y_pos";
	public static final String PROP_X_VALUE = "x_value";
	public static final String PROP_Y_VALUE = "y_value";
	public static final String PROP_ALARM_UPPER_BOUND = "alarm_upper_bound";
	public static final String PROP_ALARM_LOWER_BOUND = "alarm_lower_bound";
	public static final String PROP_ALARM_OUT_OF_BOUNDS = "alarm_out_of_bounds";

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
		addStringProperty(PROP_POLYNOMIAL_1_LABEL, "Polynomial 1 Label", WidgetPropertyCategory.MISC, "Polynomial 1", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_2, "Polynomial 2  (coefficients)", WidgetPropertyCategory.MISC, new double[]{1.0, 0.0, 0.5, -0.1}, false);
		addStringProperty(PROP_POLYNOMIAL_2_LABEL, "Polynomial 2 Label", WidgetPropertyCategory.MISC, "Polynomial 2", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_3, "Polynomial 3  (coefficients)", WidgetPropertyCategory.MISC, new double[]{3.0, 0.0, 0.4, -0.075}, false);
		addStringProperty(PROP_POLYNOMIAL_3_LABEL, "Polynomial 3 Label", WidgetPropertyCategory.MISC, "Polynomial 3", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_4, "Polynomial 4  (coefficients)", WidgetPropertyCategory.MISC, new double[]{5.0, 0.0, 0.3, -0.05}, false);
		addStringProperty(PROP_POLYNOMIAL_4_LABEL, "Polynomial 4 Label", WidgetPropertyCategory.MISC, "Polynomial 4", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_5, "Polynomial 5  (coefficients)", WidgetPropertyCategory.MISC, new double[]{7.0, 0.0, 0.2, -0.025}, false);
		addStringProperty(PROP_POLYNOMIAL_5_LABEL, "Polynomial 5 Label", WidgetPropertyCategory.MISC, "Polynomial 5", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_6, "Polynomial 6  (coefficients)", WidgetPropertyCategory.MISC, new double[]{9.0, 0.0, 0.1, -0.01}, false);
		addStringProperty(PROP_POLYNOMIAL_6_LABEL, "Polynomial 6 Label", WidgetPropertyCategory.MISC, "Polynomial 6", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_7, "Polynomial 7  (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		addStringProperty(PROP_POLYNOMIAL_7_LABEL, "Polynomial 7 Label", WidgetPropertyCategory.MISC, "", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_8, "Polynomial 8  (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		addStringProperty(PROP_POLYNOMIAL_8_LABEL, "Polynomial 8 Label", WidgetPropertyCategory.MISC, "", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_9, "Polynomial 9  (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		addStringProperty(PROP_POLYNOMIAL_9_LABEL, "Polynomial 9 Label", WidgetPropertyCategory.MISC, "", false);
		addDoubleArrayProperty(PROP_POLYNOMIAL_10,"Polynomial 10 (coefficients)", WidgetPropertyCategory.MISC, new double[0], false);
		addStringProperty(PROP_POLYNOMIAL_10_LABEL, "Polynomial 10 Label", WidgetPropertyCategory.MISC, "", false);
		// Linienfarbe (Linientyp, Liniendicke) zu den Kennlinien
		addColorProperty(PROP_POLYNOMIAL_LINE_COLOR, "Polynomial line color", WidgetPropertyCategory.MISC, "#969696", false);
		addIntegerProperty(PROP_POLYNOMIAL_LINE_WIDTH, "Polynomial line width", WidgetPropertyCategory.MISC, 1, 1, 10, false);
		addColorProperty(PROP_POLYNOMIAL_LABEL_COLOR, "Polynomial label color", WidgetPropertyCategory.MISC, "#0d50ee", false);
		
		// Kennlinien (Polylines)
		addDoubleArrayProperty(PROP_POLYLINE_1, "Polyline 1  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[]{1.0, 0.0, 0.0, 1}, false);
		addDoubleArrayProperty(PROP_POLYLINE_2, "Polyline 2  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[]{2.0, 0.0, 0.0, 2.0}, false);
		addDoubleArrayProperty(PROP_POLYLINE_3, "Polyline 3  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[]{3.0, 0.0, 0.0, 3.0}, false);
		addDoubleArrayProperty(PROP_POLYLINE_4, "Polyline 4  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[]{4.0, 0.0, 0.0, 4.0}, false);
		addDoubleArrayProperty(PROP_POLYLINE_5, "Polyline 5  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[]{5.0, 0.0, 0.0, 5.0}, false);
		addDoubleArrayProperty(PROP_POLYLINE_6, "Polyline 6  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[]{6.0, 0.0, 0.0, 6.0}, false);
		addDoubleArrayProperty(PROP_POLYLINE_7, "Polyline 7  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[0], false);
		addDoubleArrayProperty(PROP_POLYLINE_8, "Polyline 8  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[0], false);
		addDoubleArrayProperty(PROP_POLYLINE_9, "Polyline 9  (x;y coordinates)", WidgetPropertyCategory.MISC, new double[0], false);
		addDoubleArrayProperty(PROP_POLYLINE_10,"Polyline 10 (x;y coordinates)", WidgetPropertyCategory.MISC, new double[0], false);
		// Linienfarbe (Linientyp, Liniendicke) zu den Polyline-Kennlinien
		addColorProperty(PROP_POLYLINE_LINE_COLOR, "Polyline line color", WidgetPropertyCategory.MISC, "#969696", false);
		addIntegerProperty(PROP_POLYLINE_LINE_WIDTH, "Polyline line width", WidgetPropertyCategory.MISC, 1, 1, 10, false);
				
		// Arbeitsfeld: 2 Kantenzüge mit je 3-4 Punkten (Linienfarbe, Linientyp, Liniendicke), ggf. Polygon
		// TODO Properties für zwei Linien fehlen noch
		addDoubleArrayProperty(PROP_FIELD_OF_WORK_UPPER, "Field of work upper limit", WidgetPropertyCategory.MISC, new double[]{0.0, 0.0, 0.0, 2.0, 2.0, 8.0, 4.0, 4.0, 6.0, 15.0, 9.0, 10.0, 10.0, 10.0}, false);
		addDoubleArrayProperty(PROP_FIELD_OF_WORK_LOWER, "Field of work lower limit", WidgetPropertyCategory.MISC, new double[]{0.0, 0.0, 1.5, 0.0, 5.0, 1.0, 10.0, 8.0, 10.0, 10.0}, false);
		addColorProperty(PROP_FIELD_OF_WORK_COLOR, "Field of work line color", WidgetPropertyCategory.MISC, "#ff7f00", false);
		addIntegerProperty(PROP_FIELD_OF_WORK_WIDTH, "Field of work line width", WidgetPropertyCategory.MISC, 1, 1, 10, false);
		
		// Anzahl der dargestellten Punkte, 1-100
		addIntegerProperty(PROP_NUMBER_OF_POINTS, "Number of points", WidgetPropertyCategory.MISC, 100, 1, 100, false);
		// Durchmesser eines Punktes
		addIntegerProperty(PROP_POINT_SIZE, "Point size", WidgetPropertyCategory.MISC, 3, 1, 20, false);
		// Helligkeitswert f��r den letzten Punkt
		addDoubleProperty(PROP_POINT_BRIGHTNESS, "Second point brightness", WidgetPropertyCategory.MISC, 0.4, 0.0, 1.0, false);
		// Wartezeit 1 und Wartezeit 2 (gr����er als W1)
		addIntegerProperty(PROP_WAITTIME_1, "Waittime 1", WidgetPropertyCategory.MISC, 5, 1, 1000, false);
		addIntegerProperty(PROP_WAITTIME_2, "Waittime 2", WidgetPropertyCategory.MISC, 60, 2, 1000, false);
		
		// Grenzabstand
		addDoubleProperty(PROP_WARNING_DISTANCE, "Warning distance", WidgetPropertyCategory.MISC, 0.2, 0, Double.MAX_VALUE, false);
		
		// Warnungstext... Position, Information...
		addStringProperty(PROP_WARNING_TEXT_NEAR_UPPER_BOUND, "Warning text near upper bound", WidgetPropertyCategory.MISC, "Punkt naehert sich oberer Grenze", false);
		addStringProperty(PROP_WARNING_TEXT_NEAR_LOWER_BOUND, "Warning text near lower bound", WidgetPropertyCategory.MISC, "Punkt naehert sich unterer Grenze", false);
		addStringProperty(PROP_WARNING_TEXT_OUT_OF_BOUNDS, "Warning text out of bounds", WidgetPropertyCategory.MISC, "Punkt ausserhalb des Arbeitsfelds", false);
		addColorProperty(PROP_WARNING_TEXT_COLOR, "Warning text color", WidgetPropertyCategory.MISC, "#ff0000", false);
		addFontProperty(PROP_WARNING_TEXT_FONT, "Warning text font", WidgetPropertyCategory.MISC, "", false);
		addIntegerProperty(PROP_WARNING_TEXT_X_POS, "Warning text position x", WidgetPropertyCategory.MISC, 50, false);
		addIntegerProperty(PROP_WARNING_TEXT_Y_POS, "Warning text position y", WidgetPropertyCategory.MISC, 50, false);
		
		// Warning output boolean
		addBooleanProperty(PROP_ALARM_UPPER_BOUND, "true if values near upper bound", WidgetPropertyCategory.MISC, false, false);
		addBooleanProperty(PROP_ALARM_LOWER_BOUND, "true if values near lower bound", WidgetPropertyCategory.MISC, false, false);
		addBooleanProperty(PROP_ALARM_OUT_OF_BOUNDS, "true if values out of bounds", WidgetPropertyCategory.MISC, false, false);
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

	public final String getPolynomialLabel1() {
		return getStringProperty(PROP_POLYNOMIAL_1_LABEL);
	}
	
	public final String getPolynomialLabel2() {
		return getStringProperty(PROP_POLYNOMIAL_2_LABEL);
	}
	
	public final String getPolynomialLabel3() {
		return getStringProperty(PROP_POLYNOMIAL_3_LABEL);
	}
	
	public final String getPolynomialLabel4() {
		return getStringProperty(PROP_POLYNOMIAL_4_LABEL);
	}
	
	public final String getPolynomialLabel5() {
		return getStringProperty(PROP_POLYNOMIAL_5_LABEL);
	}
	
	public final String getPolynomialLabel6() {
		return getStringProperty(PROP_POLYNOMIAL_6_LABEL);
	}
	
	public final String getPolynomialLabel7() {
		return getStringProperty(PROP_POLYNOMIAL_7_LABEL);
	}
	
	public final String getPolynomialLabel8() {
		return getStringProperty(PROP_POLYNOMIAL_8_LABEL);
	}
	
	public final String getPolynomialLabel9() {
		return getStringProperty(PROP_POLYNOMIAL_9_LABEL);
	}
	
	public final String getPolynomialLabel10() {
		return getStringProperty(PROP_POLYNOMIAL_10_LABEL);
	}
	
	public final int getPolynomialLineWidth() {
		return getIntegerProperty(PROP_POLYNOMIAL_LINE_WIDTH);
	}
	
	public final String getPolynomialLineColor() {
		return getColor(PROP_POLYNOMIAL_LINE_COLOR);
	}
	
	public final String getPolynomialLabelColor() {
		return getColor(PROP_POLYNOMIAL_LABEL_COLOR);
	}
	
	public final int getPolylineLineWidth() {
		return getIntegerProperty(PROP_POLYLINE_LINE_WIDTH);
	}
	
	public final String getPolylineLineColor() {
		return getColor(PROP_POLYLINE_LINE_COLOR);
	}
	
	public final Polyline getPolyline1() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_1));
	}
	
	public final Polyline getPolyline2() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_2));
	}
	
	public final Polyline getPolyline3() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_3));
	}
	
	public final Polyline getPolyline4() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_4));
	}
	
	public final Polyline getPolyline5() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_5));
	}
	
	public final Polyline getPolyline6() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_6));
	}
	
	public final Polyline getPolyline7() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_7));
	}
	
	public final Polyline getPolyline8() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_8));
	}
	
	public final Polyline getPolyline9() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_9));
	}

	public final Polyline getPolyline10() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_POLYLINE_10));
	}
	
	private Polyline convertDoublesToPolyLine(double... doubleArray) {
		List<Coordinate2D> coordinates = new ArrayList<Coordinate2D>();
		for (int i = 0; i < doubleArray.length-1; i += 2) {
			coordinates.add(new Coordinate2D(doubleArray[i], doubleArray[i+1]));
		}
		return new Polyline(coordinates.toArray(new Coordinate2D[coordinates.size()]));
	}
	
	public final Polyline getUpperPolyline() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_FIELD_OF_WORK_UPPER));
	}
	
	public final Polyline getLowerPolyline() {
		return convertDoublesToPolyLine(getDoubleArrayProperty(PROP_FIELD_OF_WORK_LOWER));
	}
	
	public final int getFieldOfWorkLineWidth() {
		return getIntegerProperty(PROP_FIELD_OF_WORK_WIDTH);
	}
	
	public final String getFieldOfWorkColor() {
		return getColor(PROP_FIELD_OF_WORK_COLOR);
	}
	
	public final int getNumberOfPoints() {
		return getIntegerProperty(PROP_NUMBER_OF_POINTS);
	}
	
	public final int getPointSize() {
		return getIntegerProperty(PROP_POINT_SIZE);
	}
	
	public final double getPointBrightness() {
		return getDoubleProperty(PROP_POINT_BRIGHTNESS);
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
	
	public final String getWarningTextNearUpperBound() {
		return getStringProperty(PROP_WARNING_TEXT_NEAR_UPPER_BOUND);
	}
	
	public final String getWarningTextNearLowerBound() {
		return getStringProperty(PROP_WARNING_TEXT_NEAR_LOWER_BOUND);
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
