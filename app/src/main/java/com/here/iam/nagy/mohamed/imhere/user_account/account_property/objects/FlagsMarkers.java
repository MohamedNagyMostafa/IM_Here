package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

import java.util.HashMap;

/**
 * Created by mohamednagy on 12/29/2016.
 */
public class FlagsMarkers {

    private String userCreatedName;
    private String title;
    private String image;
    private String details;
    private int likesNumber;
    private CurrentLocation flagLocation;
    private HashMap<String,Boolean> isVisited;
    private HashMap<String,Boolean> flagLovers;

    public FlagsMarkers(){}

    public FlagsMarkers(String userCreatedName, String title, String image,
                        String details, int likesNumber,
                        CurrentLocation flagLocation){
        this.userCreatedName = userCreatedName;
        this.title = title;
        this.likesNumber = likesNumber;
        this.image = image;
        this.details = details;
        this.flagLocation = flagLocation;
        isVisited = null;
        flagLovers = null;
    }


    public int getLikesNumber() {
        return likesNumber;
    }

    public String getDetails() {
        return details;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getUserCreatedName() {
        return userCreatedName;
    }

    public CurrentLocation getFlagLocation() {
        return flagLocation;
    }

    public HashMap<String, Boolean> getIsVisited() {
        return isVisited;
    }

    public HashMap<String, Boolean> getFlagLovers() {
        return flagLovers;
    }
}
