package com.here.iam.nagy.mohamed.imhere.location_service;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

/**
 * Created by mohamednagy on 12/30/2016.
 */
public interface UserLocationCallback {
    void createGoogleClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                            GoogleApiClient.OnConnectionFailedListener connectionFailedListener);

    void onConnected(LocationListener locationListener);
    void onConnectionSuspended();
    void onConnectionFailed();
    void onLocationChanged(Location location);

}
