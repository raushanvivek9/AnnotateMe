package com.example.anno_tool.Model;

public class UserDetailNote {
    private String username;
    private String uname;
    private String uid;
    private String phone_no;
    private String deviceToken;
    private String imageId;
    public UserDetailNote() {
    }
    public UserDetailNote(String username, String uname, String uid, String phone_no, String deviceToken) {
        this.username = username;
        this.uname = uname;
        this.uid = uid;
        this.phone_no = phone_no;
        this.deviceToken = deviceToken;
    }

    public UserDetailNote(String username, String uname, String uid, String phone_no, String deviceToken, String imageId) {
        this.username = username;
        this.uname = uname;
        this.uid = uid;
        this.phone_no = phone_no;
        this.deviceToken = deviceToken;
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
