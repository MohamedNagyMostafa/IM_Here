package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

/**
 * Created by mohamednagy on 12/28/2016.
 */
public class MapMarkers {

    private CurrentLocation currentLocation;
    private String userName;
    private String userImage;

    public MapMarkers(){}

    public MapMarkers(CurrentLocation currentLocation, String userName,
                      String userImage){
        this.currentLocation = currentLocation;
        this.userName = userName;
        this.userImage = userImage;
    }

    public CurrentLocation getCurrentLocation() {
        return currentLocation;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }
}
