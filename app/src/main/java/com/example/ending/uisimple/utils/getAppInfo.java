package com.example.ending.uisimple.utils;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.example.ending.uisimple.javabean.AppInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/12/22.
 * **获取APP使用情况
 * 获取包名：usageStats.getPackageName()
 * 获取第一次运行时间：usageStats.getFirstTimeStamp()
 * 获取最近一次运行时间：usageStats.getLastTimeStamp()
 * 获取总运行时间：usageStats.getTotalTimeInForeground()
 * 将获得的long型时间戳格式化为"yyyy-MM-dd HH:mm:ss"：
 *                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 *                  sdf.format(new Date(long型时间戳));
 *
 * 新加一个功能：获取应用图标()
 *
 * 注意：
 * 某段时间的使用次数可以由 使用次数(下课签到) - 使用次数(上课签到) 得到
 * 其中，
 * 上课签到时，要用Daily来获取次数,endTime为扫描二维码的时间，startTime为扫描前1小时
 * 下课签到，也要用Daily来获取次数，endTime为现在，startTime为扫描二维码时间
 * */

public class getAppInfo {
    public List getappinfo(Context context){
        List<AppInfo> appInfoList = new ArrayList<>();
        //加入版本控制
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
//            ActivityManager activityManager =
//                    (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//            List<ActivityManager.RunningTaskInfo> appTasks =
//                    activityManager.getRunningTasks(1);
//            if (null != appTasks && !appTasks.isEmpty()){
//                return appTasks;
//            }
//        }
        //安卓版本大于5.0的，使用以下方法
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            //获取一段时间内(40分钟)手机应用的运行情况,把详细信息放到list中
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInformation;//存放应用详细信息
            Drawable appIcon;//存放应用图标
            String PN;//存放应用名称
            String str_LTS;//存放应用最近使用时间
            AppInfo appInfo;//AppInfo实例，用来存储图标，名称和时间
            UsageStatsManager usm = (UsageStatsManager)
                    context.getSystemService(Context.USAGE_STATS_SERVICE);
            //startTime和endTime指的是这一段时间内系统日志里的信息(因为系统日志信息时不时更新，所以时间间隔太短会导致获取信息为空)
            long endTime = System.currentTimeMillis();
            long startTime = endTime - 40*60*1000;
            List<UsageStats> list = usm.queryUsageStats
                    (UsageStatsManager.INTERVAL_DAILY,startTime,endTime);

            //如果list的大小为0，意味着没有权限获取APP信息，需要用户授予权限
            if (list.size() == 0){
                try {
                    context.startActivity(new Intent
                            (Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }catch (Exception e){
                    Toast.makeText(context,
                            "无法开启授权界面",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }else{
                //定义时间格式，将时间戳转化为此格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //用循环的方式将每一个应用信息提取出来
                for (UsageStats usageStats : list){
                    //获取此应用最近一次使用时间，并和开始监测时间作比较
                    long LTS = usageStats.getLastTimeStamp();
                    if (LTS > startTime){
                        try {
                            //根据包名取得应用的详细信息，放在ApplicationInfo对象中
                            appInformation = pm.getApplicationInfo(usageStats.getPackageName(),PackageManager.GET_META_DATA);
                            //根据ApplicationInfo(应用详细信息)获得应用名称和图标
                            PN = pm.getApplicationLabel(appInformation).toString();
                            appIcon = pm.getApplicationIcon(appInformation);

                            str_LTS = sdf.format(new Date(LTS));
                            appInfo = new AppInfo(appIcon,PN,str_LTS);
                            appInfoList.add(appInfo);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        //获取应用启动次数，UsageStats未提供方法获取，只能通过反射得到
                        /*try{
                            Field field = usageStats.getClass().getDeclaredField("mLaunchCount");
                            show.append("使用次数：" + field.getInt(usageStats) + '\n');
                        }catch (Exception e){
                            e.printStackTrace();
                        }*/

                    }
                }
            }
        }

        return appInfoList;
    }
    /**获取App方法完成*/
}
