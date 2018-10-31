package com.aheaditec.sample.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;

import com.aheaditec.apdu.enums.VerifyCodes;
import com.aheaditec.apdu.structures.ContainerBasic;
import com.aheaditec.apdu.structures.PinInfoBasic;
import com.aheaditec.wrapper.Reader;
import com.aheaditec.wrapper.interfaces.CardReader;
import com.aheaditec.wrapper.interfaces.ReaderStatusListener;
import com.aheaditec.sample.Logger;
import com.aheaditec.sample.R;
import com.aheaditec.sample.enums.SpinnerOptions;
import com.aheaditec.sample.listeners.PinEntryListener;
import com.aheaditec.sample.listeners.SharedStatusListener;
import com.aheaditec.sample.activity.fragments.ContainerFragment;
import com.aheaditec.sample.activity.fragments.PinFragment;
import com.aheaditec.sample.model.ReaderModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * ViewModel of ApduActivity.
 */

public class ApduViewModel extends BaseViewModel implements ReaderStatusListener {
    private static final String TAG = ApduViewModel.class.getSimpleName();

    private ReaderModel readerModel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
    private StringBuilder outputString = new StringBuilder();

    private MutableLiveData<DialogFragment> dialogFragmentLiveData = new MutableLiveData<>();
    private MutableLiveData<String> outputLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressLiveData = new MutableLiveData<>();
    private MutableLiveData<SpinnerOptions> stateLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ContainerBasic>> containersLiveData = new MutableLiveData<>();
    private MutableLiveData<Pair<PinInfoBasic, PinEntryListener>> pinEntryLiveData = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> cardStateLiveData = new MutableLiveData<>();

    public ApduViewModel(@NonNull Application application) {
        super(application);
        // register reader state listener
        new SharedStatusListener().registerListener(this);
        // set up model class
        setUpModel();
        isCardInserted();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        readerModel.disconnect();
        readerModel.getAddToOutputObservable().unsubscribeOn(AndroidSchedulers.mainThread());
        readerModel.getApduStatesObservable().unsubscribeOn(AndroidSchedulers.mainThread());
        readerModel.getContainersObservable().unsubscribeOn(AndroidSchedulers.mainThread());
        readerModel.getPinEntryObservable().unsubscribeOn(AndroidSchedulers.mainThread());
        readerModel = null;
    }

    private void setUpModel() {
        if (getImplementation() == null) {
            throw new IllegalArgumentException("Reader implementation is null.");
        }
        this.readerModel = new ReaderModel(getImplementation());

        observerApduResponse();
        observerApduState();
        observerContainerList();
        observerPinEntry();
    }

    @Override
    public void onReaderConnected(@NonNull CardReader readerInstance) {
        // nothing to do here, onReaderConnected is handled in ConnectViewModel
    }

    @Override
    public void onReaderDisconnected(@NonNull Reader reader) {
        addToOutputString("Disconnected from " + reader.getNickname() + ".");
        stateLiveData.postValue(SpinnerOptions.DISCONNECTED);
    }

    @Override
    public void onCardRemoved() {
        cardStateLiveData.postValue(false);
        stateLiveData.postValue(SpinnerOptions.NO_CARD);
    }

    @Override
    public void onCardInserted() {
        cardStateLiveData.postValue(true);
        stateLiveData.postValue(SpinnerOptions.INITIALIZE_LIST);
    }

    @Override
    public void onCardUnknownState() {
        errorLiveData.postValue(new Exception("Unknown card inserted."));
        stateLiveData.postValue(SpinnerOptions.NO_CARD);
    }

    @Override
    public void onReaderNotFound() {
        // nothing to do here, onReaderConnected is handled in ConnectViewModel
    }

    @Override
    public void onError(@NonNull Exception e) {
        errorLiveData.postValue(e);
    }

