package com.lyapota.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class TweaksHelper {

    public final static String FILES_PATH = "/data/local/tmp";

    private static Context context;

    public static void setContext(Context a_context) {
        context = a_context;
    }

    private static byte[] getAssetsData(String filename) {
        byte[] data = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int length = is.available();
            data = new byte[length];

            is.read(data);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return data;
    }

    private static String getAssetsText(String filename) {
        byte[] data = getAssetsData(filename);
        if (data == null)
            return null;
        return data.toString();
    }

    private static File extractExecutable(String filename) {
        try {
            File file = new File(FILES_PATH, filename);
            byte[] data = getAssetsData(filename);

            if (data == null)
                return null;

            if (file.exists())
                file.delete();

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);

            outputStream.flush();
            outputStream.close();

            file.setExecutable(true);

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String runScript(String script, String params) {
        try {
            return Shell.sudo(script + " " + params);
        } catch (Shell.ShellException e) {
            e.printStackTrace();
            return "";
        }
    }

}

