/*
 * Copyright (c) 2013. Alexander Martinz
 */

package net.openfiresecurity.ofsdoser.fragments;

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

import net.openfiresecurity.ofsdoser.MainActivity;
import net.openfiresecurity.ofsdoser.R;
import net.openfiresecurity.ofsdoser.services.DosService;
import net.openfiresecurity.ofsdoser.util.PreferenceStorage;
import net.openfiresecurity.ofsdoser.util.asynctasks.TargetValidator;

/**
 * DoSFragment, contains the interface for editing and starting the Stress-Test.
 */
public class DosFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private RadioButton rbJava;
    private EditText etTarget;
    private SeekBar sbThreads, sbPacketSize;
    private TextView tvPacketSize, tvThreads, mTimeout, mCounter;
    private View v;
    private boolean mRunning = false;

    private void logDebug(String msg) {
        ((MainActivity) getActivity()).logDebug(msg);
    }

    private void checkArguments() {
        if (etTarget.getText() != null) {
            logDebug("Target not null");
            if (!(etTarget.getText().toString().length() == 0)) {
                String tmp = etTarget.getText().toString().trim();
                tmp = tmp.replace("http://", "").replace("/", "");
                new TargetValidator(this).execute(tmp);
            } else {
                ((MainActivity) getActivity()).makeToast(getString(R.string.warning_valid_target));
            }
        }
    }

    public void setValidationResult(String result) {
        String[] parts = result.split("\\|");
        boolean isValid = parts[0].equals("1");
        final String mResult = parts[1];
        if (isValid) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(getString(R.string.dialog_success));
            dialog.setMessage(getString(R.string.info_target_reachable_full, mResult));
            dialog.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    etTarget.setText(mResult);
                    ((MainActivity) getActivity()).toggleStartVisibility(false);
                    mRunning = true;
                    startThread();
                }
            });
            dialog.show();
        } else {
            ((MainActivity) getActivity()).makeToast(getString(R.string.info_target_not_reachable));
            ((MainActivity) getActivity()).toggleStartVisibility(true);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopThread();
        ((MainActivity) getActivity()).makeToast(getString(R.string.warning_oom));
    }

    public void toggleDos() {
        if (!mRunning) {
            checkArguments();
        } else {
            stopThread();
            mRunning = false;
            ((MainActivity) getActivity()).toggleStartVisibility(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_doser, container, false);

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

        etTarget.setText(PreferenceStorage.LAST_TARGET);
        sbThreads.setProgress(PreferenceStorage.LAST_THREADS);
        sbPacketSize.setProgress(PreferenceStorage.LAST_PACKETSIZE);

        // Information
        mTimeout = (TextView) v.findViewById(R.id.tvTimeout);
        mTimeout.setText(getString(R.string.info_timeout, PreferenceStorage.DOS_TIMEOUT));

        mCounter = (TextView) v.findViewById(R.id.tvCounter);
        mCounter.setText(getString(R.string.info_counter, "0"));

        return v;
    }

    public void updateProgress(final int mCount) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mCounter != null && v != null) {
                        mCounter.setText(getString(R.string.info_counter, mCount));
                        v.invalidate();
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    public void update() {
        // Update Timeout Information
        mTimeout.setText(getString(R.string.info_timeout, PreferenceStorage.DOS_TIMEOUT));
    }

    private void startThread() {
        final String mTarget = (etTarget.getText() != null ? etTarget.getText().toString() : "");
        final int mThreads = sbThreads.getProgress() + 1;
        final int mPacketSize = sbPacketSize.getProgress() + 1;
        PreferenceStorage.setPreference(PreferenceStorage.PREF_LAST_TARGET, mTarget);
        PreferenceStorage.setPreference(PreferenceStorage.PREF_LAST_THREADS, mThreads);
        PreferenceStorage.setPreference(PreferenceStorage.PREF_LAST_PACKETSIZE, mPacketSize);
        ((MainActivity) getActivity()).makeToast(getString(R.string.info_stress_test_started));
        Intent i = new Intent(getActivity(), DosService.class);
        i.putExtra(DosService.BUNDLE_THREADS, mThreads)
                .putExtra(DosService.BUNDLE_PACKETSIZE, mPacketSize)
                .putExtra(DosService.BUNDLE_JAVA, rbJava.isChecked())
                .putExtra(DosService.BUNDLE_HOST, mTarget);
        getActivity().startService(i);
    }

    private void stopThread() {
        ((MainActivity) getActivity()).makeToast(getString(R.string.info_stress_test_stopped));
        getActivity().startService(new Intent(getActivity(), DosService.class));
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
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
