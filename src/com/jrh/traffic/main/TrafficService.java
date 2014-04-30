package com.jrh.traffic.main;

import java.util.List;

import com.jrh.traffic.model.AppInfo;
import com.jrh.traffic.model.PackagesInfo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class TrafficService extends Service {
	private PackagesInfo mPackageInfo;
	private final IBinder MBINDER = new MBinder();
	public Boolean mGet = false;
	public List<AppInfo> mInfos;

	// 使用handler不停获取程序信息
	private Handler mHandler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			mInfos = mPackageInfo.getNetworkApps();
			mGet = true;
			mHandler.postDelayed(runnable, 3000);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		mInfos = mPackageInfo.getNetworkApps();
		mHandler.postDelayed(runnable, 3000);
		return MBINDER;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPackageInfo = new PackagesInfo(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mInfos = mPackageInfo.getNetworkApps();
		return super.onStartCommand(intent, flags, START_STICKY);
	}

	@Override
	public void onDestroy() {
		startService(new Intent(getApplicationContext(), TrafficService.class));
		super.onDestroy();
	}

	// 自定义binder
	class MBinder extends Binder {
		public TrafficService getService() {
			return TrafficService.this;
		}
	}	
}