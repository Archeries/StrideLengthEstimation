package com.ict.gpsmodule;

public class Coordinator {
	public double lon;
	public double lat;
	
	public Coordinator() {
		this.lon = 0;
		this.lat = 0;
	}
	
	public Coordinator(double longitude, double latitude) {
		this.lon = longitude;
		this.lat = latitude;
	}
}
