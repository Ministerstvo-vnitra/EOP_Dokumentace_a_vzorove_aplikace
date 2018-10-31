package com.aheaditec.sample.viewmodel.architecture;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.aheaditec.sample.enums.ReaderType;
import com.aheaditec.sample.viewmodel.PairingViewModel;
import com.aheaditec.sample.viewmodel.ConnectViewModel;

/**
 * Factory for ViewModels adding readerType type parameter into constructor.
 */

public class ReaderViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application application;
    private ReaderType readerType;

    public ReaderViewModelFactory(@NonNull Application application,
                                  @NonNull ReaderType readerType) {
        this.application = application;
        this.readerType = readerType;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.equals(PairingViewModel.class)) {
            return (T) new PairingViewModel(application, readerType);
        } else {
            return (T) new ConnectViewModel(application, readerType);
        }
    }
}
