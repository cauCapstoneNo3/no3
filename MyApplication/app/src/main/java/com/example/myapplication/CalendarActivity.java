package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import java.io.FileInputStream;
import java.security.Permission;
import java.util.Calendar;
import java.util.List;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class CalendarActivity extends AppCompatActivity {
    public CalendarView calendarView;
    public Button map_Btn, stop_Btn, record_Btn, restart_Btn;
    public TextView textview;
    Calendar cal = Calendar.getInstance();
    public static String chosenDate;
    public int today_year = cal.get(Calendar.YEAR);
    public int today_month = cal.get(Calendar.MONTH);
    public int today_day = cal.get(Calendar.DATE);;
    public int cnt_num;
    public static final int PERMISSION_CHECK = 3333;
    public HandlerThread locationHandler;
    public Handler handler;
    private String[] PERMISSIONS ={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    locationDatabase tmpDB;
    locationDao tmpDao;
    public int CHKDB=0;
    String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todayDate = todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        record_Btn = findViewById(R.id.record_Btn);
        map_Btn = findViewById(R.id.map_Btn);
        stop_Btn = findViewById(R.id.stop_Btn);
        restart_Btn = findViewById(R.id.restart_Btn);
        textview = findViewById(R.id.textView);
        Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
        Intent intent2 = new Intent(getApplicationContext(), map_service.class);
        tmpDB = locationDatabase.getAppDatabase(this);
        tmpDao = tmpDB.locationDao();
        //mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                chosenDate = Integer.toString(year)+ (month+1) +Integer.toString(dayOfMonth);
                Log.d("Calendar", "date : "+chosenDate);
                if (year > today_year){
                    textview.setText("not available");
                }else if(year == today_year){
                    if (month > today_month){
                        textview.setText("not available");
                        record_Btn.setVisibility(View.INVISIBLE);
                        map_Btn.setVisibility(View.INVISIBLE);
                        stop_Btn.setVisibility(View.INVISIBLE);
                        restart_Btn.setVisibility(View.INVISIBLE);
                    }else if(month == today_month){
                        if (dayOfMonth > today_day){
                            textview.setText("not available");
                            record_Btn.setVisibility(View.INVISIBLE);
                            map_Btn.setVisibility(View.INVISIBLE);
                            stop_Btn.setVisibility(View.INVISIBLE);
                            restart_Btn.setVisibility(View.INVISIBLE);
                        } else if (dayOfMonth == today_day){
                            textview.setText("");
                            record_Btn.setVisibility(View.VISIBLE);
                            map_Btn.setVisibility(View.INVISIBLE);
                            stop_Btn.setVisibility(View.INVISIBLE);
                            restart_Btn.setVisibility(View.INVISIBLE);
//                            if (CHKDB ==1){
//                                textview.setText("");
//                                restart_Btn.setVisibility(View.VISIBLE);
//                                map_Btn.setVisibility(View.INVISIBLE);
//                                stop_Btn.setVisibility(View.INVISIBLE);
//                            } else{
//                                textview.setText("");
//                                record_Btn.setVisibility(View.VISIBLE);
//                                map_Btn.setVisibility(View.INVISIBLE);
//                                stop_Btn.setVisibility(View.INVISIBLE);
//                            }
//                            CHKDB=0;
                        } else if (dayOfMonth < today_day){
                            textview.setText("check sqllite");
                            record_Btn.setVisibility(View.INVISIBLE);
                            map_Btn.setVisibility(View.VISIBLE);
                            stop_Btn.setVisibility(View.INVISIBLE);
                            restart_Btn.setVisibility(View.INVISIBLE);
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
                if(isMyServiceRunning(map_service.class)){
                    //getApplicationContext().startService(new Intent(getApplicationContext(), map_service.class));
                } else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent2);
                    } else {
                        startService(intent2);
                    }
                }
                //startService(intent2);
            }
        });
        map_Btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                textview.setText("show map,,,");
                new Thread(new threadCHKdbforMap(view, intent1, tmpDao, chosenDate)).start();
//                Toast tmpToast = new Toast(view.getContext());
//                tmpToast.makeText(view.getContext(), " NO DATA STORED ", Toast.LENGTH_SHORT).show();

