package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.Manifest;

import java.io.FileInputStream;
import java.security.Permission;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    public CalendarView calendarView;
    public Button map_Btn, stop_Btn, record_Btn;
    public TextView textview;
    Calendar cal = Calendar.getInstance();
    public int today_year = cal.get(Calendar.YEAR);
    public int today_month = cal.get(Calendar.MONTH);
    public int today_day = cal.get(Calendar.DATE);;
    public int cnt_num;
    public static final int PERMISSION_CHECK = 3333;
    private String[] PERMISSIONS ={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        record_Btn = findViewById(R.id.record_Btn);
        map_Btn = findViewById(R.id.map_Btn);
        stop_Btn = findViewById(R.id.stop_Btn);
        textview = findViewById(R.id.textView);
        Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
        Intent intent2 = new Intent(getApplicationContext(), map_service.class);
        //mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if (year > today_year){
                    textview.setText("not available");
                }else if(year == today_year){
                    if (month > today_month){
                        textview.setText("not available");
                        record_Btn.setVisibility(View.INVISIBLE);
                        map_Btn.setVisibility(View.INVISIBLE);
                        stop_Btn.setVisibility(View.INVISIBLE);
                    }else if(month == today_month){
                        if (dayOfMonth > today_day){
                            textview.setText("not available");
                            record_Btn.setVisibility(View.INVISIBLE);
                            map_Btn.setVisibility(View.INVISIBLE);
                            stop_Btn.setVisibility(View.INVISIBLE);
                        } else if (dayOfMonth == today_day){
                            //
                            // if already recording today diary ( by checking existance of today database), SHOW
                            // record_Btn.setVisibility(View.INVISIBLE);
                            // map_Btn.setVisibility(View.VISIBLE);
                            // stop_Btn.setVisibility(View.VISIBLE); , else
                            //
                            textview.setText("");
                            record_Btn.setVisibility(View.VISIBLE);
                            map_Btn.setVisibility(View.INVISIBLE);
                            stop_Btn.setVisibility(View.INVISIBLE);
                        } else if (dayOfMonth < today_day){
                            textview.setText("check sqllite");
                            record_Btn.setVisibility(View.INVISIBLE);
                            map_Btn.setVisibility(View.VISIBLE);
                            stop_Btn.setVisibility(View.INVISIBLE);
                        }
                        else textview.setText("ERR");
                        }else if(month < today_month){
                        textview.setText("check sqllite");
                    }else textview.setText("ERR");
                }else if (year < today_year) {
                    textview.setText("check sqllite");
                } else textview.setText("ERR");
                // checkDay(year, month, dayOfMonth);
            }
        });
        record_Btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
//                if(view.getId() == R.id.record_Btn){
//                    intent2.putExtra(String.valueOf(map_service.CHECK), cnt_num);
//                } else {
//                    intent2.putExtra(String.valueOf(map_service.CHECK), "false");
//                }
                ActivityCompat.requestPermissions(CalendarActivity.this , new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CHECK);



                record_Btn.setVisibility(View.INVISIBLE);
                map_Btn.setVisibility(View.VISIBLE);
                stop_Btn.setVisibility(View.VISIBLE);
                createLocationHandler();
                //startService(intent2);
            }
        });
        map_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                // show data from sqllite as Fragment??
                //
                textview.setText("show map,,,");
                startActivity(intent1);
            }
        });
        stop_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textview.setText("stop map");
                map_Btn.setVisibility(View.VISIBLE);
                stop_Btn.setVisibility(View.INVISIBLE);
                stopService(intent2);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case PERMISSION_CHECK:
                if(grantResults.length >0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                } else return;

        }
    }


    public void createLocationHandler(){
        HandlerThread locationHandler = new HandlerThread("LocationHandler"); //Creates a new handler thread
        locationHandler.start(); //Starts the thread
        Handler handler = new Handler(locationHandler.getLooper()); //Get the looper from the handler thread
        handler.postDelayed(new Runnable() {//Run the runnable only after the given time
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                //Check if the location service is running, if its not. lets start it!
                if(!isMyServiceRunning(map_service.class)){
                    getApplicationContext().startService(new Intent(getApplicationContext(), map_service.class));
                }
                ////Requests a new location from the location service(Feel like it could be done in a less static way)
                //LocationService.requestNewLocation();
                //createLocationHandler();//Call the create location handler again, this will not be added to the stack because of the looper.
            }
        }, 1000);//Set the delay to be 10 seconds, 1 second = 1000 milliseconds
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



}