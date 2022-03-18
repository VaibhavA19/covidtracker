package com.valreja.covidtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.valreja.covidtracker.DataBase.DBConstants;
import com.valreja.covidtracker.DataBase.UserDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String[] permissionsArray;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionsArray = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestAllPermissions();
        ArrayList<Symptom> symptomArrayList = Utils.getSymptomsArrayList();

        SymptomAdapter symptomsAdapter = new SymptomAdapter(symptomArrayList, new OnRatingChangeListener() {
            @Override
            public void onItemClick(int id, float newRating) {
                symptomArrayList.get(id).setRating(newRating);
              //  Toast.makeText(MainActivity.this, symptomArrayList.get(id).getName() + ": " + symptomArrayList.get(id).getRating(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("AAAAAAAAAAAAAAA"," in gomeeeeeeeeee fragment");
        RecyclerView symptomsRecyclerView = (RecyclerView)findViewById(R.id.symptom_list_view);
        symptomsRecyclerView.setAdapter(symptomsAdapter);
        symptomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button submitButton = (Button)findViewById(R.id.submit_symptoms_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float heartRate = getIntent().getFloatExtra("HEART_RATE",0);
                float respRate = getIntent().getFloatExtra("RESP_RATE",0);

                UserDatabase userDatabase = new UserDatabase(getApplicationContext());
                LocationHelper locationHelper = new LocationHelper(getApplicationContext());
                userDatabase.insertSymptoms(symptomArrayList,heartRate,respRate,locationHelper.getLatitude(),locationHelper.getLongitude());
                for (Symptom s: symptomArrayList
                ) {
                    s.setRating(0);
                }
                symptomsAdapter.notifyDataSetChanged();

                //startActivity(new Intent(getApplicationContext(),VideoRecording.class));
                //Intent i = new Intent(getApplicationContext(),RespirationActivity.class);
                //startActivity(new Intent(getApplicationContext(),RespirationActivity.class));
                Toast.makeText(MainActivity.this, "Details submitted", Toast.LENGTH_SHORT).show();
                finish();
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