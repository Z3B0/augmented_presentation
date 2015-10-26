package se.chalmers.ocuclass.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import se.chalmers.ocuclass.net.BaseResponse;

/**
 * Created by richard on 24/09/15.
 */
public class User implements Serializable{




    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private User() {

    }


    private String deviceId = null;

    public String getDeviceId() {
        return deviceId;
    }

    public static User password(String username, String password){
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        return user;
    };

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User(String username, String name, String accountType) {
        this.username = username;
        this.name = name;
        this.accountType = accountType;
    }

    public String getPassword() {
        return password;
    }

    public enum UserType{

        @SerializedName("teacher")
        TEACHER("teacher"),
        @SerializedName("student")
        STUDENT("student");


        private final String value;
        UserType(String value) {
            this.value = value;
        }

    }

    private String id;
    private String username;
    private String name;
    private String password;
    private UserType userType = UserType.STUDENT;
    private String accountType = null;

    public String getAccountType() {
        return accountType;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserType getUserType() {
        return userType;
    }
}
