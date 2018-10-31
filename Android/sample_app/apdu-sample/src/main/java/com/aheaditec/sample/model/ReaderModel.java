package com.aheaditec.sample.model;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.aheaditec.apdu.ApduProvider;

import com.aheaditec.apdu.callbacks.ByteArrayCallback;
import com.aheaditec.apdu.callbacks.CertificateCallback;
import com.aheaditec.apdu.callbacks.ContainerListCallback;
import com.aheaditec.apdu.callbacks.EmptyCallback;
import com.aheaditec.apdu.callbacks.InitializeCallback;
import com.aheaditec.apdu.callbacks.PinInfoCallback;
import com.aheaditec.apdu.callbacks.PublicKeyCallback;
import com.aheaditec.apdu.callbacks.SignatureCallback;
import com.aheaditec.apdu.callbacks.SignatureContinueCallback;
import com.aheaditec.apdu.callbacks.VerifyCallback;
import com.aheaditec.apdu.enums.AlgorithmID;
import com.aheaditec.apdu.enums.InternalErrors;
import com.aheaditec.apdu.enums.StatusWord;
import com.aheaditec.apdu.enums.VerifyCodes;
import com.aheaditec.apdu.structures.ContainerBasic;
import com.aheaditec.apdu.structures.PinInfoBasic;
import com.aheaditec.apdu.util.Utils;
import com.aheaditec.wrapper.interfaces.CardReader;
import com.aheaditec.wrapper.interfaces.OnBatteryLevelListener;
import com.aheaditec.wrapper.interfaces.OnCardStateListener;
import com.aheaditec.sample.enums.SpinnerOptions;
import com.aheaditec.sample.listeners.PinEntryListener;

import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * ReaderModel providing reader functionality with possibility
 * to send APDU commands to the card.
 */

