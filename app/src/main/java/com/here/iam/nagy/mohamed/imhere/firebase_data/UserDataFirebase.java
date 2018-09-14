package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.flag.CreateNewFlag;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.AccountSettings;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.CurrentLocation;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Detection;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.MapMarkers;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Notification;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Track;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mohamednagy on 12/24/2016.
 */
public class UserDataFirebase {

    protected final String USER_LINK_FIREBASE;
    private ValueEventListener userDataListeners;
    private ValueEventListener userLocationSettingsListeners;
    private UserProfileUi userProfileUi;
    private LocationRequest locationRequest;
    private Context context;

    private ValueEventListener userDetectionLocationListener =
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final CurrentLocation currentLocation = dataSnapshot.getValue(CurrentLocation.class);

                                    FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                        FirebaseHelper.getUserDetection(
                                                                Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                                                .child(USER_LINK_FIREBASE)
                                                                .child(Constants.CURRENT_DETECTION_LOCATION)
                                                                .setValue(currentLocation);
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


    public UserDataFirebase(String userLinkFirebase, UserProfileUi userProfileUi, Context context){
        this.USER_LINK_FIREBASE = userLinkFirebase;
        this.userProfileUi = userProfileUi;
        this.context = context;
    }

    public void setUserIdentifiers(){
        assignListeners();
        setListeners();
    }

    public UserDataFirebase(String userLinkFirebase){
        this.USER_LINK_FIREBASE = userLinkFirebase;
    }

    public void setListeners(){
//        Log.e("set lsitener","done");
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addValueEventListener(userDataListeners);

        // detection.
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AccountSettings accountSettings = dataSnapshot.getValue(AccountSettings.class);
                        if(accountSettings.getDetectionMode() == Constants.USER_DETECTION_ACTIVE){
                            FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                                    .addValueEventListener(userDetectionLocationListener);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void assignListeners(){
//        Log.e("assignListeners called","done");

        userDataListeners = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e("just listener","done");
                userProfileUi.setUserIdentifierData(getUserIdentifiersData(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    public void detachProfileListener(){
//        Log.e("detach called","done");
        if(userDataListeners != null)
            FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                    .removeEventListener(userDataListeners);

        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .removeEventListener(userDetectionLocationListener);

    }

    public void detachLocationListener(){
        if(userLocationSettingsListeners != null)
            FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                    .removeEventListener(userLocationSettingsListeners);
    }

    protected UserAccount getUserIdentifiersData(DataSnapshot dataSnapshot){
        return dataSnapshot.getValue(UserAccount.class);
    }

    public String getUserLinkFirebase(){
        return USER_LINK_FIREBASE;
    }

    /**
     * This method store the user profile image to firebase storage.
     * if it stored in storage, the image is load to database of firebase which
     * make change in userdata and sets it's image to imageview.
     * @param imageUri
     */
    public void storeUserImageInDatabase(final Uri imageUri){
        FirebaseHelper.getUserImageStorageReference(USER_LINK_FIREBASE)
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Map<String,Object> userImage  = new HashMap<String, Object>();

                        userImage.put("userImage",taskSnapshot.getUploadSessionUri().getPath());

                        FirebaseHelper.USERS_DATABASE_REFERENCE.child(USER_LINK_FIREBASE).updateChildren(userImage);

                    }
                });
    }

    /// Friends Relation Process.

    /**
     * Send Request from user A to user B.
     * Put email of user A to friend request list of user B.
     * Put email of user B to friend request send list of user A.
     * @param USER_EMAIL
     */
    public void sendFriendRequest(final String USER_EMAIL){

        FirebaseHelper.getUserFriendRequestList(Utility.encodeUserEmail(USER_EMAIL))
                .push().setValue(Utility.decodeUserEmail(USER_LINK_FIREBASE));

        FirebaseHelper.getUserFriendRequestsSend(USER_LINK_FIREBASE).push().setValue(USER_EMAIL);

        /// send Notification
        sendFriendRequestNotification(USER_EMAIL);
        // increment notifications number.
        UserDataFirebaseMainActivity.newNotificationsNumberIncrement(
                Utility.encodeUserEmail(USER_EMAIL), context);
    }

    /**
     * add both users to friends list and remove request friend
     */
    public void acceptFriendRequest(final String USER_EMAIL){

        // Remove both users email from request friend list
        removeFriendRequestAFromB(Utility.decodeUserEmail(USER_LINK_FIREBASE),USER_EMAIL);
        removeFriendRequestAFromB(USER_EMAIL,Utility.decodeUserEmail(USER_LINK_FIREBASE));
        removeFriendSendRequestAFromB(Utility.decodeUserEmail(USER_LINK_FIREBASE),USER_EMAIL);

        // add both users to friends list ..
        FirebaseHelper.getUserFriendList(Utility.encodeUserEmail(USER_EMAIL))
                .push().setValue(Utility.decodeUserEmail(USER_LINK_FIREBASE));

        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                .push().setValue(USER_EMAIL);

        // send notification
        sendFriendAcceptNotification(USER_EMAIL);
        // increment notifications number.
        UserDataFirebaseMainActivity.newNotificationsNumberIncrement(
                Utility.encodeUserEmail(USER_EMAIL), context);
        // Add user options.
        setUserDetectionAToB(USER_LINK_FIREBASE, Utility.encodeUserEmail(USER_EMAIL));
        setUserDetectionAToB(Utility.encodeUserEmail(USER_EMAIL), USER_LINK_FIREBASE);

    }

    private void setUserDetectionAToB(final String USER_A_EMAIL, final String USER_B_EMAIL){
        FirebaseHelper.getUserAccountSettings(USER_A_EMAIL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AccountSettings accountSettings = dataSnapshot.getValue(AccountSettings.class);

                        if(accountSettings.getDetectionMode() == Constants.DETECTION_ACTIVE){

                            FirebaseHelper.getUserDatabaseReference(USER_A_EMAIL)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            UserAccount userAccountA =
                                                    dataSnapshot.getValue(UserAccount.class);
                                            CurrentLocation currentLocationA =
                                                    dataSnapshot.child(Constants.USER_LOCATION)
                                                    .getValue(CurrentLocation.class);

                                            final Detection DETECTION_USER_A  =
                                                    new Detection(userAccountA, currentLocationA, false);

                                            HashMap<String, Object> hashMap =
                                                    new HashMap<String, Object>();
                                            hashMap.put(USER_A_EMAIL, DETECTION_USER_A);

                                            FirebaseHelper.getUserDetection(USER_B_EMAIL)
                                                    .setValue(hashMap);
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
    /**
     * remove friend from friends
     */
    public void removeFriendFromListFriend(final String USER_EMAIL){

        removeFriendAFromFriendListB(Utility.decodeUserEmail(USER_LINK_FIREBASE),USER_EMAIL);
        removeFriendAFromFriendListB(USER_EMAIL,Utility.decodeUserEmail(USER_LINK_FIREBASE));
        // remove flags.
        removeFriendFlagAFromFriendB(Utility.encodeUserEmail(USER_EMAIL),USER_LINK_FIREBASE);
        removeFriendFlagAFromFriendB(USER_LINK_FIREBASE,Utility.encodeUserEmail(USER_EMAIL));
        // remove marker.
        removeFriendMarkerAFromFriendB(Utility.encodeUserEmail(USER_EMAIL),USER_LINK_FIREBASE);
        removeFriendMarkerAFromFriendB(USER_LINK_FIREBASE,Utility.encodeUserEmail(USER_EMAIL));
        // remove geofence.
        removeFriendGeofenceAFromFriendB(Utility.encodeUserEmail(USER_EMAIL),USER_LINK_FIREBASE);
        removeFriendGeofenceAFromFriendB(USER_LINK_FIREBASE,Utility.encodeUserEmail(USER_EMAIL));
        // remove detection.
        removeFriendDetectionAFromFriendB(Utility.encodeUserEmail(USER_EMAIL), USER_LINK_FIREBASE);
        removeFriendDetectionAFromFriendB(USER_LINK_FIREBASE, Utility.encodeUserEmail(USER_EMAIL));
    }

    /**
     * ignore friend request
     */
    public void ignoreFriendRequest(final String USER_EMAIL){
        removeFriendRequestAFromB(USER_EMAIL, Utility.decodeUserEmail(USER_LINK_FIREBASE));
        removeFriendSendRequestAFromB(Utility.decodeUserEmail(USER_LINK_FIREBASE), USER_EMAIL);
        /// remove notification send
        removeFriendRequestBNotificationFromA(Utility.decodeUserEmail(USER_LINK_FIREBASE),USER_EMAIL);
    }

    /**
     * remove user from send friend request
     */
    public void cancelFriendSendRequestBySender(final String USER_EMAIL){

        removeFriendRequestAFromB(Utility.decodeUserEmail(USER_LINK_FIREBASE),USER_EMAIL);
        removeFriendSendRequestAFromB(USER_EMAIL,Utility.decodeUserEmail(USER_LINK_FIREBASE));

        removeFriendRequestBNotificationFromA(USER_EMAIL, Utility.decodeUserEmail(USER_LINK_FIREBASE));
        UserDataFirebaseMainActivity.newNotificationsNumberDecrement(Utility.encodeUserEmail(USER_EMAIL), context);

    }

    /// Removing data from Firebase

    private void removeFriendRequestAFromB(final String USER_A_EMAIL, final String USER_B_EMAIL){
        FirebaseHelper.getUserFriendRequestList(Utility.encodeUserEmail(USER_B_EMAIL))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(dataSnapshot1.getValue().equals(USER_A_EMAIL)){
                                FirebaseHelper.getUserFriendRequestList(Utility.encodeUserEmail(USER_B_EMAIL))
                                        .child(dataSnapshot1.getKey()).removeValue();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void removeFriendSendRequestAFromB(final String USER_A_EMAIL, final String USER_B_EMAIL){

        FirebaseHelper.getUserFriendRequestsSend(Utility.encodeUserEmail(USER_B_EMAIL))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(dataSnapshot1.getValue().equals(USER_A_EMAIL)){
                                FirebaseHelper.getUserFriendRequestsSend(Utility.encodeUserEmail(USER_B_EMAIL))
                                        .child(dataSnapshot1.getKey()).removeValue();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void removeFriendAFromFriendListB(final String USER_A_EMAIL, final String USER_B_EMAIL){

        FirebaseHelper.getUserFriendList(Utility.encodeUserEmail(USER_B_EMAIL))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("search","Search in friend list of " + USER_B_EMAIL + " To remove " + USER_A_EMAIL );
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Log.e("search process "," value = " + dataSnapshot1.getValue());
                            if(dataSnapshot1.getValue().equals(USER_A_EMAIL)){
                                FirebaseHelper.getUserFriendList(Utility.encodeUserEmail(USER_B_EMAIL))
                                        .child(dataSnapshot1.getKey()).removeValue();
                                Log.e("user is founded","removed from friend");
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void removeFriendDetectionAFromFriendB(final String USER_A_EMAIL, final String USER_B_EMAIL){
        FirebaseHelper.getUserDetection(USER_B_EMAIL).child(USER_A_EMAIL).removeValue();
    }

    private void removeFriendFlagAFromFriendB(final String USER_A_EMAIL, final String USER_B_EMAIL){
        FirebaseHelper.getUserFriendsFlags(USER_B_EMAIL).child(USER_A_EMAIL).removeValue();
    }

    private void removeFriendMarkerAFromFriendB(final String USER_A_EMAIL, final String USER_B_EMAIL){
        FirebaseHelper.getUserMapFriendMarks(USER_B_EMAIL).child(USER_A_EMAIL).removeValue();
    }

    private void removeFriendGeofenceAFromFriendB(final String USER_A_EMAIL, final String USER_B_EMAIL) {
        FirebaseHelper.getUserGeofenceFriendsFlagsActiveList(USER_B_EMAIL).child(USER_A_EMAIL).removeValue();
    }
    /// Notification process

    protected void sendFriendRequestNotification(final String USER_EMAIL){
        // get User account information.

        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        UserAccount notificationAccountData = dataSnapshot.getValue(UserAccount.class);

                        String notificationInf = "Want to be friend";
                        List<String> notificationData = new ArrayList<String>();
                        notificationData.add(Utility.decodeUserEmail(USER_LINK_FIREBASE));
                        /// TODO Validation image
                        Log.e("user name",notificationAccountData.getUserName());
                        Notification notification =
                                new Notification(notificationAccountData.getUserImage(),
                                        notificationAccountData.getUserName(),notificationInf,
                                        notificationData,Constants.NOTIFICATION_FRIEND_REQUEST);

                        FirebaseHelper.getUserNotifications(Utility.encodeUserEmail(USER_EMAIL))
                                .push().setValue(notification);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    protected void sendFriendAcceptNotification(final String USER_EMAIL){
        /// Notification to current user.
    Log.e("send","notification send is called");
        FirebaseHelper.getUserDatabaseReference(Utility.encodeUserEmail(USER_EMAIL))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        UserAccount notificationAccountData = dataSnapshot.getValue(UserAccount.class);

                        String notificationInf =
                                "You and " + notificationAccountData.getUserName() + " become friends.";

                        List<String> notificationData = new ArrayList<>();
                        notificationData.add(USER_EMAIL);

                        final Notification notification =
                                new Notification(notificationAccountData.getUserImage(),
                                        notificationAccountData.getUserName(),notificationInf,
                                        notificationData,Constants.NOTIFICATION_ORIGINAL);

                        /// update notification.
                        FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                                            Notification notification1 =
                                                    dataSnapshot1.getValue(Notification.class);
                                            if(notification1.getNotificationData().get(0).equals(USER_EMAIL) &&
                                                    notification1.getNotificationType() == Constants.NOTIFICATION_FRIEND_REQUEST){

                                                FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                                                        .child(dataSnapshot1.getKey()).setValue(notification);
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

        // Notification to the other user.

        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        UserAccount notificationAccountData = dataSnapshot.getValue(UserAccount.class);

                        String notificationInf =
                                "You and " + notificationAccountData.getUserName() + "become friends.";
                        List<String> notificationData = new ArrayList<String>();
                        notificationData.add(Utility.decodeUserEmail(USER_LINK_FIREBASE));
                        Notification notification =
                                new Notification(notificationAccountData.getUserImage(),
                                        notificationAccountData.getUserName(),notificationInf,
                                        notificationData,Constants.NOTIFICATION_ORIGINAL);

                        FirebaseHelper.getUserNotifications(Utility.encodeUserEmail(USER_EMAIL)).push().setValue(notification);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    protected void removeFriendRequestBNotificationFromA(
            final String USER_A_EMAIL, final String USER_B_EMAIL){

        FirebaseHelper.getUserNotifications(Utility.encodeUserEmail(USER_A_EMAIL))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            Notification notification = dataSnapshot1.getValue(Notification.class);

                            if(notification.getNotificationData().equals(USER_B_EMAIL) &&
                                    notification.getNotificationType() == Constants.NOTIFICATION_FRIEND_REQUEST){

                                FirebaseHelper.getUserNotifications(Utility.encodeUserEmail(USER_A_EMAIL))
                                        .child(dataSnapshot1.getKey()).setValue(null);
                                break;
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // Account Settings process

    public void setLocationFriendsVisibility(boolean locationFriendVisibility){

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put(Constants.LOCATION_APPEAR_TO_FRIENDS,locationFriendVisibility);

        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .updateChildren(hashMap);
    }

    public void setPublicFlagsShow(boolean publicFlagsShow){

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Constants.SHOW_PUBLIC_FLAGS,publicFlagsShow);

        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .updateChildren(hashMap);
    }

    public void setFriendsFlagsShow(boolean friendsFlagsShow){

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Constants.SHOW_FRIENDS_FLAGS,friendsFlagsShow);

        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .updateChildren(hashMap);
    }

    // Map process

    /**
     * add User mark to all his friends
     */
    public void addUserMarkToFriendsMap(){
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnapshot) {
                        UserAccount userAccount = userSnapshot.getValue(UserAccount.class);
                        CurrentLocation currentLocation = userSnapshot.child(Constants.USER_LOCATION)
                                .getValue(CurrentLocation.class);
                        MapMarkers mapMarkers = new MapMarkers(currentLocation,userAccount.getUserName(),
                                userAccount.getUserImage());

                        final HashMap<String,Object> hashMap = new HashMap<String, Object>();

                        hashMap.put(Utility.encodeUserEmail(userAccount.getUserEmail()), mapMarkers);
                        for(DataSnapshot friendsSnapshot : userSnapshot.child(Constants.USER_FRIENDS_LIST).getChildren()){
                            FirebaseHelper.getUserMapFriendMarks(Utility.encodeUserEmail(friendsSnapshot.getValue(String.class)))
                                    .setValue(hashMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * remove user mark from all his friends
     */
    public void removeUserMarkFromFriendsMap(){
        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            FirebaseHelper.getUserMapFriendMarks(
                                    Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                    .child(USER_LINK_FIREBASE).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * check state of user location visible
     */
    public void setSwitchesState(final ViewAppHolder.UserProfileViewHolder userProfileViewHolder){
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AccountSettings accountSettings = dataSnapshot.getValue(AccountSettings.class);
                        userProfileViewHolder.RECEIVE_FRIEND_FLAGS
                                .setChecked(accountSettings.getShowFriendsFlags());
                        userProfileViewHolder.RECEIVE_PUBLIC_FLAGS
                                .setChecked(accountSettings.getShowPublicFlags());
                        userProfileViewHolder.LOCATION_FRIEND_VISIBILITY
                                .setChecked(accountSettings.getLocationAppearToFriends());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Set User Location
     */
    public void updateUserLocation(Location location){
        CurrentLocation currentLocation =
                new CurrentLocation(location.getLongitude(),location.getLatitude()," ");

        Track trackPos = new Track(location.getLatitude(), location.getLongitude(), Utility.getCurrentDate());

        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .setValue(currentLocation);
        FirebaseHelper.getUserTrack(USER_LINK_FIREBASE)
                .push().setValue(trackPos);
    }

    public void setRequestDependOnSettings(final LocationCallback locationCallback,
                                           final GoogleApiClient googleApiClient, final Context context){
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addValueEventListener(userLocationSettingsListeners = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AccountSettings accountSettings = dataSnapshot.getValue(AccountSettings.class);
                            Log.e("location settings","checked and set new location interval");
                        try { // remove previous request.
                                LocationServices.getFusedLocationProviderClient(context)
                                        .removeLocationUpdates(
                                                locationCallback
                                        );

                            locationRequest = LocationRequest.create();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                            if(accountSettings.isHelpModeActive()){
                                Log.e("Help me interval ","is set");
                                locationRequest.setInterval(Constants.HELP_MODE);
                            }else if(!accountSettings.getLocationAppearToFriends()){
                                locationRequest.setInterval(Constants.IDLE_MODE);
                                Log.e("idle interval ","is set");

                            }else{
                                locationRequest.setInterval(Constants.ACTIVITY_MODE);
                                Log.e("active interval ","is set");

                            }

                            LocationServices.getFusedLocationProviderClient(context)
                                    .requestLocationUpdates(
                                            locationRequest ,locationCallback, null
                                    );

                        }catch (SecurityException e){

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Help Handling.
     */

    public void setHelpProcess(final ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
                               final Context context,final boolean inStart){

        // Change Help state.
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AccountSettings accountSettings = dataSnapshot.getValue(AccountSettings.class);

                        if(accountSettings != null && accountSettings.isHelpModeActive()){

                            if(!inStart) {
                                deleteHelpRequest();
                                accountSettings.setHelpModeActive(false);
                                FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                                        .setValue(accountSettings);
                            }

                            setHelpSafeModeUi(
                                    userProfileViewHolder,
                                    context);
                        }else{

                            if(!inStart) {
                                sendHelpRequest();
                                accountSettings.setHelpModeActive(true);
                                FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                                        .setValue(accountSettings);
                            }

                            setHelpSafeModeUi(
                                    userProfileViewHolder,
                                    context);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void sendHelpRequest(){
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);

                        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final MapMarkers userMarker = new MapMarkers(
                                                dataSnapshot.getValue(CurrentLocation.class),
                                                userAccount.getUserName(),
                                                userAccount.getUserImage()
                                        );

                                        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        HashMap<String,Object> hashMap = new HashMap<String, Object>();
                                                        hashMap.put(USER_LINK_FIREBASE,userMarker);
                                                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                                            Log.e("done","frined " + dataSnapshot1.getKey());
                                                            FirebaseHelper.getUserHelpRequest(
                                                                    Utility.encodeUserEmail(dataSnapshot1.getValue(String.class))
                                                            )
                                                                    .updateChildren(hashMap);

                                                            // send notification.
                                                            sendHelpNotification(
                                                                    Utility.encodeUserEmail(dataSnapshot1.getValue(String.class))
                                                            );
                                                            // increment notifications number.
                                                            UserDataFirebaseMainActivity.newNotificationsNumberIncrement(
                                                                    Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)), context);
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void deleteHelpRequest(){
        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            FirebaseHelper.getUserHelpRequest(Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                    .child(USER_LINK_FIREBASE).removeValue();
                            Log.e("help ",dataSnapshot1.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendHelpNotification(final String USER_EMAIL){
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<String> notificationData = new ArrayList<String>();
                        notificationData.add(dataSnapshot.getValue(UserAccount.class).getUserEmail());
                        Notification notification
                                = new Notification(
                                dataSnapshot.getValue(UserAccount.class).getUserImage(),
                                "Help Request",
                                dataSnapshot.getValue(UserAccount.class).getUserName(),
                                notificationData,
                                Constants.NOTIFICATION_HELP);

                        FirebaseHelper.getUserNotifications(USER_EMAIL)
                                .push().setValue(notification);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void setHelpSafeModeUi(
            final ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
            final Context context){

        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AccountSettings accountSettings =
                                dataSnapshot.getValue(AccountSettings.class);

                        if(accountSettings.isHelpModeActive()){
                            // set red color
                            setIconsColor(userProfileViewHolder,
                                    context.getResources().getColorStateList(R.color.help_color));
                            setLinesColor(userProfileViewHolder,
                                    context.getResources().getColor(R.color.help_color));

                            // set visible
                            setMapButtonInvisible(true, userProfileViewHolder);

                            //Checkable
                            setCheckAble(false, userProfileViewHolder);

                        }else{

                            setIconsColor(userProfileViewHolder,
                                    context.getResources().getColorStateList(R.color.safe_color));
                            setLinesColor(userProfileViewHolder,
                                    context.getResources().getColor(R.color.safe_color));

                            // set visible
                            setMapButtonInvisible(false, userProfileViewHolder);

                            //Checkable
                            setCheckAble(true, userProfileViewHolder);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setCheckAble(boolean state,
                              ViewAppHolder.UserProfileViewHolder userProfileViewHolder){

        userProfileViewHolder.LOCATION_FRIEND_VISIBILITY.setEnabled(state);
        userProfileViewHolder.RECEIVE_FRIEND_FLAGS.setEnabled(state);
        userProfileViewHolder.RECEIVE_PUBLIC_FLAGS.setEnabled(state);
        userProfileViewHolder.CREATE_FLAG_LAYOUT.setEnabled(state);
        userProfileViewHolder.DETECTION_LAYOUT.setEnabled(state);


        if(state){

            userProfileViewHolder.LOCATION_FRIEND_VISIBILITY.setAlpha(1f);
            userProfileViewHolder.RECEIVE_FRIEND_FLAGS.setAlpha(1f);
            userProfileViewHolder.RECEIVE_PUBLIC_FLAGS.setAlpha(1f);
            userProfileViewHolder.CREATE_FLAG_LAYOUT.setAlpha(1f);
            userProfileViewHolder.DETECTION_LAYOUT.setAlpha(1f);



        }else{

            userProfileViewHolder.LOCATION_FRIEND_VISIBILITY.setAlpha(0.5f);
            userProfileViewHolder.RECEIVE_FRIEND_FLAGS.setAlpha(0.5f);
            userProfileViewHolder.RECEIVE_PUBLIC_FLAGS.setAlpha(0.5f);
            userProfileViewHolder.CREATE_FLAG_LAYOUT.setAlpha(0.5f);
            userProfileViewHolder.DETECTION_LAYOUT.setAlpha(0.5f);



        }

    }

    private void setMapButtonInvisible(boolean state,
                                       ViewAppHolder.UserProfileViewHolder userProfileViewHolder){
        if(state)
            userProfileViewHolder.MAP_BUTTON.setVisibility(View.GONE);
        else
            userProfileViewHolder.MAP_BUTTON.setVisibility(View.VISIBLE);

    }

    private void setIconsColor(ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
                               ColorStateList color){

        userProfileViewHolder.SIGN_OUT_ICON.setImageTintList(color);
        userProfileViewHolder.CREATE_FLAG_ICON.setImageTintList(color);
        userProfileViewHolder.HELP_ME_ICON.setImageTintList(color);
        userProfileViewHolder.DETECTION_ICON.setImageTintList(color);

    }

    private void setLinesColor(ViewAppHolder.UserProfileViewHolder userProfileViewHolder,int color){
        userProfileViewHolder.LINE_ONE.setBackgroundColor(color);
        userProfileViewHolder.LINE_TWO.setBackgroundColor(color);
        userProfileViewHolder.LINE_FOUR.setBackgroundColor(color);
        userProfileViewHolder.LINE_FIVE.setBackgroundColor(color);
        userProfileViewHolder.LINE_SIX.setBackgroundColor(color);
        userProfileViewHolder.LINE_THREE.setBackgroundColor(color);

    }

    public void setCreateNewFlagListenerAction(final Context context){
        FirebaseHelper.getUserFlag(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){

                            Intent flagIntent = new Intent(context, CreateNewFlag.class);
                            flagIntent.putExtra(Constants.USER_EXTRA,USER_LINK_FIREBASE);

                            context.startActivity(flagIntent);
                        }else{
                            Toast.makeText(context,
                                    "You created one before, Please remove it first",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void setUserDetection(final int USER_DETECTION_MODE,
                                 ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
                                 Context context){
        Log.e(String.valueOf(USER_DETECTION_MODE),"doooooooneee");

        switch (USER_DETECTION_MODE){
            case Constants.USER_DETECTION_INACTIVE:
                setDetectionProcess(userProfileViewHolder, context);
                break;
            case Constants.USER_DETECTION_ACTIVE:
                removeDetectiveProcess(userProfileViewHolder, context);
                break;
        }
    }
    // done.
    private void setDetectionProcess(ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
                                     Context context){
        // set detection mode to account settings
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .child(Constants.DETECTION_MODE).setValue(Constants.USER_DETECTION_ACTIVE);

        userProfileViewHolder.DETECTION_ICON.setImageTintList(
                context.getResources().getColorStateList(R.color.active_tab));

        // add to friends map.
        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .addValueEventListener(userDetectionLocationListener);
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                        CurrentLocation currentLocation =
                                dataSnapshot.child(Constants.USER_LOCATION).getValue(CurrentLocation.class);

                        final Detection USER_DETECTION = new Detection(userAccount, currentLocation, false);

                        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                            FirebaseHelper.getUserDetection(Utility.encodeUserEmail(
                                                    dataSnapshot1.getValue(String.class)))
                                                    .child(USER_LINK_FIREBASE).setValue(USER_DETECTION);
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

    public void removeDetectiveProcess(ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
                                       Context context){
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .child(Constants.DETECTION_MODE).setValue(Constants.USER_DETECTION_INACTIVE);

        userProfileViewHolder.DETECTION_ICON.setImageTintList(
                context.getResources().getColorStateList(R.color.safe_color));

        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            FirebaseHelper.getUserDetection(Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                    .child(USER_LINK_FIREBASE).removeValue();
                        }
                        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                                .removeEventListener(userDetectionLocationListener);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void detectionButtonClicked(final ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
                                       final Context context){
        Log.e("called detector setting","doooooooneee");
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AccountSettings accountSettings = dataSnapshot.getValue(AccountSettings.class);
                        Log.e("is called  process set","doooooooneee");

                        setUserDetection(accountSettings.getDetectionMode(),
                                userProfileViewHolder, context);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void setDetectionState(final ViewAppHolder.UserProfileViewHolder userProfileViewHolder,
                                  final Context context){
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(AccountSettings.class).getDetectionMode()
                                == Constants.USER_DETECTION_ACTIVE){
//                            Log.e("iscalled","lololololo");
                            userProfileViewHolder.DETECTION_ICON.setImageTintList(
                                    context.getResources().getColorStateList(R.color.active_tab));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void setUserState(final ViewAppHolder.FriendsListViewHolder friendsListViewHolder,
                             final String USER_EMAIL){

        FirebaseHelper.getUserActiveMode(Utility.encodeUserEmail(USER_EMAIL))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Integer USER_STATE = dataSnapshot.getValue(Integer.class);
//                        Log.e("nnnnnnnnnnnnnnnnnn",String.valueOf(USER_STATE));
//                        Log.e(friendsListViewHolder.FRIEND_NAME_TEXT_VIEW.getText().toString(),String.valueOf(USER_STATE));


                        switch (USER_STATE){
                            case Constants.USER_ONLINE:
                                friendsListViewHolder.FRIEND_STATE_TEXT_VIEW.setText(R.string.online);
                                friendsListViewHolder.FRIEND_STATE_TEXT_VIEW.setTextColor(Color.GREEN);
                                break;
                            case Constants.USER_OFFLINE:
                                friendsListViewHolder.FRIEND_STATE_TEXT_VIEW.setText(R.string.offline);
                                friendsListViewHolder.FRIEND_STATE_TEXT_VIEW.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
