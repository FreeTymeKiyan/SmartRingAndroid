package me.freetymekiyan.smartring.views;

import com.afollestad.materialdialogs.MaterialDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

import de.greenrobot.event.EventBus;
import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.controllers.PrefChangedEvent;
import me.freetymekiyan.smartring.preferences.TimePickerPreference;

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
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Preference prefAbout = findPreference(getString(R.string.key_about));
        prefAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.about)
                        .content(R.string.about_content)
                        .positiveText(android.R.string.ok)
                        .show();
                return true;
            }
        });
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
            listPref.setSummary(
                    listPref.getEntry() != null ? listPref.getEntry() : getString(R.string.daily));
        }
        if (pref instanceof EditTextPreference) {
            String title = pref.getTitle().toString();
            String text = ((EditTextPreference) pref).getText();
            if (title.equals(getString(R.string.name))) {
                pref.setSummary(
                        (text == null || text.isEmpty()) ? getString(R.string.name_summary) : text);
                EventBus.getDefault().post(
                        new PrefChangedEvent(pref.getSummary() + "", R.string.key_name));
            } else if (title.equals(getString(R.string.weight))) {
                pref.setSummary(
                        (text == null || text.isEmpty()) ? getString(R.string.weight_summary)
                                : text + " " + getString(R.string.weight_unit));
            } else if (title.equals(getString(R.string.age))) {
                pref.setSummary(
                        (text == null || text.isEmpty()) ? getString(R.string.age_summary) : text);
            } else if (title.equals(getString(R.string.email))) {
                pref.setSummary(text);
                EventBus.getDefault()
                        .post(new PrefChangedEvent(pref.getSummary() + "", R.string.key_email));
            }
        }
        if (pref instanceof SwitchPreference) {
            pref.setSummary(
                    ((SwitchPreference) pref).isChecked() ? ((SwitchPreference) pref).getSummaryOn()
                            : ((SwitchPreference) pref).getSummaryOff());
        }
        if (pref instanceof TimePickerPreference) {
            TimePickerPreference t = (TimePickerPreference) pref;
            setRecurringAlarm(getActivity(), t.getHour(), t.getMinute());
        }
    }

    private void setRecurringAlarm(Context context, int hour, int min) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        Log.d("DEBUG", "hour: " + hour + " min: " + min);
//
//        PendingIntent pi = PendingIntent.getBroadcast(context,
//                0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager alarms = (AlarmManager) getActivity().getSystemService(
//                Context.ALARM_SERVICE);
//        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pi);
    }
}
