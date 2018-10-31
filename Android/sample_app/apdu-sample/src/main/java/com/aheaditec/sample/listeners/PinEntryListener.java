package com.aheaditec.sample.listeners;

/**
 * Callback used to send password from input to model class.
 */

public interface PinEntryListener {
    void onPinEntered(byte[] password);
}
