package com.sensoro.experience.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sensoro.beacon.kit.Beacon;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DetailFragment extends Fragment {

	private static final String TAG = DetailFragment.class.getSimpleName();
	Beacon beacon;

	TextView rssiTextView;
	TextView temperatureTextView;
	TextView lightTextView;
	TextView moveTextView;
	TextView moveCountTextView;
	TextView modelTextView;
	TextView firmwareTextView;
	TextView batteryTextView;

	GridView itemGridView;
	SimpleAdapter simpleAdapter;
	ArrayList<Map<String, Object>> items;

	public static final String IMG = "IMG";
	public static final String NAME = "NAME";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		beacon = (Beacon) bundle.get(BeaconsFragment.BEACON);

		initCtrl();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		initView();
		super.onResume();
	}

	private void initView() {
		if (beacon == null) {
			return;
		}
		rssiTextView.setText(beacon.getRssi() + "");
		temperatureTextView.setText(beacon.getTemperature() + "");
		lightTextView.setText(beacon.getBrightnessLux() + "");
		moveTextView.setText(beacon.getMovingState() + "");
		moveCountTextView.setText(beacon.getAccelerometerCount() + "");
		modelTextView.setText(beacon.getHardwareModelName());
		firmwareTextView.setText(beacon.getFirmwareVersion());
		batteryTextView.setText(beacon.getBatteryLevel() + "%");

		initGridView();
	}

	private void initCtrl() {
		Activity activity = getActivity();
		rssiTextView = (TextView) activity.findViewById(R.id.fragment_detail_rssi);
		temperatureTextView = (TextView) activity.findViewById(R.id.fragment_detail_temperature);
		lightTextView = (TextView) activity.findViewById(R.id.fragment_detail_light);
		moveTextView = (TextView) activity.findViewById(R.id.fragment_detail_move);
		moveCountTextView = (TextView) activity.findViewById(R.id.fragment_detail_move_count);
		modelTextView = (TextView) activity.findViewById(R.id.fragment_detail_model);
		firmwareTextView = (TextView) activity.findViewById(R.id.fragment_detail_firmware);
		batteryTextView = (TextView) activity.findViewById(R.id.fragment_detail_battery);

		itemGridView = (GridView) activity.findViewById(R.id.fragment_detail_item);
	}

	private void initGridView() {

		items = new ArrayList<Map<String, Object>>();

		String[] names = new String[] { "距离", "范围", "温度", "光线", "移动", "推送" };

		int[] imgs = new int[] { R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher };
		HashMap<String, Object> map = null;
		int pos = 0;
		for (int img : imgs) {
			map = new HashMap<String, Object>();
			map.put(IMG, img);
			map.put(NAME, names[pos]);
			items.add(map);
			pos++;
		}

		simpleAdapter = new SimpleAdapter(getActivity(), items, R.layout.fragment_detail_grid_item, new String[] { IMG, NAME }, new int[] { R.id.fragment_detail_grid_item_iv, R.id.fragment_detail_grid_item_tv_name });
		itemGridView.setAdapter(simpleAdapter);
	}
}
