package com.valreja.covidtracker;

import android.app.Activity;

public class PermissionManager {
    String[] permissionsToAsk;
    Activity context;
    public PermissionManager(Activity context, String[] permissionsToAsk) {
        this.permissionsToAsk = permissionsToAsk;
        this.context = context;
    }

    private void requestAllPermissions(){

    }

}
