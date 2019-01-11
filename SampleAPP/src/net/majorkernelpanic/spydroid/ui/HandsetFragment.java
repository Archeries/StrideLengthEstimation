/*
 * Copyright (C) 2011-2013 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.majorkernelpanic.spydroid.ui;

import java.util.Locale;

//import net.majorkernelpanic.http.TinyHttpServer;


import com.amap.location.demo.R;

import net.majorkernelpanic.spydroid.SpydroidApplication;
import net.majorkernelpanic.spydroid.Utilities;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HandsetFragment extends Fragment {

	public final static String TAG = "HandsetFragment";
	
    private TextView mDescription1, mDescription2, mLine1, mLine2, mVersion, mSignWifi, mTextBitrate;
    private LinearLayout mSignInformation, mSignStreaming;
    private Animation mPulseAnimation;
    
    private SpydroidApplication mApplication;
   // private CustomHttpServer mHttpServer;
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mApplication  = (SpydroidApplication) getActivity().getApplication();
    }
    
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.main,container,false);
        mLine1 = (TextView)rootView.findViewById(R.id.line1);
        mLine2 = (TextView)rootView.findViewById(R.id.line2);
        mDescription1 = (TextView)rootView.findViewById(R.id.line1_description);
        mDescription2 = (TextView)rootView.findViewById(R.id.line2_description);
        mVersion = (TextView)rootView.findViewById(R.id.version);
        mSignWifi = (TextView)rootView.findViewById(R.id.advice);
        mSignStreaming = (LinearLayout)rootView.findViewById(R.id.streaming);
        mSignInformation = (LinearLayout)rootView.findViewById(R.id.information);
        mPulseAnimation = AnimationUtils.loadAnimation(mApplication.getApplicationContext(), R.anim.pulse);
        mTextBitrate = (TextView)rootView.findViewById(R.id.bitrate);
        return rootView ;
    }
	
	@Override
    public void onStart() {
    	super.onStart();
    	
    	// Print version number
    	Context mContext = mApplication.getApplicationContext();
        try {
			mVersion.setText("v"+mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0 ).versionName);
		} catch (Exception e) {
			mVersion.setText("v???");
		}
    	
    }
    
	@Override
    public void onPause() {
    	super.onPause();
    
    }
	
	@Override
    public void onResume() {
    	super.onResume();
		//getActivity().bindService(new Intent(getActivity(),CustomHttpServer.class), mHttpServiceConnection, Context.BIND_AUTO_CREATE);
   }
	

	
  

    
     
  
    
	
}
