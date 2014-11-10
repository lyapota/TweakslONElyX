package com.lyapota.system;

import com.lyapota.util.TweaksHelper;

public class Script extends SystemClass {

    @Override
    public void read() {
        setValue(TweaksHelper.runScript(path_to_read, getKey() + " " + getString()));
    }

    @Override
    public void write() {
        TweaksHelper.runScript(path_to_write, getKey() + " " + getString());
    }

}
