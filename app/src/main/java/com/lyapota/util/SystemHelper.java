package com.lyapota.util;

import android.content.Context;

import com.lyapota.system.Kernel;
import com.lyapota.system.Script;
import com.lyapota.system.Setting;
import com.lyapota.system.SystemClass;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SystemHelper {

    private static Context context;
    private static HashMap<String, SystemClass> props;

    public static void setContext(Context a_context) {
        context = a_context;
        FileHelper.setContext(context);
    }

    private static void parseMapXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        SystemClass item = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            String kind, pref, path, type;
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

                        kind = parser.nextText();
                        if (kind.equals("system")) {
                            item = new Setting(pref, path, data_type);
                        } else if (kind.equals("kernel")) {
                            item = new Kernel(pref, path, data_type);
                        } else if (kind.equals("gun")) {
                            item = new Kernel(pref, path, data_type);
                        } else if (kind.equals("shell")) {
                            item = new Script(pref, path, data_type);
                        }
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

    public static void init() {
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


}
