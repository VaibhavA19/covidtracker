package com.valreja.covidtracker;

import static java.lang.Math.abs;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

public class Utils {
    private static ProgressDialog progress;
    public static void showProgressBar(Context context, String message){
        progress = new ProgressDialog(context);
        progress.setTitle("");
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.show();
    }
    public static void hideProgressBar(){
        if (progress != null){
            progress.dismiss();
        }
    }

    public static float peakFinding(ArrayList<Integer> data) {
        int diff, slope = 0;
        int peaks = 0, index = 0;
        while(slope == 0 && index + 1 < data.size()){
            diff = data.get(index + 1) - data.get(index);
            if(diff != 0){
                slope = diff/abs(diff);
            }
            index++;
        }

        int previousSample = data.get(0);
        for(int i = 1; i< data.size(); i++) {

            diff = data.get(i) - previousSample;
            previousSample = data.get(i);

            if(diff == 0){
                continue;
            }
            int currSlope = diff/abs(diff);

            if(currSlope == -1* slope){
                slope *= -1;
                peaks++;
            }
        }

        return peaks;
    }

    public static ArrayList<Integer> getMovingAvg(ArrayList<Integer> data, int windowSize){

        ArrayList<Integer> movingAvgArr = new ArrayList<>();
        int movingAvg = 0;

        for(int i = 0; i < data.size(); i++){
            movingAvg += data.get(i);
            if(i+1 < windowSize) {
                continue;
            }
            movingAvg -= data.get(i+1 - windowSize);
            movingAvgArr.add((movingAvg)/windowSize);
        }

        return movingAvgArr;

    }
}
