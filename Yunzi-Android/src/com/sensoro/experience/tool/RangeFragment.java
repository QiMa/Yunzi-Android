package com.sensoro.experience.tool;

import java.util.ArrayList;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.Beacon.Proximity;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class RangeFragment extends Fragment implements OnBeaconChangeListener {

	Beacon beacon;
	MainActivity activity;
	RelativeLayout immediateLayout;
	RelativeLayout nearLayout;
	RelativeLayout farLayout;
	TTFIcon userIcon;

	int[] immediatePostion;
	int[] nearPosition;
	int[] farPosition;
	int[] unknowPosition;
	TranslateAnimation animation;
	AccelerateInterpolator interpolator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_range, container, false);
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

	}

	private void initUserPos() {
		if (beacon == null) {
			// userIcon.setX(unknowPosition[0]);
			// userIcon.setY(unknowPosition[1]);
			userIcon.setX(immediatePostion[0]);
			userIcon.setY(immediatePostion[1]);
		} else if (beacon.getProximity() == Proximity.PROXIMITY_IMMEDIATE) {
			userIcon.setX(immediatePostion[0]);
			userIcon.setY(immediatePostion[1]);
		} else if (beacon.getProximity() == Proximity.PROXIMITY_NEAR) {
			// userIcon.setX(nearPosition[0]);
			// userIcon.setY(nearPosition[1]);
			userIcon.setX(immediatePostion[0]);
			userIcon.setY(immediatePostion[1]);
		} else if (beacon.getProximity() == Proximity.PROXIMITY_FAR) {
			// userIcon.setX(farPosition[0]);
			// userIcon.setY(farPosition[1]);
			userIcon.setX(immediatePostion[0]);
			userIcon.setY(immediatePostion[1]);
		}
	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();

		immediateLayout = (RelativeLayout) activity.findViewById(R.id.fragment_range_immediate);
		nearLayout = (RelativeLayout) activity.findViewById(R.id.fragment_range_near);
		farLayout = (RelativeLayout) activity.findViewById(R.id.fragment_range_far);
		userIcon = (TTFIcon) activity.findViewById(R.id.fragment_range_iv);
		initCircle();
	}

	private void initCircle() {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int wid = metrics.widthPixels;
		int height = metrics.heightPixels;

		int length = (height > wid) ? wid : height;

		int radius = length / 8;
		int curLength = radius * 2;
		int curRadius = radius;
		LayoutParams params = new LayoutParams(curLength, curLength);
		immediateLayout.setLayoutParams(params);
		immediateLayout.setX(wid / 2 - curRadius);
		immediateLayout.setY(height / 2 - curRadius);

		curLength = 4 * radius;
		curRadius = 2 * radius;

		params = new LayoutParams(curLength, curLength);
		nearLayout.setLayoutParams(params);
		nearLayout.setX(wid / 2 - curRadius);
		nearLayout.setY(height / 2 - curRadius);

		curLength = 6 * radius;
		curRadius = 3 * radius;
		params = new LayoutParams(curLength, curLength);
		farLayout.setLayoutParams(params);
		farLayout.setX(wid / 2 - curRadius);
		farLayout.setY(height / 2 - curRadius);

		int userRaduis = radius / 5 * 2;
		params = new LayoutParams(userRaduis * 2, userRaduis * 2);
		userIcon.setLayoutParams(params);

		initPosition(wid, height);

		initUserPos();

	}

	private void initPosition(int wid, int height) {

		int length = (height > wid) ? wid : height;

		int radius = length / 8;
		int userRaduis = radius / 5 * 2;

		immediatePostion = new int[2];
		immediatePostion[0] = wid / 2 - userRaduis;
		immediatePostion[1] = height / 2 - userRaduis;

		nearPosition = new int[2];
		nearPosition[0] = wid / 2 - userRaduis;
		nearPosition[1] = (int) (height / 2 - userRaduis + 1.5 * radius);

		farPosition = new int[2];
		farPosition[0] = wid / 2 - userRaduis;
		farPosition[1] = (int) (height / 2 - userRaduis + 2.5 * radius);

		unknowPosition = new int[2];
		unknowPosition[0] = wid / 2 - userRaduis;
		unknowPosition[1] = (int) (height / 2 - userRaduis + 4 * radius);
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
		for (Beacon beacon : beacons) {
			if (beacon.getProximityUUID().equals(this.beacon.getProximityUUID()) && beacon.getMajor() == this.beacon.getMajor() && beacon.getMinor() == this.beacon.getMinor()) {
				this.beacon = beacon;
				updateView(beacon);
				break;
			}
		}
	}
}
