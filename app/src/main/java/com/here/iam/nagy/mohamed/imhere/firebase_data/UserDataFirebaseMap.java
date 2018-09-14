package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.location_map.InfoWindowData;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.AccountSettings;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.CurrentLocation;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.FlagsMarkers;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.MapMarkers;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Track;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by mohamednagy on 12/27/2016.
 */
public class UserDataFirebaseMap extends UserDataFirebase {

    private GoogleMap googleMap;
    private Context context;
    // Hold friends ids which need to track.
    // and have access location is available.
    private HashMap<String, Polyline> friendsTrack;
    // user , friends and help markers
    private HashMap<String, Marker> usersMarkersHashMap;
    // public and friends flags
    private HashMap<String, Marker> flagsMarkersHashMap;
    // events markers
    // flags circle
    private HashMap<String,Circle> flagsCircleHashMap;
    // use in connection listeners to determine the listener and its owner.
    private HashMap<String, ValueEventListener> usersMarkersConnectionListenersHashMap;
    // Listeners
    private ValueEventListener userLocationListener;
    private ChildEventListener friendMarksListener;
    private ChildEventListener userPublicFlagsListener;
    private ChildEventListener userFriendsFlagsListener;
    private ChildEventListener userFlagsListener;
    private ChildEventListener userHelpListListener;

    public UserDataFirebaseMap(final String USER_LINK_FIREBASE, Context context) {
        super(USER_LINK_FIREBASE);

        usersMarkersHashMap = new HashMap<>();
        flagsMarkersHashMap = new HashMap<>();
        friendsTrack = new HashMap<>();
        usersMarkersConnectionListenersHashMap = new HashMap<>();
        flagsCircleHashMap = new HashMap<>();
        this.context = context;
    }

    public void setupUserMap(GoogleMap googleMap) {
        this.googleMap = googleMap;

        setupCamera();
        setupMarkers();
    }

    public void detachMapListeners() {
        /**
         // Listeners
         */
        if (userLocationListener != null) {
            FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                    .removeEventListener(userLocationListener);
        }
        if (friendMarksListener != null) {
            FirebaseHelper.getUserMapFriendMarks(USER_LINK_FIREBASE)
                    .removeEventListener(friendMarksListener);
        }

        if (userPublicFlagsListener != null) {
            FirebaseHelper.getPublicFlags()
                    .removeEventListener(userPublicFlagsListener);
        }
        if (userFriendsFlagsListener != null) {
            FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                    .removeEventListener(userFriendsFlagsListener);
        }
        if (userFlagsListener != null) {
            FirebaseHelper.getUserFlag(USER_LINK_FIREBASE)
                    .removeEventListener(userFlagsListener);
        }
        if (userHelpListListener != null) {
            FirebaseHelper.getUserHelpRequest(USER_LINK_FIREBASE)
                    .removeEventListener(userHelpListListener);
        }

        detachListenersConnection();

    }

    public void attachMapListeners() {
        if (userLocationListener != null) {
            FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                    .addValueEventListener(userLocationListener);
        }
        if (friendMarksListener != null) {
            FirebaseHelper.getUserMapFriendMarks(USER_LINK_FIREBASE)
                    .addChildEventListener(friendMarksListener);
        }

        if (userPublicFlagsListener != null) {
            FirebaseHelper.getPublicFlags()
                    .addChildEventListener(userPublicFlagsListener);
        }
        if (userFriendsFlagsListener != null) {
            FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                    .addChildEventListener(userFriendsFlagsListener);
        }
        if (userFlagsListener != null) {
            FirebaseHelper.getUserFlag(USER_LINK_FIREBASE)
                    .addChildEventListener(userFlagsListener);
        }
        if (userHelpListListener != null) {
            FirebaseHelper.getUserHelpRequest(USER_LINK_FIREBASE)
                    .addChildEventListener(userHelpListListener);
        }
    }


