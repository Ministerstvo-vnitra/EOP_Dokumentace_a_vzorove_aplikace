package com.aheaditec.sample.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.aheaditec.acs.AcsPairing;
import com.aheaditec.feitian.FeitianPairing;
import com.aheaditec.wrapper.Reader;
import com.aheaditec.wrapper.interfaces.InitStateListener;
import com.aheaditec.wrapper.interfaces.OnPairWithReaderListener;
import com.aheaditec.wrapper.interfaces.OnSearchListener;
import com.aheaditec.wrapper.interfaces.Pairing;
import com.aheaditec.sample.Logger;
import com.aheaditec.sample.enums.ReaderType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Model working with IPairingImplementation for readers ACS and Feitian.
 */

public class PairingModel {
    private static final String TAG = PairingModel.class.getSimpleName();

    private Application application;
    private Pairing pairingImpl = null;
    private ReaderType readerType;

    /**
     * Constructor of PairingModel class.
     *
     * @param application Application context from view model instance.
     * @param readerType  ReaderType enum specifying type of reader in use.
     */
    public PairingModel(Application application, ReaderType readerType) {
        this.application = application;
        this.readerType = readerType;
    }

    /**
     * Method used for initialization of Pairing SDK.
     *
     * @return Single observable which emits Boolean true if initialization is successful,
     * or Exception in case of error.
     */
    public Single<Boolean> initAsync() {

        return Single.<Boolean>create(emitter -> {
            InitStateListener listener = new InitStateListener() {
                @Override
                public void initSuccess(@NonNull Pairing pairingImplementation) {
                    Logger.d(TAG, "Initialization was successful.");
                    // in case of success, initialized instance of IPairingImplementation is returned
                    pairingImpl = pairingImplementation;
                    emitter.onSuccess(true);
                }

                @Override
                public void initFailed(@NonNull Exception e) {
                    Logger.e(TAG, "Initialization failed with exception.");
                    // in case of error, Exception from SDK is returned
                    Logger.x(e);
                    emitter.onError(e);
                }
            };
            initializeReader(listener);
        }).subscribeOn(Schedulers.io());
    }

    private void initializeReader(@NonNull InitStateListener listener) {
        switch (readerType) {
            case ACS:
                // initialization of ACS pairing SDK, asynchronous task, result returned in callback InitStateListener
                new AcsPairing.InitAsync(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, application);
                break;
            case FEITIAN:
                // initialization of Feitian pairing SDK, asynchronous task, result returned in callback InitStateListener
                new FeitianPairing.InitAsync(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, application);
                break;
            default:
                throw new IllegalArgumentException("Unknown reader type passed as argument.");
        }
    }

    /**
     * Utility method to get the state of Pairing SDK.
     *
     * @return Boolean true or false if instance of IPairingImplementation is available.
     */
    public boolean isInitialized() {
        return pairingImpl != null;
    }

    /**
     * Method starts scanning for new available devices. Only non-paired, active devices
     * of selected Manufacturer are returned through listener.
     *
     * @return Flowable observable which emits found readers, or in case of bluetooth error
     * bluetooth exception is returned.
     */
    public Flowable<Reader> searchReaders() {
        pairingImpl.stopSearching();

        return Flowable.<Reader>create(emitter -> pairingImpl.startSearching(new OnSearchListener() {
            @Override
            public void onEnableBluetoothAdapter() {
                Logger.d(TAG, "Turn on bluetooth.");
                emitter.onError(new Exception("Turn on your bluetooth."));
            }

            @Override
            public void onReaderFound(Reader device) {
                Logger.d(TAG, "onReaderFound.");
                Logger.d(TAG, device.toString());
                emitter.onNext(device);
            }
        }), BackpressureStrategy.LATEST).subscribeOn(Schedulers.io());
    }

    /**
     * Method stops scanning for new devices.
     */
    public void stopSearching() {
        pairingImpl.stopSearching();
    }

    /**
     * Method used to pair to specific reader.
     *
     * @param reader Reader to pair.
     * @return Single observable which emits Boolean true after successful pairing process, or
     * exception in case of fail during pairing.
     */
    public Single<Boolean> pairWithReader(Reader reader) {

        return Single.<Boolean>create(emitter -> pairingImpl.pairWithReader(reader, new OnPairWithReaderListener() {
            @Override
            public void onReaderPaired(Reader reader) {
                Logger.d(TAG, "ReaderType paired: " + reader.toString());
                emitter.onSuccess(true);
            }

            @Override
            public void onError(@NonNull Exception e) {
                Logger.d(TAG, "onError: " + e.getMessage());
                emitter.onError(e);
            }
        })).subscribeOn(Schedulers.io());
    }

    /**
     * Method used to unpair paired device.
     *
     * @param reader Reader to unpair.
     * @return Single observable which emits Boolean true in case of successful unpair process,
     * or false in case of fail.
     */
    @SuppressLint("StaticFieldLeak")
    public Single<Boolean> unpairReader(Reader reader) {

        return Single.<Boolean>create(emitter ->
                // method is synchronous, must be called in AsyncTask
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        Logger.d(TAG, "Unpair of device: " + reader.toString());
                        return pairingImpl.unpairReader(reader);
                    }

                    @Override
                    protected void onPostExecute(Boolean res) {
                        super.onPostExecute(res);
                        emitter.onSuccess(res);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Gets list of paired devices from SDK. If the SDK is not initialized, empty list
     * is returned.
     *
     * @return List of Reader objects which are currently paired.
     */
    public List<Reader> getPairedReaders() {
        if (pairingImpl == null) {
            return new ArrayList<>();
        }
        return pairingImpl.getPairedDevices();
    }

    /**
     * Method used to rename paired reader. Nickname of the Reader is modified by this method.
     *
     * @param reader Reader to be renamed.
     * @param name   New desired name of the reader.
     * @return Single observable returning Boolean true in case of success, false otherwise.
     */
    @SuppressLint("StaticFieldLeak")
    public Single<Boolean> renameReader(@NonNull Reader reader, @NonNull String name) {

        return Single.<Boolean>create(emitter ->
                // method is synchronous, must be called in AsyncTask
                new AsyncTask<Void, Void, Reader>() {
                    @Override
                    protected Reader doInBackground(Void... voids) {
                        Logger.d(TAG, "Rename of device: " + reader.toString());
                        return pairingImpl.changedDeviceName(reader, name);
                    }

                    @Override
                    protected void onPostExecute(Reader device) {
                        super.onPostExecute(device);
                        if (device.getNickname().equals(name)) {
                            emitter.onSuccess(true);
                        } else {
                            emitter.onSuccess(false);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR))
                .subscribeOn(Schedulers.io());
    }
}
