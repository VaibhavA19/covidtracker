package com.valreja.covidtracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class RespiratoryDataCollectionService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private ArrayList<Integer> accelerationZarrayList;
    public RespiratoryDataCollectionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accelerationZarrayList = new ArrayList<>();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerationZarrayList.add((int) (sensorEvent.values[2]*100));
            Log.d("RESP","" + sensorEvent.values[2]);
            if (accelerationZarrayList.size() >= 250){
                stopSelf();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("RESP","service stop");
                sensorManager.unregisterListener(RespiratoryDataCollectionService.this);
                Intent i = new Intent("ResirationSensorData");
                i.putIntegerArrayListExtra("accelerationZ",accelerationZarrayList);
                LocalBroadcastManager.getInstance(RespiratoryDataCollectionService.this).sendBroadcast(i);
            }
        }).start();
    }
}