package org.csstudio.sds.component.correlationplot.model;

public class DefaultPlotStyleProvider implements PlotStyleProvider {
	private double brightness = 0.8;
	private RGB polynomialColor;
	private int polynomialLineWidth;
	private RGB polylineColor;
	private int polylineWidth;
	private int plotValueSize;
	private String warningTextFontName;
	private int warningTextFontHeight;
	private int warningTextFontStyle;
	private RGB warningTextColor;
	private Coordinate2D warningTextPosition;
	private int numberOfPoints = 1;
	private RGB backgroundColor = RGB.WHITE_COLOR;
	private double brightnessDelta;;

	@Override
	public int getPolynomialLineWidth() {
		return polynomialLineWidth;
	}
	
	public void setPolynomialLineWidth(int polynomialLineWidth) {
		this.polynomialLineWidth = polynomialLineWidth;
	}

	@Override
	public LineStyle getPolynomialLineStyle() {
		return LineStyle.DOTTED_LINE;
	}
	
	@Override
	public RGB getPolynomialColor() {
		return polynomialColor;
	}
	
	public void setPolynomialColor(RGB polynomialColor) {
		this.polynomialColor = polynomialColor;
	}

	@Override
	public int getPolylineWidth() {
		return polylineWidth;
	}
	
	public void setPolylineWidth(Integer newValue) {
		this.polylineWidth = newValue;
	}

	@Override
	public LineStyle getPolylineStyle() {
		return LineStyle.DASHED_LINE;
	}
	
	@Override
	public RGB getPolylineColor() {
		return polylineColor;
	}
	
	public void setPolylineColor(RGB convertColorToRGB) {
		this.polylineColor = convertColorToRGB;
	}

	@Override
	public RGB getColorForPlotValue(PlotValue plotValue, int index) {
		RGB result = (plotValue.hasAlarm()) ? RGB.RED_COLOR : RGB.BLACK_COLOR;
		if (index > 0) {
			double brightnessValue = brightness - index * brightnessDelta;
			result = RGB.createColorBetween(getBackgroundColor(), result, brightnessValue);
		}
		
		return result;
	}
	
	public void setBrightness(double brightness) {
		this.brightness = brightness;
		brightnessDelta = brightness / numberOfPoints;
	}
	
	@Override
	public int getPlotValueSize() {
		return plotValueSize;
	}
	
	public void setPlotValueSize(int pointSize) {
		this.plotValueSize = pointSize;
	}

	@Override
	public RGB getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(RGB backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	@Override
	public RGB getAxisColor() {
		return RGB.BLACK_COLOR;
	}

	public void setWarningTextFont(String fontName, int fontHeight,
			int fontStyle) {
				this.warningTextFontName = fontName;
				this.warningTextFontHeight = fontHeight;
				this.warningTextFontStyle = fontStyle;
	}

	public void setWarningTextColor(RGB warningTextColor) {
		this.warningTextColor = warningTextColor;
		
	}

	public void setWarningTextPosition(Coordinate2D warningTextPosition) {
		this.warningTextPosition = warningTextPosition;
	}
	
	@Override
	public Coordinate2D getWarningTextPosition() {
		return warningTextPosition;
	}
	
	@Override
	public RGB getWarningTextColor() {
		return warningTextColor;
	}
	
	@Override
	public String getWarningTextFontName() {
		return warningTextFontName;
	}
	
	@Override
	public int getWarningTextFontHeight() {
		return warningTextFontHeight;
	}
	
	@Override
	public int getWarningTextFontStyle() {
		return warningTextFontStyle;
	}

	public void setNumberOfPoints(Integer numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
		brightnessDelta = brightness / numberOfPoints;
	}
}
