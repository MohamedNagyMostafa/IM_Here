package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;

/**
 * Created by mohamednagy on 1/17/2017.
 */
public class UserDataFirebaseMainActivity {

    public static String USER_LINK_FIREBASE;
    private ValueEventListener newNotificationsListener ;

    public UserDataFirebaseMainActivity(final String USER_LINK_FIREBASE_){
        USER_LINK_FIREBASE = USER_LINK_FIREBASE_;
    }

    public void attachTextNotificationsListener(){
        if(newNotificationsListener != null)
            FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
            .addValueEventListener(newNotificationsListener);
    }

    public void detachTextNotificationsListener(){
        if(newNotificationsListener != null)
            FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                    .removeEventListener(newNotificationsListener);
    }

    public void setNewNotificationsListener (final TextView notificationTextView){
        FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                .addValueEventListener(newNotificationsListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer newNotificationsNumber = dataSnapshot.getValue(Integer.class);

                        if(newNotificationsNumber != null && newNotificationsNumber > 0) {
                            notificationTextView.setVisibility(View.VISIBLE);
                            notificationTextView.setText(String.valueOf(newNotificationsNumber));
                        }else{
                            notificationTextView.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public static void newNotificationsNumberIncrement(){
        FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Integer newNotifications = dataSnapshot.getValue(int.class);
                        Log.e("notification number ",String.valueOf(newNotifications));
                        if(newNotifications != null) {
                            newNotifications++;
                            FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                                    .setValue(newNotifications);
                        }else{
                            FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                                    .setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void newNotificationsNumberIncrement(final String userLinkFirebase){
        FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer newNotifications = dataSnapshot.getValue(int.class);
                        if(newNotifications != null) {
                            newNotifications++;
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(newNotifications);
                        }else{
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void newNotificationsNumberDecrement(final String userLinkFirebase){
        FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer newNotifications = dataSnapshot.getValue(int.class);
                        if(newNotifications != null) {
                            newNotifications--;
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(newNotifications);
                        }else{
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void newNotificationsNumberToZero(){
        FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                .setValue(0);
    }

    public static void setUserModeOnline(){
        FirebaseHelper.getUserActiveMode(USER_LINK_FIREBASE)
                .setValue(Constants.USER_ONLINE);
    }

    public static void setUserModeOffline(){
        FirebaseHelper.getUserActiveMode(USER_LINK_FIREBASE)
                .setValue(Constants.USER_OFFLINE);
    }
}

