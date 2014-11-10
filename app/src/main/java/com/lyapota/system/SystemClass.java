package com.lyapota.system;

import android.content.Context;

public class SystemClass {

    public static enum DataType {BOOLEAN, YESNO, INTEGER, STRING, STRINGS};

    protected String key;
    protected String path_to_read;
    protected String path_to_write;
    protected Object value;
    protected DataType data_type;
    protected Context context;
    protected boolean exists;
    protected boolean writable;

    SystemClass() {
    }

    public SystemClass(String a_key, String a_path, DataType a_data_type){
        this(a_key, a_path, a_path, a_data_type, null);
    }

    public SystemClass(String a_key, String a_path_read, String a_path_write, DataType a_data_type){
        this(a_key, a_path_read, a_path_write, a_data_type, null);
    }

    public SystemClass(String a_key, String a_path, DataType a_data_type, Context a_context){
        this(a_key, a_path, a_path, a_data_type, a_context);
    }

    public SystemClass(String a_key, String a_path_read, String a_path_write,  DataType a_data_type, Context a_context){
        this();

        key = a_key;
        path_to_read = a_path_read;
        path_to_write = a_path_write;
        data_type = a_data_type;
        context = a_context;

        read();
    }

    protected boolean int2bool(int a_value) {
        return (1 == a_value);
    }

    protected int bool2int(boolean a_value) {
        return (a_value ? 1 : 0);
    }

    protected String bool2yesno(boolean a_value) {
        return (a_value ? new String("Y") : new String("N"));
    }

    protected boolean yesno2bool(String a_value) {
        return (a_value.equals("Y")
                || a_value.equals("y"))
                || a_value.equals("1");
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

    public DataType getDataType() {
        return data_type;
    }

    public Boolean getBoolean() {
        if (data_type == DataType.INTEGER)
            return int2bool((Integer) value);
        else if (data_type == DataType.YESNO)
            return yesno2bool(value.toString());
        else if (data_type == DataType.STRING)
            return new Boolean(value.toString());
        else
            return (Boolean) value;
    }

    public Integer getInteger() {
        if (data_type == DataType.BOOLEAN)
            return bool2int((Boolean) value);
        else if (data_type == DataType.YESNO)
            return bool2int(yesno2bool(value.toString()));
        else if (data_type == DataType.STRING)
            return new Integer(value.toString());
        else
            return (Integer) value;
    }

    public String getString() {
        return value.toString();
    }

    public String[] getStrings() {
        return (String[]) value;
    }

    public void setValue(Boolean a_value) {
        if (data_type == DataType.INTEGER)
            new Integer(bool2int(a_value));
        else if (data_type == DataType.STRING)
            value = a_value.toString();
        else if (data_type == DataType.YESNO)
            value = bool2yesno(a_value);
        else
            value = a_value;
    }

    public void setValue(Integer a_value) {
        if (data_type == DataType.BOOLEAN)
            value = new Boolean(int2bool(a_value));
        else if (data_type == DataType.STRING)
            value = a_value.toString();
        else if (data_type == DataType.YESNO)
            value = bool2yesno(int2bool(a_value));
        else
            value = a_value;
    }

    public void setValue(String a_value) {
        if (data_type == DataType.BOOLEAN)
            if (a_value.length() == 1) {
                value = yesno2bool(a_value);
            } else {
                value = new Boolean(a_value);
            }
        if (data_type == DataType.INTEGER)
            value = new Integer(a_value);
        else
          value = a_value;
    }

    public void setValue(String[] a_value) {
        value = a_value;
    }
}
