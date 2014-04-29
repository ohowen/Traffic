package com.jrh.traffic.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends android.content.BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 开机启动监控服务
		SharedPreferences preferences = context.getSharedPreferences(
		        "shadow_traffic", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("first", true);
		editor.commit();
		Intent intent2 = new Intent(context, Mservice.class);
		context.startService(intent2);
		Log.v("helsha", "1");

	}

}
