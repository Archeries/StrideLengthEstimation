package com.ict.wq.model;

public class OneTime {
	public Float getX() {
		return X;
	}
	public void setX(Float x) {
		X = x;
	}
	public Float getY() {
		return Y;
	}
	public void setY(Float y) {
		Y = y;
	}
	public Float getZ() {
		return Z;
	}
	public void setZ(Float z) {
		Z = z;
	}
	private Float X;
	@Override
	public String toString() {
		return "OneTime [X=" + X + ", Y=" + Y + ", Z=" + Z + "]";
	}
	private Float Y;
	private Float Z;

}
