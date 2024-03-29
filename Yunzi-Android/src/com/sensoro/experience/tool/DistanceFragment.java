package com.sensoro.experience.tool;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

import android.R.bool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DistanceFragment extends Fragment implements OnBeaconChangeListener {

	Beacon beacon;
	TextView distanceTextView;
	MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_distance, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initCtrl();

		super.onActivityCreated(savedInstanceState);
	}

	private void updateView(Beacon beacon) {
		if (beacon == null) {
			return;
		}
		DecimalFormat format = new DecimalFormat("#");
		String distance = format.format(beacon.getAccuracy() * 100);
		distanceTextView.setText(distance + " cm");
	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
		distanceTextView = (TextView) activity.findViewById(R.id.fragment_distance_tv);

	}

	@Override
	public void onResume() {
		updateView(beacon);
		registerBeaconChangeListener();
		super.onResume();
	}

	@Override
	public void onStop() {
		unregisterBeaconChangeListener();
		super.onStop();
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

	@Override
	public void onBeaconChange(ArrayList<Beacon> beacons) {
		boolean exist = false;
		for (Beacon beacon : beacons) {
			if (beacon.getProximityUUID().equals(this.beacon.getProximityUUID()) && beacon.getMajor() == this.beacon.getMajor() && beacon.getMinor() == this.beacon.getMinor()) {
				this.beacon = beacon;
				updateView(beacon);
				exist = true;
				break;
			}
		}
		if (!exist) {
			distanceTextView.setText(R.string.disappear);
		}
	}
}
