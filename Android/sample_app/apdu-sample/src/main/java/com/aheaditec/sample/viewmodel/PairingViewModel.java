package com.aheaditec.sample.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aheaditec.wrapper.Reader;
import com.aheaditec.sample.enums.ReaderType;
import com.aheaditec.sample.model.PairingModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * ViewModel for PairingActivity
 */

public class PairingViewModel extends AndroidViewModel {
    private static final int TIMEOUT = 40000;

    private MutableLiveData<List<Reader>> scanLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Reader>> pairedReadersLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBarLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> scanProgressBarLiveData = new MutableLiveData<>();
    private MutableLiveData<Reader> pairingDialogLiveData = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> pairingDoneLiveData = new MutableLiveData<>();
    private MutableLiveData<Reader> renameDialogLiveData = new MutableLiveData<>();

    private PairingModel pairModel;
    private CompositeDisposable compositeDisposable;
    private List<Reader> scannedReaders;

    public PairingViewModel(@NonNull Application application, ReaderType readerType) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        pairModel = new PairingModel(application, readerType);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    /**
     * Initialization of pairing SDK. If the initialization is
     * successful, already paired readers are sent to View.
     */
    public void initPairing() {
        if (pairModel.isInitialized()) {
            return;
        }
        Disposable disposable = pairModel.initAsync()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> getPairedReaders(),
                        throwable -> errorLiveData.postValue(throwable));

        compositeDisposable.add(disposable);
    }

    /**
     * Starts scanning for unknown devices and enable
     * scanning progress bar. Scan is automatically
     * disabled after TIMEOUT period.
     */
    public void startScan() {
        scannedReaders = new ArrayList<>();
        scanProgressBarLiveData.postValue(true);

        // stops scan after TIMEOUT period
        Handler handler = new Handler();
        handler.postDelayed(this::stopScan, TIMEOUT);

        Disposable disposable = pairModel.searchReaders()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reader -> {
                    if (scannedReaders.contains(reader)) {
                        return;
                    }
                    scannedReaders.add(reader);
                    scanLiveData.postValue(scannedReaders);
                }, throwable -> errorLiveData.postValue(throwable));

        compositeDisposable.add(disposable);
    }

    /**
     * Stops scanning for unknown readers and hides scanning
     * progress bar.
     */
    public void stopScan() {
        pairModel.stopSearching();
        scanProgressBarLiveData.postValue(false);
    }

    /**
     * Represents state of the pairing dialog, if reader is set, dialog
     * will be shown. Null value means that dialog is dismissed.
     */
    public void pairWithReaderDialog(@Nullable Reader reader) {
        pairingDialogLiveData.postValue(reader);
    }

    /**
     * Starts pairing with unknown reader. If successful, pairingDone
     * dialog is shown, errorDialog otherwise.
     */
    public void pairWithReader(Reader reader) {
        progressBarLiveData.postValue(true);
        pairingDialogLiveData.postValue(null);

        Disposable disposable = pairModel.pairWithReader(reader)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    pairedReadersLiveData.postValue(pairModel.getPairedReaders());
                    scannedReaders.remove(reader);
                    scanLiveData.postValue(scannedReaders);
                    progressBarLiveData.postValue(false);
                    pairingDoneLiveData.postValue(true);
                }, throwable -> errorLiveData.postValue(throwable));

        compositeDisposable.add(disposable);
    }

    /**
     * Removes device from paired readers list and updates
     * list of paired readers in View.
     */
    public void unpairReader(Reader reader) {
        progressBarLiveData.postValue(true);

        Disposable disposable = pairModel.unpairReader(reader)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    pairedReadersLiveData.postValue(pairModel.getPairedReaders());
                    progressBarLiveData.postValue(false);
                }, throwable -> {
                    errorLiveData.postValue(throwable);
                    progressBarLiveData.postValue(false);
                });

        compositeDisposable.add(disposable);
    }

    /**
     * Represents state of the rename dialog, if reader is set, dialog
     * will be shown. Null value means that dialog is dismissed.
     */
    public void showRenameDialog(Reader reader) {
        renameDialogLiveData.postValue(reader);
    }

    /**
     * Renames device from paired readers list and updates
     * list of paired readers in View.
     */
    public void renameReader(Reader reader, String name) {
        progressBarLiveData.postValue(true);
        renameDialogLiveData.postValue(null);

        Disposable disposable = pairModel.renameReader(reader, name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    pairedReadersLiveData.postValue(pairModel.getPairedReaders());
                    progressBarLiveData.postValue(false);
                }, throwable -> {
                    errorLiveData.postValue(throwable);
                    progressBarLiveData.postValue(false);
                });

        compositeDisposable.add(disposable);
    }

    /**
     * Utility method to get list of already paired readers from SDK.
     */
    private void getPairedReaders() {
        progressBarLiveData.postValue(true);
        pairedReadersLiveData.postValue(pairModel.getPairedReaders());
        progressBarLiveData.postValue(false);
    }

    /**
     * Utility method signalizing that pairing event was already handled
     * in View.
     */
    public void pairDoneDialogClosed() {
        pairingDoneLiveData.postValue(null);
    }

    /**
     * Utility method signalizing that error event was already handled
     * in View.
     */
    public void errorDialogClosed() {
        errorLiveData.postValue(null);
    }

    public MutableLiveData<List<Reader>> getScanLiveData() {
        return scanLiveData;
    }

    public MutableLiveData<List<Reader>> getPairedReadersLiveData() {
        return pairedReadersLiveData;
    }

    public MutableLiveData<Boolean> getProgressBarLiveData() {
        return progressBarLiveData;
    }

    public MutableLiveData<Boolean> getScanProgressBarLiveData() {
        return scanProgressBarLiveData;
    }

    public MutableLiveData<Throwable> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getPairingDoneLiveData() {
        return pairingDoneLiveData;
    }

    public MutableLiveData<Reader> getPairingDialogLiveData() {
        return pairingDialogLiveData;
    }

    public MutableLiveData<Reader> getRenameDialogLiveData() {
        return renameDialogLiveData;
    }
}
