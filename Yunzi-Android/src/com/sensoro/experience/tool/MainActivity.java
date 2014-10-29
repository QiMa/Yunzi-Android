package com.sensoro.experience.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.SensoroBeaconManager;
import com.sensoro.beacon.kit.SensoroBeaconManager.BeaconManagerListener;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class MainActivity extends FragmentActivity {

	RelativeLayout containLayout;
	BeaconsFragment beaconsFragment;
	DetailFragment detailFragment;
	DistanceFragment distanceFragment;
	TemperatureFragment temperatureFragment;
	LightFragment lightFragment;
	MoveFragment moveFragment;
	NotificationFragment notificationFragment;

	FragmentManager fragmentManager;
	/*
	 * Beacon Manager lister,use it to listen the appearence, disappearence and
	 * updating of the beacons.
	 */
	BeaconManagerListener beaconManagerListener;
	MyApp app;
	/*
	 * Sensoro Beacon Manager
	 */
	SensoroBeaconManager sensoroBeaconManager;
	/*
	 * store beacons in onUpdateBeacon
	 */
	CopyOnWriteArrayList<Beacon> beacons;
	Handler handler = new Handler();
	Runnable runnable;

	public static final String TAG_FRAG_BEACONS = "TAG_FRAG_BEACONS";
	public static final String TAG_FRAG_DETAIL = "TAG_FRAG_DETAIL";
	public static final String TAG_FRAG_DISTANCE = "TAG_FRAG_DISTANCE";
	public static final String TAG_FRAG_LIGHT = "TAG_FRAG_LIGHT";
	public static final String TAG_FRAG_TEMPERATURE = "TAG_FRAG_TEMPERATURE";
	public static final String TAG_FRAG_MOVE = "TAG_FRAG_MOVE";
	public static final String TAG_FRAG_NOTIFICATION = "TAG_FRAG_NOTIFICATION";

	public static final String BEACON = "beacon";

	BluetoothManager bluetoothManager;
	BluetoothAdapter bluetoothAdapter;
	ArrayList<OnBeaconChangeListener> beaconListeners;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initCtrl();
		showFragment(0);
		initSensoroListener();
		initRunnable();
		initBroadcast();

	}

	@Override
	protected void onResume() {
		boolean isBTEnable = isBlueEnable();
		if (isBTEnable) {
			startSensoroService();
		}
		handler.post(runnable);
		super.onResume();
	}

	private void showFragment(int fragmentID) {
		beaconsFragment = new BeaconsFragment();
		fragmentManager.beginTransaction().add(R.id.activity_main_container, beaconsFragment, TAG_FRAG_BEACONS).commit();
	}

	private void initCtrl() {
		containLayout = (RelativeLayout) findViewById(R.id.activity_main_container);
		fragmentManager = getSupportFragmentManager();
		app = (MyApp) getApplication();
		sensoroBeaconManager = app.sensoroBeaconManager;
		beacons = new CopyOnWriteArrayList<Beacon>();
		beaconListeners = new ArrayList<OnBeaconChangeListener>();
	}

	private void initBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
					int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
					if (state == BluetoothAdapter.STATE_ON) {
						startSensoroService();
					}
				}
			}
		}, filter);
	}

	private boolean isBlueEnable() {
		bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		boolean status = bluetoothAdapter.isEnabled();
		if (!status) {
			Builder builder = new Builder(this);
			builder.setNegativeButton(R.string.yes, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivity(intent);
				}
			}).setPositiveButton(R.string.no, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setTitle(R.string.ask_bt_open);
			builder.show();
		}

		return status;
	}

	private void initRunnable() {
		runnable = new Runnable() {

			@Override
			public void run() {
				updateGridView();
				handler.postDelayed(this, 5000);
			}
		};

	}

	/*
	 * update the grid
	 */
	private void updateGridView() {
		if (beaconsFragment == null) {
			return;
		}
		if (!beaconsFragment.isVisible()) {
			return;
		}
		beaconsFragment.notifyFresh();
	}

	private void initSensoroListener() {
		beaconManagerListener = new BeaconManagerListener() {

			@Override
			public void onUpdateBeacon(final ArrayList<Beacon> arg0) {
				if (beaconsFragment == null) {
					beaconsFragment = (BeaconsFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAG_BEACONS);
				}
				if (beaconsFragment == null) {
					return;
				}
				/*
				 * beaconsFragment.isVisible()
				 */
				if (beaconsFragment.isVisible()) {
					/*
					 * Add the update beacons into the grid.
					 */
					beacons.addAll(arg0);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						for (OnBeaconChangeListener listener : beaconListeners) {
							if (listener == null) {
								continue;
							}
							listener.onBeaconChange(arg0);
						}
					}
				});

			}

			@Override
			public void onNewBeacon(Beacon arg0) {

			}

			@Override
			public void onGoneBeacon(Beacon arg0) {

			}
		};
	}

	/*
	 * Start sensoro service.
	 */
	private void startSensoroService() {
		// set a tBeaconManagerListener.
		sensoroBeaconManager.setBeaconManagerListener(beaconManagerListener);
		try {
			sensoroBeaconManager.startService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		handler.removeCallbacks(runnable);
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAG_BEACONS);
		if (fragment != null) {
			// exit the app
			System.exit(0);
			return false;
		}
		fragment = fragmentManager.findFragmentByTag(TAG_FRAG_DETAIL);
		if (fragment != null) {
			// back to beacons fragment
			fragmentManager.beginTransaction().replace(R.id.activity_main_container, beaconsFragment).commit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * Beacon Change Listener
	 */
	public interface OnBeaconChangeListener {
		public void onBeaconChange(ArrayList<Beacon> beacons);
	}

	/*
	 * Register beacon change listener.
	 */
	public void registerBeaconChangerListener(OnBeaconChangeListener onBeaconChangeListener) {
		if (beaconListeners == null) {
			return;
		}
		beaconListeners.add(onBeaconChangeListener);
	}

	/*
	 * Unregister beacon change listener.
	 */
	public void unregisterBeaconChangerListener(OnBeaconChangeListener onBeaconChangeListener) {
		if (beaconListeners == null) {
			return;
		}
		beaconListeners.remove(onBeaconChangeListener);
	}

}
