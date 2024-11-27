package com.aheaditec.sample.model;

import android.app.Application;

import androidx.annotation.NonNull;

import com.aheaditec.acs.AcsReaderConnectionManager;
import com.aheaditec.airid.AirIdReaderConnectionManager;
import com.aheaditec.feitian.FeitianReaderConnectionManager;
import com.aheaditec.sample.Logger;
import com.aheaditec.sample.enums.ReaderType;
import com.aheaditec.usb.UsbConnectionManager;
import com.aheaditec.wrapper.Reader;
import com.aheaditec.wrapper.interfaces.OnPairedReaderFoundListener;
import com.aheaditec.wrapper.interfaces.ReaderConnectionManager;
import com.aheaditec.wrapper.interfaces.ReaderStatusListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Model class for {@link ReaderConnectionManager} interface.
 */

public class ConnectionModel {
    private static final String TAG = ConnectionModel.class.getSimpleName();

    private Application application;
    private ReaderType readerType;
    private ReaderConnectionManager readerManager;

    public ConnectionModel(Application application, ReaderType readerType) {
        this.application = application;
        this.readerType = readerType;
    }

    /**
     * Initializes plugin selected in start of the application.
     */
    public Single<Boolean> initializePlugin() {

        return Single.<Boolean>create(emitter -> {
            try {
                initPlugin();
                emitter.onSuccess(true);
                Logger.d(TAG, "SDK successfully initialized.");
            } catch (Exception e) {
                emitter.onError(e);
                Logger.d(TAG, "SDK init failed.");
            }
        }).observeOn(Schedulers.io());
    }

    /**
     * Utility method which sets the correct {@link ReaderConnectionManager}
     * implementation for reader type selected in start of the application.
     *
     * @throws Exception If the initialization process fails.
     */
    private void initPlugin() throws Exception {
        if (readerType.equals(ReaderType.ACS)) {
            readerManager = new AcsReaderConnectionManager(application);
        } else if (readerType.equals(ReaderType.FEITIAN)) {
            readerManager = new FeitianReaderConnectionManager(application);
        } else if (readerType.equals(ReaderType.AIR_ID)) {
            readerManager = new AirIdReaderConnectionManager(application);
        } else if (readerType.equals(ReaderType.USB)) {
            readerManager = new UsbConnectionManager(application);
        } else {
            throw new IllegalArgumentException("Invalid/unknown readerType passed as argument.");
        }
    }

    /**
     * Starts scanning for already paired devices. Method emits active
     * paired readers in range.
     */
    public Flowable<Reader> startScanning() {

        return Flowable.<Reader>create(emitter -> readerManager
                        .startScanningForPairedDevice(new OnPairedReaderFoundListener() {
                            @Override
                            public void onEnableBluetoothAdapter() {
                                emitter.onError(new Exception("Turn on bluetooth."));
                            }

                            @Override
                            public void onPairedReaderFound(@NonNull Reader reader) {
                                emitter.onNext(reader);
                            }
                        }),
                BackpressureStrategy.LATEST).observeOn(Schedulers.io());
    }

    /**
     * Starts connection procedure to the given reader. The state of the connection
     * is returned through {@link ReaderStatusListener} listener.
     */
    public void connectToReader(@NonNull Reader reader, @NonNull ReaderStatusListener listener) {
        if (readerManager == null) {
            throw new IllegalStateException("IReaderConnection interface was not initialized successfully.");
        }
        readerManager.connectReader(reader, listener);
    }

    /**
     * Stops scanning procedure.
     */
    public void stopScanning() {
        if (readerManager != null) {
            readerManager.stopScanningForPairedDevice();
        }
    }

    /**
     * Release and clean-up of IReaderConnection instance.
     */
    public void release() {
        if (readerManager != null) {
            readerManager.release();
        }
    }


    /**
     * Returns cached list of available readers. List will be empty if the
     * scan wasn't performed.
     *
     * @return List of available readers.
     */
    @SuppressWarnings("unused")
    public List<Reader> getAvailableReaderList() {
        if (readerManager == null) {
            return new ArrayList<>();
        }
        return readerManager.getAvailableReaderList();
    }

    /**
     * Get list of currently paired devices.
     *
     * @return List of currently paired readers.
     */
    public List<Reader> getPairedReaders() {
        if (readerManager == null) {
            return new ArrayList<>();
        }
        return readerManager.getPairedDevices();
    }
}
