package com.ict.iodetector.service.conf;

import java.util.HashMap;

public class Mode {
	private boolean enable;
	private String name;
	
	HashMap<String, Threshold> thresholds;
	
	/**
	 * @return thresholds
	 */
	public HashMap<String, Threshold> getThresholds() {
		return thresholds;
	}

	/**
	 * @param thresholds
	 */
	public void setThresholds(HashMap<String, Threshold> thresholds) {
		this.thresholds = thresholds;
	}

	/**
	 * The constructor
	 */
	public Mode(){
		//default true
		enable=true;
	}

	/**
	 * @return enable
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * @param enable
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
}
