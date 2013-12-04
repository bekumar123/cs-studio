package org.csstudio.sds.ui.internal.runmode;

import java.io.Serializable;

import org.eclipse.swt.graphics.Point;

public class RunModeBoxLayoutData implements Serializable {

	private static final long serialVersionUID = 2841566150075640846L;
	
	private String name;
	private Point position;
	private Point size;
	private double zoomFactor;

	public RunModeBoxLayoutData(String name, Point position, Point size, double zoomFactor) {
		this.name = name;
		this.position = position;
		this.size = size;
		this.zoomFactor = zoomFactor;
	}
	
	public String getName() {
		return name;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public Point getSize() {
		return size;
	}
	
	public double getZoomFactor() {
		return zoomFactor;
	}
}