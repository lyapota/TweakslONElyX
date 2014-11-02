package com.lyapota.util;

/*
 * Display gamma calibration
 *
 * Exports methods to get the valid value boundaries, the
 * number of available RGB triplets, the current color values (per triplet id)
 * and a method to set new ones (also per triplet).
 *
 * Values exported by min/max can be the direct values required
 * by the hardware, or a local (to DisplayGammaCalibration) abstraction
 * that's internally converted to something else prior to actual use. The
 * Settings user interface will present the raw values
 *
 *
 */

public class GammaCorrector {

    /*
     * All HAF classes should export this boolean.
     * Real implementations must, of course, return true
     */

    public static boolean isSupported() { return true; }

    /*
     * How many RGB triplets does the device export?
     *
     * The most common case here should be 1, but some hardware
     * has multiplier combos
     */

    public static int getNumberOfControls() {
        return 1;
    }

    /*
     * Set the RGB values to the given input triplet on this control
     * index. Input is expected to consist of a zero-indexed control
     * id, and a string containing 3 values, space-separated, each of
     * those a value between the boundaries set by get(Max|Min)Value
     * (see below), and it's meant to be locally interpreted/used.
     */

    public static boolean setGamma(int controlIdx, String gamma) {
        return true;
    }

    /*
     * What's the control specific maximum integer value
     * we take for a color
     */

    public static int getMaxValue(int controlIdx) {
        return 255;
    }

    /*
     * What's the control specific minimum integer value
     * we take for a color
     */

    public static int getMinValue(int controlIdx) {
        return 1;
    }

    /*
     * What's the current RGB triplet for this control?
     * This should return a space-separated set of integers in
     * a string, same format as the input to setColors()
     */

    public static String getCurGamma(int controlIdx) {
        return "255 255 255";
    }
}

