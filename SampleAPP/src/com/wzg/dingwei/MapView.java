package com.wzg.dingwei;

import java.io.IOException;
import java.util.ArrayList;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.location.demo.R;
import com.wzg.dingwei.Adjust;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MapView extends View {
	private Context ctx;
	private Canvas mapCan;
	private Bitmap manBmp;
	private Bitmap manGrayBmp;
	private View hostView =null;
	public AMap aMap;
	public com.amap.api.maps.MapView mapView=null;
	private boolean isInit=false;
	private static final int invalite = 100000;
	private float radius = 3f;
	private Paint run_paint,grid_paint,dash_paint,hchart_paint,hchartbg_paint;
	private int viewWidth;
	private int viewHeight;
	private int lastTouchX=0, lastTouchY=0;
	private float lastOffsetX=0, lastOffsetY=0;
	private PointF graphpos=new PointF(0,0);
	private static float baseScale = 2f;
	private static int zoom = 0;
	public static float pixelmeter = 5f;
	public static float curscale = 1f;
	private Handler handler;
	float heightMax=10;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		mapView.dispatchTouchEvent(event); 
/*		
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastTouchX = (int) event.getX();
			lastTouchY = (int) event.getY();
			//lastOffsetX=DataCenter.offsetX;
			//lastOffsetY=DataCenter.offsetY;
			break;
		case MotionEvent.ACTION_MOVE:
			int x = (int) event.getX();
			int y = (int) event.getY();
			//DataCenter.offsetX=lastOffsetX+(x-lastTouchX)/DataCenter.unitScales/DataCenter.scales;
			//DataCenter.offsetY=lastOffsetY+(x-lastTouchY)/DataCenter.unitScales/DataCenter.scales;	
			graphpos.x+=x-lastTouchX;
			graphpos.y+=y-lastTouchY;
			lastTouchX = x;
			lastTouchY = y;

			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			break;
		default:
			break;
		}
		invalidate(0, 0, viewWidth, viewHeight);
		*/
		return true;
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		hostView=this;
		run_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		run_paint.setAntiAlias(true);
		run_paint.setStyle(Paint.Style.STROKE);
		run_paint.setFilterBitmap(true);
		run_paint.setColor(Color.RED);
		run_paint.setTextSize(20);
		run_paint.setStrokeWidth(2f);
		
		grid_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		grid_paint.setAntiAlias(true);
		grid_paint.setStyle(Paint.Style.STROKE);
		grid_paint.setFilterBitmap(true);
		grid_paint.setColor(Color.GRAY);
		grid_paint.setStrokeWidth(1f);
		
		dash_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		PathEffect effects = new DashPathEffect(new float[] { 10, 4, 10, 4}, 2);   
		dash_paint.setPathEffect(effects);   
		dash_paint.setAntiAlias(true);
		dash_paint.setStyle(Paint.Style.STROKE);
		dash_paint.setFilterBitmap(true);
		dash_paint.setColor(Color.RED);
		dash_paint.setStrokeWidth(2f);
		
		hchart_paint= new Paint(Paint.ANTI_ALIAS_FLAG);
		hchart_paint.setColor(Color.RED);//Color.argb(1, 0, 0, 0));
		hchart_paint.setAntiAlias(true);
		hchart_paint.setStyle(Paint.Style.STROKE);
		hchart_paint.setFilterBitmap(true);
		hchart_paint.setStrokeWidth(2f);		
		
		hchartbg_paint= new Paint(Paint.ANTI_ALIAS_FLAG);
		hchartbg_paint.setColor(Color.argb(25, 0, 0, 0));//Color.argb(1, 0, 0, 0));
		hchartbg_paint.setStyle(Paint.Style.FILL);
		hchartbg_paint.setFilterBitmap(true);
		
		
		manBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireman); 
		manGrayBmp= BitmapFactory.decodeResource(context.getResources(), R.drawable.firemangray); 
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				int what = msg.what;
				if (what == invalite) {
					MapView.this.invalidate(0, 0,viewWidth,viewHeight);
				}
				super.handleMessage(msg);
			}
		};
	}
	
	private int style=AMap.LOCATION_TYPE_LOCATE;
	public void  setMyLocationType(int style)
	{
		this.style=style;
	}
	
	private float scale=1;
	private float scalePerPixel=1;
	public void  setScalePerPixel(float scale)
	{
		scalePerPixel=scale;
		this.scale=(float) (scalePerPixel*Math.pow(2, scaleLevel));
	}
	
	private float scaleLevel=0;
	public void  setScaleBy(int delta)
	{
		scaleLevel+=delta;
		this.scale=(float) (scalePerPixel*Math.pow(2, scaleLevel));
		
	}
	
	
	private PointF centerPoint=null;
	public void  setOrgPoint(PointF centerPoint)
	{
		this.centerPoint=centerPoint;
	}
	
	private double pathSin=0,pathCos=1;
	@Override
	protected void onDraw(Canvas canvas) {
		
		try{
		if (!isInit) {
			viewWidth = getMeasuredWidth();
			viewHeight = getMeasuredHeight();
			graphpos.x=viewWidth*.5f;
			graphpos.y=viewHeight*.5f;
			zoomBy(-5);
			isInit = true;
		}
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float scaledDensity = dm.scaledDensity;
		float unit=10;
		int thisZoom=(int)(Math.log(this.curscale)/Math.log(2));
		while(thisZoom<0)thisZoom+=3;
		float step=(float) (Math.pow(2, thisZoom%3)*unit*pixelmeter);
		/*
		int cols = (int) (viewWidth/step+3);
		int rows = (int) (viewHeight/step+3);
		float w = cols*step;
		float h = rows*step;
		float px,py;
		int left = (int) ((int)((-graphpos.x/step)-1)*step+graphpos.x);
		int top  = (int) ((int)((-graphpos.y/step)-1)*step+graphpos.y);
		for(int i = 0;i<cols;i++)
		{
			float curvalue = i*step+left;
			px =curvalue;
			py = top;
			canvas.drawLine(px,py,px,py+h,grid_paint);
		}

		for(int i = 0;i<rows;i++)
		{
			float curvalue = i*step+top;
			px =left;
			py = curvalue;
			canvas.drawLine(px,py,px+w,py,grid_paint);
		}
		*/
		this.pathSin=Math.sin(DataCenter.gpsRot);
		this.pathCos=Math.cos(DataCenter.gpsRot);
		
		int pointCount = DataCenter.points.size();
		if(pointCount==0) return;
		float[] pos={-100,0,0};
       /*
		if(pointCount>=1)
        {
        	PointF ptf=DataCenter.points.get(pointCount-1);
        	transformPoint(pos,ptf.x,ptf.y,1);
			canvas.drawCircle(pos[0], pos[1], radius, run_paint);
        }
        */
        double detailLevel=0;//2.5/this.curscale;
		int detailNum=5;
        int segMaxNum=DataCenter.segMaxNum;
		int segNum=0;
        float[] fromPos={0,0,0};
        float[] toPos={0,0,0};
        PointF start=null,end=null;
		int lastIndex=DataCenter.points.size()-detailNum;
		if(lastIndex<1) lastIndex=1;
		for(int i=DataCenter.points.size()-1;i>=lastIndex;i--)
		{
			 if(end==null)
			  {
				  start=DataCenter.points.get(i);
				  transformPoint(fromPos,start.x,start.y,1);
				  if(i==DataCenter.points.size()-1)
		          {
					  if(this.style==AMap.LOCATION_TYPE_MAP_FOLLOW)
					  {
							//this.graphpos.x-=fromPos[0]-viewWidth*.5f;
							//this.graphpos.y-=fromPos[1]-viewHeight*.5f;
							transformPoint(fromPos,start.x,start.y,1);
							aMap.moveCamera(CameraUpdateFactory.changeLatLng(aMap.getProjection().fromScreenLocation(new Point((int)fromPos[0],(int)fromPos[1]))));

					  }
		        	  pos[0]=fromPos[0];pos[1]=fromPos[1];pos[2]=fromPos[2];
		          }
				  
			  }else{
				  fromPos[0]=toPos[0]; fromPos[1]=toPos[1]; fromPos[2]=toPos[2];
			  }
			  end=DataCenter.points.get(i-1);
			  transformPoint(toPos,end.x,end.y,1);
	          canvas.drawLine(fromPos[0], fromPos[1], toPos[0], toPos[1], run_paint);
	         
			segNum++;
		}
		if(lastIndex>=1){
			fromPos[0]=toPos[0]; fromPos[1]=toPos[1]; fromPos[2]=toPos[2];
			PointF prePoint=DataCenter.points.get(lastIndex-1);
			for(int i=lastIndex-2;i>=0;i--)
			{
				PointF nxtPoint=DataCenter.points.get(i);
				if(Math.abs(nxtPoint.x-prePoint.x)+Math.abs(nxtPoint.y-prePoint.y)>detailLevel)
				{
					transformPoint(toPos,nxtPoint.x,nxtPoint.y,1);
					canvas.drawLine(fromPos[0], fromPos[1], toPos[0], toPos[1], run_paint);  
					prePoint=nxtPoint;
					fromPos[0]=toPos[0]; fromPos[1]=toPos[1]; fromPos[2]=toPos[2];
				}
				if(++segNum>segMaxNum) break;
			}
		}
		if(segNum<=segMaxNum&&DataCenter.points.size()>=2)
		{
			PointF nxtPoint=DataCenter.points.get(0);
			transformPoint(toPos,nxtPoint.x,nxtPoint.y,1);
			canvas.drawLine(fromPos[0], fromPos[1], toPos[0], toPos[1], run_paint);  
		}

		if(pointCount>0)
		{
			canvas.drawBitmap(this.manBmp, pos[0]-16*scaledDensity, pos[1]-15*scaledDensity, run_paint);
			 int textWidth=(int) run_paint.measureText( DataCenter.deviceid);
			canvas.drawText(DataCenter.deviceid, pos[0]-textWidth*.5f,pos[1]+30*scaledDensity,run_paint);
		}
		float curx=pos[0];
		float cury=pos[1];
		for(int i=0;i<DataCenter.manPos.size();i++)
		{
			ManPos manPos=DataCenter.manPos.get(i);
			if(!manPos.id.toUpperCase().equals(DataCenter.deviceid))
			{
				this.pathSin=Math.sin(manPos.rad);
				this.pathCos=Math.cos(manPos.rad);
				transformPoint(pos,manPos.x,manPos.y,1);
			    if(manPos.id.equals(DataCenter.targetid))
			    {
			    	canvas.drawCircle(pos[0], pos[1], 30*scaledDensity, dash_paint);
			    	canvas.drawLine(curx, cury, pos[0], pos[1], dash_paint);  
			    }		
			    canvas.drawBitmap(this.manGrayBmp, pos[0]-16*scaledDensity, pos[1]-15*scaledDensity, run_paint);
			    int textWidth=(int) run_paint.measureText( manPos.id);
			    canvas.drawText(manPos.id, pos[0]-textWidth*.5f,pos[1]+30*scaledDensity,run_paint);
			}
		}
/*		
		// 显示高度图
		int rawSize=DataCenter.rawPoints2.size();
		if(rawSize<2) return;
		heightMax=Math.max(heightMax, 2*Math.abs(DataCenter.rawPoints2.get(rawSize-1)[2]));
		float hChartHeight=viewHeight*0.4f;
		float hChartY=viewHeight-hChartHeight;
		float hChartWidth=this.viewWidth;
		float scalex=3,scaley=(hChartHeight-10)/heightMax;
		int xnum=(int)(hChartWidth/scalex);
		int from=(int)((float)rawSize/xnum)*xnum;
		int to=Math.min(from+xnum,DataCenter.rawPoints2.size());
		float prexx=0,preyy=0;
		canvas.drawRect(0, hChartY, hChartWidth, viewHeight, hchartbg_paint);
		canvas.drawLine(0, hChartY, hChartWidth, hChartY,run_paint);  
		canvas.drawLine(0, (hChartY+hChartHeight*.5f), hChartWidth, (hChartY+hChartHeight*.5f),run_paint);

		for(int i=from;i<to;i++)
		{
			float xx=(i-from)*scalex;
			float yy=DataCenter.rawPoints2.get(i)[2]*scaley+hChartY+hChartHeight*.5f;
			if(i>from)
			{
				canvas.drawLine(prexx, preyy, xx, yy,hchart_paint); 
			}
			prexx=xx;
			preyy=yy;
		}		
*/		
		
/*		
		// 绘制校正线
		{
			grid_paint.setColor(Color.BLACK);
			grid_paint.setStrokeWidth(2);
			this.pathSin=Math.sin(DataCenter.gpsRot);
			this.pathCos=Math.cos(DataCenter.gpsRot);
			for(int i=0;i<DataCenter.adjust.lineInfs.size();i++)
			{
				int from=DataCenter.adjust.lineInfs.get(i).from;
				int to=DataCenter.adjust.lineInfs.get(i).to;
				PointF p0=DataCenter.points.get(from);
				PointF p1=DataCenter.points.get(to);
				transformPoint(fromPos,p0.x,p0.y,1);
				transformPoint(toPos,p1.x,p1.y,1);
				canvas.drawLine(fromPos[0], fromPos[1], toPos[0], toPos[1], grid_paint);  
			}
			grid_paint.setColor(Color.GRAY);
			grid_paint.setStrokeWidth(1);
		}
*/		
		
		
		}catch(Exception e)
		{
			LogFile.trace(e);
		}
	}


	  
	public void zoomBy(int delta) {
		
		float mx = (viewWidth*.5f-graphpos.x)/curscale;
		float my = (viewHeight*.5f-graphpos.y)/curscale;
		
		float deltas = (float) Math.pow(baseScale,delta);
		float deltax = -mx*curscale*(deltas-1);
		float deltay = -my*curscale*(deltas-1);
	
		float newScale = (float) Math.pow(baseScale,zoom+delta);
		if(newScale<50&&newScale>0.05)
		{
			graphpos.x+=deltax;
			graphpos.y+=deltay;
			zoom+=delta;
			curscale = newScale;
		}	
		this.invalidate();
	}
	
	public void zoominClick() {
		zoomBy(1);
	}

    public void zoomoutClick() {
    	zoomBy(-1);
	}

    public  void fullextentClick() 
    {
    	moveTo(0,0,1);
    	zoomBy(-zoom);

	}	
	
    public void moveTo(float x,float y,int z)
	{
		zoom = z;
		curscale = (float) Math.pow(baseScale,z);
		graphpos.x = -x+viewWidth*.5f;
		graphpos.y = -y+viewHeight*.5f;
	}
    
    public void transformPoint(float[]ret,float x,float y,int z)
   	{
   		x=x/z/scale;
   		y=-y/z/scale;
   		ret[0]=(float) (x*pathCos-y*pathSin+centerPoint.x);
   		ret[1]=(float) (x*pathSin+y*pathCos+centerPoint.y);
   		ret[2]=curscale;
   	}    
    
