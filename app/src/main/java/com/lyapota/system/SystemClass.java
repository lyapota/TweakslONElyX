package com.lyapota.system;

import android.content.Context;

public class SystemClass {

    public static enum Type {BOOLEAN, INTEGER, STRING, STRINGS};

    protected String key;
    protected String path;
    protected Object value;
    protected Type type;
    protected Context context;
    protected boolean exists;
    protected boolean writable;

    public void init(String a_key, String a_path, Type a_type){
        init(a_key, a_path, a_type, null);
    }

    public void init(String a_key, String a_path, Type a_type, Context a_context){
        key = a_key;
        path = a_path;
        type = a_type;
        context = a_context;
        read();
    }

    public boolean isExists() {
        return exists;
    }

    public boolean isWritable() {
        return writable;
    }

    public void read() {
        return;
    }

    public void write() {
        return;
    }

    public String getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public Boolean getBoolean() {
        return (Boolean) value;
    }

    public Integer getInteger() {
        return (Integer) value;
    }

    public String getString() {
        return (String) value;
    }

    public String[] getStrings() {
        return (String[]) value;
    }

    public void setValue(Boolean a_value) {
        value = a_value;
        type = Type.BOOLEAN;
    }

    public void setValue(Integer a_value) {
        value = a_value;
        type = Type.INTEGER;
    }

    public void setValue(String a_value) {
        value = a_value;
        type = Type.STRING;
    }
    public void setValue(String[] a_value) {
        value = a_value;
        type = Type.STRINGS;
    }
}
