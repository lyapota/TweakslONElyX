package com.lyapota.system;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public class Setting extends SystemClass {

    public Setting(String a_key, String a_path, DataType a_data_type, DataType a_pref_type){
        this(a_key, a_path, a_data_type, a_pref_type, null);
    }

    public Setting(String a_key, String a_path, DataType a_data_type, DataType a_pref_type, Context a_context){
        super(a_key, a_path, a_data_type, a_pref_type, a_context);
    }

    @Override
    public void read() {
        ContentResolver resolver = context.getContentResolver();
        try {
            switch (data_type) {
                case BOOLEAN:
                case INTEGER:
                    setValue(Settings.System.getInt(resolver, path_to_read));
                    break;
                case YESNO:
                case STRING:
                    setValue(Settings.System.getString(resolver, path_to_read));
                    break;
                case STRINGS:
                    setValue(Settings.System.getString(resolver, path_to_read).split(" "));
                    break;
            }
            writable = true;
        } catch (Settings.SettingNotFoundException e) {
            exists = false;
            writable = true;
        }
    }

    @Override
    public void write() {
        ContentResolver resolver = context.getContentResolver();

        try {
            switch (data_type) {
                case BOOLEAN:
                case INTEGER:
                    Settings.System.putInt(resolver, path_to_write, getInteger());
                    break;
                case YESNO:
                case STRING:
                    Settings.System.putString(resolver, path_to_write, getString());
                    break;
                case STRINGS:
                    Settings.System.putString(resolver, path_to_write, TextUtils.join(" ", getStrings()));
                    break;
            }
            exists = true;
            writable = true;
       } catch (Exception e) {
          e.printStackTrace();
        }
    }
}
