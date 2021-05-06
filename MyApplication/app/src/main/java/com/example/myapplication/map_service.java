package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Dao;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//public class map_service extends Service implements TMapGpsManager.onLocationChangedCallback {
//public class map_service extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
public class map_service extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static boolean CHECK = true;
    Calendar cal = Calendar.getInstance();
    private String todayDate;
    private boolean areUTracking = true;
    // 좌표, 마커, 마커id arr
    public static ArrayList<locationEntity> MC;
    public static ArrayList<Double> LA = new ArrayList<>();
    public static ArrayList<Double> LO = new ArrayList<>();
    public static int integer_i=0;

    private NotificationManager notificationManager = null;

    private FusedLocationProviderClient ffusedLocationClient;
    private GoogleApiClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private Context context;
    private boolean stopService = false;
    private String latitude = "0.0", longitude = "0.0";
    protected GoogleApiClient googleApiClient;
    protected LocationSettingsRequest locationSettingsRequest;
    private SettingsClient settingsClient;
    Location currentLocation;
    public locationDatabase tmpDB;
    public locationDao mDao;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//        locationSettingsRequest = builder.build();
//
//        settingsClient
//                .checkLocationSettings(locationSettingsRequest)
//                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//                    @Override
//                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                        Log.e("gps activating", "GPS Success");
//                        requestLocationUpdate();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                int statusCode = ((ApiException) e).getStatusCode();
//                switch (statusCode) {
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        try {
//                            int REQUEST_CHECK_SETTINGS = 214;
//                            ResolvableApiException rae = (ResolvableApiException) e;
//                            rae.startResolutionForResult((AppCompatActivity) context, REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException sie) {
//                            Log.e("GETTING GPS LOCATION", "Unable to execute request.");
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.e("GETTING GPS LOCATION", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
//                }
//            }
//
//        });

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("service", "suspended to connect to api");
        //connetGoogleClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("service", "failed to connect to api");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyApp::myapplication");

        MC = new ArrayList<locationEntity>();
        todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
        locationDatabase tmpDB = locationDatabase.getAppDatabase(this);
        //tmpDB.locationDao().getAll();
        mDao = tmpDB.locationDao();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(19000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setSmallestDisplacement(1);
        ffusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Intent notificationIntent = new Intent(this, CalendarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notifier = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "channel_ID", "channel_ID2", NotificationManager.IMPORTANCE_LOW);
            //notificationChannel.setDescription(("channel_ID3"));
            notificationManager.createNotificationChannel(notificationChannel);
            notifier = new NotificationCompat.Builder(this, "channel_ID");
        } else {
            notifier = new NotificationCompat.Builder(this, null);
        }
        //notifier.setAutoCancel(true)
        notifier.setSmallIcon(android.R.drawable.btn_star);
        notifier.setContentTitle("발자취");
        notifier.setContentText("백그라운드에서 위치 정보 액세스 중");
        notifier.setContentIntent(pendingIntent);
        //notificationManager.notify(3385, notifier.build());
        startForeground(4885, notifier.build());


        new Thread(new threadGet(tmpDB.locationDao())).start();

        Log.d("StartService", "onCreate()");
    }

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int i, int startId) {
        Log.d("StartService", "onStartCommand()");
        wakeLock.acquire();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_STICKY;
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation= (Location) locationResult.getLocations().get(locationResult.getLocations().size() - 1);
                //currentLocation= locationResult.getLastLocation();

                if (currentLocation == null) {
                    Log.d("service","err in onLocationResult");
                } else {
                    locationEntity entity = new locationEntity();
                    entity.setLongitude(currentLocation.getLongitude());;
                    entity.setLatitude(currentLocation.getLatitude());
                    entity.setToday(todayDate);
                    Log.d("service","threadAsync");
                    Log.d("service", String.valueOf(currentLocation.getLatitude()));
                    Log.d("service", String.valueOf(currentLocation.getLongitude()));
                    new insertAsyncTask(mDao).execute(entity);
                    //new Thread(new markerThread(mDao)).start();
                    //new Thread(new threadInsert(tmpDB.locationDao(), currentLocation)).start();
                }

//                finaldist = BigDecimal.valueOf(distancetracking/1000);
//                Intent i = new Intent("location_update");
//                i.putExtra("coordinates", finaldist);
//                sendBroadcast(i);
//                notification(finaldist+ " Km");
            }
        };
        ffusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("EndService", "onDestroy()");
        CHECK = false;
        new Thread(new threadExit(mDao)).start();
        //tmpDB.destroyDatabaseInstance();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3385);
        wakeLock.release();

        if (ffusedLocationClient != null) {
            ffusedLocationClient.removeLocationUpdates(locationCallback);
            Log.d("END_location_searching", "fusedlocationclient removed");
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("StartService", "onBind()");

        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.d("service", "==DENY occurs in LOCATION PERMISSION");
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d("service", "Connected to Google API");
    }

