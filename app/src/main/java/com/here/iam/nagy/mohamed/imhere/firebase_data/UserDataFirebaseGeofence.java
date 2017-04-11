package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.AccountSettings;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.CurrentLocation;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Detection;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.FlagsMarkers;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohamednagy on 12/30/2016.
 */
public class UserDataFirebaseGeofence extends UserDataFirebase {

    private ChildEventListener userFlagGeofenceListener;
    private ChildEventListener friendsFlagsGeofenceListener;
    private ChildEventListener publicFlagsGeofenceListener;
    private ChildEventListener detectionGeofenceListener;
    private ValueEventListener userLocationGeofenceListener;
    private ValueEventListener settingsChangedListener;


    public UserDataFirebaseGeofence(String userLinkFirebase) {
        super(userLinkFirebase);
    }

    /**
     * Geofence system build on this rules :-
     * 1- circle region for any flags is 450 meter.
     * 2- circle region for any event location 300 meter.
     * 3- circle region for any clairvoyance is 250 meter.
     * 3- when flag is visited by user, This flag will set on geofence dead state.
     * To active flag again the distance between user and flag center must be
     * equals or more that 800 meter.
     */
    public void setGeofenceSystem(){
        setFlagsToActiveGeofence();
        setDetectionsToActiveGeofence();
        setListenerToUserLocation();
        setSettingsChangedListener();
    }

    public void detachListeners(){
        Log.e("listeners","detached vvvvvvvvvvvvvvvvvvvvv");
        FirebaseHelper.getUserFlag(USER_LINK_FIREBASE)
                .removeEventListener(userFlagGeofenceListener);
        FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                .removeEventListener(friendsFlagsGeofenceListener);
        FirebaseHelper.getPublicFlags()
                .removeEventListener(publicFlagsGeofenceListener);
        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .removeEventListener(userLocationGeofenceListener);
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .removeEventListener(settingsChangedListener);
        FirebaseHelper.getUserDetection(USER_LINK_FIREBASE)
                .removeEventListener(detectionGeofenceListener);
    }

