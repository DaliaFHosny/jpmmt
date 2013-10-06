package de.lsem.process.model;

public class GraphicalInformation {
	private double x,
				   y,
				   width,
				   height;
	
	public GraphicalInformation() {		
	}
	
	public GraphicalInformation(double x, double y, double height, double width) {
		this.setX(x);
		this.setY(y);
		this.setHeight(height);
		this.setWidth(width);
	}
	
	public double getHeight() {
		return this.height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public double getX() {
		return this.x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
} 
 