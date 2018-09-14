package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.flag.FlagDetailsActivity;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.FlagsMarkers;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Notification;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohamednagy on 12/26/2016.
 */
public class UserDataFirebaseNotifications extends UserDataFirebase {

    private UserNotificationsUi userNotificationsUi;
    private Context context;

    private ValueEventListener userNotificationListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.e("key",dataSnapshot.getKey());
            userNotificationsUi.setNotificationsData(getUserNotifications(dataSnapshot));
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public UserDataFirebaseNotifications(String userLinkFirebase,
                                         UserNotificationsUi userNotificationsUi,
                                         Context context) {
        super(userLinkFirebase);
        this.userNotificationsUi = userNotificationsUi;
        if(context != null)
            Log.e("not null", "fuuuuuuuuuuuuuuuuk2");
        this.context = context;
    }

    public void attachUserNotificationListener(){
        FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                .addValueEventListener(userNotificationListener);
    }

    public void detachUserNotificationListener(){
        FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                .addValueEventListener(userNotificationListener);
    }

    private ArrayList<Notification> getUserNotifications(DataSnapshot dataSnapshot){
        ArrayList<Notification> notificationses = new ArrayList<>();

        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
            notificationses.add(dataSnapshot1.getValue(Notification.class));
        }


        return notificationses;
    }

    /**
     * Determine action of notification click for flag
     * 1- if flag is visited then the click action move user to
     * flag details.
     * 2- if flas is not visited then the click action display message for
     * user said that he is became away from flag.
     * @param context
     */
    public void setFlagNotificationClickAction(
            Notification notification,
            Context context){
        // check flags type.
        switch (notification.getNotificationData().get(1)){

            case Constants.PUBLIC_FLAGS:
                setPublicFlagNotificationClickAction(notification,context);
                break;
            case Constants.FRIENDS_FLAGS:
                setFriendFlagNotificationClickAction(notification,context);
                break;
        }
    }

    private void setPublicFlagNotificationClickAction(final  Notification notification,
                                                      final Context context){
        // check first visited state.
        FirebaseHelper.getPublicFlags()
                .child(notification.getNotificationData().get(0))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null && isFlagVisited(dataSnapshot.getValue(FlagsMarkers.class))){

                            startFlagDetailsActivity(notification,context);

                        }else{
                            if(dataSnapshot.getValue() != null) {
                                Toast.makeText(context,
                                        "You became for away from that flag",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }else{
                                Toast.makeText(context,
                                        "Flag is removed.",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setFriendFlagNotificationClickAction(final  Notification notification,
                                                      final Context context){
        // check first visited state.
        FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                .child(notification.getNotificationData().get(0))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null && isFlagVisited(dataSnapshot.getValue(FlagsMarkers.class))){

                            startFlagDetailsActivity(notification,context);

                        }else{
                            if(dataSnapshot.getValue() != null) {
                                Toast.makeText(context,
                                        "You became for away from that flag",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }else{
                                Toast.makeText(context,
                                        "Flag is removed",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void startFlagDetailsActivity(Notification notification, Context context){
        Intent flagDetailsActivity = new Intent(context, FlagDetailsActivity.class);
        flagDetailsActivity.putExtra(Constants.USER_EXTRA,USER_LINK_FIREBASE);
        flagDetailsActivity.putStringArrayListExtra(Constants.FLAG_EXTRA,(ArrayList<String>)
                notification.getNotificationData());

        context.startActivity(flagDetailsActivity);
    }

    private boolean isFlagVisited(FlagsMarkers flagsMarkers){
        if(flagsMarkers.getIsVisited() != null &&
                flagsMarkers.getIsVisited().containsKey(USER_LINK_FIREBASE)){
            return flagsMarkers.getIsVisited().get(USER_LINK_FIREBASE);
        }
        return false;
    }

    public void deleteLocationRequestNotification(String notificationChild, String USER_EMAIL){
        FirebaseHelper.getUserNotifications(USER_LINK_FIREBASE)
                .child(notificationChild).removeValue();

        deleteLocationRequestFromSender(USER_EMAIL);

    }

    private void deleteLocationRequestFromSender(String USER_EMAIL) {
        FirebaseHelper.getUserLocationRequestsSend(Utility.encodeUserEmail(USER_EMAIL))
                .child(USER_LINK_FIREBASE).removeValue();
    }

    public void sendAcceptLocationRequestNotification(final String USER_EMAIL){
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);

                        List<String> notificationData = new ArrayList<String>();
                        notificationData.add(USER_LINK_FIREBASE);

                        Notification locationAcceptNotification = new Notification(
                                userAccount.getUserImage(),
                                null,
                                userAccount.getUserName() + " accept your request, You can see his location in map",
                                notificationData,
                                Constants.NOTIFICATION_LOCATION_ACCEPTED
                        );

                        FirebaseHelper.getUserNotifications(Utility.encodeUserEmail(USER_EMAIL))
                                .push().setValue(locationAcceptNotification);

                        if(context != null)
                            Log.e("not null", "fuuuuuuuuuuuuuuuuk3");
                        else
                            Log.e(" null", "fuuuuuuuuuuuuuuuuk3");
                        UserDataFirebaseMainActivity.newNotificationsNumberIncrement(
                                Utility.encodeUserEmail(USER_EMAIL), context);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void updateUserLocationSetting(){
        FirebaseHelper.getUserAccountSettings(Utility.encodeUserEmail(USER_LINK_FIREBASE))
                .child(Constants.LOCATION_APPEAR_TO_FRIENDS).setValue(true);
    }
}
