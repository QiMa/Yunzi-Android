package com.sensoro.experience.tool;

import com.sensoro.beacon.kit.Beacon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NotificationFragment extends Fragment implements OnCheckedChangeListener {

	SwitchButton switchButton;
	Beacon beacon;

	MainActivity activity;
	SharedPreferences sharedPreferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = (MainActivity) getActivity();
		sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
		return inflater.inflate(R.layout.fragment_notification, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initCtrl();

		super.onActivityCreated(savedInstanceState);
	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (beacon == null) {
			return;
		}
		String key = beacon.getProximityUUID() + beacon.getMajor() + beacon.getMinor() + beacon.getSerialNumber();
		if (isChecked) {
			boolean isExist = sharedPreferences.contains(key);
			if (isExist) {
				return;
			}
			Editor editor = sharedPreferences.edit();
			editor.putBoolean(key, true);
			editor.commit();
		} else {
			Editor editor = sharedPreferences.edit();
			editor.remove(key);
			editor.commit();
		}
	}

}
