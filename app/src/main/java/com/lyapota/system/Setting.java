package com.lyapota.system;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public class Setting extends SystemClass {

    public Setting(String a_key, String a_path, DataType a_data_type){
        this(a_key, a_path, a_path, a_data_type, null);
    }

    public Setting(String a_key, String a_path_read, String a_path_write, DataType a_data_type){
        this(a_key, a_path_read, a_path_write, a_data_type, null);
    }

    public Setting(String a_key, String a_path, DataType a_data_type, Context a_context){
        this(a_key, a_path, a_path, a_data_type, a_context);
    }

    public Setting(String a_key, String a_path_read, String a_path_write,  DataType a_data_type, Context a_context){
        super(a_key, a_path_read, a_path_write, a_data_type, a_context);
    }

    @Override
    public void read() {
        ContentResolver resolver = context.getContentResolver();
        try {
            switch (data_type) {
                case BOOLEAN: INTEGER:
                    setValue(Settings.System.getInt(resolver, path_to_read));
                    break;
                case YESNO: STRING:
                    setValue(Settings.System.getString(resolver, path_to_read));
                    break;
                case STRINGS:
                    setValue(Settings.System.getString(resolver, path_to_read).split(" "));
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
            switch (data_type) {
                case BOOLEAN: INTEGER:
                    Settings.System.putInt(resolver, path_to_write, getInteger());
                    break;
                case YESNO: STRING:
                    Settings.System.putString(resolver, path_to_write, getString());
                    break;
                case STRINGS:
                    Settings.System.putString(resolver, path_to_write, TextUtils.join(" ", getStrings()));
                    break;
            }
       } catch (Exception e) {
          e.printStackTrace();
        }
    }
}
