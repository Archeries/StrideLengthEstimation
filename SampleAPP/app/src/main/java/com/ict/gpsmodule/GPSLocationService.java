package com.ict.gpsmodule;

/**
 * Service for GPS lock.
 * @author Archeries
 * @version 2015-12-02 v1.0
 */

import java.util.ArrayList;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.*;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class GPSLocationService extends Service {

	Context context;
	private GPSBinder gpsBinder = new GPSBinder();

	Location lastLocation = null; // to judge whether the location got is a new
									// one.
	LocationManager locationManager;
	Coordinator centerOfBuilding;
	Coordinator averageLocation;
	TimeCoordinator locationTemp;
	LocationListener locationListener;
	GpsStatus.Listener gpsStatusListener;

	ArrayList<TimeCoordinator> locationList;

	long startTime;
	boolean isUsable = false;
	boolean isUpdated = false;
	int countLocation;
	int satelliteNumber;

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = getApplicationContext();
		initial();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		setListener();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		removeListener();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return gpsBinder;
	}

	public void initial() {

		startTime = System.currentTimeMillis();
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "请打开GPS开关", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				intent.setAction(Settings.ACTION_SETTINGS);
				try {
					context.startActivity(intent);
				} catch (Exception ex) {
				}
			}
		}
		centerOfBuilding = getIntersectionPoint(Constant.northEastCorner,
				Constant.southWestCorner, Constant.southEastCorner,
				Constant.northWestCorner);
		locationList = new ArrayList<TimeCoordinator>();
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				long currentTime = System.currentTimeMillis();
				if (((currentTime - startTime) > 3000) && (location != null)) { // 为了防止应用打开之前GPS中存的历史位置干扰当前的判断
					if (location.getAccuracy() > 50) return;
					// 若与上次的经纬度相同，认为位置没有更新。
					if (isLocationIdentical(location, lastLocation)) {
						isUpdated = false;
						return;
					}
					isUpdated = true;
					lastLocation = location;
					Coordinator locationTransformed = CoordinateTransformer
							.gps2google(new Coordinator(
									location.getLongitude(), location
											.getLatitude()));
					locationTemp = new TimeCoordinator(locationTransformed,
							currentTime);
					locationList.add(locationTemp);

					averageLocation = new Coordinator();
					countLocation = 0;
					while (System.currentTimeMillis()
							- locationList.get(0).time > 30 * 1000) {
						locationList.remove(0);
					}
					for (TimeCoordinator loc : locationList) {
						averageLocation.lon = (averageLocation.lon
								* countLocation + loc.lon)
								/ (countLocation + 1);
						averageLocation.lat = (averageLocation.lat
								* countLocation + loc.lat)
								/ (countLocation + 1);
						countLocation++;
					}

					if (countLocation > 5) {
						Coordinator intersectionLocation = getIntersectionPointWithBuildingEdge(averageLocation);
						averageLocation = intersectionLocation;
						isUsable = true;
					} else {
						isUsable = false;
					}
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};

		gpsStatusListener = new GpsStatus.Listener() {
			@Override
			public void onGpsStatusChanged(int event) {
				switch (event) {
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS: {
					GpsStatus gpsStatus = locationManager.getGpsStatus(null);
					Iterable<GpsSatellite> satellites = gpsStatus
							.getSatellites();
					satelliteNumber = 0;
					for (GpsSatellite satellite : satellites) {
						satelliteNumber++;
					}
					if (satelliteNumber < 4) {
						locationList.clear();
					}
					break;
				}

				default:
					break;
				}
			}
		};
	}

	/**
	 * Set the Listeners of location and GPS status.
	 */
	private void setListener() {
		if (locationManager != null) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			locationManager.addGpsStatusListener(gpsStatusListener);
		}
	}

	/**
	 * Method to remove the Listeners of location.
	 */
	private void removeListener() {
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
			locationManager.removeGpsStatusListener(gpsStatusListener);
		}
	}

	/**
	 * The Binder connected with the MainActicity.
	 * 
	 * @author Archeries
	 */
	public class GPSBinder extends Binder {

		public boolean isGPSUsable() {
			return isUsable;
		}
		
		public boolean isLocationUpdated() {
			return isUpdated;
		}

		public Coordinator getLocation() {
			return averageLocation;
		}

		public boolean isGPSOpen() {
			return locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}

		public ArrayList<TimeCoordinator> getLocationList() {
			return locationList;
		}

		public void clearLocationList() {
			locationList.clear();
			startTime = System.currentTimeMillis();
			isUsable = false;
			lastLocation = null;
			averageLocation = new Coordinator();
		}

		public int getSatelliteNumber() {
			return satelliteNumber;
		}
	}

	/**
	 * Method to judge whether the two given locations are identical.
	 * 
	 * @param locA
	 *            The first location.
	 * @param locB
	 *            The second location.
	 * @return If they are identical, return true; else return false.
	 */
	private boolean isLocationIdentical(Location locA, Location locB) {
		if (locA == null) {
			if (locB == null)
				return true;
			else
				return false;
		} else {
			if (locB == null)
				return false;
		}
		if (Math.abs(locA.getLongitude() + locA.getLatitude()
				- locB.getLongitude() - locB.getLatitude()) < 0.00000000000001)
			return true;
		else
			return false;
	}

	/**
	 * Method to get the intersection point of intersectant two line segments.
	 * Make sure that p0p1 is intersectant with p2p3, otherwise the method will
	 * return a meaningless result.
	 * 
	 * @param p0
	 *            One end of the first line segment.
	 * @param p1
	 *            One end of the first line segment.
	 * @param p2
	 *            One end of the second line segment.
	 * @param p3
	 *            One end of the second line segment.
	 * @return A Coordinator which represents the intersection point of two
	 *         intersectant line segments.
	 */
	private Coordinator getIntersectionPoint(Coordinator p0, Coordinator p1,
			Coordinator p2, Coordinator p3) {
		double lon;
		double lat;

		double x0 = p0.lon;
		double x1 = p1.lon;
		double x2 = p2.lon;
		double x3 = p3.lon;
		double y0 = p0.lat;
		double y1 = p1.lat;
		double y2 = p2.lat;
		double y3 = p3.lat;
		lon = ((y2 - y0) * (x1 - x0) * (x3 - x2) + (y1 - y0) * (x3 - x2) * x0 - (y3 - y2)
				* (x1 - x0) * x2)
				/ ((y1 - y0) * (x3 - x2) - (y3 - y2) * (x1 - x0));
		lat = ((x0 - x2) * (y3 - y2) * (y1 - y0) + (x3 - x2) * (y1 - y0) * y2 - (x1 - x0)
				* (y3 - y2) * y0)
				/ ((x3 - x2) * (y1 - y0) - (x1 - x0) * (y3 - y2));

		return new Coordinator(lon, lat);
	}

	/**
	 * Method to get the intersection point of the segment from the center of
	 * building to location and building edges.
	 * 
	 * @param loc
	 *            Current location.
	 * @return A Coordinator which represents the intersection point.
	 */
	private Coordinator getIntersectionPointWithBuildingEdge(Coordinator loc) {

		Coordinator verticalPoint = getVerticalPointOnBuildingEdge(loc);
		if (verticalPoint != null)
			return verticalPoint;

		int mark = -1;
		for (int i = 0; i < Constant.edgeVector.length; i++) {
			if (isIntersectant(loc, centerOfBuilding, Constant.edgeVector[i].endPoint1,
					Constant.edgeVector[i].endPoint2)) {
				mark = i;
			}
		}

		return getIntersectionPoint(loc, centerOfBuilding, Constant.edgeVector[mark].endPoint1, Constant.edgeVector[mark].endPoint2);
	}

	/**
	 * Method to judge whether two given segments are intersectant.
	 * 
	 * @param p0
	 *            One end of the first line segment.
	 * @param p1
	 *            One end of the first line segment.
	 * @param p2
	 *            One end of the second line segment.
	 * @param p3
	 *            One end of the second line segment.
	 * @return True if the two given segments are intersectant; otherwise false.
	 */
	private boolean isIntersectant(Coordinator p0, Coordinator p1,
			Coordinator p2, Coordinator p3) {
		double flag1 = 0, flag2 = 0;
		Vector p0Top1 = new Vector(p0, p1);
		Vector p2Top3 = new Vector(p2, p3);

		flag1 = p0Top1.getCrossProductZ(new Vector(p0, p2))
				* p0Top1.getCrossProductZ(new Vector(p0, p3));
		flag2 = p2Top3.getCrossProductZ(new Vector(p2, p0))
				* p2Top3.getCrossProductZ(new Vector(p2, p1));

		if ((flag1 < 0) && (flag2 < 0))
			return true;
		else
			return false;
	}

	/**
	 * Method to get the vertical point of location on building edges. The
	 * location could be inside or outside the building.
	 * 
	 * @param loc
	 *            Current location.
	 * @return A Coordinator which represents the vertical point. Will return
	 *         null if vertical point is not available.
	 */
	private Coordinator getVerticalPointOnBuildingEdge(Coordinator loc) {

		if (isBetween(Constant.southWest, Constant.northWest,
				Constant.southEast, Constant.northEast, loc)
				&& isBetween(Constant.northWest, Constant.northEast,
						Constant.southWest, Constant.southEast, loc)) {
			double minDistance = Double.MAX_VALUE;
			int vectorNum = -1;
			for (int i = 0; i < Constant.edgeVector.length; i++) {
				double disTemp = getPointDistanceToSegment(Constant.edgeVector[i].endPoint1,
						Constant.edgeVector[i].endPoint2, loc);
				if (disTemp < minDistance) {
					minDistance = disTemp;
					vectorNum = i;
				}
			}
			return getVerticalPoint(Constant.edgeVector[vectorNum].endPoint1, Constant.edgeVector[vectorNum].endPoint2,
					loc);
		}

		if (isBetween(Constant.southWest, Constant.northWest,
				Constant.southEast, Constant.northEast, loc)) {
			Vector northEdge = new Vector(Constant.northWest,
					Constant.northEast);
			Vector northWestToLoc = new Vector(Constant.northWest, loc);
			if ((northEdge.getCrossProductZ(northWestToLoc) > 0)
					&& (northEdge.dotProduct(northWestToLoc) > 0)) {
				return getVerticalPoint(Constant.northWest, Constant.northEast,
						loc);
			}

			Vector southEdge = new Vector(Constant.southWest,
					Constant.southEast);
			Vector southWestToLoc = new Vector(Constant.southWest, loc);
			if ((southEdge.getCrossProductZ(southWestToLoc) < 0)
					&& (southEdge.dotProduct(southWestToLoc) > 0)) {
				return getVerticalPoint(Constant.southWest, Constant.southEast,
						loc);
			}
		}

		if (isBetween(Constant.northWest, Constant.northEast,
				Constant.southWest, Constant.southEast, loc)) {

			Vector eastEdge = new Vector(Constant.southEast, Constant.northEast);
			Vector westEdge = new Vector(Constant.southWest, Constant.northWest);
			Vector southEastToLoc = new Vector(Constant.southEast, loc);
			Vector southWestToLoc = new Vector(Constant.southWest, loc);
			if (westEdge.getCrossProductZ(southWestToLoc) > 0) {
				for (int i = 0; i < Constant.westEdgeVector.length; i++) {
					if (Constant.westEdgeVector[i].dotProduct(southWestToLoc) > 0) {
//						return getVerticalPoint(Constant.points[7 + i * 2],
//								Constant.points[(8 + i * 2) % 12], loc);
						return getVerticalPoint(Constant.westEdgeVector[i].endPoint1,
								Constant.westEdgeVector[i].endPoint2, loc);
					}
				}
			}

			if (eastEdge.getCrossProductZ(southEastToLoc) < 0) {
				for (int i = 0; i < Constant.eastEdgeVector.length; i++) {
					if (Constant.eastEdgeVector[i].dotProduct(southEastToLoc) > 0) {
//						return getVerticalPoint(Constant.points[1 + i * 2],
//								Constant.points[2 + i * 2], loc);
						return getVerticalPoint(Constant.eastEdgeVector[i].endPoint1, 
								Constant.eastEdgeVector[i].endPoint2, loc);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Method to get the vertical distance of the given point to the line where
	 * the given segment locates.
	 * 
	 * @param p0
	 *            One end of the segment.
	 * @param p1
	 *            One end of the segment.
	 * @param p
	 *            The point.
	 * @return A double. The distance.
	 */
	public double getPointDistanceToSegment(Coordinator p0, Coordinator p1,
			Coordinator p) {
		Vector p0Top = new Vector(p0, p);
		Vector p0Top1 = new Vector(p0, p1);
		double cosTheta = p0Top.dotProduct(p0Top1) / p0Top.getMold()
				/ p0Top1.getMold();

		return p0Top.getMold() * (Math.sqrt(1 - Math.pow(cosTheta, 2)));
	}

	/**
	 * Method to get the vertical point of the given point on the line where the
	 * given segment locates. Make sure that the vertical point is on the
	 * segment.
	 * 
	 * @param p0
	 *            One end of the segment.
	 * @param p1
	 *            One end of the segment.
	 * @param p
	 *            The point.
	 * @return A Coordinator which represents the vertical point.
	 */
	private Coordinator getVerticalPoint(Coordinator p0, Coordinator p1,
			Coordinator p) {
		Vector p0Top1 = new Vector(p0, p1);
		Vector p0Top = new Vector(p0, p);
		double d = p0Top1.dotProduct(p0Top) / p0Top1.getMold();
		double lon = p0.lon + d * ((p1.lon - p0.lon) / p0Top1.getMold());
		double lat = p0.lat + d * ((p1.lat - p0.lat) / p0Top1.getMold());

		return new Coordinator(lon, lat);
	}

	/**
	 * Method to judge whether the given point is between the two line where the
	 * two given segments locate.
	 * 
	 * @param p0
	 *            One end of the first segment.
	 * @param p1
	 *            One end of the first segment.
	 * @param p2
	 *            One end of the second segment.
	 * @param p3
	 *            One end of the second segment.
	 * @param loc
	 *            The point. Current location.
	 * @return True if the given point is between the two line. Otherwise false.
	 */
	private boolean isBetween(Coordinator p0, Coordinator p1, Coordinator p2,
			Coordinator p3, Coordinator loc) {
		Vector v1 = new Vector(p0, p1);
		Vector v2 = new Vector(p2, p3);
		Vector toLoc1 = new Vector(p0, loc);
		Vector toLoc2 = new Vector(p2, loc);
		if (v1.getCrossProductZ(toLoc1) * v2.getCrossProductZ(toLoc2) < 0)
			return true;
		else
			return false;
	}
}
