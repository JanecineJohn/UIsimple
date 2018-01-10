package com.example.ending.uisimple.javabean;

import java.util.List;

/**
 * Created by dell on 2018/1/10.
 */

public class OnClassInfo {
    private String trueName;
    private String schoolId;
    private List<AppInfo> appInfoList;

    public OnClassInfo(String trueName,String schoolId,List<AppInfo> appInfoList){
        this.trueName = trueName;
        this.schoolId = schoolId;
        this.appInfoList = appInfoList;
    }

    public String getTrueName(){return  trueName;}
    public void setTrueName(String trueName){this.trueName = trueName;}

    public String getSchoolID(){return schoolId;}
    public void setSchoolID(String schoolId){this.schoolId = schoolId;}

    public List<AppInfo> getAppList(){return appInfoList;}
    public void setAppList(List<AppInfo> appInfoList){this.appInfoList = appInfoList;}
}
