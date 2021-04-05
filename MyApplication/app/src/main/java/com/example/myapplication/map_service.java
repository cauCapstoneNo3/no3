package com.example.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
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

public class map_service extends Service implements TMapGpsManager.onLocationChangedCallback{
    public static String CHECK = "true";

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
        Log.d("StartService", "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int i, int startId){
        Log.d("StartService", "onStartCommand()");
        Intent nMainIntent = new Intent(this, CalendarActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 1, nMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder nBuiler = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("발자취")
                .setContentText("백그라운드에서 위치 정보 액세스 중")
                .setContentIntent(mPendingIntent);
        NotificationManager nNotifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nNotifier.notify(001, nBuiler.build());
//        do{
//        }while(CHECK!="false");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("StartService","onBind()");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}