package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.widget.WidgetUtils;

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


    public static void newNotificationsNumberIncrement(final Context context){
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
                            WidgetUtils.WidgetBroadcast.sendBroadcastToWidget(newNotifications, context);

                        }else{
                            FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                                    .setValue(1);
                            WidgetUtils.WidgetBroadcast.sendBroadcastToWidget(1, context);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void newNotificationsNumberIncrement(final String userLinkFirebase, final Context context){
        FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer newNotifications = dataSnapshot.getValue(int.class);
                        if(newNotifications != null) {
                            newNotifications++;
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(newNotifications);
                            WidgetUtils.WidgetBroadcast.sendBroadcastToWidget(newNotifications, context);

                        }else{
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(1);
                            WidgetUtils.WidgetBroadcast.sendBroadcastToWidget(1, context);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void newNotificationsNumberDecrement(final String userLinkFirebase, final Context context){
        FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer newNotifications = dataSnapshot.getValue(int.class);
                        if(newNotifications != null) {
                            newNotifications--;
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(newNotifications);
                            WidgetUtils.WidgetBroadcast.sendBroadcastToWidget(newNotifications, context);
                        }else{
                            FirebaseHelper.getUserNewNotificationsNumber(userLinkFirebase)
                                    .setValue(0);
                            WidgetUtils.WidgetBroadcast.sendBroadcastToWidget(0, context);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void newNotificationsNumberToZero(Context context){
        FirebaseHelper.getUserNewNotificationsNumber(USER_LINK_FIREBASE)
                .setValue(0);

        WidgetUtils.WidgetBroadcast.sendBroadcastToWidget(0, context);
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

