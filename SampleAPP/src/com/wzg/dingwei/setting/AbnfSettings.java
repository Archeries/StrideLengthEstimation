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
public class AbnfSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	
	public static final String PREFER_NAME = "com.iflytek.setting";
	private String TAG = "AbnfSettings";
	private SharedPreferences mSharedPreferences;
	private EditTextPreference mVadbosPreference;
	private EditTextPreference mVadeosPreference;
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
		addPreferencesFromResource(R.xml.abnf_setting);
		
		mVadbosPreference = (EditTextPreference)findPreference("abnf_vadbos_preference");
		mVadbosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(AbnfSettings.this,mVadbosPreference,1));
		
		mVadeosPreference = (EditTextPreference)findPreference("abnf_vadeos_preference");
		mVadeosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(AbnfSettings.this,mVadeosPreference,2));
		
		mSharedPreferences = getSharedPreferences(AbnfSettings.PREFER_NAME, Activity.MODE_PRIVATE);
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
	
	protected void onstop() {
		setResult(100);
		finish();
	}
}

