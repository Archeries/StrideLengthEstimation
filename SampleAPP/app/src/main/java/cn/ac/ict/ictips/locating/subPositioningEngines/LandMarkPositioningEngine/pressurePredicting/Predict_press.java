package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.pressurePredicting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.R.integer;
import android.os.Environment;
import cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.Entity.FloorInformation;

public class Predict_press {

	private double h_ch_TH = 2.0;// 每隔time_th*H_list_num毫秒时间，高度变化的阈值，若超过，则是正常楼层转移引起，否则为气压随时间变化而引起
	private int H_list_num = 20;// 窗口中保存的高度数据数目
	private int stable_num = 10;// 稳定num次数之后第二次校准
	private double iden_TH = 1.2;// 楼层判别上下楼阈值
	private int time_th = 1000;// 时间间隔，每隔这么久平均一下高度值，
	private int window_size = 10;
	private long init_time;//开始快变化的初始时刻
	private boolean upDown = false;// 根据是否处于快变化来判断是否在上下楼
	// 中教每层3.5米
	private int[] hollowFloor;
	private double[] eachfloorHeight;
	private double[] floorHeight;


	private double height_diff0;//
	private double init_height;// 初始楼层高度
	private double heightAvg;

	private double height_ch_time = 0, height_ch_floor;// 随时间而变化的高度累积值，和通过上下楼动作变化的高度累计值
	private ArrayList<Double> height_list, height_window;// 高度历史记录，和平均值窗口
	private int init_floor = 0;// 初始楼层
	private int pre_floor_index;// 预测楼层
	private boolean init_flag = false;// 初始化标记
	private boolean floor_init_flag = false;
	private int state=0; //0是没有判断，1 是楼梯 2 是电梯
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	private int time_threshold=2000;//判断是电梯还是楼梯的时间阈值

	private SimpleDateFormat dateformat;
	private long fastChangeNum = 0;// 连续经历快变化的数目

	private long last_time = 0;

	private int repet_num = 0;
	private int big_repet_num = 50;

	public double height_dif = 0;

	private FileOutputStream fout;
	int[] floorNumArray;

	public Predict_press(int initfloor) {

		init_floor = initfloor;
		pre_floor_index = init_floor;
		eachfloorHeight = FloorInformation.getEachfloorHeight();
		hollowFloor = FloorInformation.getHollowFloor();
		floorHeight = new double[eachfloorHeight.length];
		floorHeight[0] = 0;

		for (int i = 1; i < eachfloorHeight.length; i++) {
			floorHeight[i] = floorHeight[i - 1] + eachfloorHeight[i - 1];
		}

		height_ch_floor = getheight(initfloor);

		height_window = new ArrayList<Double>();
		height_list = new ArrayList<Double>();
		height_ch_time = 0;
		init_flag = false;
	}

	// 楼层高度，修改此函数
	private double getheight(int floor) {

		if (floor >= 0 && floor < floorHeight.length)
			return floorHeight[floor];
		else if (floor < 0) {
			return -1e8;
		} else {
			return 1e8;
		}

	}

	public int get_pre_floor() {
		return pre_floor_index;
	}

	public int get_init_floor() {
		return init_floor;
	}

	public double get_height_time() {
		return height_ch_time;
	}

	public double get_height_floor() {
		return height_ch_floor;
	}

	public int get_floor(double height) {
		int i = 0;
		while (i < floorHeight.length && floorHeight[i] < height) {
			i++;
		}
		return i;
	}

	public double get_avg_height() {
		return heightAvg;
	}

	public double get_height_diff() {
		return height_dif;
	}

	public boolean isUpDown() {
		return upDown;
	}

	public void setUpDown(boolean upDown) {
		this.upDown = upDown;
	}

	public long getFastChangeNum() {
		return fastChangeNum;
	}

	public void setFastChangeNum(long fastChangeNum) {
		this.fastChangeNum = fastChangeNum;
	}


