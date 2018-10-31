package com.aheaditec.sample.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aheaditec.sample.Logger;

/**
 * Base activity which checks state of bluetooth and location adapters.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) {
            throw new IllegalStateException("Location manager not found.");
        }

        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 10, listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Logger.e(TAG, "Bluetooth turn off.");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Logger.e(TAG, "Bluetooth turn off.");
                        showErrorDialog("Bluetooth was disabled. This error is not handled.");
                        break;
                    default:
                        Logger.d(TAG, "Bluetooth state change.");
                }
            }
        }
    };

    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // nothing to do here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // nothing to do here
        }

        @Override
        public void onProviderEnabled(String provider) {
            // nothing to do here
        }

        @Override
        public void onProviderDisabled(String provider) {
            showErrorDialog("Location was disabled. This error is not handled.");
        }
    };

    private void showErrorDialog(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Application error.")
                .setMessage(msg)
                .setOnCancelListener(dialog1 -> {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                })
                .create();

        dialog.show();
    }

    boolean checkBluetoothAndLocation() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            showErrorDialog("Bluetooth is disabled.");
            return false;
        }

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) {
            throw new IllegalStateException("Location manager not found.");
        }
        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showErrorDialog("Location is disabled.");
            return false;
        }
        return true;
    }
}
