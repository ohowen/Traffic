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
		// 通包管理器，检索所有的应用程序（甚至卸载的）与数据目录
		this.context = context;
		pm = context.getApplicationContext().getPackageManager();
		appList = pm.getInstalledApplications(0);
	}

	/**
	 * 通过一个程序名返回该程序的一个Application对象。
	 * 
	 * @param name
	 *            程序名
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

	public List<AppInfo> getNetworkApps() {
		
		// 获取包管理器，在这里主要通过包名获取程序的图标和程序名
		List<AppInfo> list = new ArrayList<AppInfo>();
		for (ApplicationInfo ra : appList) {

			// 过滤掉没有联网功能的软件
			int reslut = context.getPackageManager().checkPermission(
			        "android.permission.INTERNET", ra.processName);
			if (reslut != PackageManager.PERMISSION_GRANTED) {
				continue;
			}
			// 过滤掉无对应的程序
			if (getInfo(ra.processName) != null) {
				if ((getInfo(ra.processName).flags & ApplicationInfo.FLAG_SYSTEM) == 0
				        && (getInfo(ra.processName).flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
					AppInfo appInfo = new AppInfo();
					appInfo.setApp_icon(getInfo(ra.processName).loadIcon(pm));
					appInfo.setApp_name(getInfo(ra.processName).loadLabel(pm)
					        .toString());
					appInfo.setApp_package(ra.processName);

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
					long besend = preferences.getLong(ra.uid + "besend", 0);
					long berev = preferences.getLong(ra.uid + "berev", 0);
					long send = preferences.getLong(ra.uid + "send", 0);
					long rev = preferences.getLong(ra.uid + "rev", 0);
					long wisend = preferences.getLong(ra.uid + "wisend", 0);
					long wirev = preferences.getLong(ra.uid + "wirev", 0);
					// 获取单个程序的流量
					// 如果设备不支持，则表示为0
					if (TrafficStats.getUidTxBytes(ra.uid) == -1) {
						editor.putLong(ra.uid + "send", 0);
						editor.putLong(ra.uid + "rev", 0);
						editor.commit();
					} else {
						// 移动数据连接
						if (mobile.getState() == NetworkInfo.State.CONNECTED
						        && wifi.getState() != NetworkInfo.State.CONNECTED) {
							// 开机后，first是ture
							if (!preferences.getBoolean("first", true)) {
								editor.putLong(ra.uid + "send",
								        TrafficStats.getUidTxBytes(ra.uid)
								                - besend + send);
								editor.putLong(ra.uid + "rev",
								        TrafficStats.getUidRxBytes(ra.uid)
								                - berev + rev);
							} else {
								// 开机第一次运行
								editor.putLong(ra.uid + "send",
								        TrafficStats.getUidTxBytes(ra.uid)
								                + send);
								editor.putLong(ra.uid + "rev",
								        TrafficStats.getUidRxBytes(ra.uid)
								                + besend);
								editor.putBoolean("first", false);
							}
							besend = TrafficStats.getUidTxBytes(ra.uid);
							berev = TrafficStats.getUidRxBytes(ra.uid);
							editor.putLong(ra.uid + "besend", besend);
							editor.putLong(ra.uid + "berev", berev);
							editor.commit();
						} else {
							// wifi连接
							if (!preferences.getBoolean("first", true)) {
								editor.putLong(ra.uid + "wisend",
								        TrafficStats.getUidTxBytes(ra.uid)
								                - besend + wisend);
								editor.putLong(ra.uid + "wirev",
								        TrafficStats.getUidRxBytes(ra.uid)
								                - berev + wirev);
							} else {
								editor.putLong(ra.uid + "wisend",
								        TrafficStats.getUidTxBytes(ra.uid)
								                + wisend);
								editor.putLong(ra.uid + "wirev",
								        TrafficStats.getUidRxBytes(ra.uid)
								                + wirev);
								editor.putBoolean("first", false);
							}
							besend = TrafficStats.getUidTxBytes(ra.uid);
							berev = TrafficStats.getUidRxBytes(ra.uid);
							editor.putLong(ra.uid + "besend", besend);
							editor.putLong(ra.uid + "berev", berev);
							editor.commit();
						}
					}
					appInfo.setApp_rev(preferences.getLong(ra.uid + "rev", 0));
					appInfo.setApp_sent(preferences.getLong(ra.uid + "send", 0));
					appInfo.setApp_traffic(preferences.getLong(ra.uid + "rev",
					        0) + preferences.getLong(ra.uid + "send", 0));
					Log.v("xxss",
					        appInfo.getApp_sent() + "ss" + appInfo.getApp_rev()
					                + "aa" + appInfo.getApp_name());
					list.add(appInfo);
					Log.v("shadow", appInfo.getApp_name());

					// 每月1日把记录清零
					SharedPreferences preferences2 = context
					        .getSharedPreferences("shadow_traffic",
					                Context.MODE_PRIVATE);
					String now = "" + getCurrentTime()[2];
					String yet = "" + preferences2.getInt("day", 1);
					if (now.equals(yet)) {
						SharedPreferences preference = context
						        .getSharedPreferences("shadow_traffic",
						                Context.MODE_PRIVATE);
						SharedPreferences.Editor edito = preference.edit();
						edito.clear().commit();
					}
				}
			}
		}
		return list;
	}

	// 获取系统时间。返回数组
	private int[] getCurrentTime() {
		int[] is = { 0, 0, 0 };
		Time time = new Time();
		time.setToNow();
		is[0] = time.year;
		is[1] = time.month;
		is[2] = time.monthDay;
		return is;
	}
}