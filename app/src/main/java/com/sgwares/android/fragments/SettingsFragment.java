package com.sgwares.android.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgwares.android.R;
import com.sgwares.android.models.User;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREF_KEY_NAME = "pref_key_name";
    public static final String PREF_KEY_COLOUR = "pref_key_colour";
    private static final String TAG = "SettingsFragment";
    private DatabaseReference mUserRef;
    private User mUser;
    private EditTextPreference mNamePref;
    private EditTextPreference mColourPref;
    private ValueEventListener mUserListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mNamePref = (EditTextPreference) findPreference(PREF_KEY_NAME);
        mColourPref = (EditTextPreference) findPreference(PREF_KEY_COLOUR);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        mUserRef = usersRef.child(auth.getCurrentUser().getUid());
        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                mUser = dataSnapshot.getValue(User.class);
                mNamePref.setText(mUser.getName());
                mColourPref.setText(mUser.getColour());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
        mUserRef.addValueEventListener(mUserListener);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: " + key);

        if (PREF_KEY_NAME.equals(key)) {
            mUser.setName(mNamePref.getEditText().getText().toString());
            mUserRef.setValue(mUser);
        } else if (PREF_KEY_COLOUR.equals(key)) {
            mUser.setColour(mColourPref.getEditText().getText().toString());
            mUserRef.setValue(mUser);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        if ((mUserRef != null) && (mUserListener != null)) {
            mUserRef.removeEventListener(mUserListener);
        }
        super.onPause();
    }

}
