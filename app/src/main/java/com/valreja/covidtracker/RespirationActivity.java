package com.valreja.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RespirationActivity extends AppCompatActivity {

    float resultRespRate =0;
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
                Intent i = new Intent(RespirationActivity.this,MainActivity.class);
                i.putExtra("RESP_RATE", resultRespRate);
                float heartRate = getIntent().getFloatExtra("HEART_RATE",0);
                i.putExtra("HEART_RATE",heartRate);
                startActivity(i);
                finish();
            }
        });
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Utils.hideProgressBar();
                ArrayList<Integer> accelerationZ = intent.getIntegerArrayListExtra("accelerationZ");
                if (accelerationZ == null){
                    Toast.makeText(context, "received null dATA", Toast.LENGTH_SHORT).show();
                    resultTV.setText("error recieved null fata");
                    nextButton.setVisibility(View.INVISIBLE);
                }else{
                    nextButton.setVisibility(View.VISIBLE);
                    ArrayList<Integer> movingAvg = Utils.getMovingAvg(accelerationZ,10);
                    float peaks = Utils.peakFinding(movingAvg);
                    resultRespRate = ((peaks*60)/90);
                    resultTV.setText( "" + resultRespRate);
                }
                button.setEnabled(true);
                button.setText("RE CALCULATE");
            }
        };
        LocalBroadcastManager.getInstance(RespirationActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("ResirationSensorData"));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showProgressBar(RespirationActivity.this, "Calculating respiratory rate");
                resultTV.setText("0");
                button.setEnabled(false);
                Intent i = new Intent(RespirationActivity.this,RespiratoryDataCollectionService.class);
                startService(i);
            }
        });
    }
}