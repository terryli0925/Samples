package com.terry.samples.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by terry on 2016/4/21.
 */
public class LogUtils {

    private static final boolean DEBUG = true;


    public static void print(String tag, String msg) {
        if (DEBUG) Log.i(tag, msg);
    }

    public static void printStringList(String tag, List<String> list) {
        if (DEBUG) Log.i(tag, TextUtils.join(System.getProperty("line.separator"), list));
    }
}
