package com.jrh.traffic.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jrh.traffic.R;
import com.jrh.traffic.model.AppInfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter extends BaseAdapter {
	private List<AppInfo> list = new ArrayList<AppInfo>();

	public List<AppInfo> getList() {
		return list;
	}

	public void setList(List<AppInfo> list) {
		this.list = list;
	}

	private Context context;
	private LayoutInflater la;

	public Adapter(List<AppInfo> list, Context context) {
		this.list = list;
		this.context = context;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		la = LayoutInflater.from(context);

		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag(R.string.first);
			Log.v("title", list.get(position).getApp_name());
		} else {
			convertView = la.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.imgage = (ImageView) convertView
			        .findViewById(R.id.list_image);
			holder.text = (TextView) convertView.findViewById(R.id.list_text);
			holder.gprs_rev = (TextView) convertView
			        .findViewById(R.id.gprs_down);
			holder.gprs_send = (TextView) convertView
			        .findViewById(R.id.gprs_upload);
			holder.gprs_total = (TextView) convertView
			        .findViewById(R.id.gprs_total);
			holder.process_name = (TextView) convertView
			        .findViewById(R.id.process_name);
			convertView.setTag(R.string.first, holder);
			Log.v("title", list.get(position).getApp_name() + "ss");
		}
		final AppInfo appinfo = (AppInfo) list.get(position);
		// 设置图标
		holder.imgage.setImageDrawable(appinfo.getApp_icon());
		// 设置程序名
		holder.text.setText(appinfo.getApp_name());
		holder.gprs_rev.setText(appinfo.getApp_rev());
		holder.gprs_send.setText(appinfo.getApp_sent());
		holder.gprs_total.setText(appinfo.getApp_traffic());
		holder.process_name.setText(appinfo.getApp_package());
		return convertView;

	}

	class ViewHolder {
		TextView text, process_name;
		ImageView imgage;
		TextView gprs_send, gprs_rev, gprs_total;
	}

	class ViewTitle {
		TextView title;
	}
}
