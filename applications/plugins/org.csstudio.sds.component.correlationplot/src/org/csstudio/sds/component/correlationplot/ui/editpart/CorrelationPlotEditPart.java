package org.csstudio.sds.component.correlationplot.ui.editpart;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.component.correlationplot.model.Axis;
import org.csstudio.sds.component.correlationplot.model.Coordinate2D;
import org.csstudio.sds.component.correlationplot.model.CorrelationPlotModel;
import org.csstudio.sds.component.correlationplot.model.DefaultPlotStyleProvider;
import org.csstudio.sds.component.correlationplot.model.FieldOfWork;
import org.csstudio.sds.component.correlationplot.model.Plot;
import org.csstudio.sds.component.correlationplot.model.PlotController;
import org.csstudio.sds.component.correlationplot.model.PlotWarningListener;
import org.csstudio.sds.component.correlationplot.model.Polyline;
import org.csstudio.sds.component.correlationplot.model.Polynomial;
import org.csstudio.sds.component.correlationplot.model.RGB;
import org.csstudio.sds.component.correlationplot.ui.figure.CorrelationPlotFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;

public class CorrelationPlotEditPart extends AbstractWidgetEditPart {

	private Axis xAxis;
	private Axis yAxis;
	private DefaultPlotStyleProvider styleProvider;
	private PlotController plotController;

