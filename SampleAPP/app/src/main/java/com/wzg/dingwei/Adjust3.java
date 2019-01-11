package com.wzg.dingwei;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PointF;
import android.util.Log;




public class Adjust3 {


	Adjust3()
	{
			
	}
	
	double[] pos0={0,0,0};
	double[] pos1={0,0,0};
	double[] pos2={0,0,0};
	public double[] curPos={0,0,0};
	public int size=0;
	public double total=0;
	double step=0.2;
	double levelStep=0.1;		//	0.1
	double heightStep=0.3;		//	0.3
	public double coe=0;		//-0.8
	public double downCoe=0.0;	//	0.2
	public boolean needRaw=false;
	public int lastAdjustIndex=0;//第一条校正线的结束索引
	public void init()
	{
		pos0[0]=pos0[1]=pos0[2]=0;
		pos1[0]=pos1[1]=pos1[2]=0;
		curPos[0]=curPos[1]=curPos[2]=0;
		size=0;
		total=0;
	}
	double totallen=0;
	double totalRad=0; 
	public void adjustAddPos(float[] pos)
	{
		if(needRaw)	
		{
			float[] rawPos={pos[0],pos[1],pos[2]};
			DataCenter.rawPoints2.add(rawPos);
		}
		
		size++;
		if(size==2)
		{
			curPos[0]=pos[0];
			curPos[1]=pos[1];
			curPos[2]=pos[2];
		}
		
		if(size>=4)
		{
			// 0.28f
			// 0.32f
			double deltah=pos[2]-pos0[2];
			double deltah1=pos0[2]-pos1[2];
			double deltah2=pos1[2]-pos2[2];
			//deltah=deltah-deltah1;
			double len0= Math.sqrt((pos[0]-pos0[0])*(pos[0]-pos0[0])+(pos[1]-pos0[1])*(pos[1]-pos0[1]));
			double len1= Math.sqrt((pos0[0]-pos1[0])*(pos0[0]-pos1[0])+(pos0[1]-pos1[1])*(pos0[1]-pos1[1]));
			double len2= Math.sqrt((pos1[0]-pos2[0])*(pos1[0]-pos2[0])+(pos1[1]-pos2[1])*(pos1[1]-pos2[1]));
			double rad= Math.atan2(deltah, len0);//+len1+len2);
			double raddir0= Math.atan2(pos[1]-pos0[1], pos[0]-pos0[0]);
			double raddir1= Math.atan2(pos0[1]-pos1[1], pos0[0]-pos1[0]);
			double raddir2= Math.atan2(pos1[1]-pos2[1], pos1[0]-pos2[0]);
			double deltarad=Utility.getDeltaDir(raddir1, raddir0);
			double deltarad1=Utility.getDeltaDir(raddir2, raddir1);
			totallen+=len0;
			/*
			if(Math.abs(deltah)<0.1f&&len0>0.1f)//+len1+len2>0.2f)//&&deltarad*deltarad1>0)
				rad=-0.022*rad;
			else 
				rad=0;
			rad=0;
			*/
			double scale=1/Math.cos(rad);
			if(Math.abs(deltah)<levelStep&&len0>step)//+len1+len2>0.2f)//&&deltarad*deltarad1>0)
				rad=coe*.02;//*Math.sqrt(1-Math.cos(rad));
			else if(deltah>heightStep&&len0>step) 
			{
				rad=downCoe*Math.sqrt(1-Math.cos(rad));
			}else
				rad=0;
			total+=rad;
			double xx=pos[0]-pos0[0];
			double yy=pos[1]-pos0[1];
			
			double x= (Math.cos(total)*xx-Math.sin(total)*yy)*scale;
			double y= (Math.sin(total)*xx+Math.cos(total)*yy)*scale;
			
			
			curPos[0]=x+curPos[0];
			curPos[1]=y+curPos[1];
			
		}
		pos2[0]=pos1[0];
		pos2[1]=pos1[1];
		pos2[2]=pos1[2];	
		
		pos1[0]=pos0[0];
		pos1[1]=pos0[1];
		pos1[2]=pos0[2];	
		
		pos0[0]=pos[0];
		pos0[1]=pos[1];
		pos0[2]=pos[2];
		
		if(size>=3)
		{
			pos[0]=(float)curPos[0];
			pos[1]=(float)curPos[1];
		}
	}
	
