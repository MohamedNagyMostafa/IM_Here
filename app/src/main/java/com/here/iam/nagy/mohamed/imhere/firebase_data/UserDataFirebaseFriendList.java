package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.AccountSettings;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Notification;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohamednagy on 12/25/2016.
 */
public class UserDataFirebaseFriendList extends UserDataFirebase{

    private UserFriendListUi userFriendListUi;
    private ArrayList<UserAccount> userFriendsList;
    private Context context;

    private ValueEventListener friendListListener =
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(userFriendsList != null)
                        userFriendsList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            getUserAccountFromEmail(dataSnapshot1.getValue(String.class));
                        }
                    userFriendListUi.setFriendsListData(userFriendsList);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

    public UserDataFirebaseFriendList(String userLinkFirebase,
                                      UserFriendListUi userFriendListUi,
                                      Context context) {
        super(userLinkFirebase);
        this.userFriendListUi = userFriendListUi;
        this.context  = context;
    }

    public void attachFriendListListener(){

        userFriendsList = new ArrayList<>();

        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                .addValueEventListener(friendListListener);
    }

    /**
     * Get Account User From its id
     */

    private void getUserAccountFromEmail(final String USER_EMAIL){

        FirebaseHelper.getUserDatabaseReference(Utility.encodeUserEmail(USER_EMAIL))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userFriendsList.add(dataSnapshot.getValue(UserAccount.class));
                        userFriendListUi.setFriendsListData(userFriendsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void detachFriendListListener(){
        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                .removeEventListener(friendListListener);
    }

    /**
     * First we will check if request is sent before or not.
     * Second check friend location setting is enabled or not.
     * if first is true and  second is false true then visible location request image.
     * Otherwise not.
     * @param friendLocationImage
     * @param friendAccount
     */

    public void setFriendLocationImage(final ImageView friendLocationImage, final UserAccount friendAccount){

        FirebaseHelper.getUserLocationRequestsSend(USER_LINK_FIREBASE)
                .child(Utility.encodeUserEmail(friendAccount.getUserEmail()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){
                            // check friend location setting is enabled or not.
                            FirebaseHelper.getUserAccountSettings(Utility.encodeUserEmail(friendAccount.getUserEmail()))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            AccountSettings friendAccountSettings =
                                                    dataSnapshot.getValue(AccountSettings.class);

                                            if(friendAccountSettings.getLocationAppearToFriends()){
                                                friendLocationImage.setVisibility(View.GONE);
                                            }else{
                                                // set listener for friend location image
                                                friendLocationImage.setVisibility(View.VISIBLE);
                                                friendLocationImage.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        sendLocationRequest(
                                                                Utility.encodeUserEmail(friendAccount.getUserEmail()));
                                                                friendLocationImage.setVisibility(View.GONE);

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }else{
                            friendLocationImage.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void sendLocationRequest(final String USER_EMAIL){
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);

                        String notificationKey =
                                FirebaseHelper.getUserNotifications(Utility.encodeUserEmail(USER_EMAIL))
                                .push().getKey();

                        List<String> notificationData = new ArrayList<String>();
                        notificationData.add(USER_LINK_FIREBASE);
                        notificationData.add(notificationKey);

                        Notification notification = new Notification(
                                userAccount.getUserImage(),
                                userAccount.getUserName(),
                                "Ask you make your location visible for friends.",
                                notificationData,
                                Constants.NOTIFICATION_LOCATION_REQUEST
                        );

                        FirebaseHelper.getUserNotifications(Utility.encodeUserEmail(USER_EMAIL))
                                .child(notificationKey).setValue(notification);

                        UserDataFirebaseMainActivity
                                .newNotificationsNumberIncrement(Utility.encodeUserEmail(USER_EMAIL), context);

                        FirebaseHelper.getUserLocationRequestsSend(USER_LINK_FIREBASE)
                                .child(Utility.encodeUserEmail(USER_EMAIL))
                                .setValue(true);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