	@Override
	protected IFigure doCreateFigure() {
		// get the model
		final CorrelationPlotModel model = (CorrelationPlotModel)getModel();
		
		// create the axis
		xAxis = new Axis(model.getXAxisName(), model.getXAxisMinimum(), model.getXAxisMaximum());
		yAxis = new Axis(model.getYAxisName(), model.getYAxisMinimum(), model.getYAxisMaximum());
		
		// create and configure styleprovider
		styleProvider = new DefaultPlotStyleProvider();
		styleProvider.setPolynomialColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_POLYNOMIAL_LINE_COLOR)));
		styleProvider.setPolynomialLineWidth(model.getPolynomialLineWidth());
		styleProvider.setFieldOfWorkLineColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_FIELD_OF_WORK_COLOR)));
		styleProvider.setFieldOfWorkLineWidth(model.getFieldOfWorkLineWidth());
		styleProvider.setPolylineColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_POLYLINE_LINE_COLOR)));
		styleProvider.setPolylineWidth(model.getPolylineLineWidth());
		styleProvider.setPlotValueSize(model.getPointSize());
		styleProvider.setBrightness(model.getPointBrightness());
		FontData warningTextFontData = getModelFont(CorrelationPlotModel.PROP_WARNING_TEXT_FONT).getFontData()[0];
		String fontName = warningTextFontData.getName();
		int fontHeight = warningTextFontData.getHeight();
		int fontStyle = warningTextFontData.getStyle();
		styleProvider.setWarningTextFont(fontName, fontHeight, fontStyle);
		styleProvider.setWarningTextColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_WARNING_TEXT_COLOR)));
		styleProvider.setWarningTextPosition(new Coordinate2D(model.getWarningTextPositionX(), model.getWarningTextPositionY()));
		styleProvider.setNumberOfPoints(model.getNumberOfPoints());
		styleProvider.setBackgroundColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_COLOR_BACKGROUND)));
		
		
		// create the figure
		CorrelationPlotFigure figure = new CorrelationPlotFigure(xAxis, yAxis, styleProvider);
		
		// create polynomial list
		List<Polynomial> polynomials = new ArrayList<Polynomial>(10);
		polynomials.add(new Polynomial(model.getPolynomial1()));
		polynomials.add(new Polynomial(model.getPolynomial2()));
		polynomials.add(new Polynomial(model.getPolynomial3()));
		polynomials.add(new Polynomial(model.getPolynomial4()));
		polynomials.add(new Polynomial(model.getPolynomial5()));
		polynomials.add(new Polynomial(model.getPolynomial6()));
		polynomials.add(new Polynomial(model.getPolynomial7()));
		polynomials.add(new Polynomial(model.getPolynomial8()));
		polynomials.add(new Polynomial(model.getPolynomial9()));
		polynomials.add(new Polynomial(model.getPolynomial10()));

		// create polyline list
		List<Polyline> polylines = new ArrayList<Polyline>(10);
		polylines.add(model.getPolyline1());
		polylines.add(model.getPolyline2());
		polylines.add(model.getPolyline3());
		polylines.add(model.getPolyline4());
		polylines.add(model.getPolyline5());
		polylines.add(model.getPolyline6());
		polylines.add(model.getPolyline7());
		polylines.add(model.getPolyline8());
		polylines.add(model.getPolyline9());
		polylines.add(model.getPolyline10());
		
		// create field of work
		FieldOfWork fieldOfWork = new FieldOfWork(model.getUpperPolyline(), model.getLowerPolyline());
		
		// create plot controller
		double minDistance = model.getWarningDistance();
		int numberOfPoints = model.getNumberOfPoints();
		long waitTimeForSecondValue = 200;
		long waitTime1 = model.getWaittime1InMillis();
		final long waitTime2 = model.getWaittime2InMillis();
		
		String warningTextNearUpperBound = model.getWarningTextNearUpperBound();
		String warningTextNearLowerBound = model.getWarningTextNearLowerBound();
		String warningTextOutOfBounds = model.getWarningTextOutOfBounds();
		
		plotController = new PlotController(figure,
				polynomials, polylines, fieldOfWork, minDistance, numberOfPoints,waitTimeForSecondValue, waitTime1, waitTime2, warningTextNearUpperBound, warningTextNearLowerBound, warningTextOutOfBounds);
		plotController.setWarningListener(new PlotWarningListener() {
			@Override
			public void onOutOfBounds() {
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_UPPER_BOUND, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_LOWER_BOUND, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_OUT_OF_BOUNDS, true);
			}
			@Override
			public void onNearUpperBound() {
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_OUT_OF_BOUNDS, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_LOWER_BOUND, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_UPPER_BOUND, true);
			}
			@Override
			public void onNearLowerBound() {
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_OUT_OF_BOUNDS, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_UPPER_BOUND, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_LOWER_BOUND, true);
			}
			@Override
			public void onNoWarning() {
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_OUT_OF_BOUNDS, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_UPPER_BOUND, false);
				model.setPropertyValue(CorrelationPlotModel.PROP_ALARM_LOWER_BOUND, false);
			}
		});
		
		
		// TODO remove! it is just for some first test values
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				final Random random = new Random();
//				double xValue = 5.5 - random.nextDouble();
//				double yValue = 5.5 - random.nextDouble();
//				while (true) {
//					try {
//						xValue += .2 - random.nextDouble() * 0.4;
//						xValue = (xValue + 10) % 10;
//						plotController.handleXValue(xValue);
//						
//						yValue += .2 - random.nextDouble() * 0.4;
//						yValue = (yValue + 10) % 10;
//						plotController.handleYValue(yValue);
//						
//						try {
//							Thread.sleep((long) (random.nextDouble() * 1.1 * waitTime2));
////							Thread.sleep(10);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					} catch (Throwable t) {
//						break;
//					}
//				}
//			}
//		}).start();
		
		return figure;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		
		setPropertyChangeHandler(CorrelationPlotModel.PROP_X_VALUE, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				plotController.handleXValue((Double) newValue);
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_Y_VALUE, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				plotController.handleYValue((Double) newValue);
				return true;
			}
		});
		
		setPropertyChangeHandler(CorrelationPlotModel.PROP_X_AXIS_NAME, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				xAxis.setName("" + newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_Y_AXIS_NAME, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				yAxis.setName("" + newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		//FIXME: X/Y Werte dürfen nicht gleich sein! Wie prüfen wir das?
		setPropertyChangeHandler(CorrelationPlotModel.PROP_X_AXIS_MIN, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				Double value = (Double) newValue;
				xAxis.setMinValue(value);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_X_AXIS_MAX, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				xAxis.setMaxValue((Double) newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_Y_AXIS_MIN, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				yAxis.setMinValue((Double) newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_Y_AXIS_MAX, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				yAxis.setMaxValue((Double) newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_1, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(0, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_2, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(1, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_3, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(2, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_4, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(3, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_5, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(4, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_6, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(5, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_7, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(6, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_8, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(7, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_9, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(8, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_10, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				((Plot)refreshableFigure).setPolynomial(9, new Polynomial((double[]) newValue));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_LINE_COLOR, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setPolynomialColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_POLYNOMIAL_LINE_COLOR)));
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYNOMIAL_LINE_WIDTH, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setPolynomialLineWidth((Integer)newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_1, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(0, model.getPolyline1());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_2, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(1, model.getPolyline2());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_3, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(2, model.getPolyline3());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_4, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(3, model.getPolyline4());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_5, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(4, model.getPolyline5());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_6, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(5, model.getPolyline6());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_7, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(6, model.getPolyline7());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_8, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(7, model.getPolyline8());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_9, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(8, model.getPolyline9());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_10, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				((Plot)refreshableFigure).setPolyline(9, model.getPolyline10());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_LINE_COLOR, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setPolylineColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_POLYLINE_LINE_COLOR)));
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POLYLINE_LINE_WIDTH, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setPolylineWidth((Integer)newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});

		setPropertyChangeHandler(CorrelationPlotModel.PROP_FIELD_OF_WORK_UPPER, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setFieldOfWork(new FieldOfWork(model.getUpperPolyline(), model.getLowerPolyline()));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_FIELD_OF_WORK_LOWER, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setFieldOfWork(new FieldOfWork(model.getUpperPolyline(), model.getLowerPolyline()));
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_FIELD_OF_WORK_COLOR, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setPolylineColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_FIELD_OF_WORK_COLOR)));
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_FIELD_OF_WORK_WIDTH, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setPolylineWidth((Integer)newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_NUMBER_OF_POINTS, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setNumberOfPoints((Integer)newValue);
				plotController.setNumberOfPoints((Integer)newValue);
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POINT_SIZE, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setPlotValueSize((Integer)newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_POINT_BRIGHTNESS, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setBrightness((Double) newValue);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WAITTIME_1, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setWaitTime1(model.getWaittime1InMillis());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WAITTIME_2, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setWaitTime2(model.getWaittime2InMillis());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_DISTANCE, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setWarningDistance(model.getWarningDistance());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_TEXT_NEAR_UPPER_BOUND, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setWarningTextNearUpperBound(model.getWarningTextNearUpperBound());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_TEXT_NEAR_LOWER_BOUND, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setWarningTextNearLowerBound(model.getWarningTextNearLowerBound());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_TEXT_OUT_OF_BOUNDS, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				CorrelationPlotModel model = (CorrelationPlotModel) getModel();
				plotController.setWarningTextOutOfBounds(model.getWarningTextOutOfBounds());
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_TEXT_COLOR, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setWarningTextColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_WARNING_TEXT_COLOR)));
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_TEXT_FONT, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				FontData warningTextFontData = getModelFont(CorrelationPlotModel.PROP_WARNING_TEXT_FONT).getFontData()[0];
				String fontName = warningTextFontData.getName();
				int fontHeight = warningTextFontData.getHeight();
				int fontStyle = warningTextFontData.getStyle();
				styleProvider.setWarningTextFont(fontName, fontHeight, fontStyle);
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_TEXT_X_POS, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				Coordinate2D warningPosition = styleProvider.getWarningTextPosition();
				styleProvider.setWarningTextPosition(new Coordinate2D(((Integer) newValue).doubleValue(), warningPosition.getY()));
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_WARNING_TEXT_Y_POS, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				Coordinate2D warningPosition = styleProvider.getWarningTextPosition();
				styleProvider.setWarningTextPosition(new Coordinate2D(warningPosition.getX(), ((Integer) newValue).doubleValue()));
				((Plot)refreshableFigure).onUpdatedConfiguration();
				return true;
			}
		});
		setPropertyChangeHandler(CorrelationPlotModel.PROP_COLOR_BACKGROUND, new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				styleProvider.setBackgroundColor(convertColorToRGB(getModelColor(CorrelationPlotModel.PROP_COLOR_BACKGROUND)));
				return true;
			}
		});
	}
	
	private RGB convertColorToRGB(Color modelColor){
		return new RGB(modelColor.getRed(), modelColor.getGreen(), modelColor.getBlue());
	}
	
	

}
