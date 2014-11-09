package com.lyapota.system;

import android.content.ContentResolver;
import android.provider.Settings;
import android.text.TextUtils;

public class Setting extends SystemClass {

    private boolean int2bool(int a_value) {
        return (1 == a_value);
    }

    private int bool2int(boolean a_value) {
        return (a_value ? 1 : 0);
    }

    @Override
    public void read() {
        ContentResolver resolver = context.getContentResolver();
        try {
            switch (type) {
                case BOOLEAN:
                    setValue(new Boolean(int2bool(Settings.System.getInt(resolver, path))));
                    break;
                case INTEGER:
                    setValue(new Integer(Settings.System.getInt(resolver, path)));
                    break;
                case STRING:
                    setValue(Settings.System.getString(resolver, path));
                    break;
                case STRINGS:
                    setValue(Settings.System.getString(resolver, path).split(" "));
                    break;
            }
            writable = true;
        } catch (Settings.SettingNotFoundException e) {
            exists = false;
            writable = false;
        }
    }

    @Override
    public void write() {
        ContentResolver resolver = context.getContentResolver();

        Settings.System.putInt(resolver, "", 0);
        try {
            switch (type) {
                case BOOLEAN:
                    Settings.System.putInt(resolver, path, bool2int(getBoolean()));
                    break;
                case INTEGER:
                    Settings.System.putInt(resolver, path, getInteger());
                    break;
                case STRING:
                    Settings.System.putString(resolver, path, getString());
                    break;
                case STRINGS:
                    Settings.System.putString(resolver, path, TextUtils.join(" ", getStrings()));
                    break;
            }
       } catch (Exception e) {
          e.printStackTrace();
        }
    }
}
