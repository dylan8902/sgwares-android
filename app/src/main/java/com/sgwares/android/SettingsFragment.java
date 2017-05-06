package com.sgwares.android;


import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by dylan8902 on 06/05/2017.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}
