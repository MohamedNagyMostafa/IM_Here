package com.here.iam.nagy.mohamed.imhere.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;

/**
 * Created by Mohamed Nagy on 7/13/2018 .
 * Project IM_Here
 * Time    10:57 AM
 */
public class WidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidgets(context, appWidgetManager, appWidgetIds, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Long notificationCount = null;

        if(intent != null) {
            String action = intent.getAction();
            assert action != null;

            switch (action) {
                case WidgetUtils.WidgetBroadcast.NOTIFICATION_UPDATED_ACTION:
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        notificationCount = bundle.getLong(WidgetUtils.WidgetBroadcast.EXTRA);
                    }
                    break;

                case WidgetUtils.WidgetBroadcast.SIGN_OUT_ACTION:
                    WidgetUtils.SharedPreference.removeUser(context);
                    break;
                case WidgetUtils.WidgetBroadcast.SIGN_IN_ACTION:
                    WidgetUtils.SharedPreference.saveUserEmail(context, intent.getStringExtra(Constants.USER_EXTRA));
                    break;
            }
        }
        // To update data during rescale.
        updateAppWidgetsWithNewData( notificationCount, context);
    }

    private void updateAppWidgetsWithNewData(Long notificationCount, Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));

        updateAppWidgets(context, appWidgetManager, widgetIds, notificationCount);
    }

    private void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Long notificationCount){
        for(int id : appWidgetIds)
            updateWidget(context, appWidgetManager, id, notificationCount);
    }

     void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Long notificationCount){
        if(WidgetUtils.SharedPreference.getUserEmail(context) != null) {
            RemoteViews remoteViews = getRemoteViews(context, appWidgetManager, appWidgetId, notificationCount);

            handlePendingIntent(remoteViews, context);
        }else{

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_sign_out);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }


    private void handlePendingIntent(RemoteViews remoteViews, Context context){
        PendingIntent pendingIntentProfileScreen =
                WidgetUtils.Navigation.getPendingIntentForScreen(WidgetUtils.Navigation.PROFILE_TAP, context);
        PendingIntent pendingIntentMapScreen =
                WidgetUtils.Navigation.getPendingIntentForScreen(WidgetUtils.Navigation.Map_TAP, context);
        PendingIntent pendingIntentNotificationScreen =
                WidgetUtils.Navigation.getPendingIntentForScreen(WidgetUtils.Navigation.NOTIFICATION_TAP, context);
        PendingIntent pendingIntentSearchScreen =
                WidgetUtils.Navigation.getPendingIntentForScreen(WidgetUtils.Navigation.SEARCH_TAP, context);

        remoteViews.setOnClickPendingIntent(R.id.widget_notification_image_view, pendingIntentNotificationScreen);
        remoteViews.setOnClickPendingIntent(R.id.widget_search_image_view, pendingIntentSearchScreen);
        remoteViews.setOnClickPendingIntent(R.id.widget_profile_image_view, pendingIntentProfileScreen);
        remoteViews.setOnClickPendingIntent(R.id.widget_map_image_view, pendingIntentMapScreen);
    }

    private RemoteViews getRemoteViews(Context context, AppWidgetManager widgetManager, int widgetId, Long notificationCount){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        String emailId = WidgetUtils.SharedPreference.getUserEmail(context);

        if(emailId != null)
            if(notificationCount == null)
                WidgetUtils.FirebaseConnection.updateNotifications(
                        remoteViews,
                        emailId,
                        widgetManager,
                        widgetId
                );
            else
                WidgetUtils.FirebaseConnection.updateNotifications(
                        remoteViews,
                        widgetManager,
                        widgetId,
                        notificationCount
                );

        return remoteViews;
    }


}
