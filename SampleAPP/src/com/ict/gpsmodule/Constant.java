package com.ict.gpsmodule;

public class Constant {
	
    final static int EQUATORIAL_RADIUS_OF_EARTH = 6371004; // m
	
	final static String[] TEST_POINT_NAME = {"EastMiddle", "NorthEast", "SouthEast", "SouthWest", "NorthWest", "750ConferenceRoom", "SouthEastCrossRoad"};
	
	final static double[] TEST_POINT_LONGITUDE = {116.32690080834000, 116.32704410899500, 116.32698140899500, 116.32611671668100, 116.32619441668100, 116.32644215041100, 116.327437};
	final static double[] TEST_POINT_LATITUDE =   {39.98186485076060,  39.98195595710690,  39.98179035710680,  39.98175730152120,  39.98199070152120,  39.98170873450230, 39.980947};
	
	final static String[] CORRECT_MODE_NAME = {"No Correction", "Convergence", "Intersection"};
	final static int[] CORRECT_MODE_NUMBER = {0, 1, 2};
	
	// 用来计算建筑中心点
	final static Coordinator northEastCorner = new Coordinator(116.32706010899500, 39.98194595710690);
	final static Coordinator southEastCorner = new Coordinator(116.32699100899500, 39.98172635710680);
	final static Coordinator southWestCorner = new Coordinator(116.32612216681000, 39.98177501521200);
	final static Coordinator northWestCorner = new Coordinator(116.32619441668100, 39.98199070152120);
	
	// 用来计算建筑物边缘
//	protected static Coordinator northEast = new Coordinator(116.32705710899500, 39.98201895710690);
//	protected static Coordinator southEast = new Coordinator(116.32699100899500, 39.98172635710680);
	final static Coordinator northEast = new Coordinator(116.32688500000001, 39.98202);
	final static Coordinator southEast = new Coordinator(116.326885, 39.981724);
	final static Coordinator southWest = new Coordinator(116.32620216681000, 39.98169601521200);
	final static Coordinator northWest = new Coordinator(116.32619441668100, 39.98199070152120);
	
	// 组成边缘线段的点
	final static Coordinator[] points = {northWest,
											new Coordinator(116.32705710899500, 39.98201895710690),
											northEastCorner,
											new Coordinator(116.326891, 39.98194595710690),
											new Coordinator(116.326897, 39.981788),
											new Coordinator(116.326995, 39.981795),
											southEastCorner,
											southWest,
											new Coordinator(116.326187, 39.981753),
											new Coordinator(116.326107, 39.981749),
											southWestCorner,
											new Coordinator(116.326198,39.981775)};
	
	// 用来算交点的建筑物边缘线段
//	protected static Segment[] northEdgeSeg = {new Segment(points[0], points[1])};
//	protected static Segment[] eastEdgeSeg = {new Segment(points[1], points[2]),
//											new Segment(points[3], points[4]),
//											new Segment(points[5], points[6])};
//	protected static Segment[] southEdgeSeg = {new Segment(points[6], points[7])};
//	protected static Segment[] westEdgeSeg = {new Segment(points[7], points[8]),
//											new Segment(points[9], points[10]),
//											new Segment(points[11], points[0])};
	final static Vector[] northEdgeVector = {new Vector(points[0], points[1])};
//	final static Vector[] eastEdgeVector = {new Vector(points[1], points[2]),
//											new Vector(points[3], points[4]),
//											new Vector(points[5], points[6])};
//	final static Vector[] eastEdgeVector = {new Vector(points[1], new Coordinator(TEST_POINT_LONGITUDE[0], TEST_POINT_LATITUDE[0])),
//											new Vector(new Coordinator(TEST_POINT_LONGITUDE[0], TEST_POINT_LATITUDE[0]), points[6])};
	final static Vector[] eastEdgeVector = {new Vector(points[1], southEast)};
	final static Vector[] southEdgeVector = {new Vector(points[6], points[7])};
	final static Vector[] westEdgeVector = {new Vector(points[7], points[8]),
											new Vector(points[9], points[10]),
											new Vector(points[11], points[0])};
//	final static Vector[] edgeVector = {new Vector(points[0], points[1]), 
//										new Vector(points[1], new Coordinator(TEST_POINT_LONGITUDE[0], TEST_POINT_LATITUDE[0])),
//										new Vector(new Coordinator(TEST_POINT_LONGITUDE[0], TEST_POINT_LATITUDE[0]), points[6]),
//										new Vector(points[6], points[7]),
//										new Vector(points[7], points[8]),
//										new Vector(points[9], points[10]),
//										new Vector(points[11], points[0])};
	final static Vector[] edgeVector = {new Vector(points[0], points[1]), 
		new Vector(points[1], southEast),
		new Vector(points[6], points[7]),
		new Vector(points[7], points[8]),
		new Vector(points[9], points[10]),
		new Vector(points[11], points[0])};
}