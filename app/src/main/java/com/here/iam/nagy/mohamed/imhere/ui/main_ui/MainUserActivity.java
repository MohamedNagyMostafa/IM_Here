package com.here.iam.nagy.mohamed.imhere.ui.main_ui;

import android.annotation.TargetApi;
import android.graphics.Color;
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
import android.widget.Toast;

import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseMainActivity;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.widget.WidgetUtils;

import java.util.Objects;

public class MainUserActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private UserDataFirebaseMainActivity userDataFirebaseMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_user_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        handleFirebaseAndPager();

    }

    private void handleFirebaseAndPager(){
        final Bundle USER_LINK_FIREBASE = getIntent().getExtras();

        Integer selectedTap = null;

        if(USER_LINK_FIREBASE != null) {
            userDataFirebaseMainActivity = new UserDataFirebaseMainActivity(
                    USER_LINK_FIREBASE.getString(Constants.USER_EXTRA));

            selectedTap = USER_LINK_FIREBASE.getInt(WidgetUtils.Navigation.NAVIGATION_EXTRA);

            // Set To Widget Preference.
            WidgetUtils.WidgetBroadcast.sendBroadcastToWidgetLogin(getBaseContext(),
                    USER_LINK_FIREBASE.getString(Constants.USER_EXTRA));

        }

        handlePager(USER_LINK_FIREBASE, selectedTap);
    }

    private void handlePager(final Bundle USER_LINK_FIREBASE, Integer selectedTap){

        ViewPager viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        PagerAppAdapter pagerAppAdapter =
                new PagerAppAdapter(getSupportFragmentManager(), USER_LINK_FIREBASE);

        viewPager.setAdapter(pagerAppAdapter);

        tabLayout.setupWithViewPager(viewPager);

        createTabIcons();

        tabLayout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == Constants.USER_NOTIFICATIONS_TAB)
                    UserDataFirebaseMainActivity.newNotificationsNumberToZero(getApplicationContext());

                assert tab.getCustomView() != null;

                new ViewAppHolder.CustomTabViewHolder(tab.getCustomView()).TAB_IMAGE_VIEW
                        .setColorFilter(
                                ContextCompat.getColor(
                                        MainUserActivity.this,
                                        R.color.active_tab
                                )
                        );
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition() == Constants.USER_NOTIFICATIONS_TAB)
                    UserDataFirebaseMainActivity.newNotificationsNumberToZero(getApplicationContext());

                assert tab.getCustomView() != null;

                new ViewAppHolder.CustomTabViewHolder(tab.getCustomView()).TAB_IMAGE_VIEW
                        .setColorFilter(
                                ContextCompat.getColor(
                                        MainUserActivity.this,
                                        R.color.non_active_tab
                                )
                        );
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(selectedTap != null)
            viewPager.setCurrentItem(selectedTap);
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

        Objects.requireNonNull(tabLayout.getTabAt(Constants.USER_FRIENDS_TAB)).setCustomView(friendsTab);

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

        Objects.requireNonNull(tabLayout.getTabAt(Constants.USER_NOTIFICATIONS_TAB)).setCustomView(notificationTab);


        View SearchTab = LayoutInflater.from(this)
                .inflate(R.layout.custom_tab,null);

        ViewAppHolder.CustomTabViewHolder customTabSearchViewHolder =
                new ViewAppHolder.CustomTabViewHolder(SearchTab);

        customTabSearchViewHolder.TAB_IMAGE_VIEW
                .setImageDrawable(getDrawable(R.drawable.ic_search_black_24dp));
        customTabSearchViewHolder.TAB_IMAGE_VIEW
                .setColorFilter(ContextCompat.getColor(this,R.color.non_active_tab));

        Objects.requireNonNull(tabLayout.getTabAt(Constants.USER_SEARCH_TAB)).setCustomView(SearchTab);

    }

    @Override
    protected void onStart() {
        super.onStart();
        userDataFirebaseMainActivity.attachTextNotificationsListener();
        UserDataFirebaseMainActivity.setUserModeOnline();

    }
    @Override
    protected void onStop() {
        userDataFirebaseMainActivity.detachTextNotificationsListener();
        UserDataFirebaseMainActivity.setUserModeOffline();
        super.onStop();
    }

}
