package org.csstudio.common.trendplotter.ui;
/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/


//import org.csstudio.swt.xygraph.Preferences;
import org.csstudio.swt.xygraph.figures.Axis;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * TimeIndexLine
 * 
 * @author Christian Mein
 * 
 */
public class TimeIndexLine extends Figure {

    private Axis xAxis;
	private Axis yAxis;

	private double xValue;
	private double yValue;
	
	private IPositionListener positionListener;
	
	/**
	 * Construct a free annotation.
	 * 
	 * @param xAxis
	 *            the xAxis of the annotation.
	 * @param yAxis
	 *            the yAxis of the annotation.
	 */
	public TimeIndexLine(Axis xAxis, Axis yAxis) {
		this.xAxis = xAxis;
		this.yAxis = yAxis;

		setCursor(Cursors.CROSS);
		LineDragger dragger = new LineDragger(this);
		addMouseMotionListener(dragger);
		addMouseListener(dragger);
	}
	
	public void setPositonListener(IPositionListener listener) {
	    positionListener = listener;
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
//		
//		if (Preferences.useAdvancedGraphics())
//			graphics.setAntialias(SWT.ON);
		
		updateBoundsWithCurrentPosition();

		Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		graphics.setForegroundColor(red);

		// up
		graphics.drawLine(xAxis.getValuePosition(xValue, false), yAxis.getValuePosition(yValue, false), xAxis.getValuePosition(xValue, false),
				yAxis.getValuePosition(yAxis.getRange().getUpper(), false));
		// down
		graphics.drawLine(xAxis.getValuePosition(xValue, false), yAxis.getValuePosition(yValue, false), xAxis.getValuePosition(xValue, false),
				yAxis.getValuePosition(yAxis.getRange().getLower(), false));
	}

	@Override
	protected void layout() {
		updateBoundsWithCurrentPosition();
		super.layout();
	}
	
	private void updateBoundsWithCurrentPosition() {
		int yUpper = yAxis.getValuePosition(yAxis.getRange().getUpper(), false);
		int yLower = yAxis.getValuePosition(yAxis.getRange().getLower(), false);
		
		Rectangle boundsRect = new Rectangle(xAxis.getValuePosition(xValue, false) - 3, yUpper, 8, yLower);
		setBounds(boundsRect);
	}

	/**
	 * move the annotation to the center of the plot area or trace.
	 */
	public void updateToDefaultPosition() {
		if (xAxis.isLogScaleEnabled())
			xValue = Math.pow(10, (Math.log10(xAxis.getRange().getLower()) + Math.log10(xAxis.getRange().getUpper())) / 2);
		else
			xValue = (xAxis.getRange().getLower() + xAxis.getRange().getUpper()) / 2;
		if (yAxis.isLogScaleEnabled())
			yValue = Math.pow(10, (Math.log10(yAxis.getRange().getLower()) + Math.log10(yAxis.getRange().getUpper())) / 2);
		else
			yValue = (yAxis.getRange().getLower() + yAxis.getRange().getUpper()) / 2;

		System.err.println(xValue); //TODO: remove
	}

	/**
	 * updates the position to the given x value using the xygraph axis as the coordinate system.
	 * 
	 * @param newXValue
	 *            the new position to set
	 */
	public void setPosition(double newXValue) {
		xValue = newXValue;
		repaint();
	}

	public double getCurrentPosition() {
		return xValue;
	}
	
	class LineDragger extends MouseMotionListener.Stub implements MouseListener {
		private Point location;
		private IFigure figure;
		
		public LineDragger(IFigure figure) {
			this.figure=figure;
		}

		@Override
		public void mouseDragged(MouseEvent event) {

			if (location == null)
				return;
			Point newLocation = event.getLocation();
			if (newLocation == null)
				return;
			Dimension offset = newLocation.getDifference(location);
			
			if (offset.width == 0)
				return;
			
			double newXvalue = xAxis.getPositionValue(newLocation.x, false);
			if (!xAxis.getRange().inRange(newXvalue, false)) {
			    return;
            }
			xValue = newXvalue;
			location = newLocation;
			
			UpdateManager updateMgr = figure.getUpdateManager();
			Rectangle tmpBounds = figure.getBounds();
			updateMgr.addDirtyRegion(figure.getParent(), tmpBounds);
			tmpBounds = tmpBounds.getCopy().translate(offset.width, 0);
			figure.translate(offset.width, 0);
			updateMgr.addDirtyRegion(figure.getParent(), tmpBounds);
			
			positionListener.positionChanged(xValue, false);
			event.consume();
		}

		@Override
		public void mouseDoubleClicked(MouseEvent me) {
		    //not needed
		}

		@Override
		public void mousePressed(MouseEvent event) {
			location = event.getLocation();
			   event.consume();
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			 if (location == null)
			      return;

			 location = null;
			 event.consume();
			 
			 positionListener.positionChanged(xValue, true);
		}
	}

}