package com.valreja.covidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RespirationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiration);
        TextView x_accel = (TextView) findViewById(R.id.x_accel_tv);
        TextView y_accel = (TextView) findViewById(R.id.y_accel_tv);
        TextView z_accel = (TextView) findViewById(R.id.z_accel_tv);

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorEventListener sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    x_accel.setText(String.valueOf(Math.ceil(sensorEvent.values[0])));
                    y_accel.setText(String.valueOf(Math.ceil(sensorEvent.values[1])));
                    z_accel.setText(String.valueOf(Math.ceil(sensorEvent.values[2])));
                    Log.d("heart","" + sensorEvent.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        Sensor accelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(sel, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}