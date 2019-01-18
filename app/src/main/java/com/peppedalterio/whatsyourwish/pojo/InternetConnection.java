package com.peppedalterio.whatsyourwish.pojo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.FirebaseDatabase;

public class InternetConnection {

    public static boolean checkForInternetConnection(Context context) {

        boolean isConnected = false;

        if(context!=null) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }

        if(isConnected)
            FirebaseDatabase.getInstance().goOnline();
        else
            FirebaseDatabase.getInstance().goOffline();

        return isConnected;
    }
}
