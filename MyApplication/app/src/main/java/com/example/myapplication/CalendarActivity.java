package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.io.FileInputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        record_Btn = findViewById(R.id.record_Btn);
        map_Btn = findViewById(R.id.map_Btn);
        stop_Btn = findViewById(R.id.stop_Btn);
        textview = findViewById(R.id.textView);
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
            @Override
            public void onClick(View view) {
//                if(view.getId() == R.id.record_Btn){
//                    intent2.putExtra(String.valueOf(map_service.CHECK), cnt_num);
//                } else {
//                    intent2.putExtra(String.valueOf(map_service.CHECK), "false");
//                }
                record_Btn.setVisibility(View.INVISIBLE);
                map_Btn.setVisibility(View.VISIBLE);
                stop_Btn.setVisibility(View.VISIBLE);
                startService(intent2);
            }
        });
        map_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                // show data from sqllite as Fragment??
                //
                textview.setText("show map,,,");
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

}