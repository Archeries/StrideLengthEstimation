package com.ict.gyroscopecalibrate;

import java.util.ArrayList;
import java.util.List;

public class MinEnclosingBall {
	
	public static Point getCenterOfMinEnclosingBall(List<Point> list) {
		Point p1 = list.get(0);
		Point p2 = list.get(1);
		
		double r = Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2) + Math.pow(p1.z-p2.z, 2))/2;
		Point center = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, (p1.z + p2.z) / 2);
		
		for (int i = 2; i < list.size(); i ++) {
			Point newP = list.get(i);
			double d = Math.sqrt(Math.pow(center.x-newP.x, 2) + Math.pow(center.y-newP.y, 2) + Math.pow(center.z-newP.z, 2))/2;
			if (d > r) {
				r = (r + d) / 2;
				center.x = center.x + (d - r) / d * (newP.x - center.x);
				center.y = center.y + (d - r) / d * (newP.y - center.y);
				center.z = center.z + (d - r) / d * (newP.z - center.z);
			}
		}
		
		return center;
	}

	public static void main(String[] args) {
		
	}

}
