package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

/**
 * Created by mohamednagy on 12/27/2016.
 */
public class AccountSettings {

    private boolean locationAppearToFriends;
    private boolean showPublicFlags;
    private boolean showFriendsFlags;
    private boolean helpModeActive;
    private int detectionMode;
    private int userState;

    public AccountSettings(){}

    public AccountSettings(boolean locationAppearToFriends,
                           boolean showPublicFlags, boolean showFriendsFlags,
                           boolean helpModeActive, int detectionMode,
                           int userState){

        this.locationAppearToFriends = locationAppearToFriends;
        this.showFriendsFlags = showFriendsFlags;
        this.showPublicFlags = showPublicFlags;
        this.helpModeActive = helpModeActive;
        this.detectionMode = detectionMode;
        this.userState = userState;

    }

    public boolean getLocationAppearToFriends(){
        return locationAppearToFriends;
    }

    public boolean getShowFriendsFlags(){
        return showFriendsFlags;
    }

    public boolean getShowPublicFlags(){
        return showPublicFlags;
    }

    public boolean isHelpModeActive() {
        return helpModeActive;
    }

    public void setHelpModeActive(boolean helpModeActive){
        this.helpModeActive = helpModeActive;
    }

    public int getDetectionMode() {
        return detectionMode;
    }

    public int getUserState() {
        return userState;
    }
}
