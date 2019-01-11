package com.wzg.dingwei;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;

public class Speek {

	private static final String TAG = "log";
	private static final String SPEEK_PREFER_NAME = "SPEEK_PREFER_NAME";
	// 语音合成对象
	private SpeechSynthesizer mTts;
	private SharedPreferences mSharedPreferences;
	private Context context;
	private boolean initOK = false;

	/**
	 * 初期化监听。
	 */
	public InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code == ErrorCode.SUCCESS) {
				initOK = true;
			}
//			say("导航开始");
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = null;

	public void init(Context ctx, SynthesizerListener listener) {
		context = ctx;
		mSharedPreferences = ctx.getSharedPreferences(SPEEK_PREFER_NAME,
				Activity.MODE_PRIVATE);
		mTtsListener = listener;
		mTts = new SpeechSynthesizer(ctx, mTtsInitListener);
	}

	public void destory() {
		if (mTts != null) {
			mTts.stopSpeaking(mTtsListener);
		}
	}

	public void say(String content) {
		if (initOK) {
			setParam();
			int code = mTts.startSpeaking(content, mTtsListener);
			if (code != 0) {
				Toast.makeText(context, "语音失败！", Toast.LENGTH_LONG).show();
			} else {
				// Toast.makeText(context, "语音成功！", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {
		mTts.setParameter(SpeechConstant.ENGINE_TYPE,
				mSharedPreferences.getString("engine_preference", "local"));

		if (mSharedPreferences.getString("engine_preference", "local")
				.equalsIgnoreCase("local")) {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, mSharedPreferences
					.getString("role_cn_preference", "xiaoyan"));
		} else {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, mSharedPreferences
					.getString("role_cn_preference", "xiaoyan"));
		}
		mTts.setParameter(SpeechSynthesizer.SPEED,
				mSharedPreferences.getString("speed_preference", "50"));

		mTts.setParameter(SpeechSynthesizer.PITCH,
				mSharedPreferences.getString("pitch_preference", "50"));

		mTts.setParameter(SpeechSynthesizer.VOLUME,
				mSharedPreferences.getString("volume_preference", "10"));
	}
}