    /**
     * any changing in flags add/change will updated in active geofence.
     */
    private void setFlagsToActiveGeofence(){
        setUserFlagToAvtiveGeofence();
        setFriendsFlagsToActiveGeofence();
        setPublicFlagsToActiveGeofence();
    }
    //done.
    private void setDetectionsToActiveGeofence(){

        FirebaseHelper.getUserDetection(USER_LINK_FIREBASE)
                .addChildEventListener(detectionGeofenceListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Detection detection = dataSnapshot.getValue(Detection.class);
                        FirebaseHelper.getUserDetectionActiveGeofence(USER_LINK_FIREBASE)
                                .child(dataSnapshot.getKey())
                                .setValue(detection);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.e("kkkkkkkkkkkkkkkk","kkkkkkkkkkkkkk");
                        Detection detection = dataSnapshot.getValue(Detection.class);
                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put(dataSnapshot.getKey(),detection);
                        FirebaseHelper.getUserDetectionActiveGeofence(USER_LINK_FIREBASE)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        FirebaseHelper.getUserDetectionActiveGeofence(USER_LINK_FIREBASE)
                                .child(dataSnapshot.getKey())
                                .removeValue();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setUserFlagToAvtiveGeofence(){
        FirebaseHelper.getUserFlag(USER_LINK_FIREBASE)
                .addChildEventListener(userFlagGeofenceListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        FlagsMarkers flagsMarkers =
                                dataSnapshot.getValue(FlagsMarkers.class);

                        hashMap.put(USER_LINK_FIREBASE,flagsMarkers);
                        FirebaseHelper.getUserGeofenceUserFlagsActiveList(USER_LINK_FIREBASE)
                                .setValue(hashMap);
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        HashMap<String,Object> hashMap = new HashMap<String, Object>();

                        hashMap.put(dataSnapshot.getKey(),dataSnapshot.getValue(FlagsMarkers.class));

                        FirebaseHelper.getUserGeofenceUserFlagsActiveList(USER_LINK_FIREBASE)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        FirebaseHelper.getUserGeofenceUserFlagsActiveList(USER_LINK_FIREBASE)
                                .child(dataSnapshot.getKey()).removeValue();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setFriendsFlagsToActiveGeofence(){
        FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                .addChildEventListener(friendsFlagsGeofenceListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot friendFlagMarker, String s) {
                        HashMap<String,Object> hashMap = new HashMap<String, Object>();

                        hashMap.put(friendFlagMarker.getKey(), friendFlagMarker.getValue(FlagsMarkers.class));
                        FirebaseHelper.getUserGeofenceFriendsFlagsActiveList(
                                USER_LINK_FIREBASE).setValue(hashMap);

                        // check location with respect to user.
                        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        CurrentLocation currentUserLocation =
                                                dataSnapshot.getValue(CurrentLocation.class);
                                        // get user location.
                                        final Location userLocation = new Location(Constants.CURRENT_USER_LOCATION_PROVIDER);
                                        if(currentUserLocation != null) {

                                            userLocation.setLongitude(currentUserLocation.getLocationLongitude());
                                            userLocation.setLatitude(currentUserLocation.getLocationLatitude());

                                            checkLocationWithRespectToFlags(
                                                    userLocation,
                                                    friendFlagMarker,
                                                    Constants.FRIENDS_FLAGS);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        HashMap<String,Object> hashMap = new HashMap<String, Object>();

                        hashMap.put(dataSnapshot.getKey(),dataSnapshot.getValue(FlagsMarkers.class));
                        FirebaseHelper.getUserGeofenceFriendsFlagsActiveList(
                                USER_LINK_FIREBASE).updateChildren(hashMap);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        FirebaseHelper.getUserGeofenceFriendsFlagsActiveList(
                                USER_LINK_FIREBASE).child(dataSnapshot.getKey())
                                .removeValue();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void setPublicFlagsToActiveGeofence(){

        FirebaseHelper.getPublicFlags()
                .addChildEventListener(publicFlagsGeofenceListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot dataSnapshot1, String s) {
                        if(dataSnapshot1.getKey().equals(USER_LINK_FIREBASE))
                            return;
                        HashMap<String,Object> hashMap = new HashMap<String, Object>();

                        hashMap.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(FlagsMarkers.class));
                        FirebaseHelper.getUserGeofencePublicFlagsActiveList(
                                USER_LINK_FIREBASE).updateChildren(hashMap);

                        // check location with respect to user.
                        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        CurrentLocation currentUserLocation =
                                                dataSnapshot.getValue(CurrentLocation.class);
                                        // get user location.
                                        final Location userLocation = new Location(Constants.CURRENT_USER_LOCATION_PROVIDER);
                                        if(currentUserLocation != null) {

                                            userLocation.setLongitude(currentUserLocation.getLocationLongitude());
                                            userLocation.setLatitude(currentUserLocation.getLocationLatitude());

                                            checkLocationWithRespectToFlags(
                                                    userLocation,dataSnapshot1,
                                                    Constants.PUBLIC_FLAGS);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        HashMap<String,Object> hashMap = new HashMap<String, Object>();
                        Log.e("caaled","changed");
                        hashMap.put(dataSnapshot.getKey(),dataSnapshot.getValue(FlagsMarkers.class));
                        FirebaseHelper.getUserGeofencePublicFlagsActiveList(
                                USER_LINK_FIREBASE)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        FirebaseHelper.getUserGeofencePublicFlagsActiveList(
                                USER_LINK_FIREBASE)
                                .child(dataSnapshot.getKey()).removeValue();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setListenerToUserLocation(){
        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .addValueEventListener(userLocationGeofenceListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CurrentLocation currentUserLocation =
                                dataSnapshot.getValue(CurrentLocation.class);
                        // get user location.
                        final Location userLocation = new Location(Constants.CURRENT_USER_LOCATION_PROVIDER);
                        if(currentUserLocation != null) {

                            userLocation.setLongitude(currentUserLocation.getLocationLongitude());
                            userLocation.setLatitude(currentUserLocation.getLocationLatitude());
                            Log.e("location called","changed");
                            checkLocationWithRespectToFlags(userLocation);
                            checkLocationWithRespectToDetections(userLocation);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    // done.
    private void checkLocationWithRespectToDetections(final Location location){
        Log.e("vvvvvvvvvvvvvvvvvvvv1","vvvvvvvvvvvvv");
        FirebaseHelper.getUserDetectionActiveGeofence(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("vvvvvvvvvvvvvvvvvvvv2","vvvvvvvvvvvvv");

                        for(final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            final Detection DETECTION_USER = dataSnapshot1.getValue(Detection.class);
                            Log.e("dddddd",dataSnapshot1.getKey());
                            FirebaseHelper.getUserAccountSettings(dataSnapshot1.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e("vvvvvvvvvvvvvvvvvvvv3","vvvvvvvvvvvvv");

                                            AccountSettings accountSettings =
                                                    dataSnapshot.getValue(AccountSettings.class);

                                            if(accountSettings.getUserState() == Constants.USER_ONLINE){
                                                Log.e("vvvvvvvvvvvvvvvvvvvv4","vvvvvvvvvvvvv");

                                                Location location1 =
                                                        new Location(Constants.NEW_LOCATION_PROVIDER);
                                                location1.setLongitude(
                                                        DETECTION_USER.getCurrentLocation()
                                                        .getLocationLongitude());
                                                location1.setLatitude(
                                                        DETECTION_USER.getCurrentLocation()
                                                        .getLocationLatitude()
                                                );

                                                float distance = location.distanceTo(location1);
                                                Log.e("distance",String.valueOf(distance));
                                                if(distance <= Constants.DETECTION_RADIUS &&
                                                        !DETECTION_USER.isDetected()){
                                                    Log.e("vvvvvvvvvvvvvvvvvvvv5",String.valueOf(DETECTION_USER.isDetected()));
                                                    ///TODO .. send notification.
                                                    final String notificationText  =
                                                            "You are nearby from " + DETECTION_USER
                                                            .getUserAccount().getUserName();
                                                    List<String> notificationData =
                                                            new ArrayList<String>();
                                                    notificationData.add(
                                                            DETECTION_USER.getUserAccount()
                                                            .getUserEmail());

                                                    Notification notification =
                                                            new Notification(
                                                                    DETECTION_USER.getUserAccount().getUserImage(),
                                                                    null,
                                                                    notificationText,
                                                                    notificationData,
                                                                    Constants.NOTIFICATION_DETECTION

                                                            );

                                                    FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                                                            .push().setValue(notification);
                                                    UserDataFirebaseMainActivity
                                                            .newNotificationsNumberIncrement(USER_LINK_FIREBASE);

                                                    DETECTION_USER.setDetected(true);

                                                    FirebaseHelper.getUserDetection(USER_LINK_FIREBASE)
                                                            .child(dataSnapshot1.getKey())
                                                            .setValue(DETECTION_USER);

                                                }else if(distance >= Constants.DETECTION_ACTIVE &&
                                                        DETECTION_USER.isDetected()){
                                                    Log.e("detective","sass");
                                                    DETECTION_USER.setDetected(false);
                                                    FirebaseHelper.getUserDetection(USER_LINK_FIREBASE)
                                                            .child(dataSnapshot1.getKey())
                                                            .setValue(DETECTION_USER);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void checkLocationWithRespectToFlags(final Location location){
        // check user settings first.
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        AccountSettings accountSettings =
                                dataSnapshot.getValue(AccountSettings.class);

                        // set flags with respect to user.

//                        checkLocationWithRespectToUserFlags(location);

                        if(accountSettings.getShowFriendsFlags()){
                            checkLocationWithRespectToFriendsFlags(location);
                        }
                        if(accountSettings.getShowPublicFlags()){
                            Log.e("public flag","checkLocationWithRespectToFlags()");
                            checkLocationWithRespectToPublicFlags(location);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void checkLocationWithRespectToFlags(final Location location,
                                                 final DataSnapshot dataSnapshot1,
                                                 final String FLAG_TYPE){
        // check user settings first.
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        AccountSettings accountSettings =
                                dataSnapshot.getValue(AccountSettings.class);

                        // set flags with respect to user.

//                        checkLocationWithRespectToUserFlags(location);

                        if(accountSettings.getShowFriendsFlags() && FLAG_TYPE.equals(Constants.FRIENDS_FLAGS)){
                            checkLocationWithRespectToFriendsFlags(location, dataSnapshot1);
                        }else if(accountSettings.getShowPublicFlags() && FLAG_TYPE.equals(Constants.PUBLIC_FLAGS)){
                            Log.e("public flag","checkLocationWithRespectToFlags()()()");
                            checkLocationWithRespectToPublicFlags(location, dataSnapshot1);
                        }

                        Log.e("done","ooooooooopopopopopopo");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void checkLocationWithRespectToUserFlags(final Location userLocation){
        FirebaseHelper.getUserGeofenceUserFlagsActiveList(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get user flags
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            /// visited no handle with flag owner.
                            FlagsMarkers userFlag = dataSnapshot1.getValue(FlagsMarkers.class);
                            Location userFlagLocation = new Location(Constants.NEW_LOCATION_PROVIDER);

                            userFlagLocation.setLongitude(userFlag.getFlagLocation().getLocationLongitude());
                            userFlagLocation.setLatitude(userFlag.getFlagLocation().getLocationLatitude());

                            float distance = userLocation.distanceTo(userFlagLocation);

                            if(distance <= 450){
                                /// TODO ... Notification ...

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /// One Flag
    private void checkLocationWithRespectToFriendsFlags(final Location userLocation,
                                                        final DataSnapshot dataSnapshot1){
        Location friendFlagLocation = new Location(Constants.NEW_LOCATION_PROVIDER);

        FlagsMarkers friendFlag = dataSnapshot1.getValue(FlagsMarkers.class);

        friendFlagLocation.setLongitude(friendFlag.getFlagLocation().getLocationLongitude());
        friendFlagLocation.setLatitude(friendFlag.getFlagLocation().getLocationLatitude());

        float distance = userLocation.distanceTo(friendFlagLocation);

        if(!isFlagVisited(friendFlag)) {

            if (distance <= Constants.FLAG_RADIUS) {

                FirebaseHelper.getUserGeofenceFriendsFlagsActiveList(USER_LINK_FIREBASE)
                        .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                        .child(USER_LINK_FIREBASE).setValue(true);
                // set flag is visited.
                FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                        .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                        .child(USER_LINK_FIREBASE).setValue(true);

                List<String> notificationData = new ArrayList<String>();
                notificationData.add(dataSnapshot1.getKey());
                notificationData.add(Constants.FRIENDS_FLAGS);

                String notificationFirstText = "You can check this Flag";
                String notificationSecondText =
                        friendFlag.getUserCreatedName() + " flag is nearby.";

                Notification friendsFlagNotification
                        = new Notification(
                        friendFlag.getImage(),
                        notificationFirstText,
                        notificationSecondText,
                        notificationData,
                        Constants.NOTIFICATION_FLAG
                );

                removeAndAddFlagNotification(
                        dataSnapshot1.getKey(),
                        friendsFlagNotification);
            }
        }else{

            if(distance >= Constants.DISTANCE_ACTIVE_FLAG){
                // set flag is not visited.
                FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                        .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                        .child(USER_LINK_FIREBASE).setValue(false);
            }

        }
    }

    private void checkLocationWithRespectToFriendsFlags(final Location userLocation){
        FirebaseHelper.getUserGeofenceFriendsFlagsActiveList(
                USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get friends flags
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            //TODO .. why this check... !!
                            if(dataSnapshot1.getKey().equals(USER_LINK_FIREBASE))
                                continue;
                            FlagsMarkers friendFlag = dataSnapshot1.getValue(FlagsMarkers.class);

                            Location friendFlagLocation = new Location(Constants.NEW_LOCATION_PROVIDER);
                            friendFlagLocation.setLongitude(friendFlag.getFlagLocation().getLocationLongitude());
                            friendFlagLocation.setLatitude(friendFlag.getFlagLocation().getLocationLatitude());

                            float distance = userLocation.distanceTo(friendFlagLocation);

                            if(!isFlagVisited(friendFlag)) {

                                if (distance <= Constants.FLAG_RADIUS) {

                                    FirebaseHelper.getUserGeofenceFriendsFlagsActiveList(USER_LINK_FIREBASE)
                                            .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                                            .child(USER_LINK_FIREBASE).setValue(true);
                                    // set flag is visited.
                                    FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                                            .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                                            .child(USER_LINK_FIREBASE).setValue(true);

                                    List<String> notificationData = new ArrayList<String>();
                                    notificationData.add(dataSnapshot1.getKey());
                                    notificationData.add(Constants.FRIENDS_FLAGS);

                                    String notificationFirstText = "You can check this Flag";
                                    String notificationSecondText =
                                            friendFlag.getUserCreatedName() + " flag is nearby.";

                                    Notification friendsFlagNotification
                                            = new Notification(
                                            friendFlag.getImage(),
                                            notificationFirstText,
                                            notificationSecondText,
                                            notificationData,
                                            Constants.NOTIFICATION_FLAG
                                    );

                                    removeAndAddFlagNotification(
                                            dataSnapshot1.getKey(),
                                            friendsFlagNotification);

                                }
                            }else{

                                if(distance >= Constants.DISTANCE_ACTIVE_FLAG){
                                    // set flag is not visited.
                                    FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                                            .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                                            .child(USER_LINK_FIREBASE).setValue(false);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void checkLocationWithRespectToPublicFlags(final Location userLocation, final DataSnapshot dataSnapshot1){
        FlagsMarkers publicFlag = dataSnapshot1.getValue(FlagsMarkers.class);

        Location publicFlagLocation = new Location(Constants.NEW_LOCATION_PROVIDER);


        publicFlagLocation.setLongitude(publicFlag.getFlagLocation().getLocationLongitude());
        publicFlagLocation.setLatitude(publicFlag.getFlagLocation().getLocationLatitude());

        float distance = userLocation.distanceTo(publicFlagLocation);

        if(!isFlagVisited(publicFlag)) {

            if (distance <= Constants.FLAG_RADIUS) {
                Log.e("called","done");

                // To prevent two notifications....
                FirebaseHelper.getUserGeofencePublicFlagsActiveList(USER_LINK_FIREBASE)
                        .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                        .child(USER_LINK_FIREBASE).setValue(true);

                // set flag is visited.
                FirebaseHelper.getPublicFlags()
                        .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                        .child(USER_LINK_FIREBASE).setValue(true);

                /// TODO ... Notification ...
                List<String> notificationData = new ArrayList<String>();
                notificationData.add(dataSnapshot1.getKey());
                notificationData.add(Constants.PUBLIC_FLAGS);


                String notificationFirstText = "You can check this Flag";
                String notificationSecondText =
                        publicFlag.getTitle() + "'s public flag is nearby.";

                Notification publicFlagNotification
                        = new Notification(
                        publicFlag.getImage(),
                        notificationFirstText,
                        notificationSecondText,
                        notificationData,
                        Constants.NOTIFICATION_FLAG
                );

                removeAndAddFlagNotification(
                        dataSnapshot1.getKey(),
                        publicFlagNotification);

            }
        }else{

            if(distance >= Constants.DISTANCE_ACTIVE_FLAG){
                // set flag is not visited.
                FirebaseHelper.getPublicFlags()
                        .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                        .child(USER_LINK_FIREBASE).setValue(false);
            }

        }

    }


    private void checkLocationWithRespectToPublicFlags(final Location userLocation){
        Log.e("public flag","before geofencelist");
        FirebaseHelper.getUserGeofencePublicFlagsActiveList(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get public flags
                        Log.e("public flag","after geofencelist");
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(dataSnapshot1.getKey().equals(USER_LINK_FIREBASE))
                                continue;

                            Log.e("public flag","in for loop");
                            FlagsMarkers publicFlag = dataSnapshot1.getValue(FlagsMarkers.class);

                            Location publicFlagLocation = new Location(Constants.NEW_LOCATION_PROVIDER);

                            publicFlagLocation.setLongitude(publicFlag.getFlagLocation().getLocationLongitude());
                            publicFlagLocation.setLatitude(publicFlag.getFlagLocation().getLocationLatitude());

                            float distance = userLocation.distanceTo(publicFlagLocation);

                            if(!isFlagVisited(publicFlag)) {

                                if (distance <= Constants.FLAG_RADIUS) {

                                    // To prevent two notifications....
                                    FirebaseHelper.getUserGeofencePublicFlagsActiveList(USER_LINK_FIREBASE)
                                            .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                                            .child(USER_LINK_FIREBASE).setValue(true);

                                    // set flag is visited.
                                    FirebaseHelper.getPublicFlags()
                                            .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                                            .child(USER_LINK_FIREBASE).setValue(true);

                                    /// TODO ... Notification ...
                                    List<String> notificationData = new ArrayList<String>();
                                    notificationData.add(dataSnapshot1.getKey());
                                    notificationData.add(Constants.PUBLIC_FLAGS);


                                    String notificationFirstText = "You can check this Flag";
                                    String notificationSecondText =
                                            publicFlag.getTitle() + "'s public flag is nearby.";

                                    Notification publicFlagNotification
                                            = new Notification(
                                            publicFlag.getImage(),
                                            notificationFirstText,
                                            notificationSecondText,
                                            notificationData,
                                            Constants.NOTIFICATION_FLAG
                                    );

                                    removeAndAddFlagNotification(
                                            dataSnapshot1.getKey(),
                                            publicFlagNotification);

                                }
                            }else{

                                if(distance >= Constants.DISTANCE_ACTIVE_FLAG){
                                    // set flag is not visited.
                                    FirebaseHelper.getPublicFlags()
                                            .child(dataSnapshot1.getKey()).child(Constants.FLAG_VISITORS)
                                            .child(USER_LINK_FIREBASE).setValue(false);
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private boolean isFlagVisited(FlagsMarkers flagsMarkers){
        if(flagsMarkers.getIsVisited() != null &&
                flagsMarkers.getIsVisited().containsKey(USER_LINK_FIREBASE)){
            return flagsMarkers.getIsVisited().get(USER_LINK_FIREBASE);
        }
        return false;
    }

    private void setSettingsChangedListener(){

        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addValueEventListener(settingsChangedListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final AccountSettings accountSettings =
                                dataSnapshot.getValue(AccountSettings.class);

                        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        CurrentLocation currentUserLocation =
                                                dataSnapshot.getValue(CurrentLocation.class);
                                        // get user location.
                                        final Location userLocation = new Location(Constants.CURRENT_USER_LOCATION_PROVIDER);
                                        if(currentUserLocation != null) {

                                            userLocation.setLongitude(currentUserLocation.getLocationLongitude());
                                            userLocation.setLatitude(currentUserLocation.getLocationLatitude());



                                            if(accountSettings.getShowFriendsFlags()){
                                                checkLocationWithRespectToFriendsFlags(userLocation);
                                            }

                                            if(accountSettings.getShowPublicFlags()){
                                                Log.e("public flag","setSettingsChangedListener");
                                                checkLocationWithRespectToPublicFlags(userLocation);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void removeAndAddFlagNotification(final String FLAG_KEY,final Notification notification){

        FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isExist = false;

                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(dataSnapshot1.getKey().equals(FLAG_KEY)){
                                isExist = true;
                                break;
                            }
                        }

                        if(isExist) {
                            FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                                    .child(FLAG_KEY).removeValue();
                        }
                            // send notification app
                            FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                                    .child(FLAG_KEY).setValue(notification);
                            // increment new notifications text
                            UserDataFirebaseMainActivity.newNotificationsNumberIncrement();
                            // TODO ... send notification device.

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
