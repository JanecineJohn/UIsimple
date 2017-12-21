package com.example.ending.uisimple.javabean;

/**
 * Created by dell on 2017/12/21.
 */

public class MidJoinner {
    private String name;
    private String studentId;

    public MidJoinner(String name,String studentId){
        this.name = name;
        this.studentId = studentId;
    }

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getStudentId(){return studentId;}
    public void setStudentId(String studentId){this.studentId = studentId;}
}
