package com.valreja.covidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.valreja.covidtracker.DataBase.DBConstants;

public class UserNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
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
}