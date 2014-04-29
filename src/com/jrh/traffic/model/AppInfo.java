package com.jrh.traffic.model;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppInfo {
	// ����
	private String app_package;
	// ͼ��
	private Drawable app_icon;
	// ����
	private String app_name = "�������еĳ���";
	// ���gprs�ϴ�����
	private String app_sent = "0K";
	// ���gprs��������
	private String app_rev = "0K";
	// �����������
	private String app_traffic = "0K";
	// ��ʾ
	private boolean ta = true;

	public boolean isTa() {
		return ta;
	}

	public void setTa(boolean ta) {
		this.ta = ta;
	}

	public String getApp_package() {
		return app_package;
	}

	public void setApp_package(String app_package) {
		this.app_package = app_package;
	}

	public Drawable getApp_icon() {
		return app_icon;
	}

	public void setApp_icon(Drawable app_icon) {
		this.app_icon = app_icon;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getApp_sent() {
		return app_sent;
	}

	public void setApp_sent(long app_sent) {
		this.app_sent = refreshTraffic(app_sent);
	}

	public String getApp_rev() {
		return app_rev;
	}

	public void setApp_rev(long app_rev) {
		this.app_rev = refreshTraffic(app_rev);
	}

	public String getApp_traffic() {
		return app_traffic;
	}

	public void setApp_traffic(long app_traffic) {
		this.app_traffic = refreshTraffic(app_traffic);
	}

	private String refreshTraffic(long lg) {
		String str = "0K";
		int b = 1024, c = 1048576;

		Log.v("hells", lg + "");
		if (lg < 1024) {
			str = "0K";
		} else if (lg >= b && lg < c) {
			int d = (int) (lg / b);
			str = d + "K";
		} else {
			int e = (int) (lg / c);
			int f = (int) ((lg - e * c) / 1024);
			str = e + "M" + " " + f + "K";
		}
		return str;
	}
}