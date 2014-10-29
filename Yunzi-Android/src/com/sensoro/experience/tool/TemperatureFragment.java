package com.sensoro.experience.tool;

import java.util.ArrayList;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TemperatureFragment extends Fragment implements OnBeaconChangeListener {

	TextView valueTextView;
	Beacon beacon;
	RelativeLayout relativeLayout;

	MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_temperature, container, false);
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
		int temperature = beacon.getTemperature();
		if (temperature == Integer.MAX_VALUE) {
			valueTextView.setText(R.string.disable);
		}
		String format = String.format("%d", temperature);
		

		if (temperature < 0) {
			relativeLayout.setBackgroundColor(getResources().getColor(R.color.dark));
		} else if (temperature >= 0 && temperature < 10) {
			relativeLayout.setBackgroundColor(getResources().getColor(R.color.normal));
		} else if (temperature >= 10) {
			relativeLayout.setBackgroundColor(getResources().getColor(R.color.glare));
		}
		valueTextView.setText(format + getString(R.string.degree));
	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
		valueTextView = (TextView) activity.findViewById(R.id.fragment_temperature_value);
		relativeLayout = (RelativeLayout) activity.findViewById(R.id.fragment_temperature_rl);
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
