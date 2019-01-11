package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.pressurePredicting;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Floor_position_msg_p {
	Build_msg Buildmsg;
	Macdata macdata;
	Bayes_model bayesmodel;

	public Floor_position_msg_p(String obj) {
		try {
			JSONObject jsonobj = new JSONObject(obj);
			Buildmsg=new Build_msg(jsonobj.getJSONObject("Buildmsg"));
			macdata=new Macdata(jsonobj.getJSONObject("macdata"));
			bayesmodel=new Bayes_model(jsonobj.getJSONObject("bayesmodel"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public Floor_position_msg_p(JSONObject jsonobj) {
		try {
			Buildmsg=new Build_msg(jsonobj.getJSONObject("Buildmsg"));
			macdata=new Macdata(jsonobj.getJSONObject("macdata"));
			bayesmodel=new Bayes_model(jsonobj.getJSONObject("bayesmodel"));
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getfloorname(int index)
	{
		return Buildmsg.floor[index].floorname;
	}
	public double getfloorheight(int index){
		return Buildmsg.floor[index].floorheight;
	}
	public int getfloornum(){
		return Buildmsg.floornum;
	}
	public int getmacindex(String macadd)
	{
		int i=0;
		for(i=0;i<macdata.macnum;i++)
		{
			if(macadd.equals(macdata.mac.get(i).macadd))
				return macdata.mac.get(i).macindex;
		}
		return -1;
	}
	
}

class Build_msg{
	int floornum;
	floormsg []floor;
	public Build_msg(JSONObject obj) {
		try {
			floornum=obj.getInt("floornum");
			floor=new floormsg[floornum];
			JSONArray jsonarray = obj.getJSONArray("floor");
			for(int i=0;i<floornum;i++){
				floor[i]=new floormsg(jsonarray.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
}
class floormsg{
	
	int floorindex;
	String floorname;
	double floorheight;	
	public floormsg(JSONObject obj){
		try {
			floorindex=obj.getInt("floorindex");
			floorname=obj.getString("floorname");
			floorheight=obj.getDouble("floorheight");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

class Macdata{
	int macnum;
	ArrayList<Macmsg> mac;	
	public Macdata(JSONObject obj) {
		try {
			macnum=obj.getInt("macnum");
			mac=new ArrayList<Macmsg>();
			JSONArray jsonarr = obj.getJSONArray("mac");
			for(int i=0;i<jsonarr.length();i++)
			{
				mac.add(new Macmsg(jsonarr.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
class Macmsg{
	String macadd;
	int macindex;
	public Macmsg(JSONObject obj){
		try {
			macadd=obj.getString("macadd");
			macindex = obj.getInt("macindex");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

class Bayes_model{
	int threshold;
	int totalsamplenum;
	ArrayList<bayes_floor_msg> floor_AP_count;
	public Bayes_model(JSONObject obj){
		try {
			threshold=obj.getInt("threshold");		
			JSONArray jsonarr = obj.getJSONArray("floor_AP_count");
			floor_AP_count=new ArrayList<bayes_floor_msg>();
			totalsamplenum=0;
			for(int i=0;i<jsonarr.length();i++)
			{
				bayes_floor_msg bfm=new bayes_floor_msg(jsonarr.getJSONObject(i));
				floor_AP_count.add(bfm);
				totalsamplenum+=bfm.sample_num;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
	int getAPcount(int floor,int apindex){		
		return floor_AP_count.get(floor).AP_count[apindex];
	}
	int getfloorsamplenum(int floor){
		return floor_AP_count.get(floor).sample_num;
	}
	int gettotalsamplenum(){
		return totalsamplenum;
	}
	int getfloorAPnum(int floor){
		return floor_AP_count.get(floor).AP_num;
	}
}
class bayes_floor_msg{
	int floor_index;
	int sample_num;
	int AP_num;
	int []AP_count;
	public bayes_floor_msg(JSONObject obj){
		try {
			floor_index=obj.getInt("floor_index");		
			sample_num=obj.getInt("sample_num");
			AP_num=obj.getInt("AP_num");
			JSONArray json_ap_count=obj.getJSONArray("AP_count");
			AP_count = new int [json_ap_count.length()];
			for(int i=0;i<json_ap_count.length();i++)
			{
				AP_count[i]=(Integer)json_ap_count.get(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
}


