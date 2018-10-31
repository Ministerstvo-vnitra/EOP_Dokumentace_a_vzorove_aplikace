package com.aheaditec.sample.listeners;

import android.support.annotation.NonNull;

import com.aheaditec.wrapper.Reader;
import com.aheaditec.wrapper.interfaces.CardReader;
import com.aheaditec.wrapper.interfaces.ReaderStatusListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Signal reader status event in all subscribed listeners.
 */

public class SharedStatusListener implements ReaderStatusListener {
    private static List<ReaderStatusListener> listeners = new ArrayList<>();

    public void registerListener(@NonNull ReaderStatusListener listener) {
        listeners.add(listener);
    }

    @SuppressWarnings("unused")
    public void unregisterListener(@NonNull ReaderStatusListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onReaderConnected(@NonNull CardReader readerInstance) {
        for (ReaderStatusListener listener : listeners) {
            listener.onReaderConnected(readerInstance);
        }
    }

    @Override
    public void onReaderDisconnected(@NonNull Reader reader) {
        for (ReaderStatusListener listener : listeners) {
            listener.onReaderDisconnected(reader);
        }
    }

    @Override
    public void onCardRemoved() {
        for (ReaderStatusListener listener : listeners) {
            listener.onCardRemoved();
        }
    }

    @Override
    public void onCardInserted() {
        for (ReaderStatusListener listener : listeners) {
            listener.onCardInserted();
        }
    }

    @Override
    public void onReaderNotFound() {
        for (ReaderStatusListener listener : listeners) {
            listener.onReaderNotFound();
        }
    }

    @Override
    public void onCardUnknownState() {
        for (ReaderStatusListener listener : listeners) {
            listener.onCardUnknownState();
        }
    }

    @Override
    public void onError(@NonNull Exception e) {
        for (ReaderStatusListener listener : listeners) {
            listener.onError(e);
        }
    }
}
