package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class map_service extends Service implements TMapGpsManager.onLocationChangedCallback{
    public static boolean CHECK = true;

    Calendar cal = Calendar.getInstance();
    private class timeData{
        public int today_year = cal.get(Calendar.YEAR);
        public int today_month = cal.get(Calendar.MONTH);
        public int today_day = cal.get(Calendar.DATE);;
    }

    private class recordPoint {
        //private String currentTime = currentTime().substring(0, 8);
        private double latitude;
        private double longitude;
    }

    private boolean areUTracking = true;
    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;

    // 좌표, 마커, 마커id arr
    private ArrayList<TMapPoint> arrMarker = new ArrayList<TMapPoint>();
    private ArrayList<String> arrMarkerID = new ArrayList<String>();
    private ArrayList<MapActivity.recordPoint> arrPoint = new ArrayList<MapActivity.recordPoint>();

    //tmapgpsmanger
    public void onLocationChange (Location location){
        if (areUTracking) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    public void onCreate() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    "channel_ID", "channel_ID2", NotificationManager.IMPORTANCE_LOW);
            //notificationChannel.setDescription(("channel_ID3"));
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notifier = new NotificationCompat.Builder(getBaseContext(),"channel_ID")
                //.setAutoCancel(true)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("발자취")
                .setContentText("백그라운드에서 위치 정보 액세스 중");
        notificationManager.notify(1, notifier.build());

        TMapView tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxb3fcc775f3cf452ea70f97fcfa0d5367");

        Log.d("StartService", "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int i, int startId){
        Log.d("StartService", "onStartCommand()");

        Timer Scheduled = new Timer();
        GetLocation addLocation = new GetLocation();
        TMapGpsManager tmapgps = new TMapGpsManager(this);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();
        while(CHECK){Scheduled.scheduleAtFixedRate(addLocation,0, 10000);}
        //
        // while loop as thread??
        //
        // tmapview.setOnCalloutRightButtonClickListener();
        // ADD 'marker determining function'
        //

        Scheduled.cancel();
        tmapgps.CloseGps();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.d("EndService","onDestroy()");
        CHECK = false;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("StartService","onBind()");

        throw new UnsupportedOperationException("Not yet implemented");
    }

    class GetLocation extends TimerTask{
        public void run(){
            TMapPoint tmp = tmapgps.getLocation();
            //
            // ADD this data to APP db
            //
        }
    }

}