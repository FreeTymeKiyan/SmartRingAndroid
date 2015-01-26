package me.freetymekiyan.smartring;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MyPreferenceFragment extends PreferenceFragment {

    public MyPreferenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}
