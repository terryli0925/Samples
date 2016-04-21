package com.terry.samples.utils;

import android.util.Log;

/**
 * Created by terry on 2016/4/21.
 */
public class LogUtils {

    private static final boolean DEBUG = true;


    public static void print(String tag, String msg) {
        if (DEBUG) Log.i(tag, msg);
    }
}
