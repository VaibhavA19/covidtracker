package com.valreja.covidtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.valreja.covidtracker.DataBase.DBConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNameActivity extends AppCompatActivity {

    String[] permissionsArray;
    Long milliSecond ;
    UserManagementHelper userManagementHelper;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        showPrivacyDialog();
        userManagementHelper = new UserManagementHelper(UserNameActivity.this);
        /*milliSecond = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uploadImage();
        }*/
        permissionsArray = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        requestAllPermissions();
        EditText userNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        EditText passwordEditText = (EditText)findViewById(R.id.password_edit_text);
        Button submitUserNameButton = (Button) findViewById(R.id.submit_user_name);
        submitUserNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean userAuthenticated = false;
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                User currentUser = new User(userName,password);
                if(userManagementHelper.anyUserRegistered()) {
                    if(userManagementHelper.userExists(currentUser)){
                        userAuthenticated = true;
                    }else{
                        userNameEditText.setText("");
                        passwordEditText.setText("");
                        Toast.makeText(UserNameActivity.this, "Incorrect username password", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    userManagementHelper.addUser(currentUser);
                    userAuthenticated = true;
                    Toast.makeText(UserNameActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                }
                if (userAuthenticated){
                    Toast.makeText(UserNameActivity.this, "Welcome "+ userName, Toast.LENGTH_SHORT).show();
                    DBConstants.TABLE_NAME = userName;
                    Intent i = new Intent(UserNameActivity.this,VideoRecording.class);
                    startActivity(i);
                }
                userNameEditText.setText("");
                passwordEditText.setText("");
            }
        });
        /*((Button)findViewById(R.id.start_f_service)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserNameActivity.this,LocationService.class);
                startForegroundService(i);
            }
        });
        ((Button)findViewById(R.id.stop_f_service)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserNameActivity.this,LocationService.class);
                i.setAction(Utils.ACTION_STOP_FOREGROUND);
                startForegroundService(i);
            }
        });*/
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean requestPermAgain = false;
        if (requestCode == 999){
            for (int i = 0 ; i < grantResults.length;i++) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission " + permissions[i] + " is required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestAllPermissions() {
        for (String perm: permissionsArray
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED && ActivityCompat.shouldShowRequestPermissionRationale(this,perm)){
                    this.requestPermissions(new String[]{perm},999);
                }else if(checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Please allow " + perm + " in settings", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showPrivacyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserNameActivity.this);
        UserManagementHelper userManagementHelper = new UserManagementHelper(UserNameActivity.this);
        if(userManagementHelper.userPrivacyAgreement()){
            return;
        }
        builder.setMessage(R.string.privacy_notice)
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        userManagementHelper.setPrivacyAgreement(true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        // Create the AlertDialog object and return it
         builder.create().show();
    }
}