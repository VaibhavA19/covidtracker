package com.valreja.covidtracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserManagementHelper {
    Activity activity;
    SharedPreferences sharedPreferences;
    public UserManagementHelper(Activity activity){
        this.activity = activity;
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public boolean addUser(User user){
        if(userExists(user)){
            return false;
        }
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(activity.getString(R.string.user_name), user.getUsername());
        sharedPreferencesEditor.putString(activity.getString(R.string.password), user.getPassword());
        sharedPreferencesEditor.putBoolean(activity.getString(R.string.user_registered),true);
        sharedPreferencesEditor.apply();
        return true;
    }

    public boolean userExists(User user){
        String username = sharedPreferences.getString(activity.getString(R.string.user_name),"");
        String password = sharedPreferences.getString(activity.getString(R.string.password),"");
        if(username.equals("") || password.equals("")){
            return false;
        }
        if(username.equals(user.getUsername()) && password.equals(user.getPassword())){
            return true;
        }
        return false;
    }

    public String getUPass(){
        String username = sharedPreferences.getString(activity.getString(R.string.user_name),"");
        String password = sharedPreferences.getString(activity.getString(R.string.password),"");
        return username+":"+password;
    }
    public  boolean anyUserRegistered(){
        return sharedPreferences.getBoolean(activity.getString(R.string.user_registered),false);
    }
}
