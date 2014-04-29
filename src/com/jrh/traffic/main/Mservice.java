package com.jrh.traffic.main;

import java.util.List;

import com.jrh.traffic.model.AppInfo;
import com.jrh.traffic.model.PackagesInfo;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Mservice extends Service {
	private PackagesInfo packageInfo;
	private SharedPreferences preferences;
	public List<AppInfo> infos;
	private final IBinder myBinder = new MyBinder();
	public Boolean mGet = false;
	public String[] tra = { "0K", "0K", "0K" };
	public double l;
	// ʹ��handler��ͣ��ȡ������Ϣ
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			infos = packageInfo.getRunningProcess();
			setTraffic();
			mGet = true;
			handler.postDelayed(runnable, 3000);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("shadow", "2");
		infos = packageInfo.getRunningProcess();
		handler.postDelayed(runnable, 3000);
		return myBinder;

	}

	@Override
	public void onCreate() {
		super.onCreate();
		packageInfo = new PackagesInfo(getApplicationContext());
		preferences = getSharedPreferences("small_setting", MODE_PRIVATE);// ��ȡ������
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		infos = packageInfo.getRunningProcess();
		Log.v("helsha", "2");
		return super.onStartCommand(intent, flags, START_STICKY);
	}

	@Override
	public void onDestroy() {
		startService(new Intent(getApplicationContext(), Mservice.class));

		super.onDestroy();
	}

	// �Զ���binder
	class MyBinder extends Binder {

		public Mservice getService() {
			return Mservice.this;
		}

	}

	// ��λ����
	// ת����λ
	private String refreshTraffic(long lg, Boolean o) {
		String str = "0K";
		int b = 1024, c = 1048576;
		;

		Log.v("hells", lg + "");
		if (lg < 1024) {
			str = "0K";
		} else if (lg >= b && lg < c) {
			int d = (int) (lg / b);
			str = d + "K";
		} else {
			double e = (double) lg / c;
			java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
			str = df.format(e) + "M";
			if (o == true) {
				l = e;
			}
		}
		return str;
	}

	private void setTraffic() {
		Traffic traffic = new Traffic(getApplicationContext());
		// ��ȡ����
		tra[1] = refreshTraffic(traffic.getAllWifi()[2], false);
		tra[0] = refreshTraffic(traffic.getAllGprs()[2], false);
		tra[2] = refreshTraffic(preferences.getLong("mouth_gprs_all", 0), true);

		// Log.v("fuck", traffic.getAllGprs()[2]+"ss"+traffic.getAllWifi()[2]);
		// Log.v("fuck", tra[0]+"ss"+tra[1]);
	}
}