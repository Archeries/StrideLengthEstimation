package com.ict.gyroscopecalibrate;

import Jama.Matrix;


public class MRMpair {
 
	 public Matrix getFirstMagneticmMatrix() {
		return firstMagneticmMatrix;
	}
	public void setFirstMagneticmMatrix(Matrix firstMagneticmMatrix) {
		this.firstMagneticmMatrix = firstMagneticmMatrix;
	}
	public Matrix getFinalMagneticmMatrix() {
		return finalMagneticmMatrix;
	}
	public void setFinalMagneticmMatrix(Matrix finalMagneticmMatrix) {
		this.finalMagneticmMatrix = finalMagneticmMatrix;
	}
	public Matrix getRotationMatrix() {
		return rotationMatrix;
	}
	public void setRotationMatrix(Matrix rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
	}
	public MRMpair(Matrix firstMagneticmMatrix, Matrix finalMagneticmMatrix,
			Matrix rotationMatrix) {
		super();
		this.firstMagneticmMatrix = firstMagneticmMatrix;
		this.finalMagneticmMatrix = finalMagneticmMatrix;
		this.rotationMatrix = rotationMatrix;
	}
	private Matrix firstMagneticmMatrix;
 
	private Matrix finalMagneticmMatrix;
	 private Matrix rotationMatrix;
	 
	

}
