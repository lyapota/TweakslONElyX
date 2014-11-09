package com.lyapota.system;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Kernel extends SystemClass {

    @Override
    public void read() {
        File f;

        try {
            f = new File(path);
            exists = f.exists();

            if (exists) {
                writable = f.canWrite();

                InputStream is = new FileInputStream(f);
                byte[] b = new byte[is.available()];
                is.read(b);

                switch (type) {
                    case BOOLEAN:
                        if (b.length > 0)
                            setValue(Boolean.getBoolean(new String(b)));
                        else
                            setValue(new Boolean(false));
                        break;
                    case INTEGER:
                        if (b.length > 0)
                            setValue(Integer.getInteger(new String(b)));
                        else
                            setValue(new Integer(0));
                        break;
                    case STRING:
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

        switch (type) {
            case BOOLEAN:
                content = getBoolean().toString();
                break;
            case INTEGER:
                content = getInteger().toString();
                break;
            case STRING:
                content = getString();
                break;
            case STRINGS:
                content = TextUtils.join(" ", getStrings());
                break;
        }
        try {
            f = new File(path);
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
    }

}
