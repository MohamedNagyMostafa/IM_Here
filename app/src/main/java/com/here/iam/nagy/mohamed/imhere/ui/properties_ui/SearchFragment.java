package com.here.iam.nagy.mohamed.imhere.ui.properties_ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseSearch;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserSearchUi;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.ui.properties_adapter_ui.UserPropertiesAdapter;
import com.here.iam.nagy.mohamed.imhere.ui.properties_adapter_ui.UserPropertiesAdapterUi;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchFragment extends Fragment
    implements UserPropertiesAdapterUi, UserSearchUi{

    private ArrayList<UserAccount> userAccountArrayList;
    private UserPropertiesAdapter searchAdapter;
    private UserDataFirebaseSearch userDataFirebaseSearch;

    private TextWatcher editTextSearchWatcher =
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(Utility.networkIsConnected(getContext())) {
                        if (!charSequence.toString().equals(""))
                            userDataFirebaseSearch.notifySearchChanged(charSequence.toString());
                        else {
                            searchAdapter.clear();
                            userAccountArrayList.clear();
                        }
                    }else{
                        Toast.makeText(getContext(),getString(R.string.network_connection),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView =  inflater.inflate(R.layout.fragment_search, container, false);

        // set empty frame.
        View emptyView = rootView.findViewById(R.id.search_empty_frame);

        final EditText SEARCH_EDIT_TEXT = (EditText) rootView.findViewById(R.id.search_edit_text);
        final ListView SEARCH_LIST_VIEW = (ListView) rootView.findViewById(R.id.search_list_view);
        SEARCH_LIST_VIEW.setEmptyView(emptyView);

        userAccountArrayList = new ArrayList<>();

        searchAdapter = new UserPropertiesAdapter(getContext(),this);

        SEARCH_EDIT_TEXT.addTextChangedListener(editTextSearchWatcher);
        SEARCH_LIST_VIEW.setAdapter(searchAdapter);
        // get data

        final String USER_LINK_FIREBASE = getArguments().getString(Constants.USER_EXTRA);

        userDataFirebaseSearch = new UserDataFirebaseSearch(USER_LINK_FIREBASE,this);

        return rootView;
    }

    @Override
    public void setSearchData(ArrayList<UserAccount> usersAccounts) {
        this.userAccountArrayList = usersAccounts;
        searchAdapter.clear();
        searchAdapter.setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, ViewGroup parent) {

        UserAccount currentUserAccount = userAccountArrayList.get(position);

        View searchRecycle = LayoutInflater.from(getContext())
                .inflate(R.layout.search_recycle,parent,false);

        ViewAppHolder.SearchViewHolder searchViewHolder =
                new ViewAppHolder.SearchViewHolder(searchRecycle);


        if(currentUserAccount.getUserImage() != null)
            Picasso.with(getContext())
                    .load(currentUserAccount.getUserImage())
                    .into(searchViewHolder.SEARCH_IMAGE_VIEW);
        else
            searchViewHolder.SEARCH_IMAGE_VIEW
                    .setImageDrawable(getActivity().getDrawable(R.drawable.me));

        searchViewHolder.SEARCH_FIRST_TEXT_VIEW.setText(currentUserAccount.getUserName());
        searchViewHolder.SEARCH_SECOND_TEXT_VIEW.setText(currentUserAccount.getUserEmail());

        // relation between Current user and others user in search list.

        userDataFirebaseSearch.setUserRelation(currentUserAccount.getUserEmail(),searchViewHolder);

        return searchRecycle;
    }

    @Override
    public void setUserView(final boolean userInFriendList, final boolean userInFriendRequest,
                            final boolean userInSendFriendRequest,
                            final ViewAppHolder.SearchViewHolder searchViewHolder,
                            final String USER_EMAIL) {

        // Listeners ...

        final View.OnClickListener ACCEPT_FRIEND_LISTENER =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Utility.networkIsConnected(getContext())) {
                            userDataFirebaseSearch.acceptFriendRequest(USER_EMAIL);
                            userDataFirebaseSearch.setFriendsFlag(USER_EMAIL);
                            userDataFirebaseSearch.setUserRelation(USER_EMAIL, searchViewHolder);
                        }else{
                            Toast.makeText(getContext(),
                                    R.string.accept_friend_network
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        final View.OnClickListener ADD_FRIEND_LISTENER =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(Utility.networkIsConnected(getContext())) {
                            userDataFirebaseSearch.sendFriendRequest(USER_EMAIL);
                            userDataFirebaseSearch.setUserRelation(USER_EMAIL, searchViewHolder);
                        }else{
                            Toast.makeText(getContext(),
                                    getString(R.string.send_friend_network)
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        final View.OnClickListener REMOVE_FRIEND_LISTENER =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Utility.networkIsConnected(getContext())) {
                            userDataFirebaseSearch.removeFriendFromListFriend(USER_EMAIL);
                            userDataFirebaseSearch.removeFriendsFlags(USER_EMAIL);
                            userDataFirebaseSearch.setUserRelation(USER_EMAIL, searchViewHolder);
                        }else{
                            Toast.makeText(getContext(),
                                    getString(R.string.remove_friend_network)
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        final View.OnClickListener IGNORE_FRIEND_LISTENER =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Utility.networkIsConnected(getContext())) {
                            userDataFirebaseSearch.ignoreFriendRequest(USER_EMAIL);
                            userDataFirebaseSearch.setUserRelation(USER_EMAIL, searchViewHolder);
                        }else{
                            Toast.makeText(getContext(),
                                    getString(R.string.ignore_friend_network)
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        final View.OnClickListener CANCEL_FRIEND_REQUEST_LISTENER =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Utility.networkIsConnected(getContext())) {
                            userDataFirebaseSearch.cancelFriendSendRequestBySender(USER_EMAIL);
                            setUserView(false, false,
                                    false, searchViewHolder,
                                    USER_EMAIL);
                        }else{
                            Toast.makeText(getContext(),
                                    getString(R.string.cancel_friend_network)
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        if(userInFriendList){

            searchViewHolder.SEARCH_ACCEPT_TEXT_VIEW.setVisibility(View.INVISIBLE);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setVisibility(View.VISIBLE);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setText(getString(R.string.remove_friend));
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setOnClickListener(REMOVE_FRIEND_LISTENER);

        }else if(userInFriendRequest){

            searchViewHolder.SEARCH_ACCEPT_TEXT_VIEW.setVisibility(View.VISIBLE);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setVisibility(View.VISIBLE);
            searchViewHolder.SEARCH_ACCEPT_TEXT_VIEW.setText(getString(R.string.accept_friend));
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setText(getString(R.string.ignore_friend));
            searchViewHolder.SEARCH_ACCEPT_TEXT_VIEW.setOnClickListener(ACCEPT_FRIEND_LISTENER);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setOnClickListener(IGNORE_FRIEND_LISTENER);

        }else if(userInSendFriendRequest){

            searchViewHolder.SEARCH_ACCEPT_TEXT_VIEW.setVisibility(View.INVISIBLE);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setVisibility(View.VISIBLE);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setText(getString(R.string.remove_friend_request));
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setOnClickListener(CANCEL_FRIEND_REQUEST_LISTENER);

        }else{

            searchViewHolder.SEARCH_ACCEPT_TEXT_VIEW.setVisibility(View.INVISIBLE);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setVisibility(View.VISIBLE);
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setText(getString(R.string.add_friend));
            searchViewHolder.SEARCH_IGNORE_TEXT_VIEW.setOnClickListener(ADD_FRIEND_LISTENER);

        }
    }

    @Override
    public int getCount() {
        return userAccountArrayList.size();
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
