package com.valreja.covidtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ConnectionTestBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.getFilesDir();
        Toast.makeText(context, "Alarm received", Toast.LENGTH_SHORT).show();
        Utils.appendToFile(context,"test/newtest","ll");
    }
}
