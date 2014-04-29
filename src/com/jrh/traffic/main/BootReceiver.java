package com.jrh.traffic.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends android.content.BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// ����������ط���
		SharedPreferences preferences = context.getSharedPreferences(
		        "shadow_traffic", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("first", true);
		editor.commit();
		Intent inten = new Intent(context, Mservice.class);
		context.startService(inten);
		Log.v("helsha", "1");
	}
}