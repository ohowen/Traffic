package com.jrh.traffic.main;

import java.util.ArrayList;
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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.jrh.traffic.R;
import com.jrh.traffic.adapter.Adapter;
import com.jrh.traffic.adapter.CustomDialog;
import com.jrh.traffic.adapter.ViewPagerAdapter;
import com.jrh.traffic.main.Mservice.MyBinder;
import com.jrh.traffic.model.AppInfo;

public class MainActivity extends Activity {
	// ��������
	private TextView mouth_used, mouth_leave, gprs_all, wifi_all, mouth_title,
	        mon_overday, mon_alltraffic;
	private ImageView image_row;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private List<View> mListViews;
	private LayoutInflater mInflater;
	private View vTotal = null, vApp = null;
	private ListView listView;
	private Adapter list_adapter;
	private List<AppInfo> appInfos;
	private Mservice mservice;
	private RadioGroup group;
	private Boolean mClick = false;
	private int x = 0;
	private SharedPreferences preferences;
	private Context mContext;

	int idx = 0;
	// ����һ��handlerˢ�½��棻
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			appInfos = mservice.infos;
			// ˢ������
			if (mservice.mGet == true && mClick == true) {
				list_adapter.setList(appInfos);
				list_adapter.notifyDataSetChanged();

			}
			if (mservice.mGet == true) {
				gprs_all.setText(mservice.tra[0]);
				wifi_all.setText(mservice.tra[1]);
				mouth_used.setText(mservice.tra[2]);
				mouth_leave.setText((double) (x - mservice.l) + "M");
				if ((double) (x - mservice.l) < 0) {
					mouth_title.setText("�������Ѿ�ȫ������!");
					mouth_title.setBackgroundResource(R.color.red);

				}
			}

			handler.postDelayed(runnable, 3000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.monitor);
		mContext = this;
		preferences = getSharedPreferences("small_setting", MODE_PRIVATE);
		setPreferrences();
		init();
	}

	// ���ó�ʼ��
	private void setPreferrences() {
		Time time = new Time();
		time.setToNow();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("year", time.year);
		editor.putInt("mouth", time.month + 1);
		editor.putInt("day", time.monthDay);
		editor.putInt("overday", time.monthDay);
		editor.putInt("usertraffic", 30);
		editor.commit();
	}

	// ��������

	private void init() {

		AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		image_row = (ImageView) findViewById(R.id.mon_image_row);
		image_row.getBackground().setAlpha(100);
		group = (RadioGroup) findViewById(R.id.radioGroup1);
		mInflater = getLayoutInflater();
		// ��ȡ��view��������ӣ�������Ӧ
		vTotal = mInflater.inflate(R.layout.total, null);
		vApp = mInflater.inflate(R.layout.applist, null);
		mListViews = new ArrayList<View>();
		mListViews.add(vTotal);
		mListViews.add(vApp);
		// viewpager �Ļ���
		adapter = new ViewPagerAdapter(mListViews);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 1) {
					mClick = false;
					RadioButton rb = (RadioButton) group.getChildAt(arg0);
					rb.setChecked(true);
				} else {
					mClick = true;
					RadioButton rb = (RadioButton) group.getChildAt(arg0);
					rb.setChecked(true);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == R.id.radio0) {
					idx = 0;

				} else if (checkedId == R.id.radio1) {
					idx = 1;
				} else {
					return;
				}
				viewPager.setCurrentItem(idx);
			}
		});
		// ��������
		Intent intent = new Intent(MainActivity.this, Mservice.class);
		this.startService(intent);
		// listview�����
		listView = (ListView) vApp.findViewById(R.id.list_view);
	}

	@Override
	protected void onResume() {
		new CustomDialog(this);
		// �󶨺�̨����
		Intent binderIntent = new Intent(mContext, Mservice.class);
		mContext.bindService(binderIntent, connection, BIND_AUTO_CREATE);
		// ��������ĳ�ʼ��
		preferences = getSharedPreferences("small_setting", MODE_PRIVATE);// ��ȡ������
		mon_overday = (TextView) findViewById(R.id.mon_overday);
		mon_alltraffic = (TextView) findViewById(R.id.mon_alltraffic);
		mouth_title = (TextView) vTotal.findViewById(R.id.textView7);
		mouth_used = (TextView) vTotal.findViewById(R.id.alltraffic);
		mouth_leave = (TextView) vTotal.findViewById(R.id.leave);
		gprs_all = (TextView) vTotal.findViewById(R.id.total_gprs);
		wifi_all = (TextView) vTotal.findViewById(R.id.total_wifi);
		mon_overday.setText(preferences.getInt("overday", 1) + "��");
		x = preferences.getInt("usertraffic", 30);
		mon_alltraffic.setText(x + "M");

		super.onResume();
	}

	// ����serviceconnection
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// ǿ��ת��ibinderΪmybinder�Ի��monitorservice����
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