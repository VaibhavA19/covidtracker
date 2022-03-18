package com.valreja.covidtracker;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.valreja.covidtracker.DataBase.DBEntry;
import com.valreja.covidtracker.DataBase.UserDatabase;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service implements LocationListener {
    PowerManager pm;
    PowerManager.WakeLock wl;
    Long milliSecond;

    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 15*60*1000 - SystemClock.elapsedRealtime()%1000);
            LocationHelper locationHelper = new LocationHelper(getApplicationContext());
            if(locationHelper.canGetLocation()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                int hours = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                if(!(( hours == 9 || hours == 15 || hours == 22) && min < 15)) {
                    double latitude = locationHelper.getLatitude();
                    double longitude = locationHelper.getLongitude();
                    UserDatabase userDatabase = new UserDatabase(getApplicationContext());
                    userDatabase.insertSymptoms(Utils.getSymptomsArrayList(), 0, 0, latitude, longitude);
                    Utils.appendToFile(getApplicationContext(), "test/newtest", "" + latitude + " " + longitude + " " + Calendar.getInstance().getTime().toString());
                    ArrayList<DBEntry> dbEntries = (ArrayList<DBEntry>) userDatabase.getDBEntries();
                    uploadDB(dbEntries);
                }
            }else{
                Utils.appendToFile(getApplicationContext(),"test/newtest","cannot get location"+ Calendar.getInstance().getTime().toString());
            }

            milliSecond = System.currentTimeMillis();
            uploadImage();
        }
    };

    private void uploadDB(ArrayList<DBEntry> dbEntries) {
        API api = RetrofitClient.getInstance().getAPI();
        Call<JSONArray> upload = api.uploadDB(dbEntries);
        upload.enqueue(new Callback<JSONArray>() {

            @Override
            public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                Toast.makeText(LocationService.this, "uploaded dbbbbbb", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JSONArray> call, Throwable t) {
                Utils.appendToFile(getApplicationContext(),"test/newtest",t.getLocalizedMessage());
                Toast.makeText(LocationService.this, ""+ t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    String s = "https://stackoverflow.com/questions/1381264/password-protect-a-sqlite-db-is-it-possible";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals(Utils.ACTION_STOP_FOREGROUND)){
            stopForeground(true);
            stopSelf();
        }
        startForeground(startId,buildForegroundNotification("context"));
        handler.post(periodicUpdate);
        Log.d("FOREGROUND","onstartcommand");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "covidtracker:GpsTrackerWakelock");
        wl.acquire();
        Log.d("FOREGROUND","oncreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wl.release();
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadImage(){
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("image",
                        "raw", getPackageName()));
        String result = new BufferedReader(new InputStreamReader(ins))
                .lines().collect(Collectors.joining(""));
        Utils.writeToFile(getApplicationContext(),"test/image.jpg",result);
        File imageFile = new File(getApplicationContext().getFilesDir()+"/test/image.jpg");
        //File imageFile = new File(uri.getPath());
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imageFile);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imageFile.getName(), reqBody);
        API api = RetrofitClient.getInstance().getAPI();
        Call<ResponseBody> upload = api.uploadImage(partImage);
        upload.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Long duration = System.currentTimeMillis() - milliSecond;
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String timeStamp = dtf.format(now);
                    Utils.appendToFile(getApplicationContext(),"test/connection", timeStamp + ";"  + (imageFile.length()/((1.0*duration)/1000)) + "B/s");
                    Toast.makeText(getApplicationContext(), "Image Uploaded in " + duration +" size " + imageFile.length(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Request failed"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}