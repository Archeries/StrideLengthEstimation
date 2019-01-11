package com.ict.iodetector.service.conf;

public class Threshold {
	private String high;
	private String low;
	private String value;
	private String variance;
	private String turnthreshold;
	private String rssidifference;

	/**
	 * The constructor
	 */
	public Threshold() {
		high = "-1";
		low = "-1";
		value = "-1";
		variance = "-1";
		turnthreshold = "-1";
	}

	/**
	 * @return high
	 */
	public String getHigh() {
		return high;
	}

	/**
	 * @param high
	 */
	public void setHigh(String high) {
		this.high = high;
	}

	/**
	 * @return low
	 */
	public String getLow() {
		return low;
	}

	/**
	 * @param low
	 */
	public void setLow(String low) {
		this.low = low;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public String getTurnthreshold() {
		return turnthreshold;
	}

	public void setTurnthreshold(String turnthreshold) {
		this.turnthreshold = turnthreshold;
	}

	public String getVariance() {
		return variance;
	}

	public void setVariance(String variance) {
		this.variance = variance;
	}

	public String getRssidifference() {
		return rssidifference;
	}

	public void setRssidifference(String rssidifference) {
		this.rssidifference = rssidifference;
	}
}
