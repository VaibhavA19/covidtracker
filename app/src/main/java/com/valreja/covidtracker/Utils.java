package com.valreja.covidtracker;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {
    private static ProgressDialog progress;
    public static void showProgressBar(Context context, String message){
        progress = new ProgressDialog(context);
        progress.setTitle("");
        progress.setMessage(message);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }
    public static void hideProgressBar(){
        if (progress != null){
            progress.dismiss();
        }
    }
}
