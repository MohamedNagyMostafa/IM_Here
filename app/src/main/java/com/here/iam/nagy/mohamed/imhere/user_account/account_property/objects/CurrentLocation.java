package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

import android.support.annotation.Nullable;

/**
 * Created by mohamednagy on 12/27/2016.
 */
public class CurrentLocation {

    private Double locationLongitude;
    private Double locationLatitude;
    private String locationName;

    public CurrentLocation(){}

    public CurrentLocation(@Nullable Double locationLongitude,
                           @Nullable Double locationLatitude,
                           @Nullable String locationName){

        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationName = locationName;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public String getLocationName() {
        return locationName;
    }
}
