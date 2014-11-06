package com.lyapota.util;


public class GunCorrector {

    public static boolean isSupported() { return true; }

    public static int getNumberOfControls() {
        return 1;
    }

    public static boolean setGuns(int controlIdx, String gamma) {
        return true;
    }

    public static int getMaxValue(int controlIdx) {
        return 20;
    }

    public static int getMinValue(int controlIdx) {
        return -20;
    }

    public static String getCurGuns(int controlIdx) {
        return "0 0 0 0";
    }
}

