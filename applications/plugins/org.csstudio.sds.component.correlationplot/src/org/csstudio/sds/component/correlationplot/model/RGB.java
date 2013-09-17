package org.csstudio.sds.component.correlationplot.model;

public class RGB {

	private final int red;
	private final int green;
	private final int blue;
	
	public RGB(int red, int green, int blue) {
		assert red >= 0 : "assertion failed: red >= 0";
		assert red <= 255 : "assertion failed: red <= 255";
		assert green >= 0 : "assertion failed: green >= 0";
		assert green <= 255 : "assertion failed: green <= 255";
		assert blue >= 0 : "assertion failed: blue >= 0";
		assert blue <= 255 : "assertion failed: blue <= 255";
		
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
	public static final RGB RED_COLOR = new RGB(255,0,0);
	
	@Override
	public String toString() {
		return "(" + red + ", " + green + ", " + blue + ")";
	}

	public static RGB createColorBetween(RGB color1, RGB color2, double brightness) {
		assert brightness >= 0.0 && brightness <= 1.0 : "assertion failed: brightness >= 0.0 && brightness <= 1.0";
		
		int red = color1.red + (int) Math.round((color2.red - color1.red) * brightness);
		int green = color1.green + (int) Math.round((color2.green - color1.green) * brightness);
		int blue = color1.blue + (int) Math.round((color2.blue - color1.blue) * brightness);

		return new RGB(red, green, blue);
	}
}
