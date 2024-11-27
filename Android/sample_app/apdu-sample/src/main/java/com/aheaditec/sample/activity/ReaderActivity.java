package com.aheaditec.sample.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aheaditec.acs.AcsReaderConnectionManager;
import com.aheaditec.airid.AirIdReaderConnectionManager;
import com.aheaditec.feitian.FeitianReaderConnectionManager;
import com.aheaditec.sample.Logger;
import com.aheaditec.sample.R;
import com.aheaditec.sample.enums.ReaderType;
import com.aheaditec.sample.viewmodel.ReaderViewModel;
import com.aheaditec.usb.UsbConnectionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import java.util.List;

/**
 * Basic activity for choosing of desired reader plugin.
 */

public class ReaderActivity extends AppCompatActivity {
    private static final String TAG = ReaderActivity.class.getSimpleName();

    private static final int PERMISSION_CODE = 3332;
    private static final int BLUETOOTH_ON = 3333;

    private ReaderViewModel viewModel;
    private MaterialDialog errorDialog;

    @BindView(R.id.btnReaderContinue)
    Button continueButton;

    @BindView(R.id.radioGroupReader)
    RadioGroup radioButtonGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init logger
        Logger.init(Logger.LogLevel.VERBOSE);

        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this, this);

        if (savedInstanceState == null) {
            // prevents of opening another dialogs during configuration change
            // dialog for bluetooth check is handled by system, lifecycle doesn't apply to it
            checkBluetooth();
            onSaveInstanceState(new Bundle());
        }

        setUpViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationCheck();
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(ReaderViewModel.class);

        observePermissionDialog();
        observeBluetoothLocationDialog();
    }

    private void observePermissionDialog() {
        viewModel.getPermissionsDialog().observe(this, permissions -> {
            if (permissions != null) {
                showPermissionDeniedDialog(permissions);
            }
        });
    }

    private void observeBluetoothLocationDialog() {
        viewModel.getBluetoothLocationDialog().observe(this, msg -> {
            if (msg != null) {
                showBluetoothOrLocationDeniedDialog(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (errorDialog != null) {
            errorDialog.dismiss();
        }
    }

    @OnClick(R.id.btnReaderContinue)
    public void onContinueButtonClick() {
        switch (radioButtonGroup.getCheckedRadioButtonId()) {
            case R.id.radioBtnReaderAcs:
                Logger.d(TAG, "Acs selected.");
                viewModel.getSelectedReader().postValue(ReaderType.ACS);
                checkPermissions(AcsReaderConnectionManager.getPermissionNames());
                break;
            case R.id.radioBtnReaderFeitian:
                Logger.d(TAG, "Feitian selected.");
                viewModel.getSelectedReader().postValue(ReaderType.FEITIAN);
                checkPermissions(FeitianReaderConnectionManager.getPermissionNames());
                break;
            case R.id.radioBtnReaderAirId:
                Logger.d(TAG, "AirID selected.");
                viewModel.getSelectedReader().postValue(ReaderType.AIR_ID);
                checkPermissions(AirIdReaderConnectionManager.getPermissionNames());
                break;
            case R.id.radioBtnReaderUsb:
                Logger.d(TAG, "USB selected.");
                viewModel.getSelectedReader().postValue(ReaderType.USB);
                startActivity(ConnectActivity.getStartIntent(this, ReaderType.USB));
                break;
            default:
                Toast.makeText(this, "No reader selected!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void checkBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "No bluetooth adapter detected.", Toast.LENGTH_LONG).show();
            continueButton.setEnabled(false);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivityForResult(enableBtIntent, BLUETOOTH_ON);
            } else {
                locationCheck();
            }
        }
    }

    private void locationCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) {
            Logger.e(TAG, "Location not supported!");
            continueButton.setEnabled(false);
            return;
        }
        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            continueButton.setEnabled(true);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Turn on Location:")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> {
                    continueButton.setEnabled(false);
                    viewModel.getBluetoothLocationDialog().postValue("Location must be enabled for this application to work.");
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ON) {
            Logger.e(TAG, "Result: " + resultCode);
            if (resultCode != RESULT_OK) {
                viewModel.getBluetoothLocationDialog().postValue("Bluetooth must be enabled for this application to run.");
            } else {
                locationCheck();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_CODE) {
            return;
        }
        boolean granted = true;
        for (int i = 0; i < permissions.length; i++) {
            Logger.d(TAG, "Permission: " + permissions[i]);
            if (grantResults[i] != PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        if (!granted) {
            viewModel.getPermissionsDialog().postValue(permissions);
            return;
        }

        startActivity(ConnectActivity.getStartIntent(this, viewModel.getSelectedReader().getValue()));
    }

    private void showPermissionDeniedDialog(String[] permissions) {
        errorDialog = new MaterialDialog.Builder(this)
                .title("Application error")
                .content("The permission must be granted for application to work.")
                .positiveText("Continue")
                .onPositive((dialog, which) -> {
                    checkPermissions(permissions);
                    viewModel.getPermissionsDialog().postValue(null);
                })
                .negativeText("Exit app")
                .onNegative(((dialog, which) -> finish()))
                .build();

        errorDialog.show();
    }

    private void showBluetoothOrLocationDeniedDialog(String msg) {
        errorDialog = new MaterialDialog.Builder(this)
                .title("Application error")
                .content(msg)
                .positiveText("Continue")
                .onPositive((dialog, which) -> {
                    checkBluetooth();
                    viewModel.getBluetoothLocationDialog().postValue(null);
                })
                .negativeText("Exit app")
                .onNegative(((dialog, which) -> finish()))
                .build();

        errorDialog.show();
    }
}
