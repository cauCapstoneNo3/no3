package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, TMapView.OnApiKeyListenerCallback {
    Calendar cal = Calendar.getInstance();
    private String tmpDate;
    //좌표 객체 -> 시간, 위도, 경도
    private class recordPoint {
    private double latitude;
    private double longitude;
    }
    private int tmpNum =0;
    public ArrayList<Double> tmpLa = new ArrayList<Double>();
    public ArrayList<Double> tmpLo = new ArrayList<Double>();
    public double startLatitude=0.0;
    public double startLongitude=0.0;
    public TMapView tMapView;
    private boolean areUTracking = true;
    private TMapGpsManager tmapgps = null;
    public locationDatabase tmpDB;
    public locationDao tmpDao;
    private locationEntity LE;
    public HandlerThread locationHandler;
    public LinearLayout linearLayoutTmap;
    // 좌표, 마커, 마커id 어레이
    // private ArrayList<TMapPoint> arrMarker = new ArrayList<TMapPoint>();
    // private ArrayList<String> arrMarkerID = new ArrayList<String>();
    // private ArrayList<recordPoint> arrPoint = new ArrayList<recordPoint>();

    @Override
    public void SKTMapApikeySucceed() {
        if (startLatitude != 0.0){
            Log.d("suceedcheck", String.valueOf(startLatitude));
            //tMapView.setCenterPoint(startLongitude, startLatitude);
            tMapView.setZoomLevel(18);
        }
    }
    @Override
    public void SKTMapApikeyFailed(String s) {
    }

    //tmapgpsmanger overriding
    public void onLocationChange (Location location){
        if (areUTracking) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        tmpDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
        //createLocationHandler();

        setContentView(R.layout.activity_map);
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);

        tmpDB = locationDatabase.getAppDatabase(this);
        tmpDao = tmpDB.locationDao();
        //new Thread(new threadDraw(tmpDao)).start();

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxb3fcc775f3cf452ea70f97fcfa0d5367");
        linearLayoutTmap.addView(tMapView);

        new Thread(new threadDraw(tmpDao)).start();

        SKTMapApikeySucceed();

        // tmpdate 수정 to 달력눌린날짜로

        // <추후> 좌표에서 마커 대상 파악 후 마커 생성하는 거로 수정
        // decisionMarker()
        // TMapMarkerItem marker1 = new TMapMarkerItem();
        // marker1.setTMapPoint(point3);
        // tMapView.addMarkerItem("marker1", marker1);
    }

    class threadDraw implements Runnable{
        locationDatabase DB;
        Context context;
        Calendar cal = Calendar.getInstance();
        private locationDao Dao;
        private String todayDate;
        private Location location;
        public threadDraw(locationDao dao){
            this.Dao = dao;
        }
        public void run(){
            //Dao.insertData(1.0,1.0, todayDate);
            Log.d("Map","run start");


            //check list
            tmpNum = Dao.getData(tmpDate).size();
            //else Log.d("test", "is not Empty");
            for(locationEntity im :  Dao.getData(tmpDate)){
                if (im.getToday().equals(tmpDate)){
                    // if (im.setMarkerFlag() ==1){ make marker};
                    tmpLa.add(im.getLatitude());
                    tmpLo.add(im.getLongitude());
                }
            }
            startLatitude = tmpLa.get(1);
            startLongitude = tmpLo.get(1);


            if (tmpNum == tmpLo.size()){
                Log.d("tmpNumChk", "true");
            } else Log.d("tmpNumChk", "flase");

            ArrayList<TMapPolyLine> polyLinesList = new ArrayList<TMapPolyLine>(1);
            TMapPolyLine testLine = new TMapPolyLine();
            polyLinesList.add(testLine);
            for(TMapPolyLine tmp : polyLinesList){
                tmp.setLineColor(Color.YELLOW);
                tmp.setLineWidth(3);
            }
            TMapPoint tmpPoint;
            TMapPolyLine tmpLine = new TMapPolyLine();

            int polyLineCounter =0;
            for (int i=0; i<tmpLo.size();i++){
                //Log.d("polyline","in forloop");
                if (tmpLo.get(i) ==1.0) {
                    polyLineCounter++;
                    tmpLine = new TMapPolyLine();
                    polyLinesList.add(tmpLine);
                    continue;
                }
                else {
                    tmpPoint = new TMapPoint(tmpLa.get(i),tmpLo.get(i));

                    //tmpLine.addLinePoint(tmpPoint);
                    polyLinesList.get(polyLineCounter).addLinePoint(tmpPoint);
                }
            }
            tMapView.setCenterPoint(startLongitude, startLatitude);
            Log.d("polyline", "polylineCounter"+polyLineCounter);
            //tMapView.addTMapPolyLine("tmp", tmpLine);
            for(int addpolyline=0; addpolyline<polyLineCounter+1;addpolyline++){
                tMapView.addTMapPolyLine("line"+addpolyline, polyLinesList.get(addpolyline));
            }

        }
    }
}