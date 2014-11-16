package com.lyapota.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_WRITEABLE;


public class FileHelper {

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

    public static String getAssetsText(String filename) {
        byte[] data = getAssetsData(filename);
        if (data == null)
            return null;
        return new String(data);
    }

    private static File extractExecutable(String filename) {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/" + filename);
            if (file.exists())
                return file;

            byte[] data = getAssetsData(filename);
            if (data == null)
                return null;

            FileOutputStream outputStream = context.openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(data);

            outputStream.flush();
            outputStream.close();

            file.setExecutable(true, false);

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String run(String script, String params) {
        try {
            return Shell.sudo(script + " " + params);
        } catch (Shell.ShellException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String runFromAssets(String filename, String params) {
        File file = extractExecutable(filename);
        String result_str = null;

        if (file != null) {
            result_str = run(context.getFilesDir().getAbsolutePath() + '/' + filename, params);
        }
        return result_str;
    }

}

