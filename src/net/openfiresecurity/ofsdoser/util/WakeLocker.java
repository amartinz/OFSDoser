/*
 * Copyright (c) 2013. Alexander Martinz.
 */

package net.openfiresecurity.ofsdoser.util;

import android.content.Context;
import android.os.PowerManager;

public abstract class WakeLocker {

    private static PowerManager.WakeLock wakeLock;

    public static void acquireFull(Context context) {
        if (wakeLock != null) {
            wakeLock.release();
        }

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE
                , "WakeLocker");
        wakeLock.acquire();
    }

    public static void acquirePartial(Context context) {
        if (wakeLock != null) {
            wakeLock.release();
        }

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLocker");
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = null;
    }
}
