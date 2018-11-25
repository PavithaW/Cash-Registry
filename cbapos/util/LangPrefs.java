package com.cbasolutions.cbapos.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LangPrefs {

	private static final String PREF_NAME = "LANG_PREF";
	
	public static final String KEY_LANG_STATUS ="lang_status";
	
	public static final int LAN_EN =0;
	public static final int LAN_TA =1;
	public static final int LAN_SIN =2;
	
	public static void setLanguage(Context c, int lang){
		SharedPreferences pref = c.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putInt(KEY_LANG_STATUS, lang) ;
		editor.commit() ;
	}
	
	public static int getLanguage(Context c){
		SharedPreferences pref = c.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return pref.getInt(KEY_LANG_STATUS, LAN_EN) ;
	}

}
