package com.valreja.covidtracker;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HearRateService extends Service {


    private String videoPath;
    private Bundle resultBundle;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Started measuring service", Toast.LENGTH_LONG).show();
        String s = intent.getStringExtra("VIDEO_PATH");
        Log.d("HEART_SERVICE","inservice");
        if (s == null){
            Toast.makeText(this, "Video not found", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        videoPath = s;
        Thread thread = new Thread(new WindowFrameCollector());
        thread.start();
        return START_STICKY;
    }

    public class WindowFrameCollector implements Runnable {
        int totalWindows = 9;
        @Override
        public void run() {
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            List<FrameExtractor> tasks = new ArrayList<>();
            for (int i = 0 ; i < totalWindows ; i++){
                tasks.add(new FrameExtractor(i*5));
            }

            List<Future<ArrayList<Integer>>> windows = null;
            try{
                windows = executorService.invokeAll(tasks);
            }catch (Exception e){
                Log.d("HEART_SERVICE",e.toString() + e.getStackTrace());
            }

            executorService.shutdown();
            System.gc();

            for (int i = 0 ; i < windows.size();i++){
                Future<ArrayList<Integer>> arrayListFuture = windows.get(i);
                try {
                    resultBundle.putIntegerArrayList("window" + i, arrayListFuture.get());
                }catch (Exception e){
                    resultBundle = new Bundle();
                    Log.d("HART_SERVICE",e.toString() + e.getStackTrace());
                }
            }
            stopSelf();
        }

    }

    private class FrameExtractor implements Callable<ArrayList<Integer>> {

        private int startTime;
        public FrameExtractor(int startTime){
            this.startTime = startTime;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        private  ArrayList<Integer> getWindowFrameAvg(){
            try {
                ArrayList<Integer> frameAvg = new ArrayList<>();
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath);
                Bitmap bitmap;
                int totalFrames = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));
                int framesPerWindow = totalFrames/9; // 9 windows
                int startingFrame = (startTime/5)*framesPerWindow;
                for(int i = startingFrame; i < (startingFrame+framesPerWindow) ; i++) {

                    bitmap = retriever.getFrameAtIndex(i);
                    System.gc();
                    if (bitmap != null){
                        int avgRedcol = getAverageRedColor(bitmap);
                        frameAvg.add(avgRedcol);
                        Log.d("SERVICE_HEART",i + " " + avgRedcol);
                    }else {
                        Log.d("SERVICE_HEART","bitmap null at " + i);
                    }
                }
                return frameAvg;
            } catch(Exception e){
                Log.d("SERVICE_HEART",e.toString() + e.getStackTrace());
            }
            return null;

        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public ArrayList<Integer> call() throws Exception {
            return getWindowFrameAvg();
        }
    }
    private int getAverageRedColor(Bitmap bitmap){
        long redColor = 0, totalPixels = 0;
        for (int y = 0 ; y < bitmap.getHeight(); y+=5){
            for(int x = 0 ; x < bitmap.getWidth() ; x += 5){
                totalPixels++;
                redColor += Color.red(bitmap.getPixel(x,y));
            }
        }
        return (int) (redColor/totalPixels);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("HEART_SERVICE","destroyed");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("HEART_SERVICE", "heart service stopping");
                Intent i = new Intent("HeartRateBroadcast");
                i.putExtras(resultBundle);
                LocalBroadcastManager.getInstance(HearRateService.this).sendBroadcast(i);
                resultBundle.clear();
                System.gc();
            }
        });
        thread.start();
    }
}