/*
    public void transformPoint(float[]ret,float x,float y,int z)
	{
		x=x/z*curscale*pixelmeter;
		y=-y/z*curscale*pixelmeter;
		ret[0]=(float) (x*pathCos-y*pathSin+graphpos.x);
		ret[1]=(float) (x*pathSin+y*pathCos+graphpos.y);
		ret[2]=curscale;
	}
*/	
    public void showVoiceTip(Speek mSpeek)
    {
        	if(DataCenter.points.size()>=3)
        	{
        		ManPos dstMan=null,srcMan=null;
        		for(int i=0;i<DataCenter.manPos.size();i++)
        		{
        			ManPos manPos=DataCenter.manPos.get(i);
        			if(manPos.id.equals(DataCenter.deviceid))
        			{
        				srcMan=manPos;
        				break;
        			}
        		}
        		if(srcMan==null||srcMan.targetid==null||srcMan.targetid.equals("0000")) return;
        		for(int i=0;i<DataCenter.manPos.size();i++)
    	    	{
    	    			ManPos manPos=DataCenter.manPos.get(i);
    	    			if(manPos.id.equals(srcMan.targetid))
    	    			{
    	    				dstMan=manPos;
    	    				break;
    	    			}
    	    	}
        		if(dstMan==null) return;
        		
        		float[] dst={0,0,1};
        		this.pathSin=Math.sin(dstMan.rad);
    			this.pathCos=Math.cos(dstMan.rad);
    			transformPoint(dst,dstMan.x,dstMan.y,1);
    			
    			PointF last1Point=DataCenter.points.get(DataCenter.points.size()-1);
    			PointF last2Point=DataCenter.points.get(DataCenter.points.size()-3);
    			float[] src1={0,0,1},src2={0,0,1};
    			this.pathSin=Math.sin(srcMan.rad);
    			this.pathCos=Math.cos(srcMan.rad);
    			transformPoint(src1,last1Point.x,last1Point.y,1);
    			transformPoint(src2,last2Point.x,last2Point.y,1);
    			
				transformPoint(src1,last1Point.x,last1Point.y,1);
				transformPoint(src2,last2Point.x,last2Point.y,1);
	    		showVoiceTip(mSpeek,
	    						new PointF(src1[0],src1[1]),
	    						(float)Math.atan2(src1[1]-src2[1], src1[0]-src2[0]),
	    						new PointF(dst[0],dst[1]),
	    						null);
    		
    	
        	}
     }
	// src，target 定位点坐标，以米为单位，向右向前为正
	// dir 当前人朝向，[Math.PI , -Math.PI]
	protected void showVoiceTip(Speek mSpeek,PointF src,float dirRad,PointF target,String spkHeader) 
	{
		if(spkHeader==null) spkHeader="搜寻目标";
		float rad =  (float) Math.atan2(target.y-src.y, target.x-src.x);
		rad-=dirRad;
		if(rad<0) rad+=Math.PI*2;
		float radIndex=rad/(float) (Math.PI/8);
		if(radIndex<1||radIndex>15)
		{
			// front
			mSpeek.say(spkHeader+"在前方");
		}else if(radIndex<3)
		{
			// front left
			mSpeek.say(spkHeader+"在前方偏左");
		}else if(radIndex<5)
		{
			// left
			mSpeek.say(spkHeader+"在左边");
		}else if(radIndex<7)
		{
			// back left
			mSpeek.say(spkHeader+"在后方偏左");
		}else if(radIndex<9)
		{
			// back
			mSpeek.say(spkHeader+"在后方");
		}else if(radIndex<11)
		{
			// back right
			mSpeek.say(spkHeader+"在后方偏右");
		}else if(radIndex<13)
		{
			// right
			mSpeek.say(spkHeader+"在右边");
		}else if(radIndex<15)
		{
			// front right
			mSpeek.say(spkHeader+"在前方偏右");
		}
	}
	
	// 根据当前点查找最接近的线条,计算线条的方向，根据当前的路径方向和夹角给出路径提示
	protected void showVoiceTip2(Speek mSpeek,PointF src,float dirRad,PointF target) 
	{
		double threshRad=Math.PI/3;
		double threshDistance=2;
		int pointCount=DataCenter.points.size();
		for (int i = pointCount-2; i >=1; i--) 
		{
			PointF p0=DataCenter.points.get(i);
			PointF p1=DataCenter.points.get(i-1);
			float distanc=Math.abs(Utility.segmentDistance(src,p0,p1));
			float absdistanc=Math.abs(distanc);
			if(absdistanc<threshDistance)
			{
				PointF dir=new PointF(p1.x-p0.x,p1.y-p0.y);
				float len=dir.length();
				dir.x/=len;dir.y/=len;
				float cos=dir.x*(src.x-p0.x)+dir.y*(src.y-p0.y);
				if(cos>0&&cos<len)
				{
					float rad=Utility.getDelta(DataCenter.points.get(pointCount-1),
								DataCenter.points.get(pointCount-2),
								DataCenter.points.get(i),
								DataCenter.points.get(i-1));
					if(rad<threshRad||rad>Math.PI-threshRad)
					{
						mSpeek.say(distanc<0&&rad>Math.PI-threshRad?"偏右":"偏左");
						break;
					}
				}
				
			}
		}
	}

}
