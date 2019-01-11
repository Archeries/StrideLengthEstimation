package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.Entity;

public class FloorInformation {
	public static double[] eachfloorHeight;
	public static int[] hollowFloor;
	public static int[] floorNumArray;
	public static int[] postiveFloorNumArray;
	public static double[] postiveFloorHeight;
	public static int[] negativeFloorNumArray;
	public static double[] negativeFloorHeight;

	public static int[] getFloorNumArray() {
		return floorNumArray;
	}

	public static void setFloorNumArray(int[] floorNumArray) {
		FloorInformation.floorNumArray = floorNumArray;
	}

	public static int[] getHollowFloor() {
		return hollowFloor;
	}

	public static void setHollowFloor(int[] hollowFloor) {
		FloorInformation.hollowFloor = hollowFloor;
	}

	public static double[] getEachfloorHeight() {
		return eachfloorHeight;
	}

	public static void setEachfloorHeight(double[] eachfloorHeight) {
		FloorInformation.eachfloorHeight = eachfloorHeight;
	}

	public static int[] getPostiveFloorNumArray() {
		return postiveFloorNumArray;
	}

	public static void setPostiveFloorNumArray(int[] postiveFloorNumArray) {
		FloorInformation.postiveFloorNumArray = postiveFloorNumArray;
	}

	public static double[] getPostiveFloorHeight() {
		return postiveFloorHeight;
	}

	public static void setPostiveFloorHeight(double[] postiveFloorHeight) {
		FloorInformation.postiveFloorHeight = postiveFloorHeight;
	}

	public static int[] getNegativeFloorNumArray() {
		return negativeFloorNumArray;
	}

	public static void setNegativeFloorNumArray(int[] negativeFloorNumArray) {
		FloorInformation.negativeFloorNumArray = negativeFloorNumArray;
	}

	public static double[] getNegativeFloorHeight() {
		return negativeFloorHeight;
	}

	public static void setNegativeFloorHeight(double[] negativeFloorHeight) {
		FloorInformation.negativeFloorHeight = negativeFloorHeight;
	}

	public static void separateNegativePostive() {
		int num = 0;
		for (int i = 0; i < floorNumArray.length; i++) {
			if (floorNumArray[i] > 0) {
				break;
			}
			num++;
		}

		int[] negativeFloorNumArray1 = new int[num];
		double[] negativeFloorHeight1 = new double[num];
		for (int i = num - 1; i >= 0; i--) {
			negativeFloorNumArray1[i] = floorNumArray[num - i - 1];
			negativeFloorHeight1[i] = eachfloorHeight[num - i - 1];
		}
		
		negativeFloorNumArray=new int[num+1];
		negativeFloorHeight=new double[num+1];
		
		negativeFloorHeight[num]=0.0;
		negativeFloorNumArray[0]=1;
		for(int i=0;i<negativeFloorNumArray1.length;i++){
			negativeFloorNumArray[i+1]=negativeFloorNumArray1[i];
			negativeFloorHeight[i]=negativeFloorHeight1[i];
		}
		

		postiveFloorNumArray = new int[floorNumArray.length - num];
		postiveFloorHeight = new double[floorNumArray.length - num];

		for (int i = num; i < floorNumArray.length; i++) {
			postiveFloorNumArray[i - num] = floorNumArray[i];
			postiveFloorHeight[i - num] = eachfloorHeight[i];

		}

	}

}
