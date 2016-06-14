package com.nowfloats.util;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * Created by Boost Android on 10/05/2016.
 */
public class BoostLog {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private BoostLog() {
    }

    public static void v(String tag, String msg) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
            tr.printStackTrace();
        }
    }

    public static void d(String tag, String msg) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
            getStackTraceString(tr);
        }
    }

    public static void i(String tag, String msg) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
            getStackTraceString(tr);
        }
    }


    public static void w(String tag, String msg) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
            getStackTraceString(tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+getStackTraceString(tr));
        }
    }

    public static void e(String tag, String msg) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (!Constants.APK_MODE_RELEASE) {
            Log.v(Constants.APP_TAG,tag+msg);
            getStackTraceString(tr);
        }
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
