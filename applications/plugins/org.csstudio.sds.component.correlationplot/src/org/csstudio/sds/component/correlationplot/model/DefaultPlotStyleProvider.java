package org.csstudio.sds.component.correlationplot.model;

public class DefaultPlotStyleProvider implements PlotStyleProvider {
	private int brightness = 200;
	private RGB polynomialColor;
	private int polynomialLineWidth;
	private RGB polylineColor;
	private Integer polylineWidth;
	private int plotValueSize;
	private String warningTextFontName;
	private int warningTextFontHeight;
	private int warningTextFontStyle;
	private RGB warningTextColor;
	private Coordinate2D warningTextPosition;
	private int brightnessDelta;

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
		RGB result = null;
		int brightness = brightnessDelta * index;
		if(plotValue.hasAlarm()) {
			result = new RGB(255, brightness, brightness);
		} 
		else {
			result = new RGB(brightness, brightness, brightness);
		}
		return result;
	}
	
	public void setBrightness(int brightness) {
		this.brightness = brightness;
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
		return RGB.WHITE_COLOR;
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
		brightnessDelta = brightness / numberOfPoints;
	}
}
