package com.lyapota.tweakslonelyx;

import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyapota.util.SystemHelper;

public class PreferencesFragment extends PreferenceFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PreferencesFragment newInstance(int sectionNumber) {
        PreferencesFragment fragment = new PreferencesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PreferencesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        setupSimplePreferencesScreen();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void setupSimplePreferencesScreen() {

        int section = this.getArguments().getInt(ARG_SECTION_NUMBER);

        SystemHelper.skipPrefOnChangeValue = true;
        try {
            switch (section) {
                case 1:
                    addPreferencesFromResource(R.xml.pref_system);
                    break;
                case 2:
                    addPreferencesFromResource(R.xml.pref_ui);
                    break;
                case 3:
                    addPreferencesFromResource(R.xml.pref_kernel);
                    break;
            }

            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceCategory) {
                    PreferenceCategory category = (PreferenceCategory) preference;
                    for (int j = 0; j < category.getPreferenceCount(); j++) {
                        preference = category.getPreference(j);
                        bindPreferenceSummaryToValue(preference);
                    }
                } else
                    bindPreferenceSummaryToValue(preference);
            }
        } finally {
            SystemHelper.skipPrefOnChangeValue = false;
        }
    }

    protected static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof ListPreference) {
                String stringValue = value.toString();
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference instanceof EditTextPreference) {
                preference.setSummary(value.toString());
            }

            if (!SystemHelper.skipPrefOnChangeValue)
                SystemHelper.putToDevice(preference, value);

            return true;
        }
    };

        private static void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            if ((preference instanceof ListPreference) || (preference instanceof EditTextPreference))
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getString(preference.getKey(), ""));
        }

    }
