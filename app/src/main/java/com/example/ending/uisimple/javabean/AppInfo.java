package com.example.ending.uisimple.javabean;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by dell on 2017/12/22.
 * * set系列方法暂时无用
 */

public class AppInfo implements Comparable<AppInfo>{
    private Drawable appIcon;
    private String appLabel;
    private String appLTS;
    private long appUsedTime;

    public AppInfo(Drawable appIcon,String appLabel,long appUsedTime){
        this.appIcon = appIcon;
        this.appLabel = appLabel;
        this.appUsedTime = appUsedTime;
    }

    public AppInfo(Drawable appIcon,String appLabel,String appLTS){
        this.appIcon = appIcon;
        this.appLabel = appLabel;
        this.appLTS = appLTS;
    }

    public String getAppLabel(){
        return appLabel;
    }
    public void setAppLabel(String appLabel){
        this.appLabel = appLabel;
    }

    public Drawable getAppIcon(){
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon){
        this.appIcon = appIcon;
    }

    public String getAppLTS(){
        return appLTS;
    }
    public void setAppLTS(String appLTS){
        this.appLTS = appLTS;
    }

    public long getAppUsedTime(){return appUsedTime;}
    public void setAppUsedTime(long appUsedTime){this.appUsedTime = appUsedTime;}

    @Override
    public int compareTo(@NonNull AppInfo appInfo) {
        if (this.getAppUsedTime()-appInfo.getAppUsedTime() > 0){
            return -1;
        }else {
            return 1;
        }
    }
}
