package me.freetymekiyan.smartring.views;

import com.afollestad.materialdialogs.MaterialDialog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.preference.PreferenceFragment;
import android.view.View;

import java.util.Calendar;

import de.greenrobot.event.EventBus;
import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.controllers.PrefChangedEvent;
import me.freetymekiyan.smartring.preferences.TimePickerPreference;
import me.freetymekiyan.smartring.receivers.AlarmReceiver;

public class MyPreferenceFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final long INTERVAL_WEEK = AlarmManager.INTERVAL_DAY * 7;

    public static final long INTERVAL_MONTH = INTERVAL_WEEK * 4;

    public static final long[] INTEVALS = {AlarmManager.INTERVAL_DAY, INTERVAL_WEEK,
            INTERVAL_MONTH};

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
                    updatePreference(preferenceGroup.getPreference(j), false);
                }
            } else {
                updatePreference(pref, false);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key), true);
    }

    void updatePreference(Preference pref, boolean fromUser) {
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            listPref.setSummary(
                    listPref.getEntry() != null ? listPref.getEntry() : getString(R.string.daily));
            if (fromUser) setRecurringAlarm(getActivity(), listPref.getValue());
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
                pref.setSummary(text == null || text.isEmpty() ? getString(R.string.email_summary) : text);
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
            if (fromUser) setRecurringAlarm(getActivity(), t.getHour(), t.getMinute());
        }
    }

    private void setRecurringAlarm(Context context, int hour, int min) {
        final String intervalVal = getPreferenceScreen().getSharedPreferences()
                .getString(getString(R.string.key_frequency), getString(R.string.freq_daily));
        setAlarm(getActivity(), hour, min, INTEVALS[Integer.valueOf(intervalVal)]);
    }

    private void setRecurringAlarm(Context context, String value) {
        long interval = INTEVALS[Integer.valueOf(value)];
        final String time = getPreferenceScreen().getSharedPreferences().getString(
                getString(R.string.key_time), TimePickerPreference.DEFAULT_VALUE);
        setAlarm(getActivity(), TimePickerPreference.getHour(time),
                TimePickerPreference.getMinute(time), interval);
    }

    private void setAlarm(Context context, int hour, int min, long interval) {
        Calendar c = Calendar.getInstance();
        c.clear(Calendar.SECOND);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        if (c.getTimeInMillis() < System.currentTimeMillis()) {
            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        }

        PendingIntent pi = PendingIntent.getBroadcast(context,
                0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), interval, pi);
    }
}
