package com.example.ending.uisimple.javabean;

/**
 * Created by dell on 2017/12/20.
 */

public class Joinner {
    private int classId;//课堂号
    private String uid;//学生id
    private String informationJson;//其他信息

    public Joinner(String uid,int classId,String informationJson){
        this.uid = uid;
        this.classId = classId;
        this.informationJson = informationJson;
    }

    public int getClassId(){return classId;}
    public void setClassId(int classId){this.classId = classId;}

    public String getUid(){return uid;}
    public void setUid(String uid){this.uid = uid;}

    public String getInformationJson(){return informationJson;}
    public void setInformationJson(String informationJson){this.informationJson = informationJson;}
}
