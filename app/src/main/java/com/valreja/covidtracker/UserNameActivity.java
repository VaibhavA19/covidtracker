package com.valreja.covidtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.valreja.covidtracker.DataBase.DBConstants;

public class UserNameActivity extends AppCompatActivity {

    String[] permissionsArray;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        permissionsArray = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestAllPermissions();
        EditText userNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        Button submitUserNameButton = (Button) findViewById(R.id.submit_user_name);
        submitUserNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userNameEditText.getText().toString();
                userNameEditText.setText("");
                Toast.makeText(UserNameActivity.this, "Welcome "+ userName, Toast.LENGTH_SHORT).show();
                DBConstants.TABLE_NAME = userName;
                Intent i = new Intent(UserNameActivity.this,VideoRecording.class);
                startActivity(i);

            }
        });
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
}