package org.csstudio.sds.component.correlationplot.model;

public interface PlotStyleProvider {

	public static enum LineStyle {SOLID_LINE, DASHED_LINE, DOTTED_LINE} 
	
	RGB getColorForPlotValue(PlotValue plotValue, int index);
	int getPlotValueSize();
	
	RGB getAxisColor();
	RGB getBackgroundColor();

	RGB getPolynomialColor();
	int getPolynomialLineWidth();
	LineStyle getPolynomialLineStyle();

	RGB getFieldOfWorkLineColor();
	int getFieldOfWorkLineWidth();
	LineStyle getFieldOfWorkLineStyle();

	RGB getPolylineColor();
	int getPolylineWidth();
	LineStyle getPolylineStyle();
	
	Coordinate2D getWarningTextPosition();
	RGB getWarningTextColor();
	String getWarningTextFontName();
	int getWarningTextFontHeight();
	int getWarningTextFontStyle();
}
