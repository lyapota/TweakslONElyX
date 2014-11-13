package com.lyapota.system;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Kernel extends SystemClass {

    public Kernel(String a_key, String a_path, DataType a_data_type){
        this(a_key, a_path, a_path, a_data_type, null);
    }

    public Kernel(String a_key, String a_path_read, String a_path_write, DataType a_data_type){
        this(a_key, a_path_read, a_path_write, a_data_type, null);
    }

    public Kernel(String a_key, String a_path, DataType a_data_type, Context a_context){
        this(a_key, a_path, a_path, a_data_type, a_context);
    }

    public Kernel(String a_key, String a_path_read, String a_path_write,  DataType a_data_type, Context a_context){
        super(a_key, a_path_read, a_path_write, a_data_type, a_context);
    }

    @Override
    public void read() {
        File f;

        try {
            f = new File(path_to_read);
            exists = f.exists();

            if (exists) {
                writable = f.canWrite();

                InputStream is = new FileInputStream(f);
                byte[] b = new byte[is.available()];
                is.read(b);

                switch (data_type) {
                    case BOOLEAN:
                        if (b.length > 0)
                            setValue(new String(b));
                        else
                            setValue(new Boolean(false));
                        break;
                    case INTEGER:
                        if (b.length > 0)
                            setValue(new String(b));
                        else
                            setValue(new Integer(0));
                        break;
                    case YESNO: STRING:
                        if (b.length > 0)
                            setValue(new String(b));
                        else
                            setValue(new String(""));
                        break;
                    case STRINGS:
                        if (b.length > 0)
                            setValue((new String(b)).split(" "));
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
        File f;
        FileOutputStream os = null;
        String content = "";

        if (!exists || !writable)
            return;

        switch (data_type) {
            case BOOLEAN: INTEGER: YESNO: STRING:
                content = getString();
                break;
            case STRINGS:
                content = TextUtils.join(" ", getStrings());
                break;
        }

        try {
            f = new File(path_to_write);
            os = new FileOutputStream(f);

            byte[] b = content.getBytes();
            os.write(b);

            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (ctrl != null) {
            try {
                f = new File(ctrl);
                os = new FileOutputStream(f);
                content = "1";
                byte[] b = content.getBytes();
                os.write(b);

                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
