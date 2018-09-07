package com.example.youmen.musicplatform;

/**
 * Created by youmen on 2017/9/27.
 */

public class User_info {
    private String Email;
    private String User_id;
    private String User;
    private String Upload;

    public User_info() {
    }


    public User_info(String Email, String User_id) {
        this.Email = Email;
        this.User_id = User_id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getUpload() {
        return Upload;
    }

    public void setUpload(String upload) {
        Upload = upload;
    }
}