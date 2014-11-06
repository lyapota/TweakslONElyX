package com.lyapota.tweakslonelyx;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PreferencesFragment extends PreferenceFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static boolean in_swith_state = false;
    private Context context;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        context = rootView.getContext();

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

        switch (section) {
            case 1:
                addPreferencesFromResource(R.xml.pref_system);
                break;
            case 2:
                addPreferencesFromResource(R.xml.pref_general);
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
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference instanceof SwitchPreference) {
                PreferenceCategory pCategory = (PreferenceCategory) preference.getPreferenceManager().findPreference("hotplug_category");
                if (pCategory != null && pCategory.findPreference(preference.getKey()) != null) {
                    if ((Boolean) value) {
                        for (int i = 0; i < pCategory.getPreferenceCount(); i++) {
                            if (pCategory.getPreference(i) instanceof SwitchPreference) {
                                SwitchPreference pref = (SwitchPreference) pCategory.getPreference(i);
                                if (pref.isChecked() && pref.getKey() != preference.getKey()) {
                                    PreferencesFragment.in_swith_state = true;
                                    pref.setChecked(false);
                                }
                            }
                        }
                    } else {
                        if (!PreferencesFragment.in_swith_state) {
                            return false;
                        } else
                            PreferencesFragment.in_swith_state = false;
                    }
                }
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        if (preference instanceof ListPreference)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

}