	// 这个方法主要用于wifi信号对楼层修正，注意在中空的区域，不要利用wifi进行楼层的修正
	public boolean setfloor(int floor) {
		// 检测是否是中空区域，如果是return false
		for (int i = 0; i < hollowFloor.length; i++) {
			if (floor == hollowFloor[i]) {
				return false;
			}
		}

		if (!floor_init_flag) {
			height_ch_floor = getheight(floor);
			pre_floor_index = floor;
			floor_init_flag = true;
			return true;
		}
		if (floor - pre_floor_index > 1 || pre_floor_index - floor > 1)// 一次跳变太大，wifi不可靠，拒绝更新
			return false;
		if (floor != pre_floor_index) {

			double diff = Math
					.abs(height_ch_floor - getheight(pre_floor_index));
			if (height_dif > 1.0 || diff > 0.5)// 如果当前预测楼层变化高度与预测楼层所在的楼层高度差别太大，说明正在上下楼中，或者没有稳定，拒绝更新
			{
				return false;
			} else {
				height_ch_floor = getheight(floor);
				pre_floor_index = floor;
			}
		}
		floor_init_flag = true;
		return true;
	}

	public boolean setfloorManual(int floor) {

		height_ch_floor = getheight(floor);
		pre_floor_index = floor;
		floor_init_flag = true;
		return true;

	}

	public int predict(double height, long time, int real_floor) {

		height_window.add(height);

		if (height_window.size() > window_size) {
			height_window.remove(0);
		} else {
			return -10; // lxr改 原来为-1
		}

		if (last_time == 0) {
			last_time = time;
			return -10; // lxr改 原来为-1
		}

		if (time - last_time < time_th) {
			return pre_floor_index;
		}
		last_time += time_th;

		heightAvg = avgheight(height_window);

		height_list.add(heightAvg);

		if (height_list.size() > H_list_num) {
			height_list.remove(0);
		} else {
			return pre_floor_index;
		}

		if (!init_flag) {
			init_height = heightAvg - height_ch_floor;
			init_flag = true;
		}

		double height_diff0 = heightAvg - height_list.get(0);

		height_dif = height_diff0;
		// 在这里，如果这里的floor_init_flag不是真的（就是说没有进行初始化）（wifi或者IOdetector），就不执行下面的程序
		// 就不进行楼层的判定
		if (floor_init_flag) {

			if (big_repet_num >= 5 && Math.abs(height_diff0) < h_ch_TH) {

				fastChangeNum = 0;
				this.setUpDown(false);
				this.setState(0);//状态设置为未知
				repet_num++;
				height_ch_time += heightAvg
						- height_list.get(height_list.size() - 2);
				if (repet_num > H_list_num)// 再次校准
				{
					double cur_height = getheight(pre_floor_index);
					double diff = cur_height - height_ch_floor;
					height_ch_floor = cur_height;
					height_ch_time -= diff;

				}
			} else {

				if (big_repet_num >= 5 && repet_num != 0)// 第一次快变化
				{
					height_ch_floor += height_diff0;
					height_ch_time -= height_diff0;
					big_repet_num = 0;
					Date curDate = new Date(System.currentTimeMillis());
					init_time = curDate.getTime();

				} else {
					height_ch_floor += heightAvg
							- height_list.get(height_list.size() - 2);
				}
				fastChangeNum++;
				if (fastChangeNum >= 2) {
					this.setUpDown(true);
				}
				repet_num = 0;
				big_repet_num++;

				if (height_diff0 > 0) {
					if (getheight(pre_floor_index + 1) - height_ch_floor < iden_TH) {
						pre_floor_index++;
						Date curDate2 = new Date(System.currentTimeMillis());
						if(curDate2.getTime()-init_time>time_threshold){
							this.setState(1);//楼梯
						}else{
							this.setState(2);//电梯
						}
						init_time=curDate2.getTime();
					}
				} else {
					if (height_ch_floor - getheight(pre_floor_index - 1) < iden_TH) {
						pre_floor_index--;
						Date curDate2 = new Date(System.currentTimeMillis());
						if(curDate2.getTime()-init_time>time_threshold){
							this.setState(1);//楼梯
						}else{
							this.setState(2);//电梯
						}
						init_time=curDate2.getTime();
					}
				}
			}
		}

		return pre_floor_index;
	}

	double avgheight(ArrayList<Double> list) {
		double sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}
		return sum / list.size();
	}

	public void stop() {

	}

}
