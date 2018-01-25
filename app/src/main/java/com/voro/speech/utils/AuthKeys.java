package com.voro.speech.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;


public class AuthKeys {
	public static final String AUTH_APP_ID = "com.baidu.speech.APP_ID";
	public static final String AUTH_API_KEY = "com.baidu.speech.API_KEY";
	public static final String AUTH_SECRET_KEY = "com.baidu.speech.SECRET_KEY";

	private static AuthKeys gInstance;

	private Context mContext;
	private String mAppId;
	private String mApiKey;
	private String mSecretKey;

	public static AuthKeys getInstance(Context context){

		if (gInstance == null){
			gInstance = new AuthKeys(context);
		}
		return gInstance;
	}

	private AuthKeys(Context context){
		if (gInstance == null){
			mContext = context;
			initAuthKeys();
		}
	}

	public String getAppId(){
		return mAppId;
	}

	public String getApiKey(){
		return mApiKey;
	}

	public String getSecretKey(){
		return mSecretKey;
	}

	private void initAuthKeys(){
		try {
			ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(
					mContext.getPackageName(), PackageManager.GET_META_DATA);
			mAppId = Integer.toString(appInfo.metaData.getInt(AUTH_APP_ID));
			mApiKey = appInfo.metaData.getString(AUTH_API_KEY);
			mSecretKey = appInfo.metaData.getString(AUTH_SECRET_KEY);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
