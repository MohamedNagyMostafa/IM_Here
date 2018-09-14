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
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseNotifications;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserNotificationsUi;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.ui.properties_adapter_ui.UserPropertiesAdapter;
import com.here.iam.nagy.mohamed.imhere.ui.properties_adapter_ui.UserPropertiesAdapterUi;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Notification;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotificationsFragment extends Fragment
    implements UserNotificationsUi, UserPropertiesAdapterUi{

    private UserPropertiesAdapter notificationsAdapter;
    private ArrayList<Notification> notificationArrayList;
    private UserDataFirebaseNotifications userDataFirebaseNotifications;
    private SwipeRefreshLayout notificationSwipeRefresh;
    private SwipeRefreshLayout.OnRefreshListener notificationSwipeRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if(Utility.networkIsConnected(getActivity())) {
                        userDataFirebaseNotifications.attachUserNotificationListener();
                    }else{
                        Toast.makeText(
                                getContext(),
                                getString(R.string.network_connection),
                                Toast.LENGTH_SHORT
                        ).show();
                        notificationSwipeRefresh.setRefreshing(false);
                    }

                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get data
        final String USER_LINK_FIREBASE = getArguments().getString(Constants.USER_EXTRA);

        // get notifications
        if(getActivity() == null)
            Log.e("null", "fuuuuuuuuuuuuuuuuk");
        userDataFirebaseNotifications
                = new UserDataFirebaseNotifications(USER_LINK_FIREBASE,this, getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_notifications, container, false);

        ListView notificationListView = (ListView) rootView.findViewById(R.id.notifications_list);
        View emptyView = rootView.findViewById(R.id.notification_empty_frame);

        notificationListView.setEmptyView(emptyView);

        notificationsAdapter = new UserPropertiesAdapter(getContext(),this);
        notificationArrayList = new ArrayList<>();
        notificationSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.notification_refresh_swipe);


        if(notificationSwipeRefresh != null) {
            notificationSwipeRefresh.setColorSchemeResources(R.color.active_tab);
            notificationSwipeRefresh.setOnRefreshListener(notificationSwipeRefreshListener);
        }

        notificationListView.setAdapter(notificationsAdapter);

        return rootView;
    }

    @Override
    public void setNotificationsData(ArrayList<Notification> userNotificationList) {
        this.notificationArrayList.clear();
        // inverse...
        for(int i = userNotificationList.size()- 1 ; i > -1  ; i--){
            this.notificationArrayList.add(userNotificationList.get(i));
        }
        notificationsAdapter.clear();
        notificationsAdapter.setNotifyOnChange(true);

        if(notificationSwipeRefresh != null)
            notificationSwipeRefresh.setRefreshing(false);
    }

    @Override
    public View getView(int position, ViewGroup parent) {

        final Notification notification = notificationArrayList.get(position);

        View notificationRecycle = LayoutInflater.from(getContext())
                .inflate(R.layout.notifications_recycle,parent,false);

        ViewAppHolder.NotificationsViewHolder notificationsViewHolder
                = new ViewAppHolder.NotificationsViewHolder(notificationRecycle);

        if(notification.getNotificationFirstText() != null)
            notificationsViewHolder.NOTIFICATION_FIRST_TEXT_VIEW
                    .setText(notification.getNotificationFirstText());

        if(notification.getNotificationSecondText() != null)
            notificationsViewHolder.NOTIFICATION_SECOND_TEXT_VIEW
                    .setText(notification.getNotificationSecondText());
        if(notification.getNotificationDate() != null) {
            notificationsViewHolder.NOTIFICATION_DATE_TEXT_VIEW
                    .setText(Utility.getDateAsString((long) notification.getNotificationDate().get("date")));
        }

        switch(notification.getNotificationType()){

            case Constants.NOTIFICATION_FRIEND_REQUEST :
                setNotificationAsFriendRequest(notificationsViewHolder,notification);
                break;
            case Constants.NOTIFICATION_HELP:
                setNotificationAsHelp(notificationsViewHolder, notification);
                break;
            case Constants.NOTIFICATION_FLAG:
                setNotificationAsFlag(notificationsViewHolder,notification);
                break;
            case Constants.NOTIFICATION_LOCATION_REQUEST:
                setNotificationAsLocationRequest(notificationsViewHolder, notification);
                break;
            case Constants.NOTIFICATION_DETECTION:
                setNotificationAsDetection(notificationsViewHolder, notification);
        }

        return notificationRecycle;
    }

    private void setNotificationAsDetection(
            ViewAppHolder.NotificationsViewHolder notificationsViewHolder,
            Notification notification){

        if(notification.getNotificationImage() != null) {
            Picasso.with(getContext())
                    .load(notification.getNotificationImage())
                    .into(notificationsViewHolder.NOTIFICATION_IMAGE_VIEW);
        }else{
            notificationsViewHolder.NOTIFICATION_IMAGE_VIEW
                    .setImageDrawable(getActivity().getDrawable(R.drawable.profile_pic_image));
        }


    }

    private void setNotificationAsLocationRequest(
            final ViewAppHolder.NotificationsViewHolder notificationsViewHolder,
            final Notification notification){

        notificationsViewHolder.NOTIFICATION_ACCEPT_TEXT_VIEW
                .setText(getString(R.string.accept_friend));
        notificationsViewHolder.NOTIFICATION_IGNORE_TEXT_VIEW
                .setText(getString(R.string.ignore_friend));

        notificationsViewHolder.NOTIFICATION_ACCEPT_TEXT_VIEW
                .setVisibility(View.VISIBLE);
        notificationsViewHolder.NOTIFICATION_IGNORE_TEXT_VIEW
                .setVisibility(View.VISIBLE);

        notificationsViewHolder.NOTIFICATION_ACCEPT_TEXT_VIEW
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userDataFirebaseNotifications
                                .updateUserLocationSetting();

                        userDataFirebaseNotifications
                                .deleteLocationRequestNotification(
                                        notification.getNotificationData().get(1),
                                        notification.getNotificationData().get(0));

                        userDataFirebaseNotifications
                                .sendAcceptLocationRequestNotification
                                        (notification.getNotificationData().get(0));

                    }
                });

        notificationsViewHolder.NOTIFICATION_IGNORE_TEXT_VIEW
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        userDataFirebaseNotifications
                                .deleteLocationRequestNotification(
                                        notification.getNotificationData().get(1),
                                        notification.getNotificationData().get(0));
                    }
                });

        if(notification.getNotificationImage() != null) {
            Picasso.with(getContext())
                    .load(notification.getNotificationImage())
                    .into(notificationsViewHolder.NOTIFICATION_IMAGE_VIEW);
        }else{
            notificationsViewHolder.NOTIFICATION_IMAGE_VIEW
                    .setImageDrawable(getActivity().getDrawable(R.drawable.profile_pic_image));
        }

    }



    private void setNotificationAsFriendRequest(
            ViewAppHolder.NotificationsViewHolder notificationsViewHolder,
            final Notification notification){

        notificationsViewHolder.NOTIFICATION_ACCEPT_TEXT_VIEW
                .setText(getString(R.string.accept_friend));
        notificationsViewHolder.NOTIFICATION_IGNORE_TEXT_VIEW
                .setText(getString(R.string.ignore_friend));

        notificationsViewHolder.NOTIFICATION_ACCEPT_TEXT_VIEW
                .setVisibility(View.VISIBLE);
        notificationsViewHolder.NOTIFICATION_IGNORE_TEXT_VIEW
                .setVisibility(View.VISIBLE);

        notificationsViewHolder.NOTIFICATION_ACCEPT_TEXT_VIEW
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userDataFirebaseNotifications.acceptFriendRequest(
                                notification.getNotificationData().get(0), getContext());
                    }
                });

        notificationsViewHolder.NOTIFICATION_IGNORE_TEXT_VIEW
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userDataFirebaseNotifications.ignoreFriendRequest(notification.getNotificationData().get(0));
                    }
                });

        if(notification.getNotificationImage() != null) {
            Picasso.with(getContext())
                    .load(notification.getNotificationImage())
                    .into(notificationsViewHolder.NOTIFICATION_IMAGE_VIEW);
        }else{
            notificationsViewHolder.NOTIFICATION_IMAGE_VIEW
                    .setImageDrawable(getActivity().getDrawable(R.drawable.profile_pic_image));
        }
    }

    private void setNotificationAsHelp(ViewAppHolder.NotificationsViewHolder notificationAsHelp,
                                       Notification notification){

        notificationAsHelp.NOTIFICATION_FIRST_TEXT_VIEW.setTextColor(
                getResources().getColor(R.color.help_color));

        notificationAsHelp.NOTIFICATION_SECOND_TEXT_VIEW.setTextColor(
                getResources().getColor(R.color.help_color));

        if(notification.getNotificationImage() != null) {
            Picasso.with(getContext())
                    .load(notification.getNotificationImage())
                    .into(notificationAsHelp.NOTIFICATION_IMAGE_VIEW);
        }else{
            notificationAsHelp.NOTIFICATION_IMAGE_VIEW
                    .setImageDrawable(getActivity().getDrawable(R.drawable.profile_pic_image));
        }

        /// TODO Set onClickListener to open map.

    }

    private void setNotificationAsFlag(ViewAppHolder.NotificationsViewHolder notificationsViewHolder,
                                       final Notification notification){
        notificationsViewHolder.NOTIFICATION_LAYOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDataFirebaseNotifications
                        .setFlagNotificationClickAction(notification, getContext());
            }
        });

        if(notification.getNotificationImage() != null) {
            Picasso.with(getContext())
                    .load(notification.getNotificationImage())
                    .into(notificationsViewHolder.NOTIFICATION_IMAGE_VIEW);
        }else{
            notificationsViewHolder.NOTIFICATION_IMAGE_VIEW
                    .setImageDrawable(getActivity().getDrawable(R.drawable.empty_flag_image));
        }
    }
    @Override
    public int getCount() {
        return notificationArrayList.size();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Utility.networkIsConnected(getContext())) {
            userDataFirebaseNotifications.attachUserNotificationListener();
        }else{
            Toast.makeText(getContext(),getString(R.string.network_connection),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        userDataFirebaseNotifications.detachUserNotificationListener();
        super.onStop();
    }
}
