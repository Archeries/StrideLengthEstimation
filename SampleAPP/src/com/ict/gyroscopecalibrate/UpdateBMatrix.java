package com.ict.gyroscopecalibrate;

import Jama.Matrix;

public class UpdateBMatrix {
	private double forgettingParameter = 1;
	private int m = 1;
	private Matrix pMatrix;
	private Matrix bMatrix;

	public UpdateBMatrix(Matrix pMatrix, Matrix bMatrix) {
		super();
		this.pMatrix = pMatrix;
		this.bMatrix = bMatrix;
	}

	public Matrix doUpdateB(MRMpair mrMpair) {
		Matrix sMatrix = getSMatrix(mrMpair);
		double Sm = getSmMatrix(sMatrix, m);
		Matrix AMatrix = getAMatrix(mrMpair);
		Matrix AmMatrix = getAmMatrix(AMatrix, m);
		Matrix hMatrix = gethMatrix(AmMatrix);
		Matrix kMatrix = getKMatrix(pMatrix, hMatrix);
		Matrix pnewMatrix = getPnewMatrix(kMatrix, hMatrix, pMatrix);
		Matrix bnewMatrix = getbnewMatrix(kMatrix, hMatrix, bMatrix, Sm);
		pMatrix = (Matrix) pnewMatrix.clone();
		bMatrix = (Matrix) bnewMatrix.clone();
		return bnewMatrix;

	}

	public Matrix getSMatrix(MRMpair mrMpair) {

		Matrix SMatrix = (mrMpair.getFinalMagneticmMatrix()).minus((mrMpair
				.getRotationMatrix()).times(mrMpair.getFirstMagneticmMatrix()));
		return SMatrix;

	}

	public double getSmMatrix(Matrix sMatrix, int m) {

		return sMatrix.get(m, 0);

	}

	public Matrix getAMatrix(MRMpair mrMpair) {

		Matrix AMatrix = Matrix.identity(3, 3).minus(
				mrMpair.getRotationMatrix());
		return AMatrix;

	}

	public Matrix getAmMatrix(Matrix AMatrix, int m) {

		Matrix AmMatrix = AMatrix.getMatrix(m, m, 0, 2);
		return AmMatrix;

	}

	public Matrix gethMatrix(Matrix AmMatrix) {
		Matrix hMatrix = AmMatrix.transpose();
		return hMatrix;

	}

	public Matrix getKMatrix(Matrix pMatrix, Matrix hMatrix) {
		double htph = ((hMatrix.transpose()).times(pMatrix).times(hMatrix))
				.get(0, 0);
		double htphPlus = htph + forgettingParameter;
		Matrix htphPlusmMatrix = new Matrix(1, 1);
		htphPlusmMatrix.set(0, 0, htphPlus);

		Matrix kmMatrix = (pMatrix.times(hMatrix)).times(htphPlusmMatrix
				.inverse());
		return kmMatrix;

	}

	public Matrix getPnewMatrix(Matrix kMatrix, Matrix hMatrix, Matrix pMatrix) {
		Matrix pnewMatrix = (Matrix.identity(3, 3).minus(
				kMatrix.times(hMatrix.inverse())).times(pMatrix))
				.times((1 / forgettingParameter));
		return pnewMatrix;

	}

	public Matrix getbnewMatrix(Matrix kMatrix, Matrix hMatrix, Matrix bMatrix,
			double Sm) {
		Matrix htbMatrix = hMatrix.transpose().times(bMatrix);
		double htb = htbMatrix.get(0, 0);
		double smMinushtb = Sm - htb;
		Matrix smMinushtbMatrix = new Matrix(1, 1);
		smMinushtbMatrix.set(0, 0, smMinushtb);
		Matrix bnewMatrix = bMatrix.plus(kMatrix.times(smMinushtbMatrix));
		return bnewMatrix;

	}
}
