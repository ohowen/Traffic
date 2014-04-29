package com.jrh.traffic.main;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.widget.ListView;
import com.jrh.traffic.R;
import com.jrh.traffic.adapter.Adapter;
import com.jrh.traffic.main.Mservice.MyBinder;
import com.jrh.traffic.model.AppInfo;

public class MainActivity extends Activity {
	// 定义流量
	private ListView listView;
	private Adapter list_adapter;
	private List<AppInfo> appInfos;
	private Mservice mservice;
	private Boolean mClick = false;
	private SharedPreferences preferences;
	private Context mContext;
	// 定义一个handler刷新界面；
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			appInfos = mservice.infos;
			// 刷新总量
			if (mservice.mGet == true && mClick == true) {
				list_adapter.setList(appInfos);
				list_adapter.notifyDataSetChanged();
			}
			handler.postDelayed(runnable, 3000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist);
		mContext = this;
		
		listView = (ListView) findViewById(R.id.list_view);
		// 启动服务
		Intent intent = new Intent(MainActivity.this, Mservice.class);
		this.startService(intent);
		
		preferences = getSharedPreferences("small_setting", MODE_PRIVATE);
		setPreferrences();
	}

	// 设置初始化
	private void setPreferrences() {
		Time time = new Time();
		time.setToNow();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("year", time.year);
		editor.putInt("mouth", time.month);
		editor.putInt("day", time.monthDay);
		editor.commit();
	}

	@Override
	protected void onResume() {
		// 绑定后台服务
		Intent binderIntent = new Intent(mContext, Mservice.class);
		mContext.bindService(binderIntent, connection, BIND_AUTO_CREATE);
		super.onResume();
	}

	// 定义serviceconnection
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 强制转换ibinder为mybinder以获得monitorservice对象
			MyBinder binder = (MyBinder) service;
			mservice = binder.getService();
			getData();
			handler.postDelayed(runnable, 3000);
		}
	};

	private void getData() {
		if (mservice.infos != null) {
			appInfos = mservice.infos;
			list_adapter = new Adapter(appInfos, mContext);
			listView.setAdapter(list_adapter);
		}
	}
}