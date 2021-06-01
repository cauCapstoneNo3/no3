package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MapActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, TMapView.OnApiKeyListenerCallback {
    Calendar cal = Calendar.getInstance();
    private String tmpDate;
    public ArrayList<locationEntity> MC;

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
    public String markerMsg;
    public TextView markerText;
    public EditText markerText2;
    public TMapView tMapView;
    private boolean areUTracking = true;
    private TMapGpsManager tmapgps = null;
    public locationDatabase tmpDB;
    public locationDao tmpDao;
    private locationEntity LE;
    public HandlerThread locationHandler;
    public LinearLayout linearLayoutTmap;
    String todayDate;
    Button markerRevisionButton;
    // 좌표, 마커, 마커id 어레이
    // private ArrayList<TMapPoint> arrMarker = new ArrayList<TMapPoint>();
    // private ArrayList<String> arrMarkerID = new ArrayList<String>();
    // private ArrayList<recordPoint> arrPoint = new ArrayList<recordPoint>();


    //tmapgpsmanger overriding
    public void onLocationChange (Location location){
        if (areUTracking) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
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


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent3 = new Intent(this, onclickActivity.class);
        setContentView(R.layout.activity_map);
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        MC = new ArrayList<locationEntity>();
        todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
        Intent intent = getIntent();
        tmpDate = intent.getStringExtra("chosendate");
        Log.d("mapactivity","getExtra"+tmpDate);
        //createLocationHandler();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tmpDB = locationDatabase.getAppDatabase(this);
        tmpDao = tmpDB.locationDao();
        //new Thread(new threadDraw(tmpDao)).start();

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxb3fcc775f3cf452ea70f97fcfa0d5367");
        linearLayoutTmap.addView(tMapView);

        tMapView.setZoomLevel(18);

//        TMapTapi tMapTapi = new TMapTapi(this);
//        tMapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
//            @Override
//            public void SKTMapApikeySucceed() {
//                tMapView.setZoomLevel(18);
//            }
//            @Override
//            public void SKTMapApikeyFailed(String s) {
//            }
//        });

        if (!tmpDate.equals(todayDate)) {
            new DrawAsyncTask(tmpDao).execute(tmpDao);
            //new Thread(new threadDraw(tmpDao)).start();
        } else{
            new DrawAsyncTask(tmpDao).execute(tmpDao);
            //new Thread(new markingThread(tmpDao, MC)).start();
        }

        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                if(arrayList.size()!=0){
                    int tmpId = Integer.parseInt(arrayList.get(0).getName());
                    Log.d("!null", arrayList.get(0).getName());
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout formarker = (LinearLayout)inflater.inflate(R.layout.formarker, null);
                    LinearLayout.LayoutParams paramll = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
                    addContentView(formarker, paramll);
//                    markerText = findViewById(R.id.markerContent);
//                    markerText.setText("");
                    markerText2 = findViewById(R.id.markerContent);
                    // USE String type [markerMsg] Var
                    new markergetAyncTask(tmpDao, tmpId).execute(tmpDao);
                    markerRevisionButton = findViewById(R.id.write_button);
                    markerRevisionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            markerMsg = null;
                            String inputMsg = markerText2.getText().toString();
                            Log.d("checkinput",inputMsg);
                            new markersetAyncTask(tmpDao, tmpId, inputMsg).execute(tmpDao);
                            ((ViewManager)formarker.getParent()).removeView(formarker);
                        }
                    });
                }
                //startActivity(intent3);
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

    class markergetAyncTask extends AsyncTask<locationDao, Void, Void> {
        String tmpStr = null;
        int entityId;
        private locationDao dao;
        markergetAyncTask(locationDao parDao, int parNum){
            entityId = parNum;
            dao = parDao;
        }
        @Override
        protected Void doInBackground(locationDao... locationDaos) {
            tmpStr = dao.getDataById(entityId).get(0);
            if (tmpStr!=null) Log.d("get", tmpStr);
            else Log.d("get", "its null");
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("checkgetmarkeraync1", "1");
            if(tmpStr!=null) {
                Log.d("checkgetmarkeraync2", "2");
                markerMsg = tmpStr;
                markerText2.setText(markerMsg);
            }
        }
    }

    class markersetAyncTask extends AsyncTask<locationDao, Void, Void>{
        String tmpStr;
        int entityId;
        private locationDao dao;
        markersetAyncTask(locationDao parDao, int parNum, String parStr){
            tmpStr = parStr;
            dao = parDao;
            entityId = parNum;
        }
        @Override
        protected Void doInBackground(locationDao... locationDaos) {
            dao.updateTextData(entityId, tmpStr);
            Log.d("setmakertext",tmpStr);
            Log.d("setmarkertextchk",dao.getDataById(entityId).get(0));
            return null;
        }
    }

    class DrawAsyncTask extends AsyncTask<locationDao, TMapMarkerItem, ArrayList<TMapPolyLine>> {
        Calendar cal = Calendar.getInstance();
        private locationDao dao;
        private Location location;
        private String todayDate;
        private ArrayList<locationEntity> MC2;
        TMapView taskView;
        private boolean checkExit = true;
        private double distaneBetweenMarker = 0.0;
        int num = 0;
        int tmpCnt;
        int accumulator =0;
        DrawAsyncTask(locationDao lolo){
            todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
            dao = lolo;
        }

        @Override
        protected ArrayList<TMapPolyLine> doInBackground(locationDao... locationDaos) {
            taskView = tMapView;
            for(locationEntity tmpEntity : dao.getData(tmpDate)){
                tmpCnt = MC.size();
                //int accumulator =0;
                if(tmpEntity.getAlreadyCHK() == 1) {
                    MC.clear();
                    continue;
                }
                if(tmpEntity.getMarkerFlag() != 0) {
                    MC.clear();
                    continue;
                }
                if(tmpEntity.getLongitude() == 1.0) {
                    MC.clear();
                    continue;
                }

                if (MC.isEmpty()) {
                    Log.d("marker"," add into empty");
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
                    locationEntity tmpEntityForPreprocessing;
                    tmpEntityForPreprocessing = MC.get(0);
                    tmpEntityForPreprocessing.setAlreadyCHK(1);
                    dao.UpdateData(tmpEntityForPreprocessing);
                    double firstMeanLongitude =0.0;
                    double secondMeanLongitude =0.0;
                    double firstMeanLatitude = 0.0;
                    double secondMeanLatitude = 0.0;
                    for(int i=0;i<15;i++) {
                        firstMeanLongitude += MC.get(i).getLongitude();
                        firstMeanLatitude += MC.get(i).getLatitude();
                    }
                    for(int i=15;i<30;i++) {
                        secondMeanLongitude += MC.get(i).getLongitude();
                        secondMeanLatitude += MC.get(i).getLatitude();
                    }
                    DecimalFormat df = new DecimalFormat("#####.########");
                    firstMeanLatitude = Double.parseDouble(df.format(firstMeanLatitude/15)); secondMeanLatitude = Double.parseDouble(df.format(secondMeanLatitude/15));
                    firstMeanLongitude = Double.parseDouble(df.format(firstMeanLongitude/15));secondMeanLongitude = Double.parseDouble(df.format(secondMeanLongitude/15));
                    double distance = getDistance(firstMeanLatitude, firstMeanLongitude, secondMeanLatitude, secondMeanLongitude);
                    //Log.d("distance"," : "+distance);
                    //if(distance <100) new Thread(new markerThread(dao, MC.get(29)));
                    if(distance < 100){
                        if (!dao.getData(todayDate, 1).isEmpty()) {
                            //Log.d("service", "MARKER ARR IS NOT EMPTY");
                            for (locationEntity tmpselect : dao.getData(todayDate, 1)) {
                                if (checkExit) {
                                    distaneBetweenMarker = getDistance(tmpEntity.getLatitude(), tmpEntity.getLongitude(), tmpselect.getLatitude(), tmpselect.getLongitude());
                                    if (distaneBetweenMarker < 100) checkExit = false;
                                }
                            }
                            if (checkExit == true) {
                                tmpEntity.setMarkerFlag(tmpEntity.getID());
                                //tmpEntity.setMarkerFlag(1);
                                dao.UpdateData(tmpEntity);
                                //dao.UpdateData(tmpEntity.getID(), tmpEntity.getToday(), 1);
                                Log.d("marker", "ADDED MARKER");
                            }
                            //else Log.d("service", "NON ADDED MARKER");
                        } else {
                            Log.d("marker", "DIRECTLY ADDED MARKER");
                            tmpEntity.setMarkerFlag(tmpEntity.getID());
                            //tmpEntity.setMarkerFlag(1);
                            dao.UpdateData(tmpEntity);
                            //dao.UpdateData(tmpEntity.getID(), tmpEntity.getToday(), 1);
                        }
                    }
                }
//                tmpEntity.setAlreadyCHK(1);
//                dao.UpdateData(tmpEntity);
            }

            Log.d("Map","run start");
            //check list
            TMapMarkerItem tmpMarker = new TMapMarkerItem();
            TMapPoint tMapPoint1forMarker;
            //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pin);
            tmpNum = dao.getData(tmpDate).size();
            //else Log.d("test", "is not Empty");
            double tmpLati;
            double tmpLogi;
            int integerForMarker =0;
            for(locationEntity im :  dao.getData(tmpDate)){
                if (im.getToday().equals(tmpDate)){
                    // if (im.setMarkerFlag() ==1){ make marker};
                    tmpLati = im.getLatitude();
                    tmpLogi= im.getLongitude();
                    if (im.getMarkerFlag()!=0){
                        tMapPoint1forMarker = new TMapPoint(tmpLati, tmpLogi);
                        tmpMarker.setTMapPoint(tMapPoint1forMarker);
                        tmpMarker.setName(String.valueOf(im.getID()));
                        publishProgress(tmpMarker);
                        //tMapView.addMarkerItem("markerNo"+integerForMarker,tmpMarker);
                        //integerForMarker++;
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
                    polyLinesList.add(tmpLine);
                    tmpLine = new TMapPolyLine();
                    continue;
                }
                else {
                    tmpPoint = new TMapPoint(tmpLa.get(i),tmpLo.get(i));

                    //tmpLine.addLinePoint(tmpPoint);
                    polyLinesList.get(polyLineCounter).addLinePoint(tmpPoint);
                }
            }
            Log.d("polyline", "polylineCounter"+polyLineCounter);

//            for(int addpolyline=0; addpolyline < polyLineCounter+1 ; addpolyline++){
//                tMapView.addTMapPolyLine("line"+addpolyline, polyLinesList.get(addpolyline));
//            }
            return polyLinesList;
        }

        @Override
        protected void onProgressUpdate(TMapMarkerItem... values) {
            tMapView.addMarkerItem(values[0].getID(), values[0]);
        }
//
//        protected void onProgressUpdate(TMapMarkerItem tmpMarker){
//            tMapView.addMarkerItem(tmpMarker.getID(), tmpMarker);
//        }

        @Override
        protected void onPostExecute(ArrayList<TMapPolyLine> tMapPolyLines) {
            super.onPostExecute(tMapPolyLines);
            tMapView.setCenterPoint(startLongitude, startLatitude);
            Log.d("onPost",""+tMapPolyLines.size());
            for(int addpolyline=0; addpolyline < tMapPolyLines.size(); addpolyline++){
                tMapView.addTMapPolyLine(" "+addpolyline, tMapPolyLines.get(addpolyline));
                Log.d("onPost"," line Num : "+tMapPolyLines.get(addpolyline).getID());
            }
        }
    }


    public double getDistance(double firstMeanLatitude, double firstMeanLongitude, double secondMeanLatitude, double secondMeanLongitude){
        DecimalFormat df = new DecimalFormat("####.#######");
        double thetaLongitude = firstMeanLongitude - secondMeanLongitude;
        double distance = Math.sin(Math.toRadians(firstMeanLatitude))*Math.sin(Math.toRadians(secondMeanLatitude))+
                Math.cos(Math.toRadians(firstMeanLatitude))*Math.cos(Math.toRadians(secondMeanLatitude))*Math.cos(Math.toRadians(thetaLongitude));
        distance = Math.acos(distance); distance = Math.toDegrees(distance); distance = distance*60*1.1515*1.609344;
        distance = Double.valueOf(df.format(distance));
        return distance;
    }
}