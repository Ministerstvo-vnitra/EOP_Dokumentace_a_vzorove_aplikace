package com.aheaditec.sample;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Utility logger class (Logcat logs).
 */

public class Logger {
    private static final String TAG = Logger.class.getSimpleName();

    private static LogLevel logLevel;

    public static void init(@NonNull LogLevel logLevel) {
        Logger.logLevel = logLevel;
    }

    /**
     * Logs exception
     */
    private static void logException(Exception exception) {
        // also log message
        logWarning(TAG, exception.getMessage());
        exception.printStackTrace();
    }

    /**
     * Logs verbose message
     */
    private static void logVerbose(String tag, String log) {
        if (!logLevel.shouldLog(LogLevel.VERBOSE)) {
            return;
        }

        Log.v(tag, log);
    }

    /**
     * Logs debug message
     */
    private static void logDebug(String tag, String log) {
        if (!logLevel.shouldLog(LogLevel.DEBUG)) {
            return;
        }

        Log.d(tag, log);
    }

    /**
     * Logs warning message
     */
    private static void logWarning(String tag, String log) {
        if (!logLevel.shouldLog(LogLevel.WARNING)) {
            return;
        }

        Log.w(tag, log);
    }

    /**
     * Logs error message
     */
    private static void logError(String tag, String log) {
        if (!logLevel.shouldLog(LogLevel.ERROR)) {
            return;
        }

        Log.e(tag, log);
    }

    /**
     * Shortcut for method logException
     */
    public static void x(Exception exception) {
        logException(exception);
    }

    /**
     * Shortcut for method logError
     */
    public static void e(String tag, String log) {
        logError(tag, log);
    }

    /**
     * Shortcut for method logDebug
     */
    public static void d(String tag, String log) {
        logDebug(tag, log);
    }

    /**
     * Shortcut for method logVerbose
     */
    public static void v(String tag, String log) {
        logVerbose(tag, log);
    }

    /**
     * Shortcut for method logWarning
     */
    public static void w(String tag, String log) {
        logWarning(tag, log);
    }


    public enum LogLevel {
        OFF, ERROR, WARNING, DEBUG, VERBOSE;

        boolean shouldLog(LogLevel logLevel) {
            return this.compareTo(logLevel) > 0;
        }
    }
}
