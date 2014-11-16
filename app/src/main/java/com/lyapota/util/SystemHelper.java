package com.lyapota.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.lyapota.system.Kernel;
import com.lyapota.system.Script;
import com.lyapota.system.Setting;
import com.lyapota.system.SystemClass;
import com.lyapota.tweakslonelyx.NavigationDrawerFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SystemHelper {

    private static Context context;
    private static HashMap<String, SystemClass> props = null;
    public static boolean skipPrefOnChangeValue = false;

    private static void parseMapXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        SystemClass item = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            String kind, pref, path, type, ptype, def, path_write, ctrl, prop;
            SystemClass.DataType data_type;
            SystemClass.DataType pdata_type;

            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    props=new HashMap<String, SystemClass>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("item")){
                        pref = parser.getAttributeValue(null, "pref");
                        path = parser.getAttributeValue(null, "path");

                        type = parser.getAttributeValue(null, "type");
                        if (type.equals("bool"))
                            data_type = SystemClass.DataType.BOOLEAN;
                        else if (type.equals("string"))
                            data_type = SystemClass.DataType.STRING;
                        else if (type.equals("strings"))
                            data_type = SystemClass.DataType.STRINGS;
                        else
                            data_type = SystemClass.DataType.INTEGER;

                        ptype = parser.getAttributeValue(null, "ptype");
                        if (ptype.equals("bool"))
                            pdata_type = SystemClass.DataType.BOOLEAN;
                        else if (ptype.equals("string"))
                            pdata_type = SystemClass.DataType.STRING;
                        else if (ptype.equals("strings"))
                            pdata_type = SystemClass.DataType.STRINGS;
                        else
                            pdata_type = SystemClass.DataType.INTEGER;

                        def = parser.getAttributeValue(null, "def");

                        path_write  = parser.getAttributeValue(null, "path_write");
                        ctrl  = parser.getAttributeValue(null, "ctrl");
                        prop  = parser.getAttributeValue(null, "prop");

                        kind = parser.nextText();
                        if (kind.equals("system")) {
                            item = new Setting(pref, path, data_type, pdata_type, context);
                        } else if (kind.equals("kernel")) {
                            item = new Kernel(pref, path, data_type, pdata_type, context);
                            if (path_write != null)
                                item.setPathToWrite(path_write);
                            if (ctrl != null)
                                item.setCtrl(ctrl);
                        } else if (kind.equals("shell")) {
                            item = new Script(pref, path, data_type, pdata_type, context);
                            if (prop != null)
                                item.setProp(prop);
                        }

                        if (item != null) {
                          if (def != null)
                              item.setValue(def);
                          props.put(item.getKey(), item);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && item != null){
                        props.put(item.getKey(), item);
                    }
            }
            eventType = parser.next();
        }

    }

    public static void init(Context a_context) {
        context = a_context;
        FileHelper.setContext(context);

        if (props != null)
            return;

        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = context.getAssets().open("map.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseMapXML(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getFromDevice() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = null;

        for (SystemClass item : props.values()) {
            key = item.getKey();
            item.read();

            switch (item.getPrefType()) {
                case STRING:
                    sp.edit().putString(key, item.getString()).apply();
                    break;
                case BOOLEAN:
                    sp.edit().putBoolean(key, item.getBoolean()).apply();
                    break;
                case INTEGER:
                    sp.edit().putInt(key, item.getInteger()).apply();
                    break;
            }

            props.put(item.getKey(), item);
        }
    }

    public static void getFromDevice(Preference pref) {
        SystemClass item = null;
        item = props.get(pref.getKey());
        if (item==null)
            return;

        skipPrefOnChangeValue = true;
        try {

            item.read();

            if (pref instanceof CheckBoxPreference)
                ((CheckBoxPreference) pref).setChecked(item.getBoolean());
            else if (pref instanceof ListPreference)
                ((ListPreference) pref).setValue(item.getString());
            else if (pref instanceof EditTextPreference)
                ((EditTextPreference) pref).setText(item.getString());

            props.put(item.getKey(), item);
        } finally {
            skipPrefOnChangeValue = false;
        }
    }

    public static void putToDevice(Preference pref, Object val) {
        SystemClass item = null;

        item = props.get(pref.getKey());

        if (pref instanceof CheckBoxPreference)
            item.setValue((Boolean) val);
        else if (pref instanceof ListPreference)
            item.setValue((String) val);
        else if (pref instanceof EditTextPreference)
            item.setValue((String) val);

        item.write();
        props.put(item.getKey(), item);
    }

    public static boolean setOnBoot() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean apply_enabled = sp.getBoolean(NavigationDrawerFragment.PREF_APPLY_ON_REBOOT, false);
        String key = null;

        if (apply_enabled) {
            for (SystemClass item : props.values()) {
                if (item instanceof Kernel) {
                    key = item.getKey();
                    switch (item.getDataType()) {
                        case STRING:
                            item.setValue(sp.getString(key, item.getString()));
                            break;
                        case BOOLEAN:
                            item.setValue(sp.getBoolean(key, item.getBoolean()));
                            break;
                        case INTEGER:
                            item.setValue(sp.getInt(key, item.getInteger()));
                            break;
                    }
                    props.put(key, item);
                    item.write();
                }
            }
            return true;

        } else
            return false;
    }


}
