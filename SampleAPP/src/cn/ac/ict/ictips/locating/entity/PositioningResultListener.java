package cn.ac.ict.ictips.locating.entity;

import com.ict.wq.Coordinate;
 

public interface PositioningResultListener {
	void onLocationChanged(Coordinate positioningResultSID);
}