//                if (CHKDB ==1){
//                    intent1.putExtra("chosendate", chosenDate);
//                    startActivity(intent1);
//                } else {
//                    Toast tmpToast = new Toast(view.getContext());
//                    tmpToast.makeText(view.getContext(), " NO DATA STORED ", Toast.LENGTH_SHORT).show();
//                }
//                CHKDB = 0;

            }
        });
        stop_Btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                textview.setText("stop map");
                map_Btn.setVisibility(View.VISIBLE);
                stop_Btn.setVisibility(View.INVISIBLE);
                restart_Btn.setVisibility(View.VISIBLE);
                record_Btn.setVisibility(View.INVISIBLE);
                if(isMyServiceRunning(map_service.class)){
                    stopService(intent2);
                    //getApplicationContext().startService(new Intent(getApplicationContext(), map_service.class));
                }
                //stopService(intent2);
            }
        });
        restart_Btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                textview.setText("restart map");
                map_Btn.setVisibility(View.VISIBLE);
                restart_Btn.setVisibility(View.INVISIBLE);
                stop_Btn.setVisibility(View.VISIBLE);
                if(!isMyServiceRunning(map_service.class)){
                    //startWakefulService(getApplicationContext(), intent2);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent2);
                    } else {
                        startService(intent2);
                    }
                    //startService(intent2);
                    //getApplicationContext().startService(new Intent(getApplicationContext(), map_service.class));
                }
                //stopService(intent2);
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
        locationHandler = new HandlerThread("LocationHandler");
        locationHandler.start();
        //Handler handler = new Handler(locationHandler.getLooper());
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                //Check if service is running
                if(!isMyServiceRunning(map_service.class)){
                    getApplicationContext().startService(new Intent(getApplicationContext(), map_service.class));
                }

            }
        }, 1000);
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

    class threadCHKdb implements Runnable{
        String date;
        locationDao tmpDao;
        public threadCHKdb(locationDao dao, String getdate){
            this.tmpDao = dao;
            this.date = getdate;
        }
        @Override
        public void run() {
            if(!tmpDao.getData(date).isEmpty()){
                CHKDB =1;
            }
        }
    }

    class threadCHKdbForDayOfMonth implements Runnable{
        String date;
        locationDao tmpDao;
        public threadCHKdbForDayOfMonth(locationDao dao, String getdate){
            this.tmpDao = dao;
            this.date = getdate;
        }
        @Override
        public void run() {
            if(!tmpDao.getData(date).isEmpty()){
                CHKDB =1;
            }
            if (CHKDB ==1){
                textview.setText("");
                restart_Btn.setVisibility(View.VISIBLE);
                map_Btn.setVisibility(View.INVISIBLE);
                stop_Btn.setVisibility(View.INVISIBLE);
            } else{
                textview.setText("");
                record_Btn.setVisibility(View.VISIBLE);
                map_Btn.setVisibility(View.INVISIBLE);
                stop_Btn.setVisibility(View.INVISIBLE);
            }
            CHKDB=0;
        }

    }

    class threadCHKdbforMap implements Runnable{
        String date;
        Intent intent;
        View view;
        locationDao tmpDao;
        public threadCHKdbforMap(View view, Intent tmpIntent, locationDao dao, String getdate){
            this.date = getdate;
            this.intent = tmpIntent;
            this.view = view;
            this.tmpDao = dao;
        }
        @Override
        public void run() {
            if(!tmpDao.getData(date).isEmpty()){
                CHKDB =1;
            }
            if (CHKDB ==1){
                intent.putExtra("chosendate", chosenDate);
                startActivity(intent);
            } else Log.d("calendar->map btn", "db is empty");
            CHKDB = 0;
        }
    }

    public double getDistance(double firstMeanLatitude, double firstMeanLongitude, double secondMeanLatitude, double secondMeanLongitude){
        double thetaLongitude = firstMeanLongitude - secondMeanLongitude;
        double distance = Math.sin(Math.toRadians(firstMeanLatitude))*Math.sin(Math.toRadians(secondMeanLatitude))+
                Math.cos(Math.toRadians(firstMeanLatitude))*Math.cos(Math.toRadians(secondMeanLatitude))*Math.cos(Math.toRadians(thetaLongitude));
        distance = Math.acos(distance); distance = Math.toDegrees(distance); distance = distance*60*1.1515*1.609344;
        return distance;
    }

    class markerThread implements Runnable {
        locationEntity tmpEntity;
        locationDao dao;
        private boolean checkExit = true;
        private double distaneBetweenMarker = 0.0;
        int num = 0;

        public markerThread(locationDao dao) {
            this.dao = dao;
            this.tmpEntity = tmpEntity;
        }

        @Override
        public void run() {
            Log.d("service", "START MARKER DECISION");
            if (!dao.getData(todayDate, 1).isEmpty()) {
                Log.d("service", "MARKER ARR IS NOT EMPTY");
                for (locationEntity tmpselect : dao.getData(todayDate, 1)) {
                    if (checkExit) {
                        distaneBetweenMarker = getDistance(tmpEntity.getLatitude(), tmpEntity.getLongitude(), tmpselect.getLatitude(), tmpselect.getLongitude());
                        if (distaneBetweenMarker < 100) checkExit = false;
                    }
                }
                if (checkExit == true) {
                    tmpEntity.setMarkerFlag(1);
                    dao.UpdateData(tmpEntity);
                    //dao.UpdateData(tmpEntity.getID(), tmpEntity.getToday(), 1);
                    Log.d("service", "ADDED MARKER");
                } else Log.d("service", "NON ADDED MARKER");
            } else {
                Log.d("service", "DIRECTLY ADDED MARKER");
                tmpEntity.setMarkerFlag(1);
                dao.UpdateData(tmpEntity);
                //dao.UpdateData(tmpEntity.getID(), tmpEntity.getToday(), 1);
            }
        }
    }
}