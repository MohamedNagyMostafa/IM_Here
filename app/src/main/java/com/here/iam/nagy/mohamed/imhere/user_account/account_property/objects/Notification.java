package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mohamednagy on 12/25/2016.
 */
public class Notification {

    private String notificationImage;
    private String notificationFirstText;
    private String notificationSecondText;
    private List<String> notificationData;
    private int notificationType;
    private HashMap<String,Object> notificationDate;


    public Notification(){}

    public Notification(String notificationImage, String notificationFirstText,
                        String notificationSecondText, List<String> notificationData,
                        int notificationType){

        this.notificationImage = notificationImage;
        this.notificationFirstText = notificationFirstText;
        this.notificationSecondText = notificationSecondText;
        this.notificationData = notificationData;
        this.notificationType = notificationType;
        notificationDate = new HashMap<>();
        notificationDate.put("date", ServerValue.TIMESTAMP);

    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public String getNotificationFirstText() {
        return notificationFirstText;
    }

    public String getNotificationSecondText() {
        return notificationSecondText;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public List<String> getNotificationData() {
        return notificationData;
    }

    public HashMap<String, Object> getNotificationDate() {
        return notificationDate;
    }

}
