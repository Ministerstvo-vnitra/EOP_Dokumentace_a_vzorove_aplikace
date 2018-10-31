package com.aheaditec.sample.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.aheaditec.wrapper.Reader;
import com.aheaditec.wrapper.interfaces.CardReader;
import com.aheaditec.wrapper.interfaces.ReaderStatusListener;
import com.aheaditec.sample.listeners.SharedStatusListener;
import com.aheaditec.sample.enums.ReaderType;
import com.aheaditec.sample.model.ConnectionModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * ViewModel for ConnectActivity.
 */

public class ConnectViewModel extends BaseViewModel {
    private static final int SCAN_TIMEOUT = 40000;

    private ConnectionModel model;
    private Reader reader;

    private MutableLiveData<Boolean> progressLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Reader>> scanLiveData = new MutableLiveData<>();
    private MutableLiveData<Reader> isConnectedLiveData = new MutableLiveData<>();

    public ConnectViewModel(@NonNull Application application,
                            @NonNull ReaderType readerType) {
        super(application);
        this.model = new ConnectionModel(application, readerType);
        initializePlugin();
        isConnectedLiveData.postValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // free readers and stop scanning if necessary
        model.release();
    }

    private void initializePlugin() {
        progressLiveData.postValue(true);

        Disposable disposable = model.initializePlugin()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> progressLiveData.postValue(false), throwable -> {
                    progressLiveData.postValue(false);
                    errorLiveData.postValue(throwable.getMessage());
                });

        getCompositeDisposable().add(disposable);
    }

    public void startScanning() {
        List<Reader> readers = new ArrayList<>();
        scanLiveData.postValue(readers);

        // stops scan after timeoutPeriod
        new Handler().postDelayed(this::stopScanning, SCAN_TIMEOUT);

        Disposable disposable = model.startScanning()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reader -> {
                    readers.add(reader);
                    scanLiveData.postValue(readers);
                });

        getCompositeDisposable().add(disposable);
    }

    public void stopScanning() {
        model.stopScanning();
        scanLiveData.postValue(null);
    }

    public void connectToReader(@NonNull Reader reader) {
        progressLiveData.postValue(true);
        SharedStatusListener sharedListener = new SharedStatusListener();
        sharedListener.registerListener(listener);
        this.reader = reader;
        model.connectToReader(reader, sharedListener);
    }

    public List<Reader> getPairedDevices() {
        return model.getPairedReaders();
    }

    private ReaderStatusListener listener = new ReaderStatusListener() {
        @Override
        public void onReaderConnected(@NonNull CardReader readerInstance) {
            progressLiveData.postValue(false);
            isConnectedLiveData.postValue(reader);
            setImplementation(readerInstance);
        }

        @Override
        public void onReaderDisconnected(@NonNull Reader reader) {
            progressLiveData.postValue(false);
            isConnectedLiveData.postValue(null);
            setImplementation(null);
        }

        @Override
        public void onCardRemoved() {
            // nothing to do here
        }

        @Override
        public void onCardInserted() {
            // nothing to do here
        }

        @Override
        public void onCardUnknownState() {
            // nothing to do here
        }

        @Override
        public void onReaderNotFound() {
            progressLiveData.postValue(false);
            errorLiveData.postValue("Reader couldn't be found.");
        }

        @Override
        public void onError(@NonNull Exception e) {
            progressLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    };

    public MutableLiveData<Boolean> getProgressLiveData() {
        return progressLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<List<Reader>> getScanLiveData() {
        return scanLiveData;
    }

    public MutableLiveData<Reader> getIsConnectedLiveData() {
        return isConnectedLiveData;
    }
}
