package com.lyapota.tweakslonelyx;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import java.util.ArrayList;
import android.util.Log;

import com.lyapota.util.AboutDialog;
import com.lyapota.util.Shell;
import com.lyapota.util.Shell.ShellException;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String PREF_THEME = "isDarkTheme";
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the settings
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDark = sp.getBoolean(PREF_THEME, false);

        // set the theme according to the setting
        if (isDark)
            this.setTheme(R.style.AppThemeDark);
        else
            this.setTheme(R.style.AppThemeLight);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
             .replace(R.id.container, PreferencesFragment.newInstance(position + 1))
             .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section_system);
                break;
            case 2:
                mTitle = getString(R.string.title_section_general);
                break;
            case 3:
                mTitle = getString(R.string.title_section_kernel);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem apply_on_reboot = menu.findItem(R.id.apply_on_reboot_menu_item);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

            mNavigationDrawerFragment.mApplyOnReboot =
                    sp.getBoolean(NavigationDrawerFragment.PREF_APPLY_ON_REBOOT, false);
            if (mNavigationDrawerFragment.mApplyOnReboot)
                apply_on_reboot.setIcon(R.drawable.btn_apply_on_reboot_on);
            else
                apply_on_reboot.setIcon(R.drawable.btn_apply_on_reboot_off);

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        switch (item.getItemId()) {
            case R.id.theme_menu_item:
                sp.edit().putBoolean(PREF_THEME, !sp.getBoolean(PREF_THEME, false)).apply();
                Intent i = getBaseContext()
                        .getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext()
                                                   .getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.about_menu_item:
                AboutDialog.showAbout(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String[] getBinCommands() {
        String[] commands = null;
        try {
            commands = Shell.exec("ls").split("\\s+");
        } catch (ShellException e) {
            Log.e("DriverActivity", e.getMessage());
        }
        return commands;
        }

    private String[] getNetworkAdapters() {
        String output = null;
        String[] netcfg = null;
        ArrayList<String> adapters = null;

        try {
            output = Shell.sudo("netcfg");
            if(output != null) {
                netcfg = output.split("\\s+");
                adapters = new ArrayList<String>();

                // Parse out adapter names.
                for(int i = 0; i < netcfg.length; i+=5) {
                    adapters.add(netcfg[i]);
        }
                        }
        } catch (ShellException e) {
            Log.e("DriverActivity", e.getMessage());
                    }

        // Return null if there is no output returned.
        if(adapters != null) {
            return adapters.toArray(new String[adapters.size()]);
                } else {
            return null;
        }
    }
}
