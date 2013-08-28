package org.csstudio.sds.component.correlationplot.model;

public class RGB {

	private final int red;
	private final int green;
	private final int blue;
	
	public RGB(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int getRed() {
		return red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public int getBlue() {
		return blue;
	}
	
	public static final RGB WHITE_COLOR = new RGB(255,255,255);
	public static final RGB BLACK_COLOR = new RGB(0, 0, 0);
	
	@Override
	public String toString() {
		return "(" + red + ", " + green + ", " + blue + ")";
	}
}
