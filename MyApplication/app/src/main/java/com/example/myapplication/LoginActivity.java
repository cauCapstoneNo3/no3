package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login page");

        Button login_btn = (Button) findViewById(R.id.login_button);
        login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //
                // add login check function declaration + if TRUE ~
                //
                Intent intent = new Intent(LoginActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
    //
    // add login check function definition
    //
}