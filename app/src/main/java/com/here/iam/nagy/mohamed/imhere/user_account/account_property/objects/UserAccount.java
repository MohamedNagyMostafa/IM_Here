package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by mohamednagy on 12/23/2016.
 */
public class UserAccount {

    private String userName;
    private String userEmail;
    private String userImage;
    private HashMap<String,Object> createdAccountTime;

    public  UserAccount(){}

    public UserAccount(String userName,
                       String userEmail,
                       String userImage){

        this.userEmail = userEmail;
        this.userName = userName;
        this.userImage = userImage;

        createdAccountTime = new HashMap<>();
        createdAccountTime.put("date", ServerValue.TIMESTAMP);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserImage() {
        return userImage;
    }

    public HashMap<String, Object> getCreatedAccountTime() {
        return createdAccountTime;
    }

}
