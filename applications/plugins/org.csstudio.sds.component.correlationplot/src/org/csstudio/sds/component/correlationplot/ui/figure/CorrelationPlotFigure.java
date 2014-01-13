package org.csstudio.sds.component.correlationplot.ui.figure;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.sds.component.correlationplot.model.Axis;
import org.csstudio.sds.component.correlationplot.model.Coordinate2D;
import org.csstudio.sds.component.correlationplot.model.Plot;
import org.csstudio.sds.component.correlationplot.model.PlotStyleProvider;
import org.csstudio.sds.component.correlationplot.model.PlotValue;
import org.csstudio.sds.component.correlationplot.model.Polyline;
import org.csstudio.sds.component.correlationplot.model.Polynomial;
import org.csstudio.sds.component.correlationplot.model.RGB;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class CorrelationPlotFigure extends LayeredPane implements Plot {

	private Point offset;
	private Axis xAxis;
	private Axis yAxis;
	private AxisLayer axisLayer;
	private PolynomialLayer polynomialLayer;
	private PolylineLayer polylineLayer;
	private PolylineLayer fieldOfWorkLayer;
	private PlotValueLayer plotValueLayer;
	private CachedBackgroundLayer cachedBackgroundLayer;
	private MaskLayer maskLayer;
	private final DecimalFormat axisDecimalFormat;
	
	public CorrelationPlotFigure(Axis xAxis, Axis yAxis, PlotStyleProvider styleProvider) {
		super();
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.offset = new Point(20,20);
		axisDecimalFormat = new DecimalFormat("0.########");
		
		polynomialLayer = new PolynomialLayer(xAxis, yAxis);
		polylineLayer = new PolylineLayer(xAxis, yAxis);
		fieldOfWorkLayer = new FieldOfWorkLayer(xAxis, yAxis);
		maskLayer = new MaskLayer(xAxis, yAxis);
		cachedBackgroundLayer = new CachedBackgroundLayer(polynomialLayer, polylineLayer, fieldOfWorkLayer, maskLayer, xAxis, yAxis);
		cachedBackgroundLayer.setEnabled(true);
		add(cachedBackgroundLayer);
		
		plotValueLayer = new PlotValueLayer(xAxis, yAxis);
		plotValueLayer.setEnabled(true);
		add(plotValueLayer);
		
		axisLayer = new AxisLayer(xAxis, yAxis, axisDecimalFormat);
		axisLayer.setEnabled(true);
		add(axisLayer);
		
		setStyleProvider(styleProvider);
		
		addFigureListener(new FigureListener() {
			@Override
			public void figureMoved(IFigure arg0) {
				CorrelationPlotFigure.this.configureAxes();
			}
		});
	}
	
	private void configureAxes() {
		Rectangle clientArea = getClientArea();
		TextUtilities textUtilities = new TextUtilities();

		if (getFont() != null) {
			int xNameHeight = textUtilities.getStringExtents(xAxis.getName(),
					getFont()).height();
			int xAxisLabelsMaxHeight = 0;

			
			// Height of x axis is only dependent on maximum height of number values in font
			for (int number = 0; number < 10; number++) {
				xAxisLabelsMaxHeight = Math.max(
						xAxisLabelsMaxHeight,
						textUtilities.getStringExtents(Integer.toString(number),
								getFont()).height());
			}
			offset.y = xNameHeight + xAxisLabelsMaxHeight + 5;

			String xAxisMinValueString = axisDecimalFormat.format(xAxis.getMinValue().doubleValue());
			String xAxisMaxValueString = axisDecimalFormat.format(xAxis.getMaxValue().doubleValue());
			int xAxisLabelsMaxWidth = Math.max(
					textUtilities.getStringExtents(xAxisMinValueString,getFont()).width(),
					textUtilities.getStringExtents(xAxisMaxValueString,getFont()).width());
			xAxis.setScaleValueWidth(xAxisLabelsMaxWidth + 20);
		}
		yAxis.setMappingMinValue(clientArea.height + clientArea.y - offset.y);
		yAxis.setMappingMaxValue(clientArea.y);
		
		if (getFont() != null) {
			int yNameHeight = textUtilities.getStringExtents(yAxis.getName(),
					getFont()).height();
			int scaleValueMaxWidth = 0;

			List<Double> scaleValues = yAxis.getScaleValues();
			for (Double value : scaleValues) {
				scaleValueMaxWidth = Math.max(
						scaleValueMaxWidth,
						textUtilities.getStringExtents(axisDecimalFormat.format(value),
								getFont()).width());
			}
			offset.x = yNameHeight + scaleValueMaxWidth + 8;
			yAxis.setScaleValueWidth(30);

		}
		xAxis.setMappingMinValue(clientArea.x + offset.x);
		xAxis.setMappingMaxValue(clientArea.width + clientArea.x);
	}
	
	@Override
	public void setStyleProvider(PlotStyleProvider styleProvider) {
		polynomialLayer.setStyleProvider(styleProvider);
		polylineLayer.setStyleProvider(styleProvider);
		fieldOfWorkLayer.setStyleProvider(styleProvider);
		plotValueLayer.setStyleProvider(styleProvider);
		axisLayer.setStyleProvider(styleProvider);
	}
	
	@Override
	public Axis getXAxis() {
		return xAxis;
	}

	@Override
	public Axis getYAxis() {
		return yAxis;
	}

	@Override
	public void setPolynomials(List<Polynomial> polynomials) {
		cachedBackgroundLayer.clearCache();
		polynomialLayer.setPolynomials(polynomials);
		repaint();
	}

	@Override
	public void setPolynomial(int index, Polynomial polynomial) {
		cachedBackgroundLayer.clearCache();
		polynomialLayer.setPolynomial(index, polynomial);
		repaint();
	}
	
	@Override
	public void setPolylines(List<Polyline> polylines) {
		cachedBackgroundLayer.clearCache();
		polylineLayer.setPolyLines(polylines);
		repaint();
	}
	
	@Override
	public void setPolyline(int index, Polyline polyline) {
		cachedBackgroundLayer.clearCache();
		polylineLayer.setPolyline(index, polyline);
		repaint();
	}
	
	@Override
	public void setFieldOfWorkPolygon(Polyline fieldOfWork) {
		cachedBackgroundLayer.clearCache();
		
		List<Polyline> polylineList = (fieldOfWork != null) ? Collections.singletonList(fieldOfWork) : Collections.<Polyline>emptyList();
		fieldOfWorkLayer.setPolyLines(polylineList);
		
		repaint();
	}
	
	@Override
	public void setMask(Polyline mask) {
		cachedBackgroundLayer.clearCache();
		
		Polyline theMask = mask != null ? mask : Polyline.EMPTY_POLYLINE;  
		maskLayer.setMaskPolygon(theMask);
		
		repaint();
	}

	@Override
	public void setPlotValues(Collection<PlotValue> plotValues) {
		plotValueLayer.setPlotValues(plotValues);
	}

	@Override
	public void setWarning(String text) {
		plotValueLayer.setWarning(text);
	}

	@Override
	public void onUpdatedConfiguration() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				cachedBackgroundLayer.clearCache();
				configureAxes();
				CorrelationPlotFigure.this.repaint();
			}
		});
	}

	@Override
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		maskLayer.setBackgroundColor(bg);
		cachedBackgroundLayer.clearCache();
		cachedBackgroundLayer.setBackgroundColor(bg);
	}
	
	private static class PolynomialLayer {
		
		private final Axis xAxis;
		private final Axis yAxis;
		private List<Polynomial> polynomials;
		private PlotStyleProvider styleProvider;
		
		public PolynomialLayer(Axis xAxis, Axis yAxis) {
			this.xAxis = xAxis;
			this.yAxis = yAxis;
			this.polynomials = new ArrayList<Polynomial>();
		}
		
		public void paintFigure(Graphics graphics) {
			// Styles für Polynom setzen
			if (styleProvider != null) {
				RGB color = styleProvider.getPolynomialColor();
				Color swtColor = new Color(Display.getDefault(), color.getRed(), color.getGreen(), color.getBlue());
				graphics.setForegroundColor(swtColor);
				graphics.setBackgroundColor(swtColor);
				graphics.setLineWidth(styleProvider.getPolynomialLineWidth());
			}
			for (Polynomial polynomial : polynomials) {
				if (polynomial.isDrawable()) {
					paintPolynomial(polynomial, graphics);
				}
			}
		}
		
		private void paintPolynomial(Polynomial polynomial, Graphics graphics){
			int numberOfPixels = Math.abs(xAxis.getMappingMaxValue() - xAxis.getMappingMinValue());
			
			PointList drawList = new PointList();
			BigDecimal[] pixelXValues = xAxis.getStepsForResolution(numberOfPixels);
			for (int pixelIndex = 0; pixelIndex < pixelXValues.length; pixelIndex++) {
				BigDecimal domainX = pixelXValues[pixelIndex];
				BigDecimal domainY = polynomial.getValueForX(domainX);
				drawList.addPoint(xAxis.getMappingMinValue() + pixelIndex,
						fixPlotYValue(yAxis.getMappingValueFor(domainY)));
			}
			graphics.drawPolyline(drawList);
		}
		
		private int fixPlotYValue(int yValue) {
			int result = yValue;
			
			if (yValue < yAxis.getMappingMaxValue()) {
				result = yAxis.getMappingMaxValue() - 10;
			} else if (yValue > yAxis.getMappingMinValue()) {
				result = yAxis.getMappingMinValue() + 10;
			}
			return result;
		}
		
		public void setPolynomials(List<Polynomial> polynomials) {
			this.polynomials = polynomials;
		}
		
		public void setPolynomial(int index, Polynomial polynomial) {
			if (polynomials.size() >= index) {
				polynomials.set(index, polynomial);
			}
		}

		public void setStyleProvider(PlotStyleProvider styleProvider) {
			this.styleProvider = styleProvider;
		}
	}

	private static class AxisLayer extends Layer {
			private static final int DISTANCE_AXISTIP_TO_NAME = 15;
		
			private Axis xAxis;
			private Axis yAxis;
			private PlotStyleProvider styleProvider;

			private DecimalFormat axisDecimalFormat;
	
			public AxisLayer(Axis xAxis, Axis yAxis, DecimalFormat axisDecimalFormat) {
				this.xAxis = xAxis;
				this.yAxis = yAxis;
				this.axisDecimalFormat = axisDecimalFormat;
			}
	
			@Override
			protected void paintFigure(Graphics graphics) {
				super.paintFigure(graphics);
				
				if (styleProvider != null) {
					RGB color = styleProvider.getAxisColor();
					Color swtColor = new Color(Display.getDefault(), color.getRed(), color.getGreen(), color.getBlue());
					graphics.setForegroundColor(swtColor);
					graphics.setBackgroundColor(swtColor);
				}
				
				Point origin = new Point(xAxis.getMappingValueFor(xAxis.getMinValue()), (int)Math.round(yAxis.getMappingValueFor(yAxis.getMinValue())));
				Point xTip = new Point(xAxis.getMappingValueFor(xAxis.getMaxValue()), origin.y);
				Point yTip = new Point(origin.x, yAxis.getMappingValueFor(yAxis.getMaxValue()));
	
				// x axis
				graphics.drawLine(origin.x, origin.y, xTip.x, xTip.y);
				int[] xTipArrow = new int[] {xTip.x,xTip.y, 
											xTip.x - 10,xTip.y - 5, 
											xTip.x - 10,xTip.y + 5};
				graphics.fillPolygon(xTipArrow);
				graphics.drawPolygon(xTipArrow);
				
				// y axis
				graphics.drawLine(origin.x, origin.y, yTip.x, yTip.y);
				int[] yTipArrow = new int[] {yTip.x,yTip.y, 
						yTip.x - 5,yTip.y + 10, 
						yTip.x + 5,yTip.y + 10};
				graphics.fillPolygon(yTipArrow);
				graphics.drawPolygon(yTipArrow);
	
				// labels
				TextUtilities textUtilities = new TextUtilities();
				Dimension stringExtents = textUtilities.getStringExtents(xAxis.getName(), getFont());
				
				graphics.drawString(xAxis.getName(), xTip.x - stringExtents.width - DISTANCE_AXISTIP_TO_NAME, getClientArea().height + getClientArea().y - stringExtents.height);
				
				graphics.rotate(-90); // handle with care
	
				stringExtents = textUtilities.getStringExtents(yAxis.getName(), getFont());
				graphics.drawString(yAxis.getName(), -DISTANCE_AXISTIP_TO_NAME - stringExtents.width - yTip.y, getClientArea().x);
				
				graphics.rotate(90);
				
				//drawScales(graphics);
				int originX = xAxis.getMappingValueFor(xAxis.getMinValue());
				int originY = yAxis.getMappingValueFor(yAxis.getMinValue());
				
				// x axis
				List<Double> xScaleValues = xAxis.getScaleValues();
				for(Double value : xScaleValues) {
					int drawX = xAxis.getMappingValueFor(new BigDecimal(value, MathContext.DECIMAL128));
					String valueString  = axisDecimalFormat.format(value);
					graphics.drawLine(drawX, originY, drawX, originY + 5);
					stringExtents = textUtilities.getStringExtents(valueString, getFont());
					drawX -= (drawX + stringExtents.width > getClientArea().x + getClientArea().width) ? stringExtents.width : stringExtents.width / 2;
					graphics.drawString(valueString, drawX, originY + 5);
				}
				
				// y axis
				List<Double> yScaleValues = yAxis.getScaleValues();
				for(Double value : yScaleValues) {
					int drawY = yAxis.getMappingValueFor(new BigDecimal(value, MathContext.DECIMAL128));
					String valueString  = axisDecimalFormat.format(value);
					graphics.drawLine(originX, drawY, originX - 5, drawY);
					stringExtents = textUtilities.getStringExtents(valueString, getFont());
					drawY -= (drawY - stringExtents.height < getClientArea().y) ? 0 : stringExtents.height / 2;
					graphics.drawString(valueString, originX - 8 - stringExtents.width, drawY);
				}
			}
			
			public void setStyleProvider(PlotStyleProvider styleProvider) {
				this.styleProvider = styleProvider;
			}
		}

	private static class CachedBackgroundLayer extends Layer {

		private PolynomialLayer polynomialLayer;
		private PolylineLayer polylineLayer;
		private PolylineLayer fieldOfWorkLayer;
		private Axis xAxis;
		private Axis yAxis;
		private Image cachedImage;
		private int xAxisMappingRange;
		private int yAxisMappingRange;
		private MaskLayer maskLayer;

		public CachedBackgroundLayer(PolynomialLayer polynomialLayer,
				PolylineLayer polylineLayer, PolylineLayer fieldOfWorkLayer, MaskLayer maskLayer, Axis xAxis, Axis yAxis) {
					this.polynomialLayer = polynomialLayer;
					this.polylineLayer = polylineLayer;
					this.fieldOfWorkLayer = fieldOfWorkLayer;
					this.maskLayer = maskLayer;
					this.xAxis = xAxis;
					this.yAxis = yAxis;
		}

		public void clearCache() {
			cachedImage = null;
		}

		@Override
		protected void paintFigure(Graphics graphics) {
			super.paintFigure(graphics);
			
			if (cachedImage == null || xAxisMappingRange != xAxis.getMappingRange() || yAxisMappingRange != yAxis.getMappingRange()) {
				xAxisMappingRange = xAxis.getMappingRange();
				yAxisMappingRange = yAxis.getMappingRange();
				cachedImage = new Image(Display.getDefault(),
						xAxisMappingRange, yAxisMappingRange);
				Color backgroundColor = getBackgroundColor();
				GC gc = new GC(cachedImage);
				Graphics imageGraphics = new SWTGraphics(gc);
				imageGraphics.setBackgroundColor(backgroundColor);
				imageGraphics.fillRectangle(0, 0,
						cachedImage.getBounds().width,
						cachedImage.getBounds().height);
				imageGraphics.translate(-xAxis.getMappingMinValue(),
						-yAxis.getMappingMaxValue());
				polynomialLayer.paintFigure(imageGraphics);
				polylineLayer.paintFigure(imageGraphics);
				maskLayer.paintFigure(imageGraphics);
				fieldOfWorkLayer.paintFigure(imageGraphics);
				gc.dispose(); // TODO disposing in finally-Block
			}
			
			graphics.drawImage(cachedImage, xAxis.getMappingMinValue(), yAxis.getMappingMaxValue());
		}
	}
	
	private static class PolylineLayer {
	
		private final Axis xAxis;
		private final Axis yAxis;
		private List<Polyline> polylines;
		private PlotStyleProvider styleProvider;
	
		public PolylineLayer(Axis xAxis, Axis yAxis) {
			this.xAxis = xAxis;
			this.yAxis = yAxis;
			this.polylines = new ArrayList<Polyline>();
		}
		
		protected void configureStyle(Graphics graphics) {
			if (styleProvider != null) {
				RGB color = styleProvider.getPolylineColor();
				Color swtColor = new Color(Display.getDefault(), color.getRed(), color.getGreen(), color.getBlue());
				graphics.setForegroundColor(swtColor);
				graphics.setBackgroundColor(swtColor);
				graphics.setLineWidth(styleProvider.getPolylineWidth());
			}
		}
		
		public void paintFigure(Graphics graphics) {
			configureStyle(graphics);
			for (Polyline polyLine : polylines) {
				paintPolyLine(polyLine, graphics);
			}
		}
		
		private void paintPolyLine(Polyline polyLine, Graphics graphics) {
			Coordinate2D[] coordinates = polyLine.getCoordinates();
			int[] drawArray = new int[coordinates.length * 2];
	
			for (int coordinateIndex = 0; coordinateIndex < coordinates.length; coordinateIndex++) {
				Coordinate2D coordinate = coordinates[coordinateIndex];
				drawArray[coordinateIndex * 2] = xAxis.getMappingValueFor(new BigDecimal(coordinate.getX()));
				drawArray[coordinateIndex * 2 + 1] = yAxis.getMappingValueFor(new BigDecimal(coordinate.getY()));
			}
			
			graphics.drawPolyline(drawArray);
		}
		
		public void setPolyLines(List<Polyline> polyLines) {
			this.polylines = polyLines;
		}
		
		public void setPolyline(int index, Polyline polyline) {
			if (polylines.size() >= index) {
				polylines.set(index, polyline);
			}
		}


		
		public PlotStyleProvider getStyleProvider() {
			return styleProvider;
		}
	
		public void setStyleProvider(PlotStyleProvider styleProvider) {
			this.styleProvider = styleProvider;
		}
	}
	
	private static class FieldOfWorkLayer extends PolylineLayer {

		public FieldOfWorkLayer(Axis xAxis, Axis yAxis) {
			super(xAxis, yAxis);
		}
		
		protected void configureStyle(Graphics graphics) {
			if (getStyleProvider() != null) {
				RGB color = getStyleProvider().getFieldOfWorkLineColor();
				Color swtColor = new Color(Display.getDefault(), color.getRed(), color.getGreen(), color.getBlue());
				graphics.setForegroundColor(swtColor);
				graphics.setBackgroundColor(swtColor);
				graphics.setLineWidth(getStyleProvider().getFieldOfWorkLineWidth());
			}
		}

	}

	private static class MaskLayer {
		
		private final Axis xAxis;
		private final Axis yAxis;
		private Polyline maskPolygon;
		private Color backgroundColor;
	
		public MaskLayer(Axis xAxis, Axis yAxis) {
			this.xAxis = xAxis;
			this.yAxis = yAxis;
		}
		
		public void setBackgroundColor(Color bg) {
			this.backgroundColor = bg;
		}

		public void paintFigure(Graphics graphics) {
			if(!maskPolygon.isEmpty()) {
				Coordinate2D[] coordinates = maskPolygon.getCoordinates();
				int[] drawArray = new int[coordinates.length * 2 + 10];
		
				drawArray[0] = xAxis.getMappingMinValue();
				drawArray[1] = yAxis.getMappingMinValue();
				drawArray[2] = xAxis.getMappingMaxValue();
				drawArray[3] = yAxis.getMappingMinValue();
				drawArray[4] = xAxis.getMappingMaxValue();
				drawArray[5] = yAxis.getMappingMaxValue();
				drawArray[6] = xAxis.getMappingMinValue();
				drawArray[7] = yAxis.getMappingMaxValue();
				drawArray[8] = xAxis.getMappingMinValue();
				drawArray[9] = yAxis.getMappingMinValue();
				
				for (int coordinateIndex = 0; coordinateIndex < coordinates.length; coordinateIndex++) {
					Coordinate2D coordinate = coordinates[coordinateIndex];
					drawArray[coordinateIndex * 2 + 10] = xAxis.getMappingValueFor(new BigDecimal(coordinate.getX()));
					drawArray[coordinateIndex * 2 + 11] = yAxis.getMappingValueFor(new BigDecimal(coordinate.getY()));
				}
				graphics.setBackgroundColor(backgroundColor);
				graphics.fillPolygon(drawArray);			
			}
		}
		
		public void setMaskPolygon(Polyline maskPolygon) {
			this.maskPolygon = maskPolygon.getClosed();
		}
	}

	
	private static class PlotValueLayer extends Layer {
	
		private final Axis xAxis;
		private final Axis yAxis;
		private Collection<PlotValue> plotValues;
		private PlotStyleProvider styleProvider;
		private String warning = "";
		private Font warningTextFont;
	
		public PlotValueLayer(Axis xAxis, Axis yAxis) {
			this.xAxis = xAxis;
			this.yAxis = yAxis;
			this.plotValues = new ArrayList<PlotValue>();
		}
		
		@Override
		protected void paintFigure(Graphics graphics) {
			super.paintFigure(graphics);
			
			synchronized (this.plotValues) {
				int i = 0;
				for (PlotValue plotValue : plotValues) {
					if (styleProvider != null) {
						RGB colorForPlotValue = styleProvider.getColorForPlotValue(plotValue, plotValues.size() - i - 1);
						Color swtColor = new Color(Display.getDefault(), colorForPlotValue.getRed(), colorForPlotValue.getGreen(), colorForPlotValue.getBlue());
						graphics.setForegroundColor(swtColor);
						graphics.setBackgroundColor(swtColor);
					}
					paintPlotValue(graphics, plotValue);
					i++;
				}
			}
			
			// draw warning text
			if (!warning.isEmpty()) {
				int xOffset = 20;
				int yOffset = 20;
				if (styleProvider != null) {
					RGB warningColor = styleProvider.getWarningTextColor();
					Color swtColor = new Color(Display.getDefault(),
							warningColor.getRed(), warningColor.getGreen(),
							warningColor.getBlue());
					graphics.setForegroundColor(swtColor);
					graphics.setBackgroundColor(swtColor);

					int warningheight = styleProvider
							.getWarningTextFontHeight();
					int warningTextFontStyle = styleProvider
							.getWarningTextFontStyle();
					String warningTextFontName = styleProvider
							.getWarningTextFontName();
					Font oldFont = warningTextFont;
					warningTextFont = new Font(Display.getDefault(),
							warningTextFontName, warningheight,
							warningTextFontStyle);
					graphics.setFont(warningTextFont);
					if (oldFont != null) {
						oldFont.dispose();
					}

					Coordinate2D warningPosition = styleProvider
							.getWarningTextPosition();
					xOffset = (int) warningPosition.getX();
					yOffset = (int) warningPosition.getY();
				}
				graphics.drawString(warning, getClientArea().x + xOffset,
						getClientArea().y + yOffset);
			}
		}
	
		private void paintPlotValue(Graphics graphics, PlotValue plotValue) {
			int mappingValueForX = xAxis.getMappingValueFor(new BigDecimal(plotValue.getX()));
			int mappingValueForY = yAxis.getMappingValueFor(new BigDecimal(plotValue.getY()));
			switch (plotValue.getType()) {
			case NORMAL:
				paintNormal(graphics, mappingValueForX, mappingValueForY);
				break;
	
			case TIME_WARNING_1:
				paintWarning1(graphics, mappingValueForX, mappingValueForY);
				break;
				
			case TIME_WARNING_2:
				paintWarning2(graphics, mappingValueForX, mappingValueForY);
				break;
				
			default:
				break;
			}
		}
	
		private void paintNormal(Graphics graphics, int x, int y) {
			int lineWidth = 1;
			int drawOffset = 3;
			
			if (styleProvider != null) {
				lineWidth = styleProvider.getPlotValueSize() / 6 + 1;
				drawOffset = styleProvider.getPlotValueSize();
			}
			
			graphics.setLineWidth(lineWidth);
			graphics.drawOval(x - drawOffset, y - drawOffset, drawOffset * 2, drawOffset * 2);
			graphics.drawPoint(x, y);
		}
	
		private void paintWarning1(Graphics graphics, int x, int y) {
			int lineWidth = 1;
			int drawOffset = 3;
			
			if (styleProvider != null) {
				lineWidth = styleProvider.getPlotValueSize() / 6 + 1;
				drawOffset = styleProvider.getPlotValueSize();
			}
			
			graphics.setLineWidth(lineWidth);
			graphics.drawRectangle(x - drawOffset, y - drawOffset, drawOffset * 2, drawOffset * 2);
			graphics.drawPoint(x, y);
		}
	
		private void paintWarning2(Graphics graphics, int x, int y) {
			int lineWidth = 1;
			int drawOffset = 3;
			
			if (styleProvider != null) {
				lineWidth = styleProvider.getPlotValueSize() / 6 + 1;
				drawOffset = styleProvider.getPlotValueSize();
			}
			
			graphics.setLineWidth(lineWidth);
			graphics.drawLine(x - drawOffset, y - drawOffset, x + drawOffset, y + drawOffset);
			graphics.drawLine(x - drawOffset, y + drawOffset, x + drawOffset, y - drawOffset);
		}
		
		public void setStyleProvider(PlotStyleProvider styleProvider) {
			this.styleProvider = styleProvider;
		}
		
		public void setWarning(String warning) {
			if (!this.warning.equals(warning)) {
				this.warning = warning;
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						repaint();					
					}
				});
			}
		}

		public void setPlotValues(Collection<PlotValue> plotValues) {
			synchronized (plotValues) {
				this.plotValues = plotValues;
			}
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					repaint();					
				}
			});
		}
	}
}
