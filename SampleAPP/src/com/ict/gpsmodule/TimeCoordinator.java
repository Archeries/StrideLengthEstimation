package com.ict.gpsmodule;

public class TimeCoordinator extends Coordinator {
	long time;
	
	TimeCoordinator() {
		super();
	}	
	
	TimeCoordinator(Coordinator coordinator, long time) {
		this.lon = coordinator.lon;
		this.lat = coordinator.lat;
		this.time = time;
	}

	@Override
	public String toString() {
		return "TimeCoordinator [time=" + time + ", lon=" + lon + ", lat="
				+ lat + "]";
	}
}
