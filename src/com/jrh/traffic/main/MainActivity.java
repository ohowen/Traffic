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
import com.jrh.traffic.adapter.ListAdapter;
import com.jrh.traffic.main.TrafficService.MBinder;
import com.jrh.traffic.model.AppInfo;

public class MainActivity extends Activity {
	// 定义流量
	private ListView mListView;
	private ListAdapter mListAdapter;
	private List<AppInfo> mAppInfos;
	private TrafficService mService;
	private Boolean mClick = false;
	private SharedPreferences mPreferences;
	private Context mContext;
	// 定义一个handler刷新界面；
	private Handler mHandler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			mAppInfos = mService.mInfos;
			// 刷新总量
			if (mService.mGet == true && mClick == true) {
				mListAdapter.setList(mAppInfos);
				mListAdapter.notifyDataSetChanged();
			}
			mHandler.postDelayed(runnable, 3000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist);
		mContext = this;
		
		mListView = (ListView) findViewById(R.id.list_view);
		// 启动服务
		Intent intent = new Intent(MainActivity.this, TrafficService.class);
		this.startService(intent);
		
		mPreferences = getSharedPreferences("setting", MODE_PRIVATE);
		setPreferrences();
	}

	// 设置初始化
	private void setPreferrences() {
		Time time = new Time();
		time.setToNow();
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt("year", time.year);
		editor.putInt("mouth", time.month);
		editor.putInt("day", time.monthDay);
		editor.commit();
	}

	@Override
	protected void onResume() {
		// 绑定后台服务
		Intent binderIntent = new Intent(mContext, TrafficService.class);
		mContext.bindService(binderIntent, connection, BIND_AUTO_CREATE);
		super.onResume();
	}

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MBinder binder = (MBinder) service;
			mService = binder.getService();
			getData();
			mHandler.postDelayed(runnable, 3000);
		}
	};

	private void getData() {
		if (mService.mInfos != null) {
			mAppInfos = mService.mInfos;
			mListAdapter = new ListAdapter(mAppInfos, mContext);
			mListView.setAdapter(mListAdapter);
		}
	}
}