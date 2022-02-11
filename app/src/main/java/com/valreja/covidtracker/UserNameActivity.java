package com.valreja.covidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                Toast.makeText(UserNameActivity.this, "Welcome "+ userName, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(UserNameActivity.this,MainActivity.class);
                i.putExtra("USER_NAME",userName);
                startActivity(i);
            }
        });
    }
}