    /**
     * Set Camera position and user mark.
     */
    private void setupCamera() {
        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CurrentLocation currentLocation =
                                dataSnapshot.getValue(CurrentLocation.class);

                        if(currentLocation != null) {

                            final LatLng USER_POSITION =
                                    new LatLng(currentLocation.getLocationLatitude(),
                                            currentLocation.getLocationLongitude());

                            CameraPosition cameraPosition = CameraPosition.builder()
                                    .tilt(45)
                                    .target(USER_POSITION)
                                    .zoom(18)
                                    .build();

                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Set Markers for all account components
     */

    private void setupMarkers() {
        // TODO ... set depend on account settings.
        setupUserMarker();
        setupFriendsMarkers();
        // TODO ... set event location.
        // Check first account settings before set
        // flags.
        flagsSettingsToActive();

        setupFlagsUserMarkers();
        setupHelpMarkers();
        Log.e("called","dasdadsadsasd");
    }

    private void setupUserMarker() {
        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .addValueEventListener(userLocationListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CurrentLocation currentLocation
                                = dataSnapshot.getValue(CurrentLocation.class);

                        if (currentLocation != null ) {

                            final LatLng userLatLng = new LatLng(currentLocation.getLocationLatitude(),
                                    currentLocation.getLocationLongitude());

                            if (usersMarkersHashMap.containsKey(USER_LINK_FIREBASE)) {
                                usersMarkersHashMap.get(USER_LINK_FIREBASE)
                                        .setPosition(userLatLng);
                            } else {
                                FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);

                                                String title = Constants.USERS;
                                                MarkerOptions markerOptions = new MarkerOptions()
                                                        .title(title)
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
                                                        .position(userLatLng);

                                                Marker userMarker = googleMap.addMarker(markerOptions);

                                                userMarker.setTag(new InfoWindowData(userAccount, null, null));

                                                usersMarkersHashMap.put(USER_LINK_FIREBASE, userMarker);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void setupFriendsMarkers() {
        FirebaseHelper.getUserMapFriendMarks(USER_LINK_FIREBASE)
                .addChildEventListener(friendMarksListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(usersMarkersHashMap.containsKey(dataSnapshot.getKey()))
                            return;

                        MapMarkers mapMarkers = dataSnapshot.getValue(MapMarkers.class);

                        LatLng friendLatLng = new LatLng(
                                mapMarkers.getCurrentLocation().getLocationLatitude(),
                                mapMarkers.getCurrentLocation().getLocationLongitude());
                        String title = Constants.USERS;
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(friendLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.friend_marker))
                                .title(title);

                        Marker friendMarker = googleMap.addMarker(markerOptions);

                        updateInfoWindowData(
                                friendMarker,
                                new UserAccount(
                                mapMarkers.getUserName(),
                                Utility.decodeUserEmail(dataSnapshot.getKey()),
                                mapMarkers.getUserImage()),
                                dataSnapshot, false);

                        usersMarkersHashMap.put(dataSnapshot.getKey(), friendMarker);

                        setupFriendsMarkersListenerConnection(dataSnapshot.getKey());

                        //** Tracking **//
                        if(USER_LINK_FIREBASE.equals(Constants.USER_ADMIN))
                            setupTrackForPerson(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        MapMarkers mapMarkers = dataSnapshot.getValue(MapMarkers.class);

                        LatLng friendLatLong = new LatLng(
                                mapMarkers.getCurrentLocation().getLocationLatitude(),
                                mapMarkers.getCurrentLocation().getLocationLongitude()
                        );

                        usersMarkersHashMap.get(dataSnapshot.getKey()).setPosition(friendLatLong);

                        updateInfoWindowData(
                                usersMarkersHashMap.get(dataSnapshot.getKey()),
                                new UserAccount(
                                        mapMarkers.getUserName(),
                                        Utility.decodeUserEmail(dataSnapshot.getKey()),
                                        mapMarkers.getUserImage()),
                                dataSnapshot,
                                true
                                );

                        //** Tracking **//
                        if(USER_LINK_FIREBASE.equals(Constants.USER_ADMIN))
                            updateTrackForPerson(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        usersMarkersHashMap.get(dataSnapshot.getKey()).remove();
                        usersMarkersHashMap.remove(dataSnapshot.getKey());

                        // Remove listener
                        FirebaseHelper.getUserLocation(dataSnapshot.getKey()).removeEventListener(
                                usersMarkersConnectionListenersHashMap.get(dataSnapshot.getKey())
                        );

                        // remove listener from the HashMap
                        usersMarkersConnectionListenersHashMap.remove(dataSnapshot.getKey());

                        //** Tracking **//
                        if(USER_LINK_FIREBASE.equals(Constants.USER_ADMIN))
                            removeTrackForPerson(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setupFlagsPublicMarkers() {
        FirebaseHelper.getPublicFlags()
                .addChildEventListener(userPublicFlagsListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if(dataSnapshot.getKey().equals(USER_LINK_FIREBASE))
                            return;

                        FlagsMarkers flagsMarker = dataSnapshot.getValue(FlagsMarkers.class);
                        LatLng flagLatLng = new LatLng(
                                flagsMarker.getFlagLocation().getLocationLatitude(),
                                flagsMarker.getFlagLocation().getLocationLongitude()
                        );

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(flagLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.public_flag))
                                .title(Constants.PUBLIC_FLAGS);

                        Marker flagMarker = googleMap.addMarker(markerOptions);

                        flagMarker.setTag(flagsMarker);

                        flagsMarkersHashMap.put(dataSnapshot.getKey(), flagMarker);

                        if(!isFlagVisited(flagsMarker)) {
                            drawFlagsCircle(flagLatLng,
                                    dataSnapshot.getKey(),
                                    Constants.FLAG_PUBLIC_CIRCLE_STROKE_COLOR,
                                    Constants.FLAG_PUBLIC_CIRCLE_FILL_COLOR);
                        }

                        Log.e("mao","done");

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        if(dataSnapshot.getKey().equals(USER_LINK_FIREBASE))
                            return;

                        FlagsMarkers flagsMarker = dataSnapshot.getValue(FlagsMarkers.class);
                        LatLng flagLatLng = new LatLng(
                                flagsMarker.getFlagLocation().getLocationLatitude(),
                                flagsMarker.getFlagLocation().getLocationLongitude()
                        );

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setPosition(flagLatLng);

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setTitle(Constants.PUBLIC_FLAGS);

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setTag(flagsMarker);

                        if(!isFlagVisited(flagsMarker)) {
                            drawFlagsCircle(flagLatLng,
                                    dataSnapshot.getKey(),
                                    Constants.FLAG_PUBLIC_CIRCLE_STROKE_COLOR,
                                    Constants.FLAG_PUBLIC_CIRCLE_FILL_COLOR);
                        }else{
                            eraseFlagsCircle(dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        /// to save user flag.
                        if(flagsCircleHashMap.containsKey(dataSnapshot.getKey())) {
                            flagsCircleHashMap.remove(dataSnapshot.getKey());
                            eraseFlagsCircle(dataSnapshot.getKey());
                        }
                        if(flagsMarkersHashMap.containsKey(dataSnapshot.getKey())) {
                            flagsMarkersHashMap.get(dataSnapshot.getKey()).remove();
                            flagsMarkersHashMap.remove(dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setupFlagsFriendsMarkers() {
        FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                .addChildEventListener(userFriendsFlagsListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        FlagsMarkers flagsMarker = dataSnapshot.getValue(FlagsMarkers.class);
                        LatLng flagLatLng = new LatLng(
                                flagsMarker.getFlagLocation().getLocationLatitude(),
                                flagsMarker.getFlagLocation().getLocationLongitude()
                        );

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(flagLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.friend_flag))
                                .title(Constants.FRIENDS_FLAGS);

                        Marker flagMarker = googleMap.addMarker(markerOptions);

                        flagMarker.setTag(flagsMarker);

                        flagsMarkersHashMap.put(dataSnapshot.getKey(), flagMarker);

                        if(!isFlagVisited(flagsMarker)) {
                            drawFlagsCircle(flagLatLng,
                                    dataSnapshot.getKey(),
                                    Constants.FLAG_FRIENDS_CIRCLE_STROKE_COLOR,
                                    Constants.FLAG_FRIENDS_CIRCLE_FILL_COLOR);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        FlagsMarkers flagsMarker = dataSnapshot.getValue(FlagsMarkers.class);
                        LatLng flagLatLng = new LatLng(
                                flagsMarker.getFlagLocation().getLocationLatitude(),
                                flagsMarker.getFlagLocation().getLocationLongitude()
                        );

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setPosition(flagLatLng);

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setTitle(Constants.FRIENDS_FLAGS);

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setTag(flagsMarker);

                        if(!isFlagVisited(flagsMarker)) {
                            drawFlagsCircle(flagLatLng,
                                    dataSnapshot.getKey(),
                                    Constants.FLAG_FRIENDS_CIRCLE_STROKE_COLOR,
                                    Constants.FLAG_FRIENDS_CIRCLE_FILL_COLOR);
                        }else{
                            eraseFlagsCircle(dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        flagsMarkersHashMap.get(dataSnapshot.getKey()).remove();
                        flagsMarkersHashMap.remove(dataSnapshot.getKey());
                        flagsCircleHashMap.remove(dataSnapshot.getKey());
                        eraseFlagsCircle(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setupFlagsUserMarkers() {
        FirebaseHelper.getUserFlag(USER_LINK_FIREBASE)
                .addChildEventListener(userFlagsListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        FlagsMarkers flagsMarker = dataSnapshot.getValue(FlagsMarkers.class);
                        LatLng flagLatLng = new LatLng(
                                flagsMarker.getFlagLocation().getLocationLatitude(),
                                flagsMarker.getFlagLocation().getLocationLongitude()
                        );

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(flagLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_flag))
                                .title(Constants.USER_FLAGS);

                        Marker flagMarker = googleMap.addMarker(markerOptions);

                        flagMarker.setTag(flagsMarker);

                        flagsMarkersHashMap.put(dataSnapshot.getKey(), flagMarker);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        FlagsMarkers flagsMarker = dataSnapshot.getValue(FlagsMarkers.class);
                        LatLng flagLatLng = new LatLng(
                                flagsMarker.getFlagLocation().getLocationLatitude(),
                                flagsMarker.getFlagLocation().getLocationLongitude()
                        );

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setPosition(flagLatLng);

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setTitle(Constants.USER_FLAGS);

                        flagsMarkersHashMap.get(dataSnapshot.getKey())
                                .setTag(flagsMarker);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        flagsMarkersHashMap.get(dataSnapshot.getKey()).remove();
                        flagsMarkersHashMap.remove(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setupHelpMarkers() {
        FirebaseHelper.getUserHelpRequest(USER_LINK_FIREBASE)
                .addChildEventListener(userHelpListListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MapMarkers userNeedHelp = dataSnapshot.getValue(MapMarkers.class);
                        LatLng userPosition = new LatLng(
                                userNeedHelp.getCurrentLocation().getLocationLatitude(),
                                userNeedHelp.getCurrentLocation().getLocationLongitude()
                        );

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(userPosition)
                                .title(Constants.HELP)
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        Utility.resizeMapIcon(R.drawable.help_icon,context)));

                        Marker userMarker = googleMap.addMarker(markerOptions);

                        userMarker.setTag(new UserAccount(
                                userNeedHelp.getUserName(),
                                Utility.decodeUserEmail(dataSnapshot.getKey()),
                                userNeedHelp.getUserImage()));

                        usersMarkersHashMap.put(dataSnapshot.getKey(), userMarker);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        MapMarkers userNeedHelp = dataSnapshot.getValue(MapMarkers.class);
                        LatLng userPosition = new LatLng(
                                userNeedHelp.getCurrentLocation().getLocationLatitude(),
                                userNeedHelp.getCurrentLocation().getLocationLongitude()
                        );

                        usersMarkersHashMap.get(dataSnapshot.getKey())
                                .setPosition(userPosition);

                        usersMarkersHashMap.get(dataSnapshot.getKey())
                                .setTag(new UserAccount
                                        (userNeedHelp.getUserName(),
                                                Utility.decodeUserEmail(dataSnapshot.getKey()),
                                                userNeedHelp.getUserImage()));

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        usersMarkersHashMap.get(dataSnapshot.getKey()).remove();
                        usersMarkersHashMap.remove(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setupTrackForPerson(final String USER_EMAIL){
        FirebaseHelper.getUserTrack(USER_EMAIL).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Draw Track
                        List<LatLng> latLngs = new ArrayList<>();
                        for(DataSnapshot trackDataSnapshot: dataSnapshot.getChildren()){
                            Log.e("trace", trackDataSnapshot.getKey());
                            Track track = trackDataSnapshot.getValue(Track.class);
                            latLngs.add(new LatLng(track.getLat(), track.getLng()));
                        }

                        Polyline polyline = drawTrackOnMap(latLngs);

                        friendsTrack.put(USER_EMAIL, polyline);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void updateTrackForPerson(final String USER_EMAIL){
        FirebaseHelper.getUserTrack(USER_EMAIL).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // redraw Track
                        friendsTrack.get(USER_EMAIL).remove();

                        List<LatLng> latLngs = new ArrayList<>();
                        for(DataSnapshot trackDataSnapshot: dataSnapshot.getChildren()){
                            Track track = trackDataSnapshot.getValue(Track.class);
                            latLngs.add(new LatLng(track.getLat(), track.getLng()));
                        }

                        Polyline polyline = drawTrackOnMap(latLngs);

                        friendsTrack.put(USER_EMAIL, polyline);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void removeTrackForPerson(final String USER_EMAIL){
        FirebaseHelper.getUserTrack(USER_EMAIL).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // remove Track
                        friendsTrack.get(USER_EMAIL).remove();
                        friendsTrack.remove(USER_EMAIL);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private Polyline drawTrackOnMap(List<LatLng> latLngs){
        return googleMap.addPolyline(
                new PolylineOptions()
                .addAll(latLngs)
                .width(13)
                .color(Color.BLUE)
        );
    }
/**
 * Connect changing value of friends list location and
 * their values in friends marker
 * User Location already updated from its source.
 * user flag will never updated by user during opening map so
 * we do not need to make listener connection here.
 */

    private void setupFriendsMarkersListenerConnection(final String EMAIL_LINK_FIREBASE){
        ValueEventListener connectionListener;

        FirebaseHelper.getUserLocation(EMAIL_LINK_FIREBASE)
                .addValueEventListener(connectionListener = new ValueEventListener() {
                    /// Location is updated every 20 second so we don't need to
                    // make another listener for name and image of user.
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final CurrentLocation currentLocation = dataSnapshot.getValue(CurrentLocation.class);

                        FirebaseHelper.getUserDatabaseReference(EMAIL_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String userName =
                                                dataSnapshot.getValue(UserAccount.class).getUserName();
                                        String userImage =
                                                dataSnapshot.getValue(UserAccount.class).getUserImage();

                                        MapMarkers userMapMarkers =
                                                new MapMarkers(currentLocation,userName,userImage);

                                        HashMap<String,Object> userUpdated =
                                                new HashMap<>();
                                        userUpdated.put(EMAIL_LINK_FIREBASE,userMapMarkers);

                                        FirebaseHelper.getUserMapFriendMarks(USER_LINK_FIREBASE)
                                                .updateChildren(userUpdated);
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
        usersMarkersConnectionListenersHashMap.put(EMAIL_LINK_FIREBASE,connectionListener);
    }

    private void setupEventMemberListeners(final String EMAIL_LINK_FIREBASE){
        ValueEventListener connectionListeners;

        FirebaseHelper.getUserLocation(EMAIL_LINK_FIREBASE)
                .addValueEventListener(connectionListeners = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final CurrentLocation currentLocation = dataSnapshot.getValue(CurrentLocation.class);
                        // check if user name is changed
                        FirebaseHelper.getUserDatabaseReference(EMAIL_LINK_FIREBASE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String userName =
                                                dataSnapshot.getValue(UserAccount.class).getUserName();
                                        String userImage =
                                                dataSnapshot.getValue(UserAccount.class).getUserImage();
                                        MapMarkers mapMarkers = new MapMarkers(currentLocation,userName,userImage);

                                        final HashMap<String,Object> hashMap = new HashMap<String, Object>();
                                        hashMap.put(EMAIL_LINK_FIREBASE,mapMarkers);

                                        FirebaseHelper.getUserActiveEvents(USER_LINK_FIREBASE)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                            FirebaseHelper.getUserActiveEvents(USER_LINK_FIREBASE)
                                                                    .child(dataSnapshot1.getKey())
                                                                    .child(Constants.EVENT_MEMBER)
                                                                    .updateChildren(hashMap);
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
        usersMarkersConnectionListenersHashMap.put(EMAIL_LINK_FIREBASE,connectionListeners);
    }

    private void detachListenersConnection(){
        // FriendsMarkers
        FirebaseHelper.getUserMapFriendMarks(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            FirebaseHelper.getUserLocation(dataSnapshot1.getKey())
                                    .removeEventListener(
                                            usersMarkersConnectionListenersHashMap.get(dataSnapshot1.getKey())
                                    );
                            usersMarkersConnectionListenersHashMap.remove(dataSnapshot1.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        // Events Markers
        FirebaseHelper.getUserActiveEvents(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot eventDataSnap : dataSnapshot.getChildren()){
                            for(DataSnapshot memberDataSnap : eventDataSnap.child(Constants.EVENT_MEMBER).getChildren()){
                                FirebaseHelper.getUserLocation(memberDataSnap.getKey())
                                        .removeEventListener(
                                                usersMarkersConnectionListenersHashMap.get(memberDataSnap.getKey())
                                        );
                                usersMarkersConnectionListenersHashMap.remove(memberDataSnap.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /**
     * Check flags state
     */
    private void flagsSettingsToActive(){
        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final boolean PUBLIC_FLAG_MARKERS_ACTIVE_STATE =
                                dataSnapshot.getValue(AccountSettings.class).getShowPublicFlags();
                        final boolean FRIENDS_FLAG_MARKERS_ACTIVE_STATE =
                                dataSnapshot.getValue(AccountSettings.class).getShowFriendsFlags();

                        if(PUBLIC_FLAG_MARKERS_ACTIVE_STATE){
                            setupFlagsPublicMarkers();
                        }
                        if(FRIENDS_FLAG_MARKERS_ACTIVE_STATE){
                            setupFlagsFriendsMarkers();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /// Event location

    private void drawFlagsCircle(LatLng position, String circleKey, int strokeColor, int fillColor){

        // check if circle exist or not.
        if(flagsCircleHashMap.containsKey(circleKey)){

            flagsCircleHashMap.get(circleKey).setCenter(position);
            flagsCircleHashMap.get(circleKey).setFillColor(fillColor);
            flagsCircleHashMap.get(circleKey).setRadius(Constants.FLAG_RADIUS);
            flagsCircleHashMap.get(circleKey).setStrokeColor(strokeColor);

        }else {
            CircleOptions circleOptions = new CircleOptions()
                    .center(position)
                    .strokeColor(strokeColor)
                    .fillColor(fillColor)
                    .radius(Constants.FLAG_RADIUS);

            flagsCircleHashMap.put(circleKey, googleMap.addCircle(circleOptions));
        }

    }

    private void eraseFlagsCircle(String FLAG_KEY){
        flagsCircleHashMap.get(FLAG_KEY).remove();
    }

    public void deleteUserFlag(){

        FirebaseHelper.getUserFlag(USER_LINK_FIREBASE)
                .removeValue();
        // delete user flag if it was public flag.
        FirebaseHelper.getPublicFlags().child(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            FirebaseHelper.getPublicFlags().child(USER_LINK_FIREBASE)
                                    .removeValue();
                        }else{
                            FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChildren()){
                                                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                                                    FirebaseHelper.getUserFriendsFlags(
                                                            Utility.encodeUserEmail(dataSnapshot1.getValue(String.class))
                                                    ).child(USER_LINK_FIREBASE).removeValue();
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

    private boolean isFlagVisited(FlagsMarkers flagsMarkers){

        if(flagsMarkers.getIsVisited() != null &&
                flagsMarkers.getIsVisited().containsKey(USER_LINK_FIREBASE)){

            return flagsMarkers.getIsVisited().get(USER_LINK_FIREBASE);
        }
        return false;
    }

    // Set Info Window Data to specific marker
    private void updateInfoWindowData(Marker marker, UserAccount userAccount,
                                      DataSnapshot locationDataSnapshot,
                                      boolean show){
        if(!Utility.encodeUserEmail(userAccount.getUserEmail()).equals(Constants.USER_ADMIN)) {
            marker.setTag(new InfoWindowData(userAccount, null, null));
            return;
        }

        Iterator<DataSnapshot> iterator = locationDataSnapshot.child(Constants.TRACK).getChildren().iterator();
        Long startDate = iterator.next().getValue(Track.class).getDate();
        Long endDate = startDate;

        while (iterator.hasNext()){
            endDate = iterator.next().getValue(Track.class).getDate();
        }

        InfoWindowData infoWindowData = new InfoWindowData(userAccount, startDate, endDate);

        marker.setTag(infoWindowData);

        // Redisplay info window with updated data.
        if(show && marker.isInfoWindowShown())
            marker.showInfoWindow();
    }
}
