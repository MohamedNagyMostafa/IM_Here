package com.here.iam.nagy.mohamed.imhere.ui.main_ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.FriendsListFragment;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.NotificationsFragment;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.SearchFragment;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.UserProfileFragment;

/**
 * Created by mohamednagy on 12/25/2016.
 */
public class PagerAppAdapter extends FragmentPagerAdapter {

    private final Bundle USER_LINK_FIREBASE;

    public PagerAppAdapter(FragmentManager fragmentManager,
                           final Bundle USER_LINK_FIREBASE) {
        super(fragmentManager);
        this.USER_LINK_FIREBASE = USER_LINK_FIREBASE;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case Constants.USER_PROFILE_FRAGMENT :
                return setUserToFragment(new UserProfileFragment());
            case Constants.USER_FRIENDS_LIST_FRAGMENT:
                return setUserToFragment(new FriendsListFragment());
            case Constants.USER_NOTIFICATIONS_FRAGMENT:
                return setUserToFragment(new NotificationsFragment());
            case Constants.SEARCH_FRAGMENT:
                return setUserToFragment(new SearchFragment());
        }
        return null;
    }


    @Override
    public int getCount() {
        return 4;
    }

    public Fragment setUserToFragment(Fragment fragment){
        fragment.setArguments(USER_LINK_FIREBASE);
        return fragment;
    }



}
