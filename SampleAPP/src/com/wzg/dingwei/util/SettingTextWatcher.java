package com.wzg.dingwei.util;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

public class SettingTextWatcher implements TextWatcher {
	private int editStart ;
	private int editEnd ;
	private EditTextPreference mEditTextPreference;
	int mType;
	private Context mContext;
	
	public SettingTextWatcher(Context context,EditTextPreference e,int t) {
		mContext = context;
		mEditTextPreference = e;
		mType = t;
	 }
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		String sString = s.toString();
		int length = 0;
		editStart = sString.length();
		editEnd = sString.length();
		for(;length < sString.length();length++){
			if(sString.charAt(length) > '9' || sString.charAt(length) < '0'){
				Toast.makeText(mContext, "输入字符和汉字是不规范的！", Toast.LENGTH_SHORT).show();
				s.delete(editStart-(sString.length()-length), editEnd-(sString.length()-length)+1);
				int tempSelection = editStart;
				mEditTextPreference.getEditText().setText(s);
				mEditTextPreference.getEditText().setSelection(tempSelection);
				break;
			}
			if(length > 0){
				if(sString.charAt(length) == '0' && sString.charAt(length-1) == '0'&& length == 1){
					s.delete(editStart-1, editEnd);
					int tempSelection = editStart;
					Toast.makeText(mContext, "小伙伴不要输入太多鸭蛋啦!", Toast.LENGTH_SHORT).show();
					mEditTextPreference.getEditText().setText(s);
					mEditTextPreference.getEditText().setSelection(tempSelection);
					break;
				}
				else if(sString.charAt(length) != '0' && sString.charAt(length-1) == '0' && length == 1){
					s.delete(editStart-sString.length(), editEnd-sString.length()+1);
					Toast.makeText(mContext, "第一个数字不能是 0 啊！", Toast.LENGTH_SHORT).show();
					int tempSelection = editStart;
					mEditTextPreference.getEditText().setText(s);
					mEditTextPreference.getEditText().setSelection(tempSelection);
					break;
				}
			}
		}
		if(sString.length() == length && length >= 5){
			if(Integer.parseInt(sString) > 10000){
				switch (mType){
				case 1:
					new AlertDialog.Builder(mContext).setTitle("无效数值")
					.setMessage("超过有效值，默认为：4000").setPositiveButton("确定",null).show();
					mEditTextPreference.getEditText().setText("4000");
					break;
					
				case 2:
					new AlertDialog.Builder(mContext).setTitle("无效数值")
					.setMessage("超过有效值，默认为：700").setPositiveButton("确定",null).show();
					mEditTextPreference.getEditText().setText("700");
					break;
				case 3:
					new AlertDialog.Builder(mContext).setTitle("无效数值")
					.setMessage("超过有效值，默认为：5000").setPositiveButton("确定",null).show();
					mEditTextPreference.getEditText().setText("5000");
					break;
					
				case 4:
					new AlertDialog.Builder(mContext).setTitle("无效数值")
					.setMessage("超过有效值，默认为：1800").setPositiveButton("确定",null).show();
					mEditTextPreference.getEditText().setText("1800");
					break;
				}
			}
		}
		if(sString.length() == length && length >= 3 && mType == 5){
			if(Integer.parseInt(sString) > 100){
				new AlertDialog.Builder(mContext).setTitle("无效数值")
				.setMessage("超过有效值，默认为：50").setPositiveButton("确定",null).show();
				mEditTextPreference.getEditText().setText("50");
			}
		}
	}
};
