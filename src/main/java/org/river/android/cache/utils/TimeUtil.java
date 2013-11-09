package org.river.android.cache.utils;

/**
 * Utilities for converting times
 * @author River
 */
public class TimeUtil {

    /**
     * Constant that contains the amount of milliseconds in a second
     */
    static final long ONE_SECOND = 1000L;

    /**
     * Converts milliseconds to seconds
     * @param timeInMillis
     * @return The equivalent time in seconds
     */
    public static int toSecs(long timeInMillis) {
        return (int)Math.ceil((double)timeInMillis / ONE_SECOND);
    }

    /**
     * Converts seconds to milliseconds, with a precision of 1 second
     * @param timeInSecs the time in seconds
     * @return The equivalent time in milliseconds
     */
    public static long toMillis(int timeInSecs) {
        return timeInSecs * ONE_SECOND;
    }
}
