package com.jrh.traffic.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.text.format.Time;
import android.util.Log;

public class PackagesInfo {

	private List<ApplicationInfo> appList;
	List<PackageInfo> packs;
	PackageManager pm;
	Context context;
	ConnectivityManager manager;

	public PackagesInfo(Context context) {
		// ͨ�����������������е�Ӧ�ó�������ж�صģ�������Ŀ¼
		this.context = context;
		pm = context.getApplicationContext().getPackageManager();
		appList = pm.getInstalledApplications(0);
	}

	/**
	 * ͨ��һ�����������ظó����һ��Application����
	 * 
	 * @param name
	 *            ������
	 * @return ApplicationInfo
	 */

	public ApplicationInfo getInfo(String name) {
		if (name == null) {
			return null;
		}
		for (ApplicationInfo appinfo : appList) {
			if (name.equals(appinfo.processName)) {
				return appinfo;
			}
		}
		return null;
	}

	public List<AppInfo> getRunningProcess() {

		// ActivityManager am = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// ��ȡ�������е�Ӧ��
		// List<RunningAppProcessInfo> run = am.getRunningAppProcesses();

		// ��ȡ������������������Ҫͨ��������ȡ�����ͼ��ͳ�����
		List<AppInfo> list = new ArrayList<AppInfo>();
		List<AppInfo> slist = new ArrayList<AppInfo>();
		// for (RunningAppProcessInfo ra : run) {
		for (ApplicationInfo ra : appList) {

			// ���˵�û���������ܵ����
			int reslut = context.getPackageManager().checkPermission(
			        "android.permission.INTERNET", ra.processName);
			if (reslut != PackageManager.PERMISSION_GRANTED) {
				continue;
			}
			// ���˵��޶�Ӧ�ĳ���
			if (getInfo(ra.processName) != null) {
				if ((getInfo(ra.processName).flags & ApplicationInfo.FLAG_SYSTEM) == 0
				        && (getInfo(ra.processName).flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
					AppInfo pr = new AppInfo();
					pr.setApp_icon(getInfo(ra.processName).loadIcon(pm));
					pr.setApp_name(getInfo(ra.processName).loadLabel(pm)
					        .toString());
					pr.setApp_package(ra.processName);

					SharedPreferences preferences = context
					        .getSharedPreferences("shadow_traffic",
					                Context.MODE_PRIVATE);
					manager = (ConnectivityManager) context
					        .getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo wifi = manager
					        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					NetworkInfo mobile = manager
					        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					SharedPreferences.Editor editor = preferences.edit();
					long i = preferences.getLong(ra.uid + "besend", 0);
					long j = preferences.getLong(ra.uid + "berev", 0);
					long k = preferences.getLong(ra.uid + "send", 0);
					long l = preferences.getLong(ra.uid + "rev", 0);
					long m = preferences.getLong(ra.uid + "wisend", 0);
					long n = preferences.getLong(ra.uid + "wirev", 0);
					// ��ȡ�������������
					// ����豸��֧�֣����ʾΪ0
					if (TrafficStats.getUidTxBytes(ra.uid) == -1) {
						editor.putLong(ra.uid + "send", 0);
						editor.putLong(ra.uid + "rev", 0);
						editor.commit();
					} else {
						if (mobile.getState() == NetworkInfo.State.CONNECTED
						        && wifi.getState() != NetworkInfo.State.CONNECTED) {
							if (!preferences.getBoolean("first", true)) {
								editor.putLong(ra.uid + "send",
								        TrafficStats.getUidTxBytes(ra.uid) - i
								                + k);
								editor.putLong(ra.uid + "rev",
								        TrafficStats.getUidRxBytes(ra.uid) - j
								                + l);
							} else {
								editor.putLong(ra.uid + "send",
								        TrafficStats.getUidTxBytes(ra.uid) + k);
								editor.putLong(ra.uid + "rev",
								        TrafficStats.getUidRxBytes(ra.uid) + i);
								editor.putBoolean("first", false);
							}
							i = TrafficStats.getUidTxBytes(ra.uid);
							j = TrafficStats.getUidRxBytes(ra.uid);
							editor.putLong(ra.uid + "besend", i);
							editor.putLong(ra.uid + "berev", j);
							editor.commit();
							//
						} else {
							if (!preferences.getBoolean("first", true)) {
								editor.putLong(ra.uid + "wisend",
								        TrafficStats.getUidTxBytes(ra.uid) - i
								                + m);
								editor.putLong(ra.uid + "wirev",
								        TrafficStats.getUidRxBytes(ra.uid) - j
								                + n);
							} else {
								editor.putLong(ra.uid + "wisend",
								        TrafficStats.getUidTxBytes(ra.uid) + m);
								editor.putLong(ra.uid + "wirev",
								        TrafficStats.getUidRxBytes(ra.uid) + n);
								editor.putBoolean("first", false);
							}

							i = TrafficStats.getUidTxBytes(ra.uid);
							j = TrafficStats.getUidRxBytes(ra.uid);
							editor.putLong(ra.uid + "besend", i);
							editor.putLong(ra.uid + "berev", j);
							editor.commit();
						}
					}
					pr.setApp_rev(preferences.getLong(ra.uid + "rev", 0));
					pr.setApp_sent(preferences.getLong(ra.uid + "send", 0));
					pr.setApp_traffic(preferences.getLong(ra.uid + "rev", 0)
					        + preferences.getLong(ra.uid + "send", 0));
					Log.v("xxss", pr.getApp_sent() + "ss" + pr.getApp_rev()
					        + "aa" + pr.getApp_name());
					list.add(pr);
					Log.v("shadow", pr.getApp_name());
				} else {
					AppInfo pr = new AppInfo();
					pr.setApp_icon(getInfo(ra.processName).loadIcon(pm));
					pr.setApp_name(getInfo(ra.processName).loadLabel(pm)
					        .toString());
					pr.setApp_package(ra.processName);
					// ////////////////////////////////////////////////////////////////////////////////////////
					SharedPreferences preferences = context
					        .getSharedPreferences("shadow_traffic",
					                Context.MODE_PRIVATE);
					manager = (ConnectivityManager) context
					        .getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo wifi = manager
					        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					NetworkInfo mobile = manager
					        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					SharedPreferences.Editor editor = preferences.edit();
					long i = preferences.getLong(ra.uid + "besend", 0);
					long j = preferences.getLong(ra.uid + "berev", 0);
					long k = preferences.getLong(ra.uid + "send", 0);
					long l = preferences.getLong(ra.uid + "rev", 0);
					long m = preferences.getLong(ra.uid + "wisend", 0);
					long n = preferences.getLong(ra.uid + "wirev", 0);
					// ��ȡ�������������
					// ����豸��֧�֣����ʾΪ0
					if (TrafficStats.getUidTxBytes(ra.uid) == -1) {
						editor.putLong(ra.uid + "send", 0);
						editor.putLong(ra.uid + "rev", 0);
						editor.commit();

					}

					else {
						if (mobile.getState() == NetworkInfo.State.CONNECTED
						        && wifi.getState() != NetworkInfo.State.CONNECTED) {
							if (!preferences.getBoolean("first", true)) {
								editor.putLong(ra.uid + "send",
								        TrafficStats.getUidTxBytes(ra.uid) - i
								                + k);
								editor.putLong(ra.uid + "rev",
								        TrafficStats.getUidRxBytes(ra.uid) - j
								                + l);
							} else {
								editor.putLong(ra.uid + "send",
								        TrafficStats.getUidTxBytes(ra.uid) + k);
								editor.putLong(ra.uid + "rev",
								        TrafficStats.getUidRxBytes(ra.uid) + i);
								editor.putBoolean("first", false);
							}
							i = TrafficStats.getUidTxBytes(ra.uid);
							j = TrafficStats.getUidRxBytes(ra.uid);
							editor.putLong(ra.uid + "besend", i);
							editor.putLong(ra.uid + "berev", j);
							editor.commit();
							//
						} else {
							if (!preferences.getBoolean("first", true)) {
								editor.putLong(ra.uid + "wisend",
								        TrafficStats.getUidTxBytes(ra.uid) - i
								                + m);
								editor.putLong(ra.uid + "wirev",
								        TrafficStats.getUidRxBytes(ra.uid) - j
								                + n);
							} else {
								editor.putLong(ra.uid + "wisend",
								        TrafficStats.getUidTxBytes(ra.uid) + m);
								editor.putLong(ra.uid + "wirev",
								        TrafficStats.getUidRxBytes(ra.uid) + n);
								editor.putBoolean("first", false);
							}

							i = TrafficStats.getUidTxBytes(ra.uid);
							j = TrafficStats.getUidRxBytes(ra.uid);
							editor.putLong(ra.uid + "besend", i);
							editor.putLong(ra.uid + "berev", j);
							editor.commit();
						}
					}
					pr.setApp_rev(preferences.getLong(ra.uid + "rev", 0));
					pr.setApp_sent(preferences.getLong(ra.uid + "send", 0));
					pr.setApp_traffic(preferences.getLong(ra.uid + "rev", 0)
					        + preferences.getLong(ra.uid + "send", 0));
					Log.v("xxss", pr.getApp_sent() + "ss" + pr.getApp_rev()
					        + "aa" + pr.getApp_name());
					slist.add(pr);
				}
			}
		}
		// �ϲ���һ��list
		list.addAll(slist);
		SharedPreferences preferences2 = context.getSharedPreferences(
		        "small_setting", Context.MODE_PRIVATE);
		String now = getCurrentTime()[0] + "" + getCurrentTime()[1] + ""
		        + getCurrentTime()[2];
		String yet = preferences2.getInt("year", 2012) + ""
		        + preferences2.getInt("mouth", 2) + ""
		        + preferences2.getInt("day", 28);
		if (!now.equals(yet)) {
			SharedPreferences preferences = context.getSharedPreferences(
			        "shadow_traffic", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear().commit();
		}
		return list;
	}

	// ��ȡϵͳʱ�䡣��������
	private int[] getCurrentTime() {
		int[] is = { 0, 0, 0, 0, 0, 0 };
		Time time = new Time();
		time.setToNow();
		is[0] = time.year;
		is[1] = time.month + 1;
		is[2] = time.monthDay;
		is[3] = time.hour;
		is[4] = time.minute;
		is[5] = time.second;
		return is;
	}
}