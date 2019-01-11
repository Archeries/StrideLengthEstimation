/*
 * This class is for the profile of each sensor. 
 * It will save the environment(indoor,semi,outdoor) and the confidence value;
 */

package com.ict.ioDetect;

public class DetectionProfile {

	private String environment;
	private double confidence;
	
	/**
	 * @param env
	 */
	public DetectionProfile(String env){
		this.environment = env;
		this.confidence = 0;
	}

	/**
	 * @return environment
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @return confidence
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * @param environment
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * @param confidence
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	

}
