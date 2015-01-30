package me.freetymekiyan.smartring.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.support.v4.preference.PreferenceFragment;

import me.freetymekiyan.smartring.R;

;

public class MyPreferenceFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public MyPreferenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference pref = getPreferenceScreen().getPreference(i);
            if (pref instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) pref;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); j++) {
                    updatePreference(preferenceGroup.getPreference(j));
                }
            } else {
                updatePreference(pref);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key));
    }

    void updatePreference(Preference pref) {
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            listPref.setSummary(listPref.getEntry() != null ? listPref.getEntry() : getString(R.string.daily));
        }
        if (pref instanceof EditTextPreference) {
            String title = pref.getTitle().toString();
            String text = ((EditTextPreference) pref).getText();
            if (title.equals(getString(R.string.name))) {
                pref.setSummary(
                        (text == null || text.isEmpty()) ? getString(R.string.name_summary) : text);
            } else if (title.equals(getString(R.string.weight))) {
                pref.setSummary(
                        (text == null || text.isEmpty()) ? getString(R.string.weight_summary)
                                : text + " " + getString(R.string.weight_unit));
            } else if (title.equals(getString(R.string.age))) {
                pref.setSummary(
                        (text == null || text.isEmpty()) ? getString(R.string.age_summary) : text);
            } else if (title.equals(getString(R.string.email))) {
                pref.setSummary(text);
            }
        }
        if (pref instanceof SwitchPreference) {
            pref.setSummary(
                    ((SwitchPreference) pref).isChecked() ? ((SwitchPreference) pref).getSummaryOn()
                            : ((SwitchPreference) pref).getSummaryOff());
        }
    }
}
