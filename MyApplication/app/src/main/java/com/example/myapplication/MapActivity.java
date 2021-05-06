package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
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
import com.skt.Tmap.TMapPOIItem;
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
    public static ArrayList<locationEntity> MC;
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
    String todayDate;
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
        MC = new ArrayList<locationEntity>();
        todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
        Intent intent = getIntent();
        tmpDate = intent.getStringExtra("chosendate");
        Log.d("mapactivity","getExtra"+tmpDate);
        //createLocationHandler();

        setContentView(R.layout.activity_map);
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);

        tmpDB = locationDatabase.getAppDatabase(this);
        tmpDao = tmpDB.locationDao();
        //new Thread(new threadDraw(tmpDao)).start();

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxb3fcc775f3cf452ea70f97fcfa0d5367");
        linearLayoutTmap.addView(tMapView);

        if (!tmpDate.equals(todayDate)) {
            new Thread(new threadDraw(tmpDao)).start();
        } else{
            new Thread(new markingThread(tmpDao)).start();
        }

        tMapView.setZoomLevel(18);
        //SKTMapApikeySucceed();

        // tmpdate 수정 to 달력눌린날짜로

        // <추후> 좌표에서 마커 대상 파악 후 마커 생성하는 거로 수정
        // decisionMarker()
        // TMapMarkerItem marker1 = new TMapMarkerItem();
        // marker1.setTMapPoint(point3);
        // tMapView.addMarkerItem("marker1", marker1);
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }

            @Override
            public boolean onPressEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF) {
                //Toast.makeText(MapEvent.this, "onPress~!", Toast.LENGTH_SHORT).show();
                // OPEN MARKER INFO PAGE eg) tablayout
                return false;
            }
        });
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
            TMapMarkerItem tmpMarker = new TMapMarkerItem();
            TMapPoint tMapPoint1forMarker;
            //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pin);
            tmpNum = Dao.getData(tmpDate).size();
            //else Log.d("test", "is not Empty");
            double tmpLati;
            double tmpLogi;
            int integerForMarker =0;
            for(locationEntity im :  Dao.getData(tmpDate)){
                if (im.getToday().equals(tmpDate)){
                    // if (im.setMarkerFlag() ==1){ make marker};
                    tmpLati = im.getLatitude();
                    tmpLogi= im.getLongitude();
                    if (im.getMarkerFlag()==1){
                        tMapPoint1forMarker = new TMapPoint(tmpLati, tmpLogi);
                        tmpMarker.setTMapPoint(tMapPoint1forMarker);
                        tMapView.addMarkerItem("markerNo"+integerForMarker,tmpMarker);
                        integerForMarker++;
                    }
                    tmpLa.add(tmpLati);
                    tmpLo.add(tmpLogi);
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
                tmp.setLineWidth(4);
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

    public double getDistance(double firstMeanLatitude, double firstMeanLongitude, double secondMeanLatitude, double secondMeanLongitude){
        double thetaLongitude = firstMeanLongitude - secondMeanLongitude;
        double distance = Math.sin(Math.toRadians(firstMeanLatitude))*Math.sin(Math.toRadians(secondMeanLatitude))+
                Math.cos(Math.toRadians(firstMeanLatitude))*Math.cos(Math.toRadians(secondMeanLatitude))*Math.cos(Math.toRadians(thetaLongitude));
        distance = Math.acos(distance); distance = Math.toDegrees(distance); distance = distance*60*1.1515*1.609344;
        return distance;
    }

    class markingThread implements Runnable {
        locationDao dao;
        private boolean checkExit = true;
        private double distaneBetweenMarker = 0.0;
        int num = 0;
        int tmpCnt;
        int accumulator =0;

        public markingThread(locationDao dao) {
            this.dao = dao;
        }

        @Override
        public void run() {
            Log.d("service", "START MARKER DECISION");
            for(locationEntity tmpEntity : dao.getData(tmpDate)){
                tmpCnt = MC.size();
                //int accumulator =0;
                double firstMeanLongitude =0.0;
                double secondMeanLongitude =0.0;
                double firstMeanLatitude = 0.0;
                double secondMeanLatitude = 0.0;
                if (MC.isEmpty()) {
                    MC.add(tmpEntity);
                } else if (tmpCnt <= 29){
                    //Log.d("service","MarkerQueue"+tmpCnt);
                    MC.add(tmpEntity);
                } else {
                    MC.set(accumulator, tmpEntity);
                    accumulator++;
                    if(accumulator==29) accumulator=0;
                }

                if(tmpCnt==30){
                    for(int i=0;i<15;i++) {
                        firstMeanLongitude += MC.get(i).getLongitude();
                        firstMeanLatitude += MC.get(i).getLatitude();
                    }
                    for(int i=15;i<30;i++) {
                        secondMeanLongitude += MC.get(i).getLongitude();
                        secondMeanLatitude += MC.get(i).getLatitude();
                    }
                    firstMeanLatitude /= 15; secondMeanLatitude /= 15; firstMeanLongitude /= 15; secondMeanLongitude /= 15;
                    double distance = getDistance(firstMeanLatitude, firstMeanLongitude, secondMeanLatitude, secondMeanLongitude);
                    //Log.d("distance"," : "+distance);
                    //if(distance <100) new Thread(new markerThread(dao, MC.get(29)));
                    if(distance <100){
                        if (!dao.getData(todayDate, 1).isEmpty()) {
                            //Log.d("service", "MARKER ARR IS NOT EMPTY");
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
                            }
                            //else Log.d("service", "NON ADDED MARKER");
                        } else {
                            Log.d("service", "DIRECTLY ADDED MARKER");
                            tmpEntity.setMarkerFlag(1);
                            dao.UpdateData(tmpEntity);
                            //dao.UpdateData(tmpEntity.getID(), tmpEntity.getToday(), 1);
                        }
                    }
                }
            }
            new Thread(new threadDraw(dao)).start();
        }
    }
}