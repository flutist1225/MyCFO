package com.changda123.www.mycfo;

import android.util.Log;

public class MyLog {
    private static final int MYLOG_LEVEL_VERBOSE = 0;
    private static final int MYLOG_LEVEL_DEBUG = 1;
    private static final int MYLOG_LEVEL_INFO  = 2;
    private static final int MYLOG_LEVEL_WARN  = 3;
    private static final int MYLOG_LEVEL_ERROR = 4;

    private static int mLogLevelThreshold = MYLOG_LEVEL_VERBOSE;

    public static boolean setPrintLevel(int iLevel){
        if((iLevel > MYLOG_LEVEL_ERROR) || (iLevel < 0)){
            return false;
        }
        mLogLevelThreshold = iLevel;
        return true;
    }

    public static void i(String tag, String message){
        if((BuildConfig.LOG_DEBUG) && (mLogLevelThreshold <= MYLOG_LEVEL_INFO)) {
            Log.i(tag, message);
        }
    }
    public static void d(String tag, String message){
        if((BuildConfig.LOG_DEBUG) && (mLogLevelThreshold <= MYLOG_LEVEL_DEBUG)) {
            Log.d(tag, message);
        }
    }
    public static void v(String tag, String message){
        if((BuildConfig.LOG_DEBUG) && (mLogLevelThreshold == MYLOG_LEVEL_VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void w(String tag, String message){
        if(mLogLevelThreshold <= MYLOG_LEVEL_WARN) {
            Log.w(tag, message);
        }
    }
    public static void e(String tag, String message){
        Log.e(tag,message);
    }
}
