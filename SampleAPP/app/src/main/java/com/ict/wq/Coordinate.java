package com.ict.wq;

public class Coordinate {
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLantitude() {
		return lantitude;
	}
	public void setLantitude(double lantitude) {
		this.lantitude = lantitude;
	}
	@Override
	public String toString() {
		return "Coordinate [longitude=" + longitude + ", lantitude="
				+ lantitude + "]";
	}
	private double longitude=0;
	private double lantitude=0;
	

}
