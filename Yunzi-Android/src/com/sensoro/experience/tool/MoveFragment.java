package com.sensoro.experience.tool;

import java.util.ArrayList;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MoveFragment extends Fragment implements OnBeaconChangeListener {

	TextView valueTextView;
	TextView addTextView;
	ImageView imageView;
	Beacon beacon;

	MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_move, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initCtrl();

		super.onActivityCreated(savedInstanceState);
	}

	private void updateView() {
		if (beacon == null) {
			return;
		}
		if (beacon.getMovingState() == 0) {
			return;
		} else if (beacon.getMovingState() == 1) {
			valueTextView.setText(beacon.getAccelerometerCount() + "");
		}

	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
		valueTextView = (TextView) activity.findViewById(R.id.fragment_move_value);
		addTextView = (TextView) activity.findViewById(R.id.fragment_move_add);
		imageView = (ImageView) activity.findViewById(R.id.fragment_move_iv);
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
				updateView();
				break;
			}
		}
	}

}
