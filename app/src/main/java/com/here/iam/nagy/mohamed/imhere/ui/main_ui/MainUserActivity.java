package com.here.iam.nagy.mohamed.imhere.ui.main_ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseMainActivity;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;

public class MainUserActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private UserDataFirebaseMainActivity userDataFirebaseMainActivity;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_user_activity);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide App name
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        // get User Link and set it to all fragments.
        final Bundle USER_LINK_FIREBASE =
                getIntent().getExtras();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(USER_LINK_FIREBASE != null) {
            userDataFirebaseMainActivity = new UserDataFirebaseMainActivity(
                    USER_LINK_FIREBASE.getString(Constants.USER_EXTRA));

        }

                // set adapter with user link
                PagerAppAdapter pagerAppAdapter =
                        new PagerAppAdapter(getSupportFragmentManager(), USER_LINK_FIREBASE);

        viewPager.setAdapter(pagerAppAdapter);


        tabLayout.setupWithViewPager(viewPager);

        createTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == Constants.USER_NOTIFICATIONS_TAB)
                    UserDataFirebaseMainActivity.newNotificationsNumberToZero();

                setCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition() == Constants.USER_NOTIFICATIONS_TAB)
                    UserDataFirebaseMainActivity.newNotificationsNumberToZero();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void createTabIcons(){
        View profileTab = LayoutInflater.from(this)
                .inflate(R.layout.custom_tab,null);

        ViewAppHolder.CustomTabViewHolder customTabProfileViewHolder =
                new ViewAppHolder.CustomTabViewHolder(profileTab);

        customTabProfileViewHolder.TAB_IMAGE_VIEW
                .setImageDrawable(getDrawable(R.drawable.ic_person_black_24dp));
        customTabProfileViewHolder.TAB_IMAGE_VIEW
                .setColorFilter(ContextCompat.getColor(this,R.color.active_tab));

        tabLayout.getTabAt(Constants.USER_PROFILE_TAB).setCustomView(profileTab);

        View friendsTab = LayoutInflater.from(this)
                .inflate(R.layout.custom_tab,null);

        ViewAppHolder.CustomTabViewHolder customTabFriendsViewHolder =
                new ViewAppHolder.CustomTabViewHolder(friendsTab);

        customTabFriendsViewHolder.TAB_IMAGE_VIEW
                .setImageDrawable(getDrawable(R.drawable.ic_group_black_24dp));
        customTabFriendsViewHolder.TAB_IMAGE_VIEW
                .setColorFilter(ContextCompat.getColor(this,R.color.non_active_tab));

        tabLayout.getTabAt(Constants.USER_FRIENDS_TAB).setCustomView(friendsTab);

        View notificationTab = LayoutInflater.from(this)
                .inflate(R.layout.custom_tab,null);

        ViewAppHolder.CustomTabViewHolder customTabNotificationViewHolder =
                new ViewAppHolder.CustomTabViewHolder(notificationTab);

        customTabNotificationViewHolder.TAB_IMAGE_VIEW
                .setImageDrawable(getDrawable(R.drawable.ic_notifications_active_black_24dp));
        customTabNotificationViewHolder.TAB_IMAGE_VIEW
                .setColorFilter(ContextCompat.getColor(this,R.color.non_active_tab));

        userDataFirebaseMainActivity.setNewNotificationsListener(
                customTabNotificationViewHolder.TAB_TEXT_VIEW
        );

        tabLayout.getTabAt(Constants.USER_NOTIFICATIONS_TAB).setCustomView(notificationTab);


        View SearchTab = LayoutInflater.from(this)
                .inflate(R.layout.custom_tab,null);

        ViewAppHolder.CustomTabViewHolder customTabSearchViewHolder =
                new ViewAppHolder.CustomTabViewHolder(SearchTab);

        customTabSearchViewHolder.TAB_IMAGE_VIEW
                .setImageDrawable(getDrawable(R.drawable.ic_search_black_24dp));
        customTabSearchViewHolder.TAB_IMAGE_VIEW
                .setColorFilter(ContextCompat.getColor(this,R.color.non_active_tab));

        tabLayout.getTabAt(Constants.USER_SEARCH_TAB).setCustomView(SearchTab);



    }

    private void setCurrentTab() {
        if (tabLayout != null) {
            int currentPosition = tabLayout.getSelectedTabPosition();
            int unSelectedTabs = currentPosition;

            do {
                unSelectedTabs = (unSelectedTabs + 1) % 4;

                Log.e("un selected ", String.valueOf(unSelectedTabs));
                Log.e("un selected ", String.valueOf(currentPosition));

                ViewAppHolder.CustomTabViewHolder customTabViewHolder =
                        new ViewAppHolder.CustomTabViewHolder(
                                tabLayout.getTabAt(unSelectedTabs).getCustomView()
                        );


                if (unSelectedTabs != currentPosition) {
                    customTabViewHolder.TAB_IMAGE_VIEW
                            .setColorFilter(ContextCompat.getColor(this, R.color.non_active_tab));
                } else {
                    customTabViewHolder.TAB_IMAGE_VIEW
                            .setColorFilter(ContextCompat.getColor(this, R.color.active_tab));
                }

            } while (unSelectedTabs != currentPosition);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDataFirebaseMainActivity.attachTextNotificationsListener();
        UserDataFirebaseMainActivity.setUserModeOnline();
        Log.e("is called on start","doooooooneee");

    }
    @Override
    protected void onStop() {
        userDataFirebaseMainActivity.detachTextNotificationsListener();
        UserDataFirebaseMainActivity.setUserModeOffline();
        Log.e("is called on stop","doooooooneee");
        super.onStop();
    }

}
