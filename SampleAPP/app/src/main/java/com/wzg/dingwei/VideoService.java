package com.wzg.dingwei;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class VideoService  implements SurfaceHolder.Callback,Camera.PreviewCallback{

	public Context context=null;
	public SurfaceView mSurfaceview = null; // SurfaceView对象：(视图组件)视频显示
    private SurfaceHolder mSurfaceHolder = null; // SurfaceHolder对象：(抽象接口)SurfaceView支持类
    private Camera mCamera = null; // Camera对象，相机预览 
    
    /**服务器地址*/
    public String pUsername="XZY";
    /**服务器地址*/
    public String serverUrl="169.254.8.20";
    /**服务器端口*/
    private int serverPort=2001;//8888;
    /**视频刷新间隔*/
    private int VideoPreRate=3;
    /**当前视频序号*/
    private int tempPreRate=0;
    /**视频质量*/
    private int VideoQuality=85;
    
    /**发送视频宽度比例*/
    private float VideoWidthRatio=1;
    /**发送视频高度比例*/
    private float VideoHeightRatio=1;
    
    /**发送视频宽度*/
    private int VideoWidth=320;
    /**发送视频高度*/
    private int VideoHeight=240;
    /**视频格式索引*/
    private int VideoFormatIndex=0;
    /**是否发送视频*/
    public boolean startSendVideo=false;
    /**是否连接主机*/
    private boolean connectedServer=false;	
	
    public Button myBtn01, myBtn02;
    
    public void init()
    {
/*    	
    //开始连接主机按钮
    myBtn01.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			//Common.SetGPSConnected(LoginActivity.this, false);
			if(connectedServer){//停止连接主机，同时断开传输
				startSendVideo=false;
				connectedServer=false;					
				myBtn02.setEnabled(false);
				myBtn01.setText("开始连接");
				myBtn02.setText("开始传输");
				//断开连接
				Thread th = new MySendCommondThread("PHONEDISCONNECT|"+pUsername+"|");
		  	  	th.start(); 
			}
			else//连接主机
			{
				//启用线程发送命令PHONECONNECT
		  	  	Thread th = new MySendCommondThread("PHONECONNECT|"+pUsername+"|");
		  	  	th.start(); 
				connectedServer=true;
				myBtn02.setEnabled(true);
				myBtn01.setText("停止连接");
			}
		}});
    
    myBtn02.setEnabled(false);
    myBtn02.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			if(startSendVideo)//停止传输视频
			{
				startSendVideo=false;
				myBtn02.setText("开始传输");
			}
			else{ // 开始传输视频
				startSendVideo=true;
				myBtn02.setText("停止传输");
			}
		}});
	*/
    }
    
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		//如果没有指令传输视频，就先不传
		if(!startSendVideo)
			return;
		if(tempPreRate<VideoPreRate){
			tempPreRate++;
			return;
		}
		
		tempPreRate=0;		
		try {
		      if(DataCenter.dataStrength>=1&&data!=null)
		      {
		        YuvImage image = new YuvImage(data,VideoFormatIndex, VideoWidth, VideoHeight,null);
		        if(image!=null)
		        {
		        	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		      	  	//在此设置图片的尺寸和质量 
		        	
		      	  	image.compressToJpeg(new Rect(0, 0, (int)(VideoWidthRatio*VideoWidth), 
		      	  		(int)(VideoHeightRatio*VideoHeight)), VideoQuality, outstream);  
		      	  	outstream.flush();
		      	  	//启用线程将图像数据发送出去
		      	  	Thread th = new MySendFileThread(outstream,pUsername,serverUrl,serverPort);
		      	  	th.start();  
		        }
		      }
		  } catch (IOException e) {
		      e.printStackTrace();
		  }
		/**/
	}

	
	 	//@Override
	    public void onStart()//重新启动的时候
	    {	
	    	mSurfaceHolder = mSurfaceview.getHolder(); // 绑定SurfaceView，取得SurfaceHolder对象
	    	mSurfaceHolder.addCallback(this); // SurfaceHolder加入回调接口       
	    	mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置显示器类型，setType必须设置
	    	//*
		    //读取配置文件
	        SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(context);
	        pUsername=preParas.getString("Username", "XZY");
	        serverUrl=preParas.getString("ServerUrl", "169.254.8.20");
	    	//String tempStr=preParas.getString("ServerPort", "2001");
	    	//serverPort=Integer.parseInt(tempStr);
	        
	        String tempStr=preParas.getString("VideoPreRate", "1");
	        VideoPreRate=Integer.parseInt(tempStr);	            
	        tempStr=preParas.getString("VideoQuality", "85");
	        VideoQuality=Integer.parseInt(tempStr);
	        tempStr=preParas.getString("VideoWidthRatio", "100");
	        VideoWidthRatio=Integer.parseInt(tempStr);
	        tempStr=preParas.getString("VideoHeightRatio", "100");
	        VideoHeightRatio=Integer.parseInt(tempStr);
	        VideoWidthRatio=VideoWidthRatio/100f;
	        VideoHeightRatio=VideoHeightRatio/100f;
	        //*/
	    }
	    
	    //@Override
	    public void onResume() {
    
	        InitCamera();
	    }
	    
	    /**初始化摄像头*/
	    private void InitCamera(){
	    	try{
	    		mCamera = Camera.open();
	    	} catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 
	    //@Override
	    public void onPause() {
	        try{
		        if (mCamera != null) {
		        	mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
		            mCamera.stopPreview();
		            mCamera.release();
		            mCamera = null;
		        } 
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	    }
		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			if (mCamera == null) {
	            return;
	        }
	        mCamera.stopPreview();
	        mCamera.setPreviewCallback(this);
	        mCamera.setDisplayOrientation(0); //设置横行录制
	        //获取摄像头参数
	        Camera.Parameters parameters = mCamera.getParameters();
	        Size size = parameters.getPreviewSize();
	        VideoWidth=size.width;
	        VideoHeight=size.height;
	        VideoFormatIndex=parameters.getPreviewFormat();
	        if(VideoWidth>=800||VideoHeight>=800) VideoWidthRatio=VideoHeightRatio=0.5f;
	        mCamera.startPreview();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try {
	            if (mCamera != null) {
	                mCamera.setPreviewDisplay(mSurfaceHolder);
	                mCamera.startPreview();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } 
		}

		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (null != mCamera) {
	            mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
	            mCamera.stopPreview();
	            mCamera.release();
	            mCamera = null;
	        }
		}
		
		
	    /**发送命令线程*/
	    class MySendCommondThread extends Thread{
	    	private String commond;
	    	public MySendCommondThread(String commond){
	    		this.commond=commond;
	    	}
	    	public void run(){
	    		//实例化Socket  
	            try {
	    			Socket socket=new Socket(serverUrl,serverPort);
	    			PrintWriter out = new PrintWriter(socket.getOutputStream());
	    			out.println(commond);
	    			out.flush();
	    		} catch (UnknownHostException e) {
	    		} catch (IOException e) {
	    		}  
	    	}
	    }
	    
	    
	    /**发送文件线程*/
	    class MySendFileThread extends Thread{	
	    	private String username;
	    	private String ipname;
	    	private int port;
	    	private byte byteBuffer[] = new byte[1024];
	    	private OutputStream outsocket;	
	    	private ByteArrayOutputStream myoutputstream;
	    	
	    	public MySendFileThread(ByteArrayOutputStream myoutputstream,String username,String ipname,int port){
	    		this.myoutputstream = myoutputstream;
	    		this.username=username;
	    		this.ipname = ipname;
	    		this.port=port;
	            try {
	    			myoutputstream.close();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
	        public void run() {
	            try{
	            	//将图像数据通过Socket发送出去
	                Socket tempSocket = new Socket(ipname, port);
	                outsocket = tempSocket.getOutputStream();
	                //写入头部数据信息
	            	String msg="PHONEVIDEO|"+username+"|";//java.net.URLEncoder.encode("PHONEVIDEO|"+username+"|","utf-8");
	                byte[] buffer= msg.getBytes();
	                outsocket.write(buffer);
	                outsocket.write(intToBytes2(myoutputstream.size()));
	                ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
	                int amount;
	                while ((amount = inputstream.read(byteBuffer)) != -1) {
	                    outsocket.write(byteBuffer, 0, amount);
	                }
	                myoutputstream.flush();
	                myoutputstream.close();
	                tempSocket.close();                   
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    
	    public static byte[] intToBytes2(int n){  
            byte[] b = new byte[4];  
            for(int i = 0;i < 4;i++){  
                b[i] = (byte)(n >> (24 - i * 8));   
            }  
            return b;  
        }  
	    
}
