package com.sensoro.experience.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DetailFragment extends Fragment implements OnBeaconChangeListener, OnItemClickListener {

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
	MainActivity activity;

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
		beacon = (Beacon) bundle.get(MainActivity.BEACON);

		initCtrl();

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		updateView();
		registerBeaconChangeListener();
		super.onResume();
	}

	@Override
	public void onStop() {
		unregisterBeaconChangeListener();
		super.onStop();
	}

	private void updateView() {
		if (beacon == null) {
			return;
		}
		rssiTextView.setText(beacon.getRssi() + "");
		temperatureTextView.setText(beacon.getTemperature() + "");
		lightTextView.setText(beacon.getLight() + "");
		moveTextView.setText(beacon.getMovingState() + "");
		moveCountTextView.setText(beacon.getAccelerometerCount() + "");
		modelTextView.setText(beacon.getHardwareModelName());
		firmwareTextView.setText(beacon.getFirmwareVersion());
		batteryTextView.setText(beacon.getBatteryLevel() + "%");

	}

	private void initCtrl() {
		activity = (MainActivity) getActivity();

		rssiTextView = (TextView) activity.findViewById(R.id.fragment_detail_rssi);
		temperatureTextView = (TextView) activity.findViewById(R.id.fragment_detail_temperature);
		lightTextView = (TextView) activity.findViewById(R.id.fragment_detail_light);
		moveTextView = (TextView) activity.findViewById(R.id.fragment_detail_move);
		moveCountTextView = (TextView) activity.findViewById(R.id.fragment_detail_move_count);
		modelTextView = (TextView) activity.findViewById(R.id.fragment_detail_model);
		firmwareTextView = (TextView) activity.findViewById(R.id.fragment_detail_firmware);
		batteryTextView = (TextView) activity.findViewById(R.id.fragment_detail_battery);

		itemGridView = (GridView) activity.findViewById(R.id.fragment_detail_item);
		itemGridView.setOnItemClickListener(this);
		initGridView();
	}

	/*
	 * Register beacon change listener.
	 */
	private void registerBeaconChangeListener() {
		activity.registerBeaconChangerListener(this);
	}

	/*
	 * Register beacon change listener.
	 */
	private void unregisterBeaconChangeListener() {
		activity.unregisterBeaconChangerListener(this);
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

	@Override
	public void onBeaconChange(ArrayList<Beacon> beacons) {
		for (Beacon beacon : beacons) {
			if (beacon.getProximityUUID().equals(this.beacon.getProximityUUID()) && beacon.getMajor() == this.beacon.getMajor() && beacon.getMinor() == this.beacon.getMinor()) {
				this.beacon = beacon;
				updateView();
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			activity.distanceFragment = new DistanceFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.distanceFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.distanceFragment, MainActivity.TAG_FRAG_DISTANCE);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 2) {
			activity.temperatureFragment = new TemperatureFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.temperatureFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.temperatureFragment, MainActivity.TAG_FRAG_TEMPERATURE);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 3) {
			activity.lightFragment = new LightFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.lightFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.lightFragment, MainActivity.TAG_FRAG_LIGHT);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 4) {
			activity.moveFragment = new MoveFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.moveFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.moveFragment, MainActivity.TAG_FRAG_MOVE);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 5) {
			activity.notificationFragment = new NotificationFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.notificationFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.notificationFragment, MainActivity.TAG_FRAG_NOTIFICATION);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
}
