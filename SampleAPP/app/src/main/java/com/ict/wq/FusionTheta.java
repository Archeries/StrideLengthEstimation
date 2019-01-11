package com.ict.wq;

public class FusionTheta {
	private static double FusionFactor1=0.2;//地磁角度和陀螺角度差大于某个值（比如40度）时，陀螺角占的比重
	private static double FusionFactor2=0.8;//地磁角度和陀螺角度差小于某个值（比如40度）时，陀螺角占的比重
//	private static double FusionFactor1=0.5;//地磁角度和陀螺角度差大于某个值（比如40度）时，陀螺角占的比重
//	private static double FusionFactor2=1;//地磁角度和陀螺角度差小于某个值（比如40度）时，陀螺角占的比重
	private static double angleDifferenceThreshold=30;
	
	public static double getFusionTheta(double realtimeGyroscopeavg,
			double realtimeMagnetismavg) {
		double realtimeFusion = 0;

		if (realtimeGyroscopeavg >= 0 && realtimeMagnetismavg >= 0) {
			double angleDifferenceABS = Math.abs(realtimeGyroscopeavg
					- realtimeMagnetismavg);
			if (angleDifferenceABS<=angleDifferenceThreshold) {
				realtimeFusion=realtimeGyroscopeavg*FusionFactor2+realtimeMagnetismavg*(1-FusionFactor2);
			}else {
				realtimeFusion=realtimeGyroscopeavg*FusionFactor1+realtimeMagnetismavg*(1-FusionFactor1);
			}
			 
			return realtimeFusion;
		} else if (realtimeGyroscopeavg <= 0 && realtimeMagnetismavg <= 0) {
			double angleDifferenceABS = Math.abs(realtimeGyroscopeavg
					- realtimeMagnetismavg);
			if (angleDifferenceABS<=angleDifferenceThreshold) {
				realtimeFusion=realtimeGyroscopeavg*FusionFactor2+realtimeMagnetismavg*(1-FusionFactor2);
			}else {
				realtimeFusion=realtimeGyroscopeavg*FusionFactor1+realtimeMagnetismavg*(1-FusionFactor1);
			}
			 
			return realtimeFusion;
		} else if (realtimeGyroscopeavg > 0 && realtimeMagnetismavg < 0) {
			double angleDifferenceABS = Math.abs(realtimeGyroscopeavg
					- realtimeMagnetismavg);
			double angleDifference = 0;
			if (angleDifferenceABS >= 180) {
				angleDifference = 360 - angleDifferenceABS;				
				if (angleDifference<=angleDifferenceThreshold) {					
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeMagnetismavg - angleDifference
								*  FusionFactor2 ;
					} else {
						realtimeFusion = realtimeGyroscopeavg + angleDifference
								* (1-FusionFactor2);
					}					 
				}else {
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeMagnetismavg - angleDifference
								* FusionFactor1 ;
					} else {
						realtimeFusion = realtimeGyroscopeavg + angleDifference
								* (1-FusionFactor1) ;
						if (realtimeFusion>180) {
							realtimeFusion=realtimeFusion-360;
						}
					}					 
				}
				
				

			} else {
				angleDifference = angleDifferenceABS;
				if (angleDifference<=angleDifferenceThreshold) {					
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeGyroscopeavg - angleDifference
								* (1 - FusionFactor2);
					} else {
						realtimeFusion = realtimeMagnetismavg + angleDifference
								* FusionFactor2;
					}					 
				}else {
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeGyroscopeavg - angleDifference
								* (1 - FusionFactor1);
					} else {
						realtimeFusion = realtimeMagnetismavg + angleDifference
								* FusionFactor1;
					}					 
				}
  
			}
			return realtimeFusion;
		} else if (realtimeGyroscopeavg < 0 && realtimeMagnetismavg > 0) {
			double angleDifferenceABS = Math.abs(realtimeMagnetismavg
					- realtimeGyroscopeavg);
			double angleDifference = 0;
			
			
			if (angleDifferenceABS >= 180) {
				angleDifference = 360 - angleDifferenceABS;				
				if (angleDifference<=angleDifferenceThreshold) {					
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeMagnetismavg + angleDifference
								* FusionFactor2   ;
					} else {
						realtimeFusion = realtimeGyroscopeavg - angleDifference
								*  (1-FusionFactor2) ;
					}					 
				}else {
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeMagnetismavg + angleDifference
								* FusionFactor1 ;
						if (realtimeFusion>180) {
							realtimeFusion=realtimeFusion-360;
						}
					} else {
						realtimeFusion = realtimeGyroscopeavg - angleDifference
								*   (1-FusionFactor1)  ;
						if (realtimeFusion<-180) {
							realtimeFusion=realtimeFusion+360;
						}
					}					 
				}
				
				

			} else {
				angleDifference = angleDifferenceABS;
				if (angleDifference<=angleDifferenceThreshold) {					
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeGyroscopeavg + angleDifference
								* (1 - FusionFactor2);
					} else {
						realtimeFusion = realtimeMagnetismavg - angleDifference
								* FusionFactor2;
					}					 
				}else {
					if (Math.abs(realtimeGyroscopeavg) > Math
							.abs(realtimeMagnetismavg)) {
						realtimeFusion = realtimeGyroscopeavg + angleDifference
								* (1 - FusionFactor1);
					} else {
						realtimeFusion = realtimeMagnetismavg - angleDifference
								* FusionFactor1;
					}					 
				}
  
			}
		}
				 
		return realtimeFusion;
	}
	
	public static void main(String[] args) {
		System.out.println(getFusionTheta(161, -164));
		
	}
}
