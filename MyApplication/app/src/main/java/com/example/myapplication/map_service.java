package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class map_service extends Service {
    //날짜 기억 함수 for recordPoint currentTime
    public static String currentTime () {
        Date date = new Date();
        SimpleDateFormat time_format = new SimpleDateFormat("yyyyMMddHHmmss");
        return time_format.format(date);
    }

    // 좌표 객체 -> 시간, 위도, 경도
    private class recordPoint {
        private String currentTime = currentTime().substring(0, 8);
        private double latitude;
        private double longitude;
    }

    private boolean areUTracking = true;
    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;

    // 좌표, 마커, 마커id 어레이
    private ArrayList<TMapPoint> arrMarker = new ArrayList<TMapPoint>();
    private ArrayList<String> arrMarkerID = new ArrayList<String>();
    private ArrayList<MapActivity.recordPoint> arrPoint = new ArrayList<MapActivity.recordPoint>();

    //tmapgpsmanger 오버라이딩
    public void onLocationChange (Location location){
        if (areUTracking) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public map_service() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}