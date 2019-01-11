package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.pressurePredicting;

import java.io.FileInputStream;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Floor_positioning {
	//楼层变量
	private int cur_floor;
	private Floor_position_msg_p f_position_msg;

	private int bp_th;
	private Bayes_predict bp;

	private double last_p=0;
	private int last_apnum=0;


	//初始化类
	public Floor_positioning(){

		init_floor_position_msg();// 获得楼层定位基本信息
	}
	public Floor_positioning(JSONObject obj)
	{
		f_position_msg=new Floor_position_msg_p(obj);
		//初始化mac地址信息，对应键值		
		//建立两个基于wifi的分类函数
		bp=new Bayes_predict(f_position_msg);		
		//设置过滤阈值
		bp_th=f_position_msg.bayesmodel.threshold;
	}

	public void init_floor_position_msg(){
		//----------------------------------------------
		String msgpath = "ict_note3/floor_position_msg.mix";
		String str="";
		try {
			FileInputStream fin = new FileInputStream(msgpath);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			str = String.valueOf(buffer);
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//----------------------------------------------
		f_position_msg=new Floor_position_msg_p(str);
		str=null;




		//初始化mac地址信息，对应键值		
		//建立两个基于wifi的分类函数
		bp=new Bayes_predict(f_position_msg);		
		//设置过滤阈值
		bp_th=f_position_msg.bayesmodel.threshold;
	}

	public Floor_positioning(String modelpath){
		String str="";
		try {
			FileInputStream fin = new FileInputStream(modelpath);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			str = new String(buffer);
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//----------------------------------------------
		f_position_msg=new Floor_position_msg_p(str);
		str=null;




		//初始化mac地址信息，对应键值		
		//建立两个基于wifi的分类函数
		bp=new Bayes_predict(f_position_msg);		
		//设置过滤阈值
		bp_th=f_position_msg.bayesmodel.threshold;
	}
	public int predict_(JSONArray wifiarr) {
		Bayesresult b_rs = new Bayesresult();
		try {
			int num = wifiarr.length();
			int[] sample_b = new int[num];
			int[] sample_s = new int[num];
			int b_num = 0, s_num = 0;
			for (int i = 0; i < num; i++) {// 样本向量化
				JSONObject mac = wifiarr.getJSONObject(i);				
				String macadd = mac.getString("mac");
				int level = mac.getInt("rssi");
				int seq = f_position_msg.getmacindex(macadd);
				if (seq == -1)
					continue;
				if (level > bp_th) {
					sample_b[b_num] = seq;
					b_num++;
				}// if
			}// for
			//			SpecialAPresult s_rs = sp.predict(sample_s, s_num);// 使用特殊AP算法进行楼层定位
			last_apnum=b_num;
			if (b_num >= 6) {
				b_rs = bp.predict(sample_b, b_num);// 贝叶斯预测
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		/*if (init_flag == false)// 暂未初始化完成，返回-1，
		{
			return -1;
		}*/
		System.out.println("bayes p:"+b_rs.p);
		return b_rs.floorindex;
	}

	public int predict_(JSONArray wifiarr,int real_floor) {
		Bayesresult b_rs = new Bayesresult();
		try {
			int num = wifiarr.length();
			int[] sample_b = new int[num];
			int b_num = 0;
			for (int i = 0; i < num; i++) {// 样本向量化
				JSONObject mac = wifiarr.getJSONObject(i);
				String macadd = mac.getString("mac");
				int level = mac.getInt("rssi");
				int seq = f_position_msg.getmacindex(macadd);
				if (seq == -1)
					continue;
				if (level > bp_th) {
					sample_b[b_num] = seq;
					b_num++;
				}// if
			}// for
			last_apnum=b_num;
			//	if(b_num>6)
			{
				b_rs = bp.predict(sample_b, b_num);// 贝叶斯预测
				last_p=b_rs.p;
			}
			//	else {
			//		last_p=0;
			//		return -1;
			//	}



		} catch (JSONException e) {
			e.printStackTrace();
		}

		return b_rs.floorindex;
	}

	public double getlast_p()
	{
		return last_p;
	}
	public int getlast_apnum()
	{
		return last_apnum;
	}

	//在线楼层判别
	//msg:
	//返回：楼层索引
	public int predict(String msg){
		JSONArray json_sample=null;
		try {
			JSONObject json_msg= new JSONObject(msg);//获得样本
			json_sample=json_msg.getJSONArray("measures");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return predict_(json_sample);			
	}



	public int predict_fortest(JSONObject jsonobj){
		cur_floor=-1;
		try {
			return predict_(jsonobj.getJSONArray("wifi"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public String getfloorname(int floorindex){
		return f_position_msg.getfloorname(floorindex);
	}	
}