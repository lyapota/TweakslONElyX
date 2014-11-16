package com.lyapota.system;

import android.content.Context;

import com.lyapota.util.FileHelper;

public class Script extends SystemClass {

    public Script(String a_key, String a_path, DataType a_data_type, DataType a_pref_type){
        this(a_key, a_path, a_data_type, a_pref_type, null);
    }

    public Script(String a_key, String a_path, DataType a_data_type, DataType a_pref_type, Context a_context){
        super(a_key, a_path, a_data_type, a_pref_type, a_context);
    }


    @Override
    public void read() {
        String param;

        if (prop == null)
            param = "@";
        else
            param = prop +  " " + "@";

        String result = FileHelper.runFromAssets(path_to_read, param);
        if (result != null)
            setValue(result);
    }

    @Override
    public void write() {
        String param;

        if (prop == null)
            param = getString();
        else
            param = prop +  " " + getString();

        FileHelper.runFromAssets(path_to_write, param);
    }

}
