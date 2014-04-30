package com.jrh.traffic.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends android.content.BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 开机启动监控服务
		SharedPreferences preferences = context.getSharedPreferences(
		        "traffic_state", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("first", true);
		editor.commit();
		Intent inten = new Intent(context, TrafficService.class);
		context.startService(inten);
	}
}