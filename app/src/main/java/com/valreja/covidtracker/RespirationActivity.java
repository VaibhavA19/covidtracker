package com.valreja.covidtracker;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RespirationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiration);

        TextView resultTV = (TextView) findViewById(R.id.respiratoryRate);
        Button button = (Button) findViewById(R.id.start_stop_respiration);
        Button nextButton = (Button)findViewById(R.id.resp_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<Integer> accelerationZ = intent.getIntegerArrayListExtra("accelerationZ");
                if (accelerationZ == null){
                    Toast.makeText(context, "received null dATA", Toast.LENGTH_SHORT).show();
                    resultTV.setText("error recieved null fata");
                    nextButton.setVisibility(View.INVISIBLE);
                }else{
                    nextButton.setVisibility(View.VISIBLE);
                    ArrayList<Integer> movingAvg = getMovingAvg(accelerationZ,10);
                    float peaks = peakFinding(movingAvg);
                    resultTV.setText( "" + ((peaks*60)/90));
                }
                button.setText("RE CALCULATE");
            }
        };
        LocalBroadcastManager.getInstance(RespirationActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("ResirationSensorData"));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultTV.setText("0");
                button.setEnabled(false);
                Intent i = new Intent(RespirationActivity.this,RespiratoryDataCollectionService.class);
                startService(i);
            }
        });
    }

    private float peakFinding(ArrayList<Integer> data) {
        int diff, prev, slope = 0, zeroCrossings = 0;
        int j = 0;
        prev = data.get(0);

        //Get initial slope
        while(slope == 0 && j + 1 < data.size()){
            diff = data.get(j + 1) - data.get(j);
            if(diff != 0){
                slope = diff/abs(diff);
            }
            j++;
        }

        //Get total number of zero crossings in data curve
        for(int i = 1; i<data.size(); i++) {

            diff = data.get(i) - prev;
            prev = data.get(i);

            if(diff == 0) continue;

            int currSlope = diff/abs(diff);

            if(currSlope == -1* slope){
                slope *= -1;
                zeroCrossings++;
            }
        }

        return zeroCrossings;
    }

    public ArrayList<Integer> getMovingAvg(ArrayList<Integer> data, int filter){

        ArrayList<Integer> movingAvgArr = new ArrayList<>();
        int movingAvg = 0;

        for(int i=0; i< data.size(); i++){
            movingAvg += data.get(i);
            if(i+1 < filter) {
                continue;
            }
            movingAvgArr.add((movingAvg)/filter);
            movingAvg -= data.get(i+1 - filter);
        }

        return movingAvgArr;

    }

}