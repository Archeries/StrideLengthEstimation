package com.ict.wq.model;

import java.util.ArrayList;
import java.util.Queue;

public class Segment {
//	private ArrayList<OneTime> segment;
private OneTime oneTime;

public OneTime getOneTime() {
	return oneTime;
}

public void setOneTime(OneTime oneTime) {
	this.oneTime = oneTime;
}

@Override
public String toString() {
	return "Segment [oneTime=" + oneTime + "]";
}
 
 

}
