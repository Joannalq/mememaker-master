package com.teamtreehouse.mememaker.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.teamtreehouse.mememaker.R;

public class MemeSettingsFragment extends PreferenceFragment {
    //load preferences resource file

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
