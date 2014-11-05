package com.lyapota.tweakslonelyx;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyapota.peferences.DisplayGamma;

public class PreferencesFragment extends PreferenceFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        if (section == 1) {
            addPreferencesFromResource(R.xml.pref_system);

            bindPreferenceSummaryToValue(findPreference("pref_recentapp_style"));
            bindPreferenceSummaryToValue(findPreference("pref_dalvik_optimization"));

        } else if (section == 2) {
            addPreferencesFromResource(R.xml.pref_general);

        } else  if (section == 3) {

            // Add 'data and sync' preferences, and a corresponding header.
/*            PreferenceCategory fakeHeader = new PreferenceCategory(context);
            fakeHeader = new PreferenceCategory(context);
            fakeHeader.setTitle(R.string.pref_header_data_sync);
            getPreferenceScreen().addPreference(fakeHeader);                  */
            addPreferencesFromResource(R.xml.pref_kernel);

            bindPreferenceSummaryToValue(findPreference("pref_s2s"));
            bindPreferenceSummaryToValue(findPreference("pref_cpu_oc"));
            bindPreferenceSummaryToValue(findPreference("pref_edp"));

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

            } else if (preference instanceof DisplayGamma) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary("Empty");

                } else {
                    preference.setSummary("not Empty");
              }

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

}
