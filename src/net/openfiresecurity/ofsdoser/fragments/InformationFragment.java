/*
 * Copyright (c) 2013. Alexander Martinz
 */

package net.openfiresecurity.ofsdoser.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.openfiresecurity.ofsdoser.R;

/**
 * InformationFragment, shows information about the Stress-Test
 */
public class InformationFragment extends Fragment {

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_information, container, false);

        return v;
    }
}
