package com.wzg.dingwei;

import com.amap.api.maps.model.Marker;

public class ManPos {
	public String id,targetid;
	int status,receiveCount;
	public float rad;
	public float x=0,y=0,z=0;
	public float oldx=0,oldy=0,oldz=0;
	public Marker marker;
	public float distance=0;
	
	public float getDistance()
	{
		if(oldx!=0)
		{
			distance+=Math.sqrt((oldx-x)*(oldx-x)+(oldy-y)*(oldy-y)+(oldz-z)*(oldz-z));
		}
		oldx=x;
		oldy=y;
		oldz=z;
		return distance;
	}
	
}
