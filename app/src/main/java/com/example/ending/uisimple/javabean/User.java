package com.example.ending.uisimple.javabean;

/**
 * Created by xin on 2017/12/9.
 */

public class User {
    /*
    connection in mysql
     */
    private String uid;
    private String username;
    private String password;
    private String email;
    private String code;
    private boolean state;
    private String phone;
    private int uclassNumber;
    private int havingClass; //未创建上课为-1，否则为课堂号
    private String trueName;
    private String schoolId;

    public User(){
    }

    public int getHavingClass() {
        return havingClass;
    }

    public void setHavingClass(int havingClass) {
        this.havingClass = havingClass;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public int getUclassNumber() {
        return uclassNumber;
    }

    public void setUclassNumber(int uclassNumber) {
        this.uclassNumber = uclassNumber;
    }


    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", state=" + state +
                ", phone='" + phone + '\'' +
                ", uclassNumber=" + uclassNumber +
                '}';
    }
}