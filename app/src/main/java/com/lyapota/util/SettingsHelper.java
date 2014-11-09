package com.lyapota.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class SettingsHelper {
    private Context context;

    private String getXml(String path){

        String xmlString = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(path);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            xmlString = new String(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return xmlString;
    }
}
