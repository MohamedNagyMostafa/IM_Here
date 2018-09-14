package com.here.iam.nagy.mohamed.imhere.helper_classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by mohamednagy on 12/23/2016.
 */
public final class FirebaseHelper {

    /** Firebase **/
    public static final FirebaseDatabase FIREBASE_DATABASE =
            FirebaseDatabase.getInstance();

    public static final DatabaseReference DATABASE_REFERENCE =
            FIREBASE_DATABASE.getReference().child(Constants.HERE_APP);

    public static final DatabaseReference USERS_DATABASE_REFERENCE =
            DATABASE_REFERENCE.child(Constants.USERS);

    public static final DatabaseReference PUBLIC_FLAGS_DATABASE_REFERENCE =
            DATABASE_REFERENCE.child(Constants.PUBLIC_FLAGS);

    public static final FirebaseStorage FIREBASE_STORAGE =
            FirebaseStorage.getInstance();

    public static final StorageReference USER_IMAGE_STORAGE_REFERENCE =
            FIREBASE_STORAGE.getReference().child(Constants.IMAGES);

    public static final StorageReference USER_FLAGS_STORAGE_REFERENCE =
            FIREBASE_STORAGE.getReference().child(Constants.FLAGS);

    /**
     * Get DataReference For specific user.
     * @param USER_LINK_FIREBASE
     * @return
     */
    public static DatabaseReference getUserDatabaseReference(final String USER_LINK_FIREBASE){
        return USERS_DATABASE_REFERENCE.child(USER_LINK_FIREBASE);
    }

    /**
     * Get user image reference from storage.
     * @param USER_LINK_FIREBASE
     * @return
     */
    public static StorageReference getUserImageStorageReference(final String USER_LINK_FIREBASE){
        return USER_IMAGE_STORAGE_REFERENCE.child(USER_LINK_FIREBASE);
    }

    /**
     * Get flag image reference from storage.
     */
    public static StorageReference getUserFlagImageStorageReference(final String USER_LINK_FIREBASE){
        return USER_FLAGS_STORAGE_REFERENCE.child(USER_LINK_FIREBASE);
    }

    /**
     * Get notifications_profile_recycle reference for specific user.
     * @param USER_LINK_FIREBASE
     * @return
     */
    public static DatabaseReference getUserNotifications(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_NOTIFICATIONS);
    }

    /**
     * Get friends request list for specific user by email.
     * @param USER_LINK_FIREBASE
     * @return
     */
    public static DatabaseReference getUserFriendRequestList(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_FRIEND_REQUEST_LIST);
    }

    /**
     * Remove user from request list and add both users to firends list.
     */
    public static DatabaseReference getUserFriendList(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_FRIENDS_LIST);
    }

    /**
     * Get user requests
     */
    public static DatabaseReference getUserFriendRequestsSend(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE)
                .child(Constants.USER_REQUESTS).child(Constants.USER_FRIEND_REQUESTS);
    }

    public static DatabaseReference getUserLocationRequestsSend(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE)
                .child(Constants.USER_REQUESTS).child(Constants.USER_FRIEND_LOCATION_REQUESTS);
    }

    /**
     * Get user location
     */
    public static DatabaseReference getUserLocation(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_LOCATION);
    }

    /**
     * Get User account settings
     */
    public static DatabaseReference getUserAccountSettings(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_ACCOUNT_SETTINGS);
    }

    /**
     * Get user map friend marks
     */
    public static DatabaseReference getUserMapFriendMarks(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_MAP)
                .child(Constants.MARKS).child(Constants.FRIENDS_MARKS);
    }

    /**
     * Get public flags
     */
    public static DatabaseReference getPublicFlags(){
        return PUBLIC_FLAGS_DATABASE_REFERENCE;
    }

    /**
     * Get Friends flags
     */
    public static DatabaseReference getUserFriendsFlags(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_MAP)
                .child(Constants.FLAGS).child(Constants.FRIENDS_FLAGS);
    }

    /**
     * Get User Flag
     */
    public static DatabaseReference getUserFlag(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_MAP)
                .child(Constants.FLAGS).child(Constants.USER_FLAGS);
    }

    /**
     * Get Help List
     */
    public static DatabaseReference getUserHelpRequest(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_MAP)
                .child(Constants.HELP);
    }

    /**
     * Get user geofence
     */
    public static DatabaseReference getUserGeofenceEventsActiveList(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.GEOFENCES)
                .child(Constants.ACTIVE).child(Constants.EVENTS);
    }

    private static DatabaseReference getUserGeofenceFlagsActiveList(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.GEOFENCES)
                .child(Constants.ACTIVE).child(Constants.FLAGS);
    }

    public static DatabaseReference getUserGeofenceUserFlagsActiveList(final String USER_LINK_FIREBASE){
        return getUserGeofenceFlagsActiveList(USER_LINK_FIREBASE).child(Constants.USER_FLAGS);
    }

    public static DatabaseReference getUserGeofenceFriendsFlagsActiveList(final String USER_LINK_FIREBASE){
        return getUserGeofenceFlagsActiveList(USER_LINK_FIREBASE).child(Constants.FRIENDS_FLAGS);
    }

    public static DatabaseReference getUserGeofencePublicFlagsActiveList(final String USER_LINK_FIREBASE){
        return getUserGeofenceFlagsActiveList(USER_LINK_FIREBASE).child(Constants.PUBLIC_FLAGS);
    }

    public static DatabaseReference getUserNewNotificationsNumber(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.New_NOTIFICATIONS_NUMBER);
    }

    /**
     * Events
     */
    /**
     * Get User Events.
     */
    public static DatabaseReference getUserActiveEvents(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE)
                .child(Constants.EVENTS).child(Constants.ACTIVE_EVENT);
    }

    public static DatabaseReference getUserDeadEvents(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE)
                .child(Constants.EVENTS).child(Constants.DEAD_EVENTS_LIST);
    }

    public static DatabaseReference getUserDetection(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE)
                .child(Constants.USER_MAP).child(Constants.USER_DETECT);
    }

    public static DatabaseReference getUserActiveMode(final String USER_LINK_FIREBASE){
        return getUserAccountSettings(USER_LINK_FIREBASE).child(Constants.USER_STATE);
    }

    public static DatabaseReference getUserDetectionActiveGeofence(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.GEOFENCES)
                .child(Constants.ACTIVE).child(Constants.USER_DETECT);
    }

    /**
     * Tracking.
     */
    public static DatabaseReference getUserTrack(final String USER_LINK_FIREBASE){
        return getUserDatabaseReference(USER_LINK_FIREBASE).child(Constants.USER_LOCATION)
                .child(Constants.USER_PATH);
    }
}
