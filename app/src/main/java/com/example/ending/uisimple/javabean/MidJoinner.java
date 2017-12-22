package com.example.ending.uisimple.javabean;

/**
 * Created by dell on 2017/12/21.
 */

public class MidJoinner {
    private String trueName;
    private String studentId;

    public MidJoinner(String name,String studentId){
        this.trueName = name;
        this.studentId = studentId;
    }

    public String getName(){return trueName;}
    public void setName(String name){this.trueName = name;}

    public String getStudentId(){return studentId;}
    public void setStudentId(String studentId){this.studentId = studentId;}
}