//    public void setMarker(locationDao dao, locationEntity tmpEntity){
//        int tmpCnt = MC.size();
//        int accumulator =0;
//        double firstMeanLongitude =0.0;
//        double secondMeanLongitude =0.0;
//        double firstMeanLatitude = 0.0;
//        double secondMeanLatitude = 0.0;
//        if (MC.isEmpty()) {
//            MC.add(tmpEntity);
//        } else if (tmpCnt <= 29){
//            Log.d("service","MarkerQueue"+tmpCnt);
//            MC.add(tmpEntity);
//        } else {
//            MC.set(accumulator, tmpEntity);
//            accumulator++;
//            if(accumulator==29) accumulator=0;
//        }
//
//        if(tmpCnt==30){
//            for(int i=0;i<15;i++) {
//                firstMeanLongitude += MC.get(i).getLongitude();
//                firstMeanLatitude += MC.get(i).getLatitude();
//            }
//            for(int i=15;i<30;i++) {
//                secondMeanLongitude += MC.get(i).getLongitude();
//                secondMeanLatitude += MC.get(i).getLatitude();
//            }
//            firstMeanLatitude /= 15; secondMeanLatitude /= 15; firstMeanLongitude /= 15; secondMeanLongitude /= 15;
//            double distance = getDistance(firstMeanLatitude, firstMeanLongitude, secondMeanLatitude, secondMeanLongitude);
//            Log.d("distance"," : "+distance);
//            //if(distance <100) new Thread(new markerThread(dao, MC.get(29)));
//            if(distance <100) new Thread(new markerThread(dao, tmpEntity)).start();
//        }
//    }

    public double getDistance(double firstMeanLatitude, double firstMeanLongitude, double secondMeanLatitude, double secondMeanLongitude){
        double thetaLongitude = firstMeanLongitude - secondMeanLongitude;
        double distance = Math.sin(Math.toRadians(firstMeanLatitude))*Math.sin(Math.toRadians(secondMeanLatitude))+
                Math.cos(Math.toRadians(firstMeanLatitude))*Math.cos(Math.toRadians(secondMeanLatitude))*Math.cos(Math.toRadians(thetaLongitude));
        distance = Math.acos(distance); distance = Math.toDegrees(distance); distance = distance*60*1.1515*1.609344;
        return distance;
    }

    class markerThread implements Runnable{
        locationEntity tmpEntity;
        locationDao dao;
        int tmpCnt;
        int accumulator =0;
        double firstMeanLongitude =0.0;
        double secondMeanLongitude =0.0;
        double firstMeanLatitude = 0.0;
        double secondMeanLatitude = 0.0;
        private boolean checkExit = true;
        private double distaneBetweenMarker = 0.0;
        int num =0;
        public markerThread(locationDao dao){
            this.dao = dao;
            this.tmpEntity = tmpEntity;
        }
        @Override
        public void run() {
            Log.d("service","fucking marker decision");
            LiveData<List<locationEntity>> spareMarkerList = dao.getAll();
            spareMarkerList.observeForever((Observer<? super List<locationEntity>>) this);
            spareMarkerList.observe((LifecycleOwner) this, new Observer<List<locationEntity>>() {
                @Override
                public void onChanged(List<locationEntity> locationEntities) {
                    int tmpCnt = MC.size();
                    if (MC.isEmpty()) {
                        MC.add(tmpEntity);
                    } else if (tmpCnt <= 29){
                        Log.d("service","MarkerQueue"+tmpCnt);
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
                        Log.d("distance"," : "+distance);
                        //if(distance <100) new Thread(new markerThread(dao, MC.get(29)));
                        //if(distance <100) new Thread(new markerThread(dao, tmpEntity)).start();
                        if(distance < 100){
                            locationEntities.get(0).setMarkerFlag(1);
                            dao.UpdateData(locationEntities.get(0));
//                            for (locationEntity tmpselect : dao.getData(todayDate,1)) {
//                                distaneBetweenMarker = getDistance(locationEntities.get(0).getLatitude(), locationEntities.get(0).getLongitude(), tmpselect.getLatitude(), tmpselect.getLongitude());
//                                if (distaneBetweenMarker >100) dao.UpdateData(locationEntities.get(0));
//                            }
                        }
                    }
                }
            });
        }
    }

    static class insertAsyncTask extends AsyncTask<locationEntity,Void,Void>{
        Calendar cal = Calendar.getInstance();
        private locationDao Dao;
        private Location location;
        private String todayDate;
        insertAsyncTask(locationDao lolo){
            todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
            this.Dao = lolo;
        }
        @Override
        protected Void doInBackground(locationEntity... locationEntities) {
            if (Dao != null) {
                Dao.insertData(locationEntities[0]);
                Log.d("service","in async doinbackground");
            }
            return null;
        }
    }

    class threadGet implements Runnable{
        Context context;
        Calendar cal = Calendar.getInstance();
        private locationDatabase tmpDB;
        private locationDao Dao;
        private String todayDate;
        public threadGet(locationDao instance){
            this.Dao = instance;
            todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
        }
        public void run(){
            if (Dao.getData(todayDate)==null){
                locationEntity tmp = new locationEntity();
                tmp.setLongitude(1.0);;
                tmp.setLatitude(1.0);
                tmp.setToday(todayDate);
                Dao.insertData(tmp);
                Log.d("service","threadGet");
            }
        }
    }

    class threadExit implements Runnable{
        Context context;
        Calendar cal = Calendar.getInstance();
        private locationDao Dao;
        private String todayDate;
        private Location location;
        public threadExit(locationDao instance){
            todayDate = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);
            this.location = location;
            this.Dao = instance;
        }
        public void run(){
            locationEntity tmp = new locationEntity();
            tmp.setLongitude(1.0);;
            tmp.setLatitude(1.0);
            tmp.setToday(todayDate);
            Dao.insertData(tmp);
        }
    }
}







