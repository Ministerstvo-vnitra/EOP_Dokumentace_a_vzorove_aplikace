package com.aheaditec.sample.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aheaditec.wrapper.interfaces.CardReader;

import io.reactivex.disposables.CompositeDisposable;

/**
 * BaseViewModel handling states of reader.
 */

public class BaseViewModel extends AndroidViewModel {

    private CompositeDisposable compositeDisposable;

    // static fields for sharing necessary attributes between view models
    private static CardReader implementation;

    BaseViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
    }

    @NonNull
    CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Nullable
    static CardReader getImplementation() {
        return implementation;
    }

    static void setImplementation(@Nullable CardReader implementation) {
        BaseViewModel.implementation = implementation;
    }
}