    private void isCardInserted() {
        Disposable disposable = readerModel.getCardState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result != null && result) {
                        cardStateLiveData.postValue(true);
                        stateLiveData.postValue(SpinnerOptions.INITIALIZE_LIST);
                    } else {
                        cardStateLiveData.postValue(false);
                        stateLiveData.postValue(SpinnerOptions.NO_CARD);
                    }
                }, throwable -> errorLiveData.postValue(throwable));

        getCompositeDisposable().add(disposable);
    }

    public void getBatteryLvl() {
        Disposable disposable = readerModel.getBatteryLvl()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emitter -> addToOutputString("Battery lvl is: " + emitter),
                        throwable -> errorLiveData.postValue(throwable));

        getCompositeDisposable().add(disposable);
    }

    public void clearOutput() {
        outputString = new StringBuilder();
        outputLiveData.postValue(outputString.toString());
    }

    public void runApdu(TypedArray apduOptions, int position) {
        if (apduOptions == null) {
            return;
        }
        progressLiveData.postValue(true);

        switch (apduOptions.getResourceId(position, 0)) {
            case R.string.main_selector_init:
                readerModel.initializeApduSdk();
                break;
            case R.string.main_selector_cardId:
                readerModel.getCardId();
                break;
            case R.string.main_selector_containers:
                readerModel.getContainerList();
                break;
            case R.string.main_selector_pinVerify:
                showPinReferenceFragment(pinReference -> getPinInfo(pinReference,
                        password -> readerModel.verify(pinReference, password)));
                break;
            case R.string.main_selector_pinInfo:
                showPinReferenceFragment(pinReference -> getPinInfo(pinReference, null));
                break;
            case R.string.main_selector_sessionPin:
                showPinReferenceFragment(pinReference -> getPinInfo(pinReference,
                        password -> readerModel.generateSessionPin(pinReference, password)));
                break;
            case R.string.main_selector_unverify:
                showPinReferenceFragment(pinReference -> readerModel.unverify(pinReference));
                break;
            case R.string.main_selector_isVerified:
                showPinReferenceFragment(pinReference -> readerModel.isVerified(pinReference));
                break;
            case R.string.main_selector_publicKey:
                showContainerFragment(container -> readerModel.getPublicKey(container));
                break;
            case R.string.main_selector_certificate:
                showContainerFragment(container -> readerModel.getCertificate(container));
                break;
            case R.string.main_selector_signData:
                byte[] dataToSign = "Hello".getBytes();
                showContainerFragment(container -> readerModel.signData(container, dataToSign));
                break;
            case R.string.main_selector_reset:
                readerModel.finalizeApduSdk();
                break;
            default:
                Logger.e(TAG, apduOptions.getString(position) + " not found!");
                progressLiveData.postValue(false);
                break;
        }
    }

    private void addToOutputString(@NonNull String msg) {
        StringBuilder temp = new StringBuilder();
        temp.append(sdf.format(new Date())).append(": ").append(msg).append("\n");
        outputString.insert(0, temp);

        outputLiveData.postValue(outputString.toString());
    }

    private void observerApduResponse() {

        Disposable disposable = readerModel.getAddToOutputObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emitter -> {
                    addToOutputString(emitter);
                    progressLiveData.postValue(false);
                });

        getCompositeDisposable().add(disposable);
    }

    private void observerApduState() {

        Disposable disposable = readerModel.getApduStatesObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emitter -> stateLiveData.postValue(emitter));
        getCompositeDisposable().add(disposable);
    }

    private void observerContainerList() {

        Disposable disposable = readerModel.getContainersObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emitter -> containersLiveData.postValue(emitter));

        getCompositeDisposable().add(disposable);
    }

    private void observerPinEntry() {

        Disposable disposable = readerModel.getPinEntryObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> getPinInfo(res.first, res.second));

        getCompositeDisposable().add(disposable);
    }

    private void showContainerFragment(@Nullable ContainerFragment.OnContainerSelected listener) {
        if (listener == null) {
            dialogFragmentLiveData.postValue(null);
            return;
        }

        ContainerFragment fragment = ContainerFragment.newInstance(listener);
        dialogFragmentLiveData.postValue(fragment);
    }

    private void showPinReferenceFragment(@Nullable PinFragment.OnPinReferenceSelected listener) {
        if (listener == null) {
            dialogFragmentLiveData.postValue(null);
            return;
        }

        PinFragment fragment = PinFragment.newInstance(listener);
        dialogFragmentLiveData.postValue(fragment);
    }

    private void getPinInfo(@NonNull VerifyCodes pinReference, @Nullable PinEntryListener listener) {
        Disposable disposable = readerModel.getPinInfo(pinReference)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (listener != null) {
                        pinEntryLiveData.postValue(new Pair<>(result, listener));
                    } else {
                        addToOutputString("PinInfo: " + result.toString());
                        progressLiveData.postValue(false);
                    }
                });

        getCompositeDisposable().add(disposable);
    }

    public MutableLiveData<DialogFragment> getDialogFragmentLiveData() {
        return dialogFragmentLiveData;
    }

    public MutableLiveData<String> getOutputLiveData() {
        return outputLiveData;
    }

    public MutableLiveData<Boolean> getProgressLiveData() {
        return progressLiveData;
    }

    public MutableLiveData<SpinnerOptions> getStateLiveData() {
        return stateLiveData;
    }

    public MutableLiveData<List<ContainerBasic>> getContainersLiveData() {
        return containersLiveData;
    }

    public MutableLiveData<Pair<PinInfoBasic, PinEntryListener>> getPinEntryLiveData() {
        return pinEntryLiveData;
    }

    public MutableLiveData<Throwable> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getCardStateLiveData() {
        return cardStateLiveData;
    }
}