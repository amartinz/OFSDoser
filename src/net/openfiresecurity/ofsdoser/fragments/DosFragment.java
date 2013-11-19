package net.openfiresecurity.ofsdoser.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import net.openfiresecurity.ofsdoser.MainActivity;
import net.openfiresecurity.ofsdoser.R;
import net.openfiresecurity.ofsdoser.services.DosService;
import net.openfiresecurity.ofsdoser.util.PreferenceStorage;
import net.openfiresecurity.ofsdoser.util.asynctasks.TargetValidator;

/**
 * Created by alex on 13.11.13.
 */
public class DosFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private RadioButton rbJava;
    private ToggleButton tb;
    private EditText etTarget;
    private SeekBar sbThreads, sbPacketSize;
    private TextView tvPacketSize, tvThreads;
    private Toast mToast;
    private boolean mRunning = false;

    private void logDebug(String msg) {
        if (PreferenceStorage.EXTENSIVE_LOGGING) {
            Log.e("OFSDOSER", msg);
        }
    }

    private void checkArguments() {
        if (etTarget.getText() != null) {
            logDebug("Target not null");
            if (!(etTarget.getText().toString().length() == 0)) {
                String tmp = etTarget.getText().toString().trim();
                tmp = tmp.replace("http://", "").replace("/", "");
                new TargetValidator(this).execute(tmp);
            } else {
                makeToast("Please enter a valid Target!");
            }
        }
    }

    public void setValidationResult(String result) {
        String[] parts = result.split("\\|");
        boolean isValid = parts[0].equals("1");
        final String mResult = parts[1];
        if (isValid) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Success");
            dialog.setMessage("Target\n" + mResult + "\nis reachable!\n\nStart Stress-Test now?");
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    etTarget.setText(mResult);
                    startThread();
                    tb.setChecked(true);
                    mRunning = true;
                }
            });
            dialog.show();
        } else {
            makeToast("Target not reachable!");
            tb.setChecked(false);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopThread();
        makeToast("Running out of Memory! Lower Threads!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doser, container, false);

        tb = (ToggleButton) v.findViewById(R.id.tbHashDos);
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRunning) {
                    tb.setChecked(false);
                    checkArguments();
                } else {
                    stopThread();
                    mRunning = false;
                }
            }
        });

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

        return v;
    }

    private void startThread() {
        final String mTarget = (etTarget.getText() != null ? etTarget.getText().toString() : "");
        PreferenceStorage.setPreference(PreferenceStorage.PREF_LAST_TARGET, mTarget);
        makeToast("Stress-Test Initiated!");
        ((MainActivity) getActivity()).mProgress.setVisibility(View.VISIBLE);
        Intent i = new Intent(getActivity(), DosService.class);
        i.putExtra(DosService.BUNDLE_THREADS, sbThreads.getProgress() + 1);
        i.putExtra(DosService.BUNDLE_PACKETSIZE, sbPacketSize.getProgress() + 1);
        i.putExtra(DosService.BUNDLE_JAVA, rbJava.isChecked());
        i.putExtra(DosService.BUNDLE_HOST, mTarget);
        getActivity().startService(i);
    }

    private void stopThread() {
        makeToast("Stress-Test Stopped!");
        ((MainActivity) getActivity()).mProgress.setVisibility(View.INVISIBLE);
        getActivity().startService(new Intent(getActivity(), DosService.class));
    }

    private void makeToast(String msg) {
        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        mToast.show();
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
