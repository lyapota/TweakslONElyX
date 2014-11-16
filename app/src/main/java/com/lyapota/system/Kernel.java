package com.lyapota.system;

import android.content.Context;
import android.text.TextUtils;

import com.lyapota.util.Shell;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Kernel extends SystemClass {

    public Kernel(String a_key, String a_path, DataType a_data_type, DataType a_pref_type){
        this(a_key, a_path, a_data_type, a_pref_type, null);
    }

    public Kernel(String a_key, String a_path, DataType a_data_type, DataType a_pref_type, Context a_context){
        super(a_key, a_path, a_data_type, a_pref_type, a_context);
    }

    @Override
    public void read() {
        File f;

        try {
            f = new File(path_to_read);
            exists = f.exists();

            if (exists) {
                writable = true;

                InputStream is = new FileInputStream(f);
                DataInputStream in = new DataInputStream(is);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String b_str = br.readLine();

                in.close();

                switch (data_type) {
                    case BOOLEAN:
                        if (b_str.length() > 0)
                            setValue(new Boolean(b_str));
                        else
                            setValue(new Boolean(false));
                        break;
                    case INTEGER:
                        if (b_str.length() > 0)
                            setValue(new Integer(b_str));
                        else
                            setValue(new Integer(0));
                        break;
                    case YESNO:
                    case STRING:
                        if (b_str.length() > 0)
                            setValue(b_str);
                        else
                            setValue(new String(""));
                        break;
                    case STRINGS:
                        if (b_str.length() > 0)
                            setValue(b_str.split(" "));
                        else
                            setValue(new String[0]);
                        break;
                }
            }
        } catch (IOException e) {
            exists = false;
            writable = false;
        }
    }

    @Override
    public void write() {
        String content = "";

        if (!exists || !writable)
            return;

        switch (data_type) {
            case BOOLEAN:
            case INTEGER:
            case YESNO:
            case STRING:
                content = getString();
                break;
            case STRINGS:
                content = TextUtils.join(" ", getStrings());
                break;
        }

        if (key.equals("kpref_cpu_uv")) {
            content =((Integer)(getInteger() - (new Integer(getPrev())))).toString();
        }

        try {
            Shell.sudo("echo \"" + content + "\" > "  + path_to_write);

            if (ctrl != null) {
                try {
                    Shell.sudo("echo \"1\" > " + ctrl);
                } catch (Shell.ShellException e) {
                    e.printStackTrace();
                }
            }

        } catch (Shell.ShellException e) {
            e.printStackTrace();
        }
    }

}
