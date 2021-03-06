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
import java.util.concurrent.TimeUnit;

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
                        ArrayList<Integer> movigAvgArr = Utils.getMovingAvg(list,5);
                        float peaks = Utils.peakFinding(movigAvgArr);
                        heartRate += peaks/2;
                    }else{
                        Log.d("HEART_SERVICE", "list " + i + " is null");
                    }
                }
                heartRate = (heartRate*12)/ 9;
                resultTextView.setText(heartRate+"");
                nextButton.setVisibility(View.VISIBLE);
                startMeasuringButton.setText("RE CALCULATE");
                startMeasuringButton.setEnabled(true);
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
                MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(s));
                int duration = mediaPlayer.getDuration();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
                if (seconds < 45){
                    Toast.makeText(this, "Please record for 45 seconds", Toast.LENGTH_SHORT).show();
                    startMeasuringButton.setEnabled(true);
                    Utils.hideProgressBar();
                    return;
                }
                Log.d("SAVE",s);
                savedFilePath = s;
                readVideoFile();
            }else{
                Utils.hideProgressBar();
            }
        }
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