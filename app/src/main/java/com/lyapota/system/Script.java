package com.lyapota.system;

import android.content.Context;

import com.lyapota.util.FileHelper;

public class Script extends SystemClass {

    public Script(String a_key, String a_path, DataType a_data_type){
        this(a_key, a_path, a_path, a_data_type, null);
    }

    public Script(String a_key, String a_path_read, String a_path_write, DataType a_data_type){
        this(a_key, a_path_read, a_path_write, a_data_type, null);
    }

    public Script(String a_key, String a_path, DataType a_data_type, Context a_context){
        this(a_key, a_path, a_path, a_data_type, a_context);
    }

    public Script(String a_key, String a_path_read, String a_path_write,  DataType a_data_type, Context a_context){
        super(a_key, a_path_read, a_path_write, a_data_type, a_context);
    }

    @Override
    public void read() {
        String param;

        if (prop == null)
            param = getString();
        else
            param = prop +  " " + getString();

        setValue(FileHelper.runFromAssets(path_to_read, param));
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
