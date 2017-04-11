package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

/**
 * Created by mohamednagy on 3/29/2017.
 */
public class Detection {

    private UserAccount userAccount;
    private CurrentLocation currentLocation;
    private boolean isDetected;

    public Detection(){};

    public Detection(UserAccount userAccount, CurrentLocation currentLocation, boolean isDetected){
        this.userAccount = userAccount;
        this.currentLocation = currentLocation;
        this.isDetected = isDetected;
    }

    public CurrentLocation getCurrentLocation() {
        return currentLocation;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public boolean isDetected() {
        return isDetected;
    }

    public void setDetected(boolean detected) {
        isDetected = detected;
    }
}
