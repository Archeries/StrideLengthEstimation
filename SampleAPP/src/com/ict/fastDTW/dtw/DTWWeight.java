package com.ict.fastDTW.dtw;

public class DTWWeight {
	public double horizontal;
	public double vertical;
	public double diagonal;
	
	public DTWWeight() {
		this.vertical = 1;
		this.horizontal = 1;
		this.diagonal = 1;
	}
	
	public DTWWeight(DTWWeight weight) {
		this.vertical = weight.vertical;
		this.horizontal = weight.horizontal;
		this.diagonal = weight.diagonal;
	}
}
