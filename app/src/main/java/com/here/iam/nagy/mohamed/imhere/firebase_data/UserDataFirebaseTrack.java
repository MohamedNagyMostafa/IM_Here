package com.here.iam.nagy.mohamed.imhere.firebase_data;

import com.google.android.gms.maps.model.LatLng;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.CurrentLocation;

import java.util.HashMap;

/**
 * Created by Mohamed Nagy on 9/14/2018 .
 * Project IM_Here
 * Time    8:44 AM
 */
public class UserDataFirebaseTrack {

    public void updateUserTrack(final String USER_EMAIL, LatLng newPosition){
        HashMap<Double, Double> hashMap = new HashMap<>();
        CurrentLocation currentLocation = new CurrentLocation(
                newPosition.latitude, newPosition.longitude, "");

        FirebaseHelper.getUserTrack(USER_EMAIL).setValue()
    }
}
