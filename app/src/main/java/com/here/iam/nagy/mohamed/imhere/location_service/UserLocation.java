package com.here.iam.nagy.mohamed.imhere.location_service;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

/**
 * Created by mohamednagy on 12/27/2016.
 */
public class UserLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private UserLocationCallback userLocationCallback;

    public  UserLocation(UserLocationCallback userLocationCallback){
        this.userLocationCallback = userLocationCallback;
        userLocationCallback.createGoogleClient(this,this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        userLocationCallback.onConnected(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        userLocationCallback.onConnectionSuspended();
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocationCallback.onLocationChanged(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        userLocationCallback.onConnectionFailed();
    }

}
