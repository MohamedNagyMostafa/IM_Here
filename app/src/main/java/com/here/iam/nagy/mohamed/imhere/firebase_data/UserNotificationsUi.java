package com.here.iam.nagy.mohamed.imhere.firebase_data;

import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.Notification;

import java.util.ArrayList;

/**
 * Created by mohamednagy on 12/26/2016.
 */
public interface UserNotificationsUi {
    void setNotificationsData(ArrayList<Notification> userNotificationList);
}
