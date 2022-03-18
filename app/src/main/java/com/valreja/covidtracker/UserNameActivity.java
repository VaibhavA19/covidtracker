package com.valreja.covidtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
        registerAlarm();
        String temp = (new UserManagementHelper(UserNameActivity.this)).getUPass();
        Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
        userManagementHelper = new UserManagementHelper(UserNameActivity.this);
        Utils.writeToFile(this,"test/dummyfile","Dummy Dummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentcontentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy contentDummy content");
        Toast.makeText(this, Utils.readFile(this, "test/dummyfile"), Toast.LENGTH_SHORT).show();
        milliSecond = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uploadImage();
        }
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
            }
        });
        ((Button)findViewById(R.id.start_f_service)).setOnClickListener(new View.OnClickListener() {
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
        });
    }

    public void registerAlarm(){
        Intent i = new Intent(this, ConnectionTestBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, i, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pi);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadImage(){
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("image",
                        "raw", getPackageName()));
        String result = new BufferedReader(new InputStreamReader(ins))
                .lines().collect(Collectors.joining(""));
        Utils.writeToFile(UserNameActivity.this,"test/image.jpg",result);
        File imageFile = new File(UserNameActivity.this.getFilesDir()+"/test/image.jpg");
        //File imageFile = new File(uri.getPath());
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imageFile);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imageFile.getName(), reqBody);
        API api = RetrofitClient.getInstance().getAPI();
        Call<ResponseBody> upload = api.uploadImage(partImage);
        upload.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Long duration = System.currentTimeMillis() - milliSecond;
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String timeStamp = dtf.format(now);
                    Utils.appendToFile(UserNameActivity.this,"test/connection", timeStamp + ";"  + (imageFile.length()/((1.0*duration)/1000)) + "B/s");
                    Toast.makeText(UserNameActivity.this, "Image Uploaded in " + duration +" size " + imageFile.length(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(UserNameActivity.this, "Request failed"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}