package com.ict.wq;

public class Stair {
	 

	public double getStairID() {
		return stairID;
	}
	public void setStairID(double stairID) {
		this.stairID = stairID;
	}
	public double getStairX() {
		return stairX;
	}
	public void setStairX(double stairX) {
		this.stairX = stairX;
	}
	public double getStairY() {
		return stairY;
	}
	public void setStairY(double stairY) {
		this.stairY = stairY;
	}
	private double stairID=0;
	@Override
	public String toString() {
		return "Stair [stairID=" + stairID + ", stairX=" + stairX + ", stairY="
				+ stairY + "]";
	}
	private double stairX=0;
	private double stairY=0;
	

}
