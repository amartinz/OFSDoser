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
 * <p/>
 * UNUSED FOR NOW
 */
public class InformationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information, container, false);
    }
}
