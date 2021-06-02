package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView tv_id, tv_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent;
                //= new Intent(this, LoginActivity.class);
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("userInfo", 0);
        String tmpUserName = sharedPreferences.getString("existanceUserName", "0");
        Log.d("main",tmpUserName);
        if (tmpUserName.equals("0")){
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        }
    }
}