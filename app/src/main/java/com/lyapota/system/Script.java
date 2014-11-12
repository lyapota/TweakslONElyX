package com.lyapota.system;

import com.lyapota.util.TweaksHelper;

public class Script extends SystemClass {

    @Override
    public void read() {
        setValue(TweaksHelper.runFromAssets(path_to_read, getKey() + " " + getString()));
    }

    @Override
    public void write() {
        TweaksHelper.runFromAssets(path_to_write, getKey() + " " + getString());
    }

}
