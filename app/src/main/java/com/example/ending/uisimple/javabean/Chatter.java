package com.example.ending.uisimple.javabean;

/**
 * Created by dell on 2017/12/18.
 */

public class Chatter {
    private String userName;
    private String schoolId;
    private Boolean isStudent;//区分学生和创建者(false为创建者，true为学生)
    private String message;//聊天信息
    private int classId;//课堂号

    //构造函数1
    public Chatter(){}
    //构造函数2
    public Chatter(String userName,String schoolId,Boolean isStudent,String message,int classId){
        this.userName = userName;
        this.schoolId = schoolId;
        this.isStudent = isStudent;
        this.message = message;
        this.classId = classId;
    }
    public String getUserName(){return userName;}
    public void setUserName(String userName){this.userName = userName;}

    public String getSchoolId(){return schoolId;}
    public void setSchoolId(String schoolId){this.schoolId = schoolId;}

    public Boolean getIsStudent(){return isStudent;}
    public void setIsStudent(Boolean isStudent){this.isStudent = isStudent;}


    public String getMessage(){return message;}
    public void setMessage(String message){this.message = message;}

    public int getClassId(){return classId;}
    public void setClassId(int classId){this.classId = classId;}
}
