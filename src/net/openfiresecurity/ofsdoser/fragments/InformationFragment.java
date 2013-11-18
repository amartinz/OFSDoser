package net.openfiresecurity.ofsdoser.fragments;

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

    private TextView mTimeout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_information, container, false);

        mTimeout = (TextView) v.findViewById(R.id.tvTimeout);
        mTimeout.setText(getString(R.string.info_timeout, PreferenceStorage.DOS_TIMEOUT));

        return v;
    }

    public void update() {
        // Update Timeout Information
        mTimeout.setText(getString(R.string.info_timeout, PreferenceStorage.DOS_TIMEOUT));
    }
}
