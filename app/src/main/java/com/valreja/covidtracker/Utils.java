package com.valreja.covidtracker;

import static java.lang.Math.abs;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.valreja.covidtracker.DataBase.DBConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utils {
    private static ProgressDialog progress;
    public static final String ACTION_STOP_FOREGROUND = BuildConfig.APPLICATION_ID + ".stop" ;


    public static void writeToFile(Context context, String relative_path, String text){
        File file = new File(context.getFilesDir() + "/" + relative_path);
        Toast.makeText(context, file.toString(), Toast.LENGTH_LONG).show();
        File path = new File(file.getParent());
        Toast.makeText(context, path.toString(), Toast.LENGTH_LONG).show();
        if (path != null)
            if (!path.exists()){
                Log.d("FILETRY","creating dir");
            }else{
                Log.d("FILETRY","not creating dir");
            }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(text);
            fileWriter.flush();
            fileWriter.close();
            Log.d("FILETRY", "filewritten");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("FILETRY","creating dir" + e.toString());
        }
    }
    public static String readFile(Context context, String relative_path){
        File file = new File(context.getFilesDir() + "/" + relative_path);
        if (!file.exists()) return "";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStringBuilder.toString();
    }

    public static void appendToFile(Context context, String file, String text){
        String currentFileText = readFile(context,file);
        writeToFile(context,file,currentFileText+ text);
    }

    public static ArrayList<Symptom> getSymptomsArrayList(){
        ArrayList<Symptom> symptomArrayList = new ArrayList<Symptom>();
        symptomArrayList.add(new Symptom("Nausea",0, DBConstants.COLUMN_NAUSEA_RATING));
        symptomArrayList.add(new Symptom("Headache",0, DBConstants.COLUMN_HEADACHE_RATING));
        symptomArrayList.add(new Symptom("diarrhea",0, DBConstants.COLUMN_DIARRHEA_RATING));
        symptomArrayList.add(new Symptom("Soar Throat",0, DBConstants.COLUMN_SOAR_THROAT_RATING));
        symptomArrayList.add(new Symptom("Fever",0, DBConstants.COLUMN_FEVER_RATING));
        symptomArrayList.add(new Symptom("Muscle Pain",0, DBConstants.COLUMN_MUSCLE_ACHE_RATING));
        symptomArrayList.add(new Symptom("Loss of Smell or Taste",0, DBConstants.COLUMN_LOSS_OF_TASTE_SMELL_RATING));
        symptomArrayList.add(new Symptom("Cough",0, DBConstants.COLUMN_COUGH_RATING));
        symptomArrayList.add(new Symptom("Shortness of Breath",0, DBConstants.COLUMN_SHORT_BREATH_RATING));
        symptomArrayList.add(new Symptom("Feeling Tired",0, DBConstants.COLUMN_TIRED_RATING));
        return symptomArrayList;

    }
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
