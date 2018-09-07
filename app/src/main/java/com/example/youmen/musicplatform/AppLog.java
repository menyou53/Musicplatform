package com.example.youmen.musicplatform;

/**
 * Created by youmen on 2017/12/18.
 */
import android.util.Log;

public class AppLog {
    private static final String APP_TAG = "AudioRecorder";

    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }
}