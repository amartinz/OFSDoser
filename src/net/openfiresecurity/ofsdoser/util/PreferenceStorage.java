package net.openfiresecurity.ofsdoser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by alex on 18.11.13.
 */
public class PreferenceStorage {

    //=================
    private static PreferenceStorage prefStorage;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    //=================
    // Fields
    //=================
    public static int DOS_TIMEOUT = 1000;
    public static boolean EXTENSIVE_LOGGING = false;
    public static boolean INFORMATION_UPDATE = false;
    //=================
    // Keys
    //=================
    public static final String PREF_DOS_TIMEOUT = "dos_timeout";
    public static final String PREF_EXTENSIVE_LOGGING = "pref_extensive_logging";
    public static final String PREF_INFORMATION_UPDATE = "pref_information_update";

    private PreferenceStorage(Context context) {
        PreferenceStorage.prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        PreferenceStorage.editor = PreferenceStorage.prefs.edit();

        EXTENSIVE_LOGGING = prefs.getBoolean(PREF_EXTENSIVE_LOGGING, false);
        DOS_TIMEOUT = Integer.parseInt(prefs.getString(PREF_DOS_TIMEOUT, "1000"));
        INFORMATION_UPDATE = prefs.getBoolean(PREF_INFORMATION_UPDATE, false);
    }

    public static void setPreference(String key, Object value) {
        switch (key) {
            case PREF_EXTENSIVE_LOGGING:
                EXTENSIVE_LOGGING = (boolean) value;
                return;
            case PREF_INFORMATION_UPDATE:
                INFORMATION_UPDATE = (boolean) value;
                return;
            case PREF_DOS_TIMEOUT:
                DOS_TIMEOUT = Integer.parseInt(value.toString());
                return;
            default:
                break;
        }
    }

    public static PreferenceStorage getInstance(Context context) {
        if (prefStorage == null) {
            prefStorage = new PreferenceStorage(context);
        }
        return prefStorage;
    }

}
