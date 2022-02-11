package com.valreja.covidtracker;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;
import static java.lang.Math.abs;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

public class VideoRecording extends AppCompatActivity {

    Button nextButton, startMeasuringButton;
    TextView resultTextView;
    float heartRate = 0 ;
    String rootPath = Environment.getExternalStorageDirectory().getPath();
    File mediaFile = new File( rootPath + "/heart_rate.mp4");
    private Uri fileUri = FileProvider.getUriForFile(VideoRecording.this,"com.valreja.fileprovider",mediaFile);
    private String savedFilePath ;
    private int RECORDING_ACTIVITY_CODE = 111;

    @Override
    protected void onDestroy() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recording);
        startMeasuringButton = (Button) findViewById(R.id.measure_heart_button);
        nextButton = (Button) findViewById(R.id.heart_next_button);
        resultTextView = (TextView) findViewById(R.id.heart_rate_tv);
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this,"OpenCV fail", Toast.LENGTH_LONG).show();
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VideoRecording.this,RespirationActivity.class);
                i.putExtra("HEART_RATE", heartRate);
                startActivity(i);
                finish();
            }
        });
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Utils.hideProgressBar();
                Bundle results = intent.getExtras();
                for (int i = 0 ; i < 9 ; i++){
                    ArrayList<Integer> list = results.getIntegerArrayList("window"+i);
                    if(list != null) {
                        Log.d("HEART_SERVICE", "RESULT " + i + list.toString());
                        ArrayList<Integer> movigAvgArr = getMovingAvg(list,5);
                        float zeroCrossings = peakFinding(movigAvgArr);
                        heartRate += zeroCrossings/2;
                    }else{
                        Log.d("HEART_SERVICE", "list " + i + " is null");
                    }
                }
                heartRate = (heartRate*12)/ 9;
                resultTextView.setText(heartRate+"");
                nextButton.setVisibility(View.VISIBLE);
                startMeasuringButton.setText("RE CALCULATE");
                Log.d("FINAL",heartRate+"");
            }
        };
        LocalBroadcastManager.getInstance(VideoRecording.this).registerReceiver(broadcastReceiver, new IntentFilter("HeartRateBroadcast"));
        startMeasuringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showProgressBar(VideoRecording.this, "Calculating Heart Rate");
                startMeasuringButton.setEnabled(false);
                startCameraRecording();
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


    private void startCameraRecording(){

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,45);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        Toast.makeText(this, fileUri.getPath(), Toast.LENGTH_SHORT).show();
        startActivityForResult(intent, RECORDING_ACTIVITY_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RECORDING_ACTIVITY_CODE){
            if(resultCode == RESULT_OK){
                Uri vid = data.getData();
                String s  = getRealPathFromURI(vid);
                Log.d("SAVE",s);
                savedFilePath = s;
                readVideoFile();
            }
        }
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

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void readVideoFile() {
/*        File dir = new File(rootPath);
        File[] files = dir.listFiles();
        for (File f: files
             ) {
            Log.d("FILE",f.getName());
        }*/


        Log.d("HEART_SERVICE","read video fun");
        Intent heartRateService = new Intent(VideoRecording.this,HearRateService.class);
        heartRateService.putExtra("VIDEO_PATH",savedFilePath);
        Log.d("HEART_SERVICE", "" + startService(heartRateService));



        /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(savedFilePath);
        int i = 0 ;
        Bitmap bitmap = retriever.getFrameAtTime(i, OPTION_CLOSEST_SYNC);
        if (bitmap != null) {
            Toast.makeText(this, Color.red(bitmap.getPixel(89,97))+"",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "bitmap null", Toast.LENGTH_SHORT).show();
        }*/
    }
}