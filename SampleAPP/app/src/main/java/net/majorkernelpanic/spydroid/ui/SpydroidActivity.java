///*
// * Copyright (C) 2011-2013 GUIGUI Simon, fyhertz@gmail.com
// * 
// * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
// * 
// * Spydroid is free software; you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation; either version 3 of the License, or
// * (at your option) any later version.
// * 
// * This source code is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with this source code; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// */
//
//package net.majorkernelpanic.spydroid.ui;
//
//import java.io.File;
//
//import com.amap.location.demo.MainFrag;
//import com.amap.location.demo.R;
//import com.wzg.dingwei.DataCenter;
//
//import net.majorkernelpanic.spydroid.SpydroidApplication;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.PowerManager;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.Window;
//import android.widget.Toast;
//
///** 
// * Spydroid basically launches an RTSP server and an HTTP server, 
// * clients can then connect to them and start/stop audio/video streams on the phone.
// */
//public class SpydroidActivity extends FragmentActivity {
//
//	static final public String TAG = "SpydroidActivity";
//
//	public final int HANDSET = 0x01;
//	public final int TABLET = 0x02;
//
//	// We assume that the device is a phone
//	public int device = HANDSET;
//
//	private ViewPager mViewPager;
//	private PowerManager.WakeLock mWakeLock;
//	private SectionsPagerAdapter mAdapter;
//	private SpydroidApplication mApplication;
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
//		mApplication = (SpydroidApplication) getApplication();
//		setContentView(R.layout.spydroid);
//		if (findViewById(R.id.handset_pager) != null) {
//
//			// Handset detected !
//			mAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//			mViewPager = (ViewPager) findViewById(R.id.handset_pager);
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//			
//			
//		} else {
///*
//			// Tablet detected !
//			device = TABLET;
//			mAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//			mViewPager = (ViewPager) findViewById(R.id.tablet_pager);
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			SessionBuilder.getInstance().setPreviewOrientation(0);
//*/			
//		}
//
//		mViewPager.setAdapter(mAdapter);
//
//	
//		// Prevents the phone from going to sleep mode
//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "net.majorkernelpanic.spydroid.wakelock");
//
//	
//	
//	}
//
//	public void onStart() {
//		super.onStart();
//
//		// Lock screen
//		mWakeLock.acquire();
//
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		// A WakeLock should only be released when isHeld() is true !
//		if (mWakeLock.isHeld()) mWakeLock.release();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		mApplication.applicationForeground = true;
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		mApplication.applicationForeground = false;
//	}
//
//	@Override
//	public void onDestroy() {
//		Log.d(TAG,"SpydroidActivity destroyed");
//		super.onDestroy();
//	}
//
//	@Override    
//	public void onBackPressed() {
//		Intent setIntent = new Intent(Intent.ACTION_MAIN);
//		setIntent.addCategory(Intent.CATEGORY_HOME);
//		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(setIntent);
//	}
//	
//
//	@Override  
//	public boolean onKeyDown(int keyCode, KeyEvent event) {  
//	if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){  
//	            AlertDialog.Builder alertbBuilder=new AlertDialog.Builder(this);  
//	            alertbBuilder.setTitle("真的要离开？").setMessage("你确定要离开？").setPositiveButton("确定", new DialogInterface.OnClickListener() {  
//	                    @Override  
//	                    public void onClick(DialogInterface dialog, int which) {  
//	                    //结束这个Activity  
//	                    finish();
//	                    int nPid = android.os.Process.myPid();  
//	                    android.os.Process.killProcess(nPid);
//	                   // socketClient.disConnect();
//	                    }  
//	            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {  
//	  
//	                    @Override  
//	                    public void onClick(DialogInterface dialog, int which) {  
//	                            dialog.cancel();  
//	                    }  
//	            }).create();  
//	            alertbBuilder.show();  
//	    }  
//	    return true;  
//	}
//	
//	
//	
//		
//	public void log(String s) {
//		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//	}
//
//	class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//		public SectionsPagerAdapter(FragmentManager fm) {
//			super(fm);
//		}
//
//
//
//		
//
//		public MainFrag getHandsetFragment() {
//			if (device == HANDSET) {
//				return (MainFrag) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.handset_pager+":0");
//			}/* else {
//				return (HandsetFragment) getSupportFragmentManager().findFragmentById(R.id.handset);
//			}*/
//			return null;
//		}
//
//	
//		@Override
//		public Fragment getItem(int i) {
//			if (device == HANDSET) {
//				switch (i) {
//				case 0: return new MainFrag();//new HandsetFragment();
//				//case 1: return new PreviewFragment();
//				//case 2: return new AboutFragment();
//				}
//			} else {
//				switch (i) {
//				case 0: return new TabletFragment();
//				//case 1: return new AboutFragment();
//				}        		
//			}
//			return null;
//		}		
//		
//		
//		
//		@Override
//		public int getCount() {
//			return device==HANDSET ? 1 : 0;
//		}
//		
//		
//		@Override
//		public CharSequence getPageTitle(int position) {
//			if (device == HANDSET) {
//				switch (position) {
//				case 0: return getString(R.string.page0);
//				case 1: return getString(R.string.page1);
//				case 2: return getString(R.string.page2);
//				}        		
//			} else {
//				switch (position) {
//				case 0: return getString(R.string.page0);
//				case 1: return getString(R.string.page2);
//				}
//			}
//			return null;
//		}
//
//	}
//
//}