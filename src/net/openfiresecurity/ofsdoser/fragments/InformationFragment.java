package net.openfiresecurity.ofsdoser.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.openfiresecurity.ofsdoser.R;
import net.openfiresecurity.ofsdoser.util.PreferenceStorage;

/**
 * Created by alex on 13.11.13.
 */
public class InformationFragment extends Fragment {

    private View v;
    private TextView mTimeout, mCounter;
    private final int[] mColors = {Color.WHITE, Color.BLUE, Color.YELLOW, Color.rgb(255, 120, 0), Color.RED, Color.rgb(50, 50, 50), Color.GRAY, Color.rgb(120, 120, 120)};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_information, container, false);

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
                mCounter.setText(getString(R.string.info_counter, mCount));
                v.invalidate();
            }
        });
    }

    public void update() {
        // Update Timeout Information
        mTimeout.setText(getString(R.string.info_timeout, PreferenceStorage.DOS_TIMEOUT));
    }
}
