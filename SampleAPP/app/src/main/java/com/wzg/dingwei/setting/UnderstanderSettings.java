package com.wzg.dingwei.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.amap.location.demo.R;
import com.wzg.dingwei.util.SettingTextWatcher;

/**
 * 设置画面。
 *
 */
public class UnderstanderSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	
	public static final String PREFER_NAME = "com.iflytek.setting";
	private String TAG = "UnderstandSettings";
	private SharedPreferences mSharedPreferences;
	private EditTextPreference mVadbosPreference;
	private EditTextPreference mVadeosPreference;
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
		addPreferencesFromResource(R.xml.understand_setting);
		
		mVadbosPreference = (EditTextPreference)findPreference("understander_vadbos_preference");
		mVadbosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(UnderstanderSettings.this,mVadbosPreference,1));
		
		mVadeosPreference = (EditTextPreference)findPreference("understander_vadeos_preference");
		mVadeosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(UnderstanderSettings.this,mVadeosPreference,2));
		
		mSharedPreferences = getSharedPreferences(UnderstanderSettings.PREFER_NAME, Activity.MODE_PRIVATE);
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
}

