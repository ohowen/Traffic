package com.jrh.traffic.main;

import java.util.List;

import com.jrh.traffic.model.AppInfo;
import com.jrh.traffic.model.PackagesInfo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Mservice extends Service {
	private PackagesInfo packageInfo;
	//private SharedPreferences preferences;
	public List<AppInfo> infos;
	private final IBinder myBinder = new MyBinder();
	public Boolean mGet = false;
	public String[] tra = { "0K", "0K", "0K" };
	public double l;
	// 使用handler不停获取程序信息
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			infos = packageInfo.getNetworkApps();
			mGet = true;
			handler.postDelayed(runnable, 3000);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("shadow", "2");
		infos = packageInfo.getNetworkApps();
		handler.postDelayed(runnable, 3000);
		return myBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		packageInfo = new PackagesInfo(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		infos = packageInfo.getNetworkApps();
		Log.v("helsha", "2");
		return super.onStartCommand(intent, flags, START_STICKY);
	}

	@Override
	public void onDestroy() {
		startService(new Intent(getApplicationContext(), Mservice.class));
		super.onDestroy();
	}

	// 自定义binder
	class MyBinder extends Binder {
		public Mservice getService() {
			return Mservice.this;
		}
	}	
}