package com.here.iam.nagy.mohamed.imhere.helper_classes;

import android.graphics.Color;

/**
 * Created by mohamednagy on 12/25/2016.
 */
public class Constants {

    /** MainActivity Constants **/
    public static final int USER_PROFILE_FRAGMENT = 0;
    public static final int USER_FRIENDS_LIST_FRAGMENT = 1;
    public static final int USER_NOTIFICATIONS_FRAGMENT = 2;
    public static final int SEARCH_FRAGMENT = 3;

    /** Firebase Constants **/
    public static final String USER_NOTIFICATIONS = "Notification";
    public static final String USER_FRIEND_REQUEST_LIST = "userFriendRequests";
    public static final String USER_FRIENDS_LIST = "userFriendsList";
    public static final String USER_REQUESTS = "requests";
    public static final String USER_FRIEND_LOCATION_REQUESTS = "friendLocation";
    public static final String USER_FRIEND_REQUESTS = "friendRequest";
    public static final String USER_LOCATION = "userLocation";
    public static final String USER_ADMIN = "mohamednagy2015@outlook,com";
    public static final String New_NOTIFICATIONS_NUMBER = "newNotifications";

    // Account Settings
    public static final String USER_ACCOUNT_SETTINGS = "accountSettings";
    public static final String LOCATION_APPEAR_TO_FRIENDS = "locationAppearToFriends"; //locationAppearToFriends
    public static final String SHOW_PUBLIC_FLAGS = "showPublicFlags";
    public static final String SHOW_FRIENDS_FLAGS = "showFriendsFlags";
    public static final String DETECTION_MODE = "detectionMode";
    public static final int USER_ONLINE = 1;
    public static final int USER_OFFLINE = 2;
    public static final String USER_STATE  = "userState";
    // App
    public static final String USERS = "Users";
    public static final String HERE_APP = "HereApp";
    // Map
    public static final String USER_MAP = "map";
    public static final String USER_DETECT= "detect";
    public static final String DETECTED = "detected";
    // Marks
    public static final String MARKS = "marks";
    public static final String FRIENDS_MARKS = "friendsMarks";
    public static final String FRIENDS_EVENT_MARKS = "friendsEventMarks";
    // Help Marks
    public static final String HELP = "help";
    // Flags
    public static final String FLAGS = "flags";
    public static final String PUBLIC_FLAGS = "public";
    public static final String FRIENDS_FLAGS = "friends";
    public static final String USER_FLAGS = "user";
    public static final int NO_LIKE = 0;
    public static final String FLAG_KEY = "flag key";
    public static final String FLAG_TYPE = "flag type";
    public static final String FLAG_LIKES = "likesNumber";
    public static final String FLAGS_LOVERS = "flagLovers";
    public static final String FLAG_VISITORS = "isVisited";
    // Events
    public static final String EVENTS = "events";
    public static final String ACTIVE_EVENT = "activeEventsList";
    public static final String DEAD_EVENTS_LIST = "deadEventsList";
    public static final String EVENT_MEMBER = "members";
    public static final String REACHED_MEMBERS = "reachedMembers";
    // Geofence
    public static final String GEOFENCES = "userGeofences";
    public static final String ACTIVE = "activeList";
    public static final int FLAG_RADIUS = 450;
    public static final int DETECTION_RADIUS = 250;
    public static final int DETECTION_ACTIVE = 300;
    public static final int DISTANCE_ACTIVE_FLAG = 800;
    public static final int EVENT_CIRCLE_STROKE_COLOR= Color.rgb(48, 79,254);
    public static final int EVENT_CIRCLE_FILL_COLOR=Color.rgb(197,202,233);
    public static final int FLAG_FRIENDS_CIRCLE_STROKE_COLOR= Color.argb(50,213,0,0);
    public static final int FLAG_FRIENDS_CIRCLE_FILL_COLOR=Color.argb(100,255,205,210);
    public static final int FLAG_PUBLIC_CIRCLE_STROKE_COLOR= Color.argb(50,38, 50,65);
    public static final int FLAG_PUBLIC_CIRCLE_FILL_COLOR=Color.argb(100,207,216,220);

    // Locations
    public static final String CURRENT_USER_LOCATION_PROVIDER = "userLocation";
    public static final String NEW_LOCATION_PROVIDER= "newLocation";

    /** User Profile Constants **/
    public static final int RC_PHOTO = 1;

    /** Location services constants **/
    public static final int ACTIVITY_MODE = 60000; // 30 second
    public static final int IDLE_MODE = 900000; // 15 mint
    public static final int HELP_MODE = 300000; // 5 mint
    public static final String LOCATION_CONNECTION_ERROR = "Error during location update process\n" +
            "Please check your network connection.";

    /** Intent constants **/
    public static final String USER_EXTRA = "user email";
    public static final String FLAG_EXTRA = "flag data";

    /** Storage Constants **/
    public static final String IMAGES = "images";

    /** Notification constants **/
    public static final int NOTIFICATION_HELP = 1;
    public static final int NOTIFICATION_FRIEND_REQUEST = 2;
    public static final int NOTIFICATION_ORIGINAL = 3;
    public static final int NOTIFICATION_FLAG = 4;
    public static final int NOTIFICATION_LOCATION_REQUEST = 5;
    public static final int NOTIFICATION_LOCATION_ACCEPTED = 6;
    public static final int NOTIFICATION_EVENT_MEMBER_REACHED = 7;
    public static final int NOTIFICATION_DETECTION = 8;

    /** TABS **/
    public static final int USER_PROFILE_TAB = 0;
    public static final int USER_FRIENDS_TAB = 1;
    public static final int USER_NOTIFICATIONS_TAB = 2;
    public static final int USER_SEARCH_TAB = 3;

    /** Detection **/
    public static final int USER_DETECTION_ACTIVE = 1;
    public static final int USER_DETECTION_INACTIVE = 2;
    public static final String CURRENT_DETECTION_LOCATION = "currentLocation";


}
