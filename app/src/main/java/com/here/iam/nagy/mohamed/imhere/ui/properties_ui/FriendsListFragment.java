package com.here.iam.nagy.mohamed.imhere.ui.properties_ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseFriendList;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserFriendListUi;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.ui.properties_adapter_ui.UserPropertiesAdapter;
import com.here.iam.nagy.mohamed.imhere.ui.properties_adapter_ui.UserPropertiesAdapterUi;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsListFragment extends Fragment
    implements UserPropertiesAdapterUi, UserFriendListUi{

    private UserPropertiesAdapter userFriendsListAdapter;
    private ArrayList<UserAccount> friendsAccountList;
    private UserDataFirebaseFriendList userDataFirebaseFriendList;
    private SwipeRefreshLayout friendsListSwipeRefresh;

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(Utility.networkIsConnected(getContext())){
                        userDataFirebaseFriendList.attachFriendListListener();
                    }else{
                        Toast.makeText(
                                getContext(),
                                getString(R.string.network_connection),
                                Toast.LENGTH_SHORT
                        ).show();
                        friendsListSwipeRefresh.setRefreshing(false);
                    }
                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String USER_LINK_FIREBASE = getArguments().getString(Constants.USER_EXTRA);

        // get friendsList.
        userDataFirebaseFriendList = new UserDataFirebaseFriendList(USER_LINK_FIREBASE,this, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        ListView friendsListGridView = (ListView) rootView.findViewById(R.id.user_friend_list);
        View emptyView = rootView.findViewById(R.id.friend_list_empty_frame);

        friendsListGridView.setEmptyView(emptyView);

        friendsListSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.friends_list_swipe_refresh);

        if(friendsListSwipeRefresh != null) {
            friendsListSwipeRefresh.setColorSchemeResources(R.color.active_tab);
            friendsListSwipeRefresh.setOnRefreshListener(swipeRefreshListener);
        }

        friendsAccountList = new ArrayList<>();
        userFriendsListAdapter = new UserPropertiesAdapter(getContext(),this);

        // setAdapter ..
        friendsListGridView.setAdapter(userFriendsListAdapter);

        return rootView;
    }

    @Override
    public View getView(int position, ViewGroup parent) {

        final UserAccount friendAccount = friendsAccountList.get(position);

        View friendAccountView = LayoutInflater.from(getContext())
                .inflate(R.layout.friends_list_recycle,parent,false);

        ViewAppHolder.FriendsListViewHolder friendsListViewHolder =
                new ViewAppHolder.FriendsListViewHolder(friendAccountView);

        Log.e("username",friendAccount.getUserName());

        friendsListViewHolder.FRIEND_NAME_TEXT_VIEW.setText(friendAccount.getUserName());

        if(friendAccount.getUserImage() != null)
            Picasso.with(getContext())
                    .load(friendAccount.getUserImage())
                    .into(friendsListViewHolder.FRIEND_IMAGE_VIEW);
        else
            friendsListViewHolder.FRIEND_IMAGE_VIEW.setImageDrawable(
                    getActivity().getDrawable(R.drawable.me));

        userDataFirebaseFriendList.setFriendLocationImage(
                friendsListViewHolder.FRIEND_LOCATION_IMAGE_VIEW, friendAccount);

        friendsListViewHolder.REMOVE_FRIEND_TEXT_VIEW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDataFirebaseFriendList.removeFriendFromListFriend(friendAccount.getUserEmail());
            }
        });

        userDataFirebaseFriendList.setUserState(friendsListViewHolder, friendAccount.getUserEmail());

        return friendAccountView;
    }

    @Override
    public int getCount() {
        return friendsAccountList.size();
    }

    @Override
    public void setFriendsListData(ArrayList<UserAccount> friendsAccountList) {
        this.friendsAccountList = friendsAccountList;

        userFriendsListAdapter.clear();
        userFriendsListAdapter.setNotifyOnChange(true);

        if(friendsListSwipeRefresh != null)
            friendsListSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Utility.networkIsConnected(getContext())) {
            userDataFirebaseFriendList.attachFriendListListener();

            if(friendsListSwipeRefresh != null)
                friendsListSwipeRefresh.setRefreshing(true);
        }else{
            Toast.makeText(getContext(),getString(R.string.network_connection),Toast.LENGTH_SHORT).show();
            if(friendsListSwipeRefresh != null)
                friendsListSwipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onStop() {
        userDataFirebaseFriendList.detachFriendListListener();
        super.onStop();
    }


}
