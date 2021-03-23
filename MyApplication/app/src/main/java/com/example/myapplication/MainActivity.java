package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapMarkerItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.location.Location;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    //날짜 기억 함수 for recordPoint currentTime
    public static String currentTime(){
        Date date = new Date();
        SimpleDateFormat time_format = new SimpleDateFormat("yyyyMMddHHmmss");
        return time_format.format(date);
    }

    // 좌표 객체 -> 시간, 위도, 경도
    private class recordPoint{
        private String currentTime = currentTime().substring(0,8);
        private double latitude;
        private double longitude;
    }

    private boolean areUTracking = true;
    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;

    // 좌표, 마커, 마커id 어레이
    private ArrayList<TMapPoint> arrMarker = new ArrayList<TMapPoint>();
    private ArrayList<String> arrMarkerID = new ArrayList<String>();
    private ArrayList<recordPoint> arrPoint = new ArrayList<recordPoint>();

    //tmapgpsmanger 오버라이딩
    public void onLocationChange(Location location){
        if(areUTracking){
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayoutTmap =(LinearLayout)findViewById((R.id.linearLayoutTmap));
        TMapView tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxb3fcc775f3cf452ea70f97fcfa0d5367");
        linearLayoutTmap.addView(tMapView);

        // <추후> point들 시간대 별로 받는 좌표로 수정
        // TMapGpsManager tmapgps = new TMapGpsManager(this);
        // tmapgps.setProvider(_TMapGpsManager.GPS_PROVIDER);
        // tmapgps.setMinTime(10000);
        // tmapgps.OpenGps();
        // tmapgps.CloseGps();
        // TMapPoint point = tmapview.getLocationPoint();
        TMapPoint point1 = new TMapPoint(37.5585044, 126.925337);
        TMapPoint point2 = new TMapPoint(37.5591593, 126.924806);
        TMapPoint point3 = new TMapPoint(37.5588808, 126.924379);
        TMapPolyLine tpolyline = new TMapPolyLine();
        tpolyline.setLineColor(Color.YELLOW);
        tpolyline.setLineWidth(2);
        tpolyline.addLinePoint(point1);
        tpolyline.addLinePoint(point2);
        tpolyline.addLinePoint(point3);
        tMapView.addTMapPolyLine("line1", tpolyline);

        // <추후> 좌표에서 마커 대상 파악 후 마커 생성하는 거로 수정
        // decisionMarker()
        TMapMarkerItem marker1 = new TMapMarkerItem();
        marker1.setTMapPoint(point3);
        tMapView.addMarkerItem("marker1", marker1);




//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}