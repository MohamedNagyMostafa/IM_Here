package com.here.iam.nagy.mohamed.imhere.ui.properties_ui;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.animation.Animation;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebase;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseGeofence;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseMainActivity;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserProfileUi;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.location_service.UserLocation;
import com.here.iam.nagy.mohamed.imhere.location_service.UserLocationCallback;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.location_map.MapActivity;
import com.here.iam.nagy.mohamed.imhere.user_account.LoginActivity;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;
import com.here.iam.nagy.mohamed.imhere.widget.WidgetUtils;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserProfileFragment extends Fragment
    implements UserProfileUi, UserLocationCallback {

    private ViewAppHolder.UserProfileViewHolder userProfileViews;
    private String USER_LINK_FIREBASE;
    private UserDataFirebase userDataFirebase;
    private UserDataFirebaseGeofence userDataFirebaseGeofence;
    // User location.
    private GoogleApiClient googleApiClient;

    // Initialize listeners

    /**
     * Listener for user profile photo is fired when user click
     * on it's photo and open internal images which stored in his phone
     * and assign id "RC_PHOTO" to image which choosed by user.
     */
    private View.OnClickListener imagePhotoListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utility.networkIsConnected(getContext())) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                        startActivityForResult(Intent.createChooser(
                                intent, "load photo"),
                                Constants.RC_PHOTO);
                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private View.OnClickListener mapButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utility.networkIsConnected(getContext())) {
                        Intent mapIntent = new Intent(getContext(), MapActivity.class);
                        mapIntent.putExtra(Constants.USER_EXTRA, userDataFirebase.getUserLinkFirebase());

                        startActivity(mapIntent);
                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private View.OnClickListener createFlagListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utility.networkIsConnected(getContext())) {
                        // check if user create a flag before.
                        userDataFirebase.setCreateNewFlagListenerAction(getContext());
                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private CompoundButton.OnCheckedChangeListener locationFriendVisibilityListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean result) {
                    userDataFirebase.setLocationFriendsVisibility(result);

                    if(Utility.networkIsConnected(getContext())) {
                        if (result) {
                            userDataFirebase.addUserMarkToFriendsMap();
                        } else {
                            userDataFirebase.removeUserMarkFromFriendsMap();
                        }
                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                        userProfileViews.LOCATION_FRIEND_VISIBILITY.setChecked(!result);
                    }

                }
            };

    private CompoundButton.OnCheckedChangeListener publicFlagsShowListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean result) {
                    if(Utility.networkIsConnected(getContext())) {
                        userDataFirebase.setPublicFlagsShow(result);
                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                        userProfileViews.RECEIVE_PUBLIC_FLAGS.setChecked(!result);
                    }
                }
            };

    private CompoundButton.OnCheckedChangeListener friendsFlagsShowListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(Utility.networkIsConnected(getContext())) {
                        userDataFirebase.setFriendsFlagsShow(b);
                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                        userProfileViews.RECEIVE_FRIEND_FLAGS.setChecked(!b);
                    }
                }
            };

    private View.OnClickListener helpMeClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utility.networkIsConnected(getActivity())) {
                        userDataFirebase.setHelpProcess(userProfileViews, getContext(), false);
                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                    }
                }
            };


    private View.OnClickListener signOutClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utility.networkIsConnected(getContext())) {
                        UserDataFirebaseMainActivity.setUserModeOffline();
                        FirebaseAuth.getInstance().signOut();
                        WidgetUtils.WidgetBroadcast.sendBroadcastToWidgetLogout(getContext());

                        Intent loginActivityIntent = new Intent(getContext(), LoginActivity.class);
                        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginActivityIntent);
                        getActivity().finish();

                    }else{
                        Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                    }

                }
            };

    private View.OnClickListener detectionListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("is called on detector","doooooooneee");
                    userDataFirebase.detectionButtonClicked(userProfileViews, getContext());
                }
            };

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(Utility.networkIsConnected(getContext())) {
                        if (!googleApiClient.isConnected()) {
                            googleApiClient.connect();
                        }
                        Log.e("load data","Done");
                        userDataFirebase.setUserIdentifiers();


                    }else{
                        Toast.makeText(
                                getContext(),
                                "Check your network connection",
                                Toast.LENGTH_SHORT)
                                .show();
                        userProfileViews.USER_PROFILE_SWIPE_REFRESH.setRefreshing(false);
                    }
                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// Get user Data
        USER_LINK_FIREBASE = getArguments().getString(Constants.USER_EXTRA);

        /// firebase data
        userDataFirebase = new UserDataFirebase(USER_LINK_FIREBASE,this, getContext());

        // user location
        UserLocation userLocation = new UserLocation(this);

        // set geofence system
        userDataFirebaseGeofence = new UserDataFirebaseGeofence(USER_LINK_FIREBASE, getContext());
        userDataFirebaseGeofence.setGeofenceSystem();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("createView called","done");

        View rootView =  inflater.inflate(R.layout.fragment_user_profile, container, false);
        // initialize views
        userProfileViews = new ViewAppHolder.UserProfileViewHolder(rootView);

        // set help/safe mode
        userDataFirebase.setHelpProcess(userProfileViews,getContext(),true);

        // setListeners
        userProfileViews.USER_IMAGE_VIEW_CONTENT.setOnClickListener(imagePhotoListener);
        userProfileViews.MAP_BUTTON.setOnClickListener(mapButtonListener);
        userProfileViews.LOCATION_FRIEND_VISIBILITY
                .setOnCheckedChangeListener(locationFriendVisibilityListener);
        userProfileViews.RECEIVE_PUBLIC_FLAGS
                .setOnCheckedChangeListener(publicFlagsShowListener);
        userProfileViews.RECEIVE_FRIEND_FLAGS
                .setOnCheckedChangeListener(friendsFlagsShowListener);
        userProfileViews.USER_HELP_ME_LAYOUT.setOnClickListener(helpMeClickListener);
        userProfileViews.CREATE_FLAG_LAYOUT.setOnClickListener(createFlagListener);
        userProfileViews.SIGN_OUT_LAYOUT.setOnClickListener(signOutClickListener);
        userProfileViews.DETECTION_LAYOUT.setOnClickListener(detectionListener);

        Animation.UserProfileAnimation.startingAnimation(userProfileViews);

        if(getContext() != null && userProfileViews != null) {
            userProfileViews.USER_PROFILE_SWIPE_REFRESH.setColorSchemeResources(R.color.active_tab);
            userProfileViews.USER_PROFILE_SWIPE_REFRESH.setOnRefreshListener(swipeRefreshListener);
        }

        if(Utility.networkIsConnected(getContext())) {
            googleApiClient.connect();
        }else{
            Toast.makeText(getContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    /**
     * Set Identifiers data for user
     * @param userAccount
     */
    @Override
    public void setUserIdentifierData(UserAccount userAccount) {
        // data is loaded
        if(userProfileViews != null && getContext() != null) {
            userProfileViews.USER_NAME_TEXT_VIEW.setText(userAccount.getUserName());
            userProfileViews.USER_EMAIL_TEXT_VIEW.setText(userAccount.getUserEmail());

            if (userAccount.getUserImage() != null) {
                Picasso.with(getContext())
                        .load(userAccount.getUserImage())
                        .into(userProfileViews.USER_IMAGE_VIEW);

            } else {
                userProfileViews.USER_IMAGE_VIEW.setImageDrawable(getActivity().getDrawable(R.drawable.profile_pic_image));
            }
            // check last value
            userDataFirebase.setSwitchesState(userProfileViews);
            userDataFirebase.setDetectionState(userProfileViews, getContext());
        }
        if(userProfileViews != null)
            userProfileViews.USER_PROFILE_SWIPE_REFRESH.setRefreshing(false);

    }

    /**
     * To get image which selected by user
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.RC_PHOTO){
            if(resultCode == getActivity().RESULT_OK){
                Uri profileImage = data.getData();

                userDataFirebase.storeUserImageInDatabase(profileImage);

            }else{
                Toast.makeText(getContext(),"Can't not load this photo",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // test ..
        Log.e("onResume called", "done");
        if (Utility.networkIsConnected(getActivity())) {
            userDataFirebase.setUserIdentifiers();
            if(getContext() != null && userProfileViews != null){
                userProfileViews.USER_PROFILE_SWIPE_REFRESH.setRefreshing(true);
            }

        } else {
            Toast.makeText(getContext(), "Check your network connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        // Flag geofence
        userDataFirebaseGeofence.detachListeners();
        // Location Request depend on settings.
        userDataFirebase.detachLocationListener();
        // User Info update
        userDataFirebase.detachProfileListener();
        googleApiClient.disconnect();

        super.onDestroy();
    }

    @Override
    public void createGoogleClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }

    @Override
    public void onConnected(LocationCallback locationCallback) {
        userDataFirebase.setRequestDependOnSettings(locationCallback,googleApiClient, getContext());
    }

    @Override
    public void onConnectionSuspended() {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed() {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        userDataFirebase.updateUserLocation(location);
    }

}