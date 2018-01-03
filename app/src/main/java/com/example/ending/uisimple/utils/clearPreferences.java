package com.example.ending.uisimple.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dell on 2017/12/27.
 */

public class clearPreferences {

    public static void clearAppInfo(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("appInfo",Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static void clearUserInfo(Context context){
        //删除enterTime,classId,trueName,schoolId,
        SharedPreferences.Editor editor = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE).edit();
        editor.remove("enterTime");
        editor.remove("webAddress");
        editor.remove("classId");
        editor.remove("trueName");
        editor.remove("schoolId");
        editor.apply();
    }
}
