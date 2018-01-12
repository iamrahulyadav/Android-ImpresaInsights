package com.example.codemaven3015.sampleapplogin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class CheckConnectivityStateChange extends BroadcastReceiver {


    private static final String LOG_TAG = "NetworkChangeReceiver";
    private boolean isConnected = false;
    Context context;


    public CheckConnectivityStateChange(Context context ) {
        this.context = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(LOG_TAG, "Received notification about network status");
        DataBaseHealper myDB = new DataBaseHealper(context);
        if(myDB.checkAnswerToupdate()) {
            isNetworkAvailable(context);
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            Log.e(LOG_TAG, "Now you are connected to Internet!");
                            //Toast.makeText(context, "Internet available", Toast.LENGTH_SHORT).show();
                            isConnected = true;
                        }
                        onClickButtonNotification("Internet is available, Please Upload the answers");
                        return true;
                    }
                }
            }
        }
        Log.e(LOG_TAG, "You are not connected to Internet!");
        //Toast.makeText(context, "Internet NOT availablle via Broadcast receiver", Toast.LENGTH_SHORT).show();
        isConnected = false;
        //onClickButtonNotification("Internet NOT available");
        return false;
    }
    public void onClickButtonNotification(String Message){

        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
//        Intent intent = new Intent(context, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(context.getClass());
//        stackBuilder.addNextIntent(intent);
//        PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
//                | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setContentTitle("IMPRESA INSIGHTS")
                .setContentText(Message)
                .setContentIntent(pi)
                ;
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

}
