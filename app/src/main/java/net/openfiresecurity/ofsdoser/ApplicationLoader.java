package net.openfiresecurity.ofsdoser;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import net.openfiresecurity.ofsdoser.util.PreferenceStorage;

public class ApplicationLoader extends Application {

    public static Context sApplicationContext;

    private static boolean mDebug = false;

    @Override
    public void onCreate() {
        super.onCreate();

        sApplicationContext = this;
        PreferenceStorage.getInstance(sApplicationContext);

        mDebug = PreferenceStorage.EXTENSIVE_LOGGING;
        logDebug("Extensive Logging: " + (mDebug ? "enabled" : "disabled"));
    }

    public static void logDebug(String msg) {
        if (mDebug) {
            Log.e("STRESSTESTER", msg);
        }
    }

}
