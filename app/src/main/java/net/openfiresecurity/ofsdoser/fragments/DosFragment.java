/*
 * Copyright (c) 2013. Alexander Martinz
 */

package net.openfiresecurity.ofsdoser.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import net.openfiresecurity.ofsdoser.R;
import net.openfiresecurity.ofsdoser.events.ScheduleEvent;
import net.openfiresecurity.ofsdoser.events.ToastEvent;
import net.openfiresecurity.ofsdoser.events.VisibilityEvent;
import net.openfiresecurity.ofsdoser.services.DosService;
import net.openfiresecurity.ofsdoser.util.BusProvider;
import net.openfiresecurity.ofsdoser.util.PreferenceStorage;
import net.openfiresecurity.ofsdoser.util.asynctasks.TargetValidator;

import static net.openfiresecurity.ofsdoser.ApplicationLoader.logDebug;

/**
 * DoSFragment, contains the interface for editing and starting the Stress-Test.
 */
public class DosFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private RadioButton rbJava;
    private EditText    etTarget;
    private SeekBar     sbThreads;
    private SeekBar     sbPacketSize;
    private TextView    tvPacketSize;
    private TextView    tvThreads;

    private boolean mRunning = false;

    private void makeToast(final int msgId) {
        BusProvider.getBus().post(new ToastEvent(msgId));
    }

    private void checkArguments() {
        if (etTarget.getText() != null) {
            logDebug("Target not null");
            if (!(etTarget.getText().toString().length() == 0)) {
                String tmp = etTarget.getText().toString().trim();
                tmp = tmp.replace("http://", "").replace("/", "");
                new TargetValidator(this).execute(tmp);
            } else {
                makeToast(R.string.warning_valid_target);
            }
        }
    }

    public void setValidationResult(String result) {
        final String[] parts = result.split("\\|");
        final boolean isValid = parts[0].equals("1");
        final String mResult = parts[1];
        if (isValid) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(getString(R.string.dialog_success));
            dialog.setMessage(getString(R.string.info_target_reachable_full, mResult));
            dialog.setNegativeButton(getString(R.string.dialog_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }
            );
            dialog.setPositiveButton(getString(R.string.dialog_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            etTarget.setText(mResult);
                            BusProvider.getBus().post(new VisibilityEvent(false));
                            mRunning = true;
                            startThread();
                        }
                    }
            );
            dialog.show();
        } else {
            makeToast(R.string.info_target_not_reachable);
            BusProvider.getBus().post(new VisibilityEvent(true));
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopThread(false);
    }

    public void toggleDos() {
        if (!mRunning) {
            checkArguments();
        } else {
            stopThread(false);
            mRunning = false;
            BusProvider.getBus().post(new VisibilityEvent(true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_doser, container, false);

        rbJava = (RadioButton) v.findViewById(R.id.radio1);
        tvPacketSize = (TextView) v.findViewById(R.id.tvPacketSize);
        tvThreads = (TextView) v.findViewById(R.id.tvThreads);
        etTarget = (EditText) v.findViewById(R.id.etHashdosTarget);
        sbPacketSize = (SeekBar) v.findViewById(R.id.sbHashPacketSize);
        sbThreads = (SeekBar) v.findViewById(R.id.sbHashThreads);
        sbPacketSize.setMax(1024 - 1);
        sbThreads.setMax(512 - 1);
        sbPacketSize.setProgress(100);
        sbThreads.setProgress(1);
        tvPacketSize.setText("100");
        tvThreads.setText("1");

        sbPacketSize.setOnSeekBarChangeListener(this);
        sbThreads.setOnSeekBarChangeListener(this);

        PreferenceStorage.getInstance(getActivity());
        etTarget.setText(PreferenceStorage.LAST_TARGET);
        sbThreads.setProgress(PreferenceStorage.LAST_THREADS);
        sbPacketSize.setProgress(PreferenceStorage.LAST_PACKETSIZE);

        return v;
    }

    private void startThread() {
        final Activity activity = getActivity();

        final String mTarget = (etTarget.getText() != null ? etTarget.getText().toString() : "");
        final int mThreads = sbThreads.getProgress() + 1;
        final int mPacketSize = sbPacketSize.getProgress() + 1;

        PreferenceStorage.getInstance(activity);
        PreferenceStorage.setPreference(PreferenceStorage.PREF_LAST_TARGET, mTarget);
        PreferenceStorage.setPreference(PreferenceStorage.PREF_LAST_THREADS, mThreads);
        PreferenceStorage.setPreference(PreferenceStorage.PREF_LAST_PACKETSIZE, mPacketSize);

        makeToast(R.string.info_stress_test_started);

        final Intent i = new Intent(activity, DosService.class);
        i.putExtra(DosService.BUNDLE_THREADS, mThreads)
                .putExtra(DosService.BUNDLE_PACKETSIZE, mPacketSize)
                .putExtra(DosService.BUNDLE_JAVA, rbJava.isChecked())
                .putExtra(DosService.BUNDLE_HOST, mTarget);
        activity.startService(i);

        BusProvider.getBus().post(new ScheduleEvent(true));
    }

    private void stopThread(final boolean lowMemory) {
        final Activity activity = getActivity();

        if (lowMemory) {
            makeToast(R.string.warning_oom);
        } else {
            makeToast(R.string.info_stress_test_stopped);
        }
        activity.startService(new Intent(activity, DosService.class));

        BusProvider.getBus().post(new ScheduleEvent(false));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (seekBar == sbPacketSize) {
            tvPacketSize.setText("" + (progress + 1));
        } else if (seekBar == sbThreads) {
            tvThreads.setText("" + (progress + 1));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
}
