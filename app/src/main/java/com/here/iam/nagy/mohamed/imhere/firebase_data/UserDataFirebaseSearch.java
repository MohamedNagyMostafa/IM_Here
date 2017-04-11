package com.here.iam.nagy.mohamed.imhere.firebase_data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.FlagsMarkers;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

import java.util.ArrayList;

/**
 * Created by mohamednagy on 12/26/2016.
 */
public class UserDataFirebaseSearch extends UserDataFirebase {

    private UserSearchUi userSearchUi;
    private ArrayList<UserAccount> userAccountArrayList;

    public UserDataFirebaseSearch(final String USER_LINK_LIST,
                                  UserSearchUi userSearchUi){
        super(USER_LINK_LIST);

        this.userSearchUi = userSearchUi;
    }

    public void notifySearchChanged(final String NEW_TEXT_SEARCH){
        userAccountArrayList = new ArrayList<>();

        // Get all users
        FirebaseHelper.USERS_DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userAccountArrayList.clear();
                userSearchUi.setSearchData(addUsersToArrayList(dataSnapshot,NEW_TEXT_SEARCH));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<UserAccount> addUsersToArrayList(DataSnapshot dataSnapshot,
                                                       final String NEW_TEXT_SEARCH){

        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
            // get all users except current user and check them with search text
            if(!dataSnapshot1.getKey().equals(USER_LINK_FIREBASE) && dataSnapshot1.getKey().contains(NEW_TEXT_SEARCH)){
                userAccountArrayList.add(dataSnapshot1.getValue(UserAccount.class));
            }
        }

        return userAccountArrayList;
    }

    public void setUserRelation(final String USER_EMAIL,
                                final ViewAppHolder.SearchViewHolder searchViewHolder){

        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userInFriendList = checkUserInFriendList(dataSnapshot,USER_EMAIL);
                boolean userInFriendRequest = checkUserInFriendRequest(dataSnapshot,USER_EMAIL);
                boolean currentUserSendFriendRequest = checkCurrentUserRequestSend(dataSnapshot,USER_EMAIL);

                userSearchUi.setUserView(userInFriendList, userInFriendRequest,
                        currentUserSendFriendRequest, searchViewHolder,
                        USER_EMAIL);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean checkUserInFriendList(DataSnapshot dataSnapshot,final String USER_EMAIL){
        for(DataSnapshot dataSnapshot1 : dataSnapshot.child(Constants.USER_FRIENDS_LIST).getChildren()){
            if(dataSnapshot1.getValue().equals(USER_EMAIL)){
                return true;
            }
        }
        return false;
    }

    private boolean checkUserInFriendRequest(DataSnapshot dataSnapshot,final String USER_EMAIL){
        for(DataSnapshot dataSnapshot1 : dataSnapshot.child(Constants.USER_FRIEND_REQUEST_LIST).getChildren()){
            if(dataSnapshot1.getValue().equals(USER_EMAIL)){
                return true;
            }
        }
        return false;
    }

    private boolean checkCurrentUserRequestSend(DataSnapshot dataSnapshot,final String USER_EMAIL){
        for(DataSnapshot dataSnapshot1 : dataSnapshot.child(Constants.USER_REQUESTS)
                .child(Constants.USER_FRIEND_REQUESTS).getChildren()){
            if(dataSnapshot1.getValue().equals(USER_EMAIL)){
                return true;
            }
        }
        return false;
    }

    public void removeFriendsFlags(String USER_EMAIL){
        // remove user friend flags from this friend.
        FirebaseHelper.getUserFriendsFlags(Utility.encodeUserEmail(USER_EMAIL))
                .child(USER_LINK_FIREBASE).removeValue();
        // remove friend flag from current user.
        FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                .child(Utility.encodeUserEmail(USER_EMAIL)).removeValue();
    }

    public void setFriendsFlag(final String USER_EMAIL){
        // set current user flags to new friend.
        // if user flag is not founded in public then this flag is
        // friend flag.
        // First check if user have flag or not.
        setCurrentUserFlagAToB(Utility.decodeUserEmail(USER_LINK_FIREBASE),USER_EMAIL);

        /// set new friend flag if founded.
        setCurrentUserFlagAToB(USER_EMAIL , Utility.decodeUserEmail(USER_LINK_FIREBASE));

    }

    private void setCurrentUserFlagAToB(final String USER_EMAIL_A ,final String USER_EMAIL_B){
        FirebaseHelper.getUserFlag(Utility.encodeUserEmail(USER_EMAIL_A))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            final FlagsMarkers userFlag = dataSnapshot.getValue(FlagsMarkers.class);

                            // check flag type.
                            FirebaseHelper.getPublicFlags().child(Utility.encodeUserEmail(USER_EMAIL_A))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getValue() == null){
                                                FirebaseHelper.getUserFriendsFlags(Utility.encodeUserEmail(USER_EMAIL_B))
                                                        .child(Utility.encodeUserEmail(USER_EMAIL_A)).setValue(userFlag);
                                            }
                                            // else do nothing .. (public flag).
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                        /// else do nothing.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