	public void reAdjustAddPos(float rad)
	{
		//rad=0;
		 DataCenter.adjustIndex=0;
		 DataCenter.adjustRad=0;
		 if(rad==0) return;
		 if(DataCenter.adjust3.lastAdjustIndex==-1) DataCenter.adjust3.lastAdjustIndex=0;
		 if(DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1)-DataCenter.adjust3.lastAdjustIndex<20) return;
		 DataCenter.adjustIndex=DataCenter.adjust3.lastAdjustIndex;
		 DataCenter.adjustRad=rad;
	     float deltaRad=rad/(DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1)-DataCenter.adjust3.lastAdjustIndex);
	     int startIndex=-1,i;
	     for(i=0;i<DataCenter.pointsIndex.size();i++)
	     {
	    	 if(DataCenter.pointsIndex.get(i).equals(DataCenter.adjust3.lastAdjustIndex)) 
	    	 {
	    		 startIndex=i;
	    		 break;
	    	 }
	     }
	     // 长度，时间?
	     PointF preP=DataCenter.points.get(i);
	     float newx=preP.x,newy=preP.y;
	     for(++i;i<DataCenter.points.size();i++)
	     {
	    	 PointF p=DataCenter.points.get(i);
	    	 float offx=p.x-preP.x;
	    	 float offy=p.y-preP.y;
	    	 preP.x=newx;
	    	 preP.y=newy;
	    	 double cos=Math.cos(deltaRad*(i-startIndex));
	    	 double sin=Math.sin(deltaRad*(i-startIndex));
	    	 newx=(float) (offx*cos-offy*sin)+preP.x;
	    	 newy=(float) (offx*sin+offy*cos)+preP.y;
	    	 preP=p;
	     }
	    preP.x=newx;
	    preP.y=newy;
		DataCenter.adjust3.needRaw=false;
		DataCenter.adjustFlag=false;
		DataCenter.adjust3.total+=-rad;
		DataCenter.adjust3.curPos[0]=-newx;
		DataCenter.adjust3.curPos[1]=newy;
		DataCenter.adjust3.lastAdjustIndex=DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1);
		
	}
	
	public void reAdjustAddPos4(float rad)
	{
		//rad=0;
		 DataCenter.adjustIndex=0;
		 DataCenter.adjustRad=0;
		 if(rad==0) return;
		 if(DataCenter.adjust3.lastAdjustIndex==-1) DataCenter.adjust3.lastAdjustIndex=0;
		 if(DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1)-DataCenter.adjust3.lastAdjustIndex<20) return;
		 DataCenter.adjustIndex=DataCenter.adjust.lineInfs.get(DataCenter.adjust.lineInfs.size()-1).from;
		 DataCenter.adjustRad=rad;
	     float deltaRad=rad/(DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1)-DataCenter.adjust3.lastAdjustIndex);
	     int startIndex=-1,i;
	     for(i=0;i<DataCenter.pointsIndex.size();i++)
	     {
	    	 if(DataCenter.pointsIndex.get(i).equals(DataCenter.adjust3.lastAdjustIndex)) 
	    	 {
	    		 startIndex=i;
	    		 break;
	    	 }
	     }
	     
	     startIndex=i=DataCenter.adjust.lineInfs.get(DataCenter.adjust.lineInfs.size()-1).from;//??
	     // 长度，时间?
	     PointF preP=DataCenter.points.get(i);
	     float newx=preP.x,newy=preP.y;
	      deltaRad=rad;
	     for(++i;i<DataCenter.points.size();i++)
	     {
	    	 PointF p=DataCenter.points.get(i);
	    	 float offx=p.x-preP.x;
	    	 float offy=p.y-preP.y;
	    	 preP.x=newx;
	    	 preP.y=newy;
	    	 //double cos=Math.cos(deltaRad*(i-startIndex));
	    	 //double sin=Math.sin(deltaRad*(i-startIndex));
	    	 double cos=Math.cos(deltaRad);
	    	 double sin=Math.sin(deltaRad);
	    	 newx=(float) (offx*cos-offy*sin)+preP.x;
	    	 newy=(float) (offx*sin+offy*cos)+preP.y;
	    	 preP=p;
	     }
	    preP.x=newx;
	    preP.y=newy;
		DataCenter.adjust3.needRaw=false;
		DataCenter.adjustFlag=false;
		DataCenter.adjust3.total+=-rad;
		DataCenter.adjust3.curPos[0]=-newx;
		DataCenter.adjust3.curPos[1]=newy;
		DataCenter.adjust3.lastAdjustIndex=DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1);
		
	}
	
	
	public void reAdjustAddPos2(float rad)
	{
		
		 DataCenter.adjustIndex=0;
		 DataCenter.adjustRad=rad;
		 if(rad==0)  return;
		 if(DataCenter.points.size()==0) return;
	     float deltaRad=rad/DataCenter.points.size();
	     int startIndex=0,i=0;
	     // 长度，时间?
	     PointF preP=DataCenter.points.get(i);
	     float newx=preP.x,newy=preP.y;
	     for(++i;i<DataCenter.points.size();i++)
	     {
	    	 PointF p=DataCenter.points.get(i);
	    	 float offx=p.x-preP.x;
	    	 float offy=p.y-preP.y;
	    	 preP.x=newx;
	    	 preP.y=newy;
	    	 double cos=Math.cos(deltaRad*(i-startIndex));
	    	 double sin=Math.sin(deltaRad*(i-startIndex));
	    	 newx=(float) (offx*cos-offy*sin)+preP.x;
	    	 newy=(float) (offx*sin+offy*cos)+preP.y;
	    	 preP=p;
	     }
	    preP.x=newx;
	    preP.y=newy;
		DataCenter.adjust3.needRaw=false;
		DataCenter.adjustFlag=false;
		DataCenter.adjust3.coe=deltaRad;
		DataCenter.adjust3.total+=-rad;
		DataCenter.adjust3.curPos[0]=-newx;
		DataCenter.adjust3.curPos[1]=newy;
	}
	
	
	public void reAdjustAddPos3(float rad)
	{
		if(rad==0) return;
		 coe+=rad/(DataCenter.rawPoints2.size()-1-lastAdjustIndex);
    	 DataCenter.reset(false);
	     DataCenter.adjust3.needRaw=false;
	     DataCenter.adjustFlag=true;
	     float[] pos={0,0,0,0};
	     for(int i=lastAdjustIndex+1;i<DataCenter.rawPoints2.size();i++)
	     {
	    	 pos[0]=DataCenter.rawPoints2.get(i)[0];
	    	 pos[1]=DataCenter.rawPoints2.get(i)[1];
	    	 pos[2]=DataCenter.rawPoints2.get(i)[2];
	    	 DataCenter.adjust3.adjustAddPos(pos);
	    	 pos[0]*=-1;
	    	 pos[2]*=-1;
	    	 DataCenter.adjust.adjustAddPos(pos);
	     }
	 	DataCenter.adjust3.needRaw=true;
		DataCenter.adjustFlag=false;
		lastAdjustIndex=DataCenter.rawPoints2.size();
		
		
	}
	
	
	public void reset()
	{
		pos0[0]=pos0[1]=pos0[2]=0;
		pos1[0]=pos1[1]=pos1[2]=0;
		pos2[0]=pos2[1]=pos2[2]=0;
		curPos[0]=curPos[1]=curPos[2]=0;
		size=0;
		total=0;
	}	
}
