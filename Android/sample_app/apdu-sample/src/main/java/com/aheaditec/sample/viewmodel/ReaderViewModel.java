package com.aheaditec.sample.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.aheaditec.sample.enums.ReaderType;

/**
 * View model for ReaderActivity.
 */

public class ReaderViewModel extends AndroidViewModel {

    private MutableLiveData<String[]> permissionsDialog = new MutableLiveData<>();
    private MutableLiveData<String> bluetoothLocationDialog = new MutableLiveData<>();
    private MutableLiveData<ReaderType> selectedReader = new MutableLiveData<>();

    public ReaderViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String[]> getPermissionsDialog() {
        return permissionsDialog;
    }

    public MutableLiveData<String> getBluetoothLocationDialog() {
        return bluetoothLocationDialog;
    }

    public MutableLiveData<ReaderType> getSelectedReader() {
        return selectedReader;
    }
}
