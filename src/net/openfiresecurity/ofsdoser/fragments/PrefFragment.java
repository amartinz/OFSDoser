/*
 * Copyright (c) 2013. Alexander Martinz
 */

package net.openfiresecurity.ofsdoser.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import net.openfiresecurity.ofsdoser.R;
import net.openfiresecurity.ofsdoser.util.PreferenceStorage;
import net.openfiresecurity.ofsdoser.widgets.preferences.EditTextIntegerPreference;

public class PrefFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private EditTextIntegerPreference mTimeout;
    private CheckBoxPreference mDebug;
    private CheckBoxPreference mInformationshow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        mDebug = (CheckBoxPreference) findPreference(PreferenceStorage.PREF_EXTENSIVE_LOGGING);
        mDebug.setOnPreferenceChangeListener(this);

        mInformationshow = (CheckBoxPreference) findPreference(PreferenceStorage.PREF_INFORMATION_UPDATE);
        mInformationshow.setOnPreferenceChangeListener(this);

        mTimeout = (EditTextIntegerPreference) findPreference(PreferenceStorage.PREF_DOS_TIMEOUT);
        mTimeout.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean changed = false;

        if (preference == mDebug) {
            PreferenceStorage.setPreference(PreferenceStorage.PREF_EXTENSIVE_LOGGING, newValue);
            changed = true;
        } else if (preference == mInformationshow) {
            PreferenceStorage.setPreference(PreferenceStorage.PREF_INFORMATION_UPDATE, newValue);
            changed = true;
        } else if (preference == mTimeout) {
            if (newValue.equals(""))
                newValue = "1000";
            PreferenceStorage.setPreference(PreferenceStorage.PREF_DOS_TIMEOUT, newValue);
            changed = true;
        }

        return changed;
    }
}