public class ReaderModel implements InitializeCallback, ByteArrayCallback, VerifyCallback, EmptyCallback,
        CertificateCallback, PublicKeyCallback, ContainerListCallback {

    private CardReader readerInstance;
    private ApduProvider provider;
    private Pair<VerifyCodes, byte[]> sessionPin;
    private Subject<List<ContainerBasic>> containersObservable = PublishSubject.create();
    private Subject<String> addToOutputObservable = PublishSubject.create();
    private Subject<SpinnerOptions> apduStatesObservable = PublishSubject.create();
    private Subject<Pair<VerifyCodes, PinEntryListener>> pinEntryObservable = PublishSubject.create();

    public ReaderModel(CardReader readerInstance) {
        this.readerInstance = readerInstance;
    }

    public Single<Integer> getBatteryLvl() {

        return Single.<Integer>create(emitter -> readerInstance.getBatteryLevel(new OnBatteryLevelListener() {
            @Override
            public void onBatteryLevel(int i) {
                emitter.onSuccess(i);
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                emitter.onError(e);
            }
        })).subscribeOn(Schedulers.io());
    }

    public void disconnect() {
        if (readerInstance != null) {
            readerInstance.disconnectReader();
        }
    }

    public Single<Boolean> getCardState() {

        return Single.<Boolean>create(emitter -> readerInstance.isCardInserted(new OnCardStateListener() {
            @Override
            public void onCardInserted(boolean b) {
                emitter.onSuccess(b);
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                emitter.onError(e);
            }
        })).subscribeOn(Schedulers.io());
    }

    public void initializeApduSdk() {
        ApduProvider.initializeSession(readerInstance.getCardProvider(), this);
    }

    public void finalizeApduSdk() {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.finalizeSession(this);
    }

    public void getCardId() {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.getCardID(this);
    }

    public void verify(@NonNull VerifyCodes pinReference, @NonNull byte[] password) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.verify(pinReference, password, false, this);
    }

    public void generateSessionPin(@NonNull VerifyCodes pinReference, @NonNull byte[] password) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        sessionPin = new Pair<>(pinReference, null);
        provider.verify(pinReference, password, true, this);
    }

    public void verifyUsingSessionPin(@NonNull VerifyCodes pinReference) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        if (sessionPin == null || sessionPin.second == null || sessionPin.first == null ||
                !sessionPin.first.equals(pinReference)) {
            addToObserver("VerifyUsingSessionPin is not possible for given PIN object.");
            return;
        }

        provider.verifyUsingSessionPin(pinReference, sessionPin.second, this);
    }

    public Single<PinInfoBasic> getPinInfo(@NonNull VerifyCodes pinReference) {

        return Single.<PinInfoBasic>create( emitter -> {
            if (provider == null) {
                providerNotInitialized();
                return;
            }
            provider.getPinInfo(pinReference, new PinInfoCallback() {
                @Override
                public void onSuccess(@NonNull PinInfoBasic pinInfo) {
                    emitter.onSuccess(pinInfo);
                }

                @Override
                public void onFail(@NonNull StatusWord statusWord) {
                    addToObserver("Operation failed with statusWord: " + statusWord);
                }

                @Override
                public void onInternalError(@NonNull InternalErrors error) {
                    addToObserver("Operation failed with error: " + error.name());
                }
            });
        }).subscribeOn(Schedulers.io());
    }

    public void isVerified(@NonNull VerifyCodes pinReference) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.isVerified(pinReference, this);
    }

    public void unverify(@NonNull VerifyCodes pinReference) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.unverify(pinReference, this);
    }

    public void getContainerList() {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.getContainerList(this);
    }

    public void getPublicKey(@NonNull ContainerBasic container) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.getPublicKey(container, this);
    }

    public void getCertificate(@NonNull ContainerBasic container) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        provider.getCertificate(container, this);
    }

    public void signData(@NonNull ContainerBasic container, @NonNull byte[] data) {
        if (provider == null) {
            providerNotInitialized();
            return;
        }
        // algorithm is hardcoded in case of demo app
        AlgorithmID algId = container.getKeyType().isElliptic() ? AlgorithmID.ECDSA_SHA512
                : AlgorithmID.RSA_SHA512_PKCS1_PADDING;

        provider.signData(container, data, algId, new SignatureCallback() {
            @Override
            public void onSuccess(@NonNull byte[] data) {
                ReaderModel.this.onSuccess(data);
            }

            @Override
            public void onVerificationNeeded(@NonNull VerifyCodes pinReference, @NonNull SignatureContinueCallback callback) {
                pinEntryObservable.onNext(new Pair<>(pinReference, password -> provider.verify(pinReference, password, false, new VerifyCallback() {
                    @Override
                    public void onVerified() {
                        callback.onVerificationDone();
                    }

                    @Override
                    public void onSessionPinGenerated(@NonNull byte[] sessionPin) {
                        // cannot happen here
                    }

                    @Override
                    public void onFail(@NonNull StatusWord statusWord) {
                        ReaderModel.this.onFail(statusWord);
                    }

                    @Override
                    public void onInternalError(@NonNull InternalErrors error) {
                        ReaderModel.this.onInternalError(error);
                    }
                })));
            }

            @Override
            public void onFail(@NonNull StatusWord statusWord) {
                ReaderModel.this.onFail(statusWord);
            }

            @Override
            public void onInternalError(@NonNull InternalErrors error) {
                ReaderModel.this.onInternalError(error);
            }
        });
    }

    private void providerNotInitialized() {
        apduStatesObservable.onNext(SpinnerOptions.NO_CARD);
        addToObserver("ApduProvider is not initialized.");
    }

    @Override
    public void onSuccess(@NonNull ApduProvider provider) {
        this.provider = provider;
        addToObserver("ApduProvider successfully initialized.");
        apduStatesObservable.onNext(SpinnerOptions.NO_CONTAINERS_LIST);
    }

    @Override
    public void onSuccess() {
        addToObserver("Operation succeeded.");
    }

    @Override
    public void onSuccess(@NonNull byte[] data) {
        addToObserver("Operation succeeded. Data: " + Utils.bytes2Hex(data));
    }

    @Override
    public void onVerified() {
        addToObserver("User successfully verified.");
    }

    @Override
    public void onSessionPinGenerated(@NonNull byte[] sessionPin) {
        if (this.sessionPin != null && this.sessionPin.first != null && this.sessionPin.second == null) {
            addToObserver("SessionPin generated: " + Utils.bytes2Hex(sessionPin));
            this.sessionPin = new Pair<>(this.sessionPin.first, sessionPin);
            return;
        }
        this.sessionPin = null;
        addToObserver("Error during generating sessionPin - inconsistent states.");
    }

    @Override
    public void onSuccess(@NonNull List<ContainerBasic> containers) {
        containersObservable.onNext(containers);
        addToObserver("Containers successfully retrieved.");
        apduStatesObservable.onNext(SpinnerOptions.COMPLETE_LIST);
    }

    @Override
    public void onSuccess(@NonNull PublicKey publicKey) {
        addToObserver(publicKey.toString());
    }

    @Override
    public void onSuccess(@NonNull Certificate certificate) {
        addToObserver(certificate.toString());
    }

    @Override
    public void onFail(@NonNull StatusWord statusWord) {
        addToObserver("Operation failed with statusWord: " + statusWord);
    }

    @Override
    public void onInternalError(@NonNull InternalErrors error) {
        addToObserver("Operation failed with error: " + error.name());
    }

    private void addToObserver(@NonNull String msg) {
        addToOutputObservable.onNext(msg);
    }



    public Observable<String> getAddToOutputObservable() {
        return addToOutputObservable.observeOn(Schedulers.io());
    }

    public Observable<SpinnerOptions> getApduStatesObservable() {
        return apduStatesObservable.observeOn(Schedulers.io());
    }

    public Observable<List<ContainerBasic>> getContainersObservable() {
        return containersObservable.observeOn(Schedulers.io());
    }

    public Observable<Pair<VerifyCodes, PinEntryListener>> getPinEntryObservable() {
        return pinEntryObservable.observeOn(Schedulers.io());
    }
}
