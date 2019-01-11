package com.ict.gpsmodule;

public class Vector {
	public double x, y;
	public Coordinator endPoint1, endPoint2;
	
	Vector(Coordinator p1, Coordinator p2) {
		this.x = p2.lon - p1.lon;
		this.y = p2.lat - p1.lat;
		endPoint1 = p1;
		endPoint2 = p2;
	}
	
	public double getCrossProductZ(Vector v2) {
		return x * v2.y - y * v2.x;
	}
	
	public double dotProduct(Vector v2) {
		return x * v2.x + y * v2.y;
	}
	
	public double getMold() {
		return Math.sqrt(x * x + y * y);
	}
}

