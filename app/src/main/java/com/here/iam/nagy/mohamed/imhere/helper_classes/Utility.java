package com.here.iam.nagy.mohamed.imhere.helper_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mohamednagy on 12/23/2016.
 */
public class Utility {

    public static String encodeUserEmail(String userEmail){
        return userEmail.replace(".",",");
    }

    public static String decodeUserEmail(String userEmail){
        return userEmail.replace(",",".");
    }

    public static Bitmap resizeMapIcon(int image, Context context){
        final int HEIGHT = 100;
        final int WIDTH = 100;

        BitmapDrawable bitmapDrawable=(BitmapDrawable) context.getDrawable(image);

        Bitmap bitmapImage= bitmapDrawable.getBitmap();

        return Bitmap.createScaledBitmap(bitmapImage, WIDTH, HEIGHT, false);
    }

    public static String getDateAsString(final long TIME) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(TIME);

        return DateFormat.format("EEE, MMM d, h:mm a" , calendar).toString();
    }

    public static String getTrackDuration(long date1, long date2){
        long timeInMills = Math.abs(date1 - date2);
        int minutes = (int) timeInMills/(1000*60);
        int seconds = (int)(timeInMills - (minutes * 1000 * 60))/1000;

        return minutes + ":" + seconds;
    }

    public static Long getCurrentDate(){
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Get the state of networking connection of the current mobile
     * @return the network is connected in mobile or not
     */
    public static boolean networkIsConnected(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =
                connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
