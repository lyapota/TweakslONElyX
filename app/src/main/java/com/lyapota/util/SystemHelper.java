package com.lyapota.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

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

    private static void parseMapXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        SystemClass item = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            String kind, pref, path, type, def, path_write, ctrl, prop;
            SystemClass.DataType data_type;

            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    props=new HashMap<String, SystemClass>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name == "item"){
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

                        def = parser.getAttributeValue(null, "def");

                        path_write  = parser.getAttributeValue(null, "path_write");
                        ctrl  = parser.getAttributeValue(null, "ctrl");
                        prop  = parser.getAttributeValue(null, "prop");

                        kind = parser.nextText();
                        if (kind.equals("system")) {
                            item = new Setting(pref, path, data_type);
                        } else if (kind.equals("kernel")) {
                            item = new Kernel(pref, path, data_type);
                            if (path_write != null)
                                item.setPathToWrite(path_write);
                            if (ctrl != null)
                                item.setCtrl(ctrl);
                        } else if (kind.equals("shell")) {
                            item = new Script(pref, path, data_type);
                            if (prop != null)
                                item.setProp(prop);
                        }

                        if (item != null && def != null)
                                item.setValue(def);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && item != null){
                        props.put("key", item);
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
            item.read();
            key = item.getKey();

            if (key.contains("kpref_gun_control")) {

            }
        }


    }

    public static void getFromDevice(Preference pref) {
        SystemClass item = null;


        item.read();
    }

    public static void putToDevice(Preference pref) {
        SystemClass item = null;

        item.write();
    }


    private static SystemClass setGunCtrlByPref(SystemClass a_item, String pref_value){
        String key = a_item.getKey();
        int index = Integer.parseInt(key.substring((key.length() - 1)));
        String[] values = pref_value.split(" ");
        String item_value = values[index];

        if (index < 2)
            item_value = item_value + " " + item_value;
        a_item.setValue(item_value);

        return a_item;
    }

    public static boolean setOnBoot() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean apply_enabled = sp.getBoolean(NavigationDrawerFragment.PREF_APPLY_ON_REBOOT, false);
        String key = null;

        if (apply_enabled) {
            for (SystemClass item : props.values()) {
                if (item instanceof Kernel) {
                    key = item.getKey();
                    if (key.contains("kpref_gun_control")) {
                        item = setGunCtrlByPref(item, sp.getString("kpref_gun_control", "0"));
                    } else {
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
