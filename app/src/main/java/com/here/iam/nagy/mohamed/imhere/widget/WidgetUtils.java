package com.here.iam.nagy.mohamed.imhere.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.ui.main_ui.MainUserActivity;
import com.here.iam.nagy.mohamed.imhere.ui.properties_ui.location_map.MapActivity;

/**
 * Created by Mohamed Nagy on 7/13/2018 .
 * Project IM_Here
 * Time    1:44 PM
 */
public class WidgetUtils {

    public static class WidgetBroadcast {
        public static final String NOTIFICATION_UPDATED_ACTION = "notification_updated";
        public static final String SIGN_OUT_ACTION = "sign_out";
        public static final String SIGN_IN_ACTION = "sign_in";
        public static final String EXTRA = "notification_updated_ex";

        public static void sendBroadcastToWidget(long notificationCount, Context context){
            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(NOTIFICATION_UPDATED_ACTION);
            intent.putExtra(EXTRA, notificationCount);
            context.sendBroadcast(intent);
        }

        public static void sendBroadcastToWidgetLogout(Context context){
            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(SIGN_OUT_ACTION);
            context.sendBroadcast(intent);
        }

        public static void sendBroadcastToWidgetLogin(Context context, String userEmail){
            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(SIGN_IN_ACTION);
            intent.putExtra(Constants.USER_EXTRA, userEmail);
            context.sendBroadcast(intent);
        }
    }

    public static class Navigation{
        public static final int PROFILE_TAP = 0;
        public static final int Map_TAP = 1;
        public static final int NOTIFICATION_TAP = 2;
        public static final int SEARCH_TAP = 3;

        public static final String NAVIGATION_EXTRA = "widget_nav";

        public static PendingIntent getPendingIntentForScreen(int screenTabId, Context context){
            Intent intent;

            if(screenTabId == Map_TAP){
                intent = new Intent(context, MapActivity.class);
            }else {
                intent = new Intent(context, MainUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(NAVIGATION_EXTRA, screenTabId);
            }
            intent.putExtra(Constants.USER_EXTRA, WidgetUtils.SharedPreference.getUserEmail(context));
            return PendingIntent.getActivity(context, screenTabId, intent, PendingIntent.FLAG_UPDATE_CURRENT   );
        }

    }

    public static class FirebaseConnection{

        public static void updateNotifications(
                final RemoteViews remoteViews, String userEmailId, final AppWidgetManager widgetManager, final int widgetId){

            FirebaseHelper.getUserNewNotificationsNumber(userEmailId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long notificationCount = (Long) dataSnapshot.getValue();
                    Log.e("aaaaaaaaaa widget","set to view");
                    if(notificationCount > 0){

                        remoteViews.setTextViewText(R.id.widget_notification_text_view, notificationCount.toString());
                        remoteViews.setViewVisibility(R.id.widget_notification_text_view, View.VISIBLE);
                    }else{

                        remoteViews.setViewVisibility(R.id.widget_notification_text_view, View.GONE);
                    }

                    widgetManager.updateAppWidget(widgetId, remoteViews);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public static void updateNotifications(
                final RemoteViews remoteViews, final AppWidgetManager widgetManager, final int widgetId, Long notificationCount){

                    if(notificationCount > 0){

                        remoteViews.setTextViewText(R.id.widget_notification_text_view, notificationCount.toString());
                        remoteViews.setViewVisibility(R.id.widget_notification_text_view, View.VISIBLE);
                    }else{

                        remoteViews.setViewVisibility(R.id.widget_notification_text_view, View.GONE);
                    }

                    widgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    public static class SharedPreference{
        private static final String SHARED_DATA_ID = "user_email";
        private static final String SHARED_PREFERENCE = "user_database_id";

        public static void saveUserEmail(Context context, String userEmailId){
            SharedPreferences sharedPreferences =  context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(SHARED_DATA_ID, userEmailId);

            editor.apply();
        }

        public static void removeUser(Context context){
            SharedPreferences sharedPreferences =  context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.remove(SHARED_DATA_ID);

            editor.apply();
        }

        public static String getUserEmail(Context context){
            return context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getString(SHARED_DATA_ID, null);
        }
    }

}
