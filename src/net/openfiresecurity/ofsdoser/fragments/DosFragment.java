package net.openfiresecurity.ofsdoser.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import net.openfiresecurity.ofsdoser.R;
import net.openfiresecurity.ofsdoser.services.DosService;

/**
 * Created by alex on 13.11.13.
 */
public class DosFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private RadioButton rbJava;
    private ToggleButton tb;
    private ProgressBar cpb;
    private EditText etTarget;
    private SeekBar sbThreads, sbPacketSize;
    private TextView tvPacketSize, tvThreads;
    private Toast mToast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doser, container, false);

        tb = (ToggleButton) v.findViewById(R.id.tbHashDos);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (checkArguments()) {
                    if (arg1) {
                        makeToast("DoS Initiated!");
                        cpb.setVisibility(View.VISIBLE);
                        startThread();
                    } else {
                        makeToast("DoS Stopped!");
                        cpb.setVisibility(View.INVISIBLE);
                        stopThread();
                    }
                } else {
                    makeToast("Please recheck your Settings again.\nCouldn't start the DoS.");
                    tb.setChecked(false);
                }
            }

            private boolean checkArguments() {
                boolean valid = true;
                String check;
                check = etTarget.getText().toString();
                if (!(check.contains("."))) {
                    valid = false;
                }
                return (valid);
            }
        });

        rbJava = (RadioButton) v.findViewById(R.id.radio1);
        cpb = (ProgressBar) v.findViewById(R.id.cbpHash);
        cpb.setVisibility(View.INVISIBLE);
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

        return v;
    }

    private void startThread() {
        Intent i = new Intent(getActivity(), DosService.class);
        i.putExtra(DosService.BUNDLE_THREADS, sbThreads.getProgress() + 1);
        i.putExtra(DosService.BUNDLE_PACKETSIZE, sbPacketSize.getProgress() + 1);
        i.putExtra(DosService.BUNDLE_JAVA, rbJava.isChecked());
        i.putExtra(DosService.BUNDLE_HOST, etTarget.getText().toString());
        getActivity().startService(i);
    }

    private void stopThread() {
        getActivity().startService(new Intent(getActivity(), DosService.class));
    }

    private void makeToast(String msg) {
        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
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
