package cn.ac.ict.ictips.locating.entity;

/**
 * A couple of double digit to express positioning coordinates.
 * @author Shawn ICT & BUPT
 * @MofifiedTime 4:14:15 PM, Jul 1, 2014
 * @CreatedTime 4:14:15 PM, Jul 1, 2014
 * @Version 1.0
 */
public class PositioningCoordinates {
	private double xCoord = -1;
	private double yCoord = -1;
	
	public PositioningCoordinates(double xCoord, double yCoord) {
		super();
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}
	public double getxCoord() {
		return xCoord;
	}
	public void setxCoord(double xCoord) {
		this.xCoord = xCoord;
	}
	public double getyCoord() {
		return yCoord;
	}
	public void setyCoord(double yCoord) {
		this.yCoord = yCoord;
	}
}
