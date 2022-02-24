package com.valreja.covidtracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class LocationService extends Service implements LocationListener {
    private Context context;
    public LocationService(Context context) {
        this.context = context;
    }

    String s = "https://stackoverflow.com/questions/1381264/password-protect-a-sqlite-db-is-it-possible";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals(Utils.ACTION_STOP_FOREGROUND)){
            stopForeground(true);
            stopSelf();
        }
        startForeground(startId,buildForegroundNotification("context"));
        Log.d("FOREGROUND","onstartcommand");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FOREGROUND","oncreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Notification buildForegroundNotification(String filename) {
        String channelID = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelID = createNotificationChannel("my_service", "My Background Service");
        }
        NotificationCompat.Builder b=new NotificationCompat.Builder(this,channelID);

        b.setContentTitle("covidtracker")
                .setContentText(filename)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setTicker("locating")
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true);

        return(b.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelName, String channelID) {
        NotificationChannel notificationChannel = new NotificationChannel(channelID,channelName, NotificationManager.IMPORTANCE_NONE);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        return channelID;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}