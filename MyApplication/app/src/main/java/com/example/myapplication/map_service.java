package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import static android.telephony.CellLocation.requestLocationUpdate;

//public class map_service extends Service implements TMapGpsManager.onLocationChangedCallback {
//public class map_service extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
public class map_service extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static boolean CHECK = true;
    Calendar cal = Calendar.getInstance();

    private class timeData {
        public int today_year = cal.get(Calendar.YEAR);
        public int today_month = cal.get(Calendar.MONTH);
        public int today_day = cal.get(Calendar.DATE);
    }

    private boolean areUTracking = true;
    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    // 좌표, 마커, 마커id arr
    private ArrayList<TMapPoint> arrMarker = new ArrayList<TMapPoint>();
    private ArrayList<String> arrMarkerID = new ArrayList<String>();
    private ArrayList<MapActivity.recordPoint> arrPoint = new ArrayList<MapActivity.recordPoint>();

    private NotificationManager notificationManager = null;

    private FusedLocationProviderClient ffusedLocationClient;
    private GoogleApiClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    //added 0501
    private Context context;
    private boolean stopService = false;
    private String latitude = "0.0", longitude = "0.0";
    protected GoogleApiClient googleApiClient;
    protected LocationSettingsRequest locationSettingsRequest;
    private SettingsClient settingsClient;
    private Location currentLocation;
//
//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d("service", "new location!");
//        if (location != null) {
//            callAPI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
//        }
//    }


    //System.out.println((LocationServices.FusedLocationApi.requestLocationUpdates(fusedLocationClient, locationRequest, (com.google.android.gms.location.LocationListener) this).toString()));
    //System.out.println((LocationServices.FusedLocationApi.requestLocationUpdates(fusedLocationClient, locationRequest, (com.google.android.gms.location.LocationListener) this)).toString());

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


//        }).addOnCanceledListener(new OnCanceledListener() {
//            @Override
//            public void onCanceled() {
//                Log.e("finding location cancaled", "checkLocationSettings -> onCanceled");
//            }
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

//    private void buildLocationCallBack() {
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                for (Location location : locationResult.getLocations()) {
//                    String latitude = String.valueOf(location.getLatitude());
//                    String longitude = String.valueOf(location.getLongitude());
//                }
//            }
//        };
//    }


    @Override
    public void onCreate() {
        super.onCreate();
        //TMapView tMapView = new TMapView(this);
        //tMapView.setSKTMapApiKey("l7xxb3fcc775f3cf452ea70f97fcfa0d5367");

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(9000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(1);

        ffusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };


        fusedLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

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

        notificationManager.notify(3385, notifier.build());


        Log.d("StartService", "onCreate()");
    }


    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int i, int startId) {
        Log.d("StartService", "onStartCommand()");

        //Timer Scheduled = new Timer();
        //GetLocation addLocation = new GetLocation();
        //
        // how to access location info by tmap? how about use android gps to locate
        //

        // added 0501
        //fusedLocationClient.connect();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_NOT_STICKY;
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation= locationResult.getLastLocation();

                if (currentLocation == null)   //first gps triger.
                    Log.d("service","err in onLocationResult");
                else{
                    Log.d("service", String.valueOf(currentLocation.getLatitude()));
                    Log.d("service", String.valueOf(currentLocation.getLongitude()));
                }


//
//                finaldist = BigDecimal.valueOf(distancetracking/1000);
//                Intent i = new Intent("location_update");
//                i.putExtra("coordinates", finaldist);
//                sendBroadcast(i);
//                notification(finaldist+ " Km");

            }
        };
        ffusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);


//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            Log.d("StartService", "==Error occurs in starting GETTING LOCATION");
//            // here to request the missing permissions, and then overriding
//            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return START_NOT_STICKY;
//        }

        //fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);


//        while(CHECK){
//            Scheduled.scheduleAtFixedRate(addLocation,0, 10000);}

        //
        // while loop as thread??
        //
        // tmapview.setOnCalloutRightButtonClickListener();
        // ADD 'marker determining function'
        //

        //Scheduled.cancel();
        //tmapgps.CloseGps();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("EndService", "onDestroy()");
        CHECK = false;
        //tmapgps.CloseGps();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3385);

//        if (fusedLocationClient != null) {
//            fusedLocationClient.removeLocationUpdates(locationCallback);
//            Log.d("END_location_searching", "fusedlocationclient removed");
//        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("StartService", "onBind()");

        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    class GetLocation extends TimerTask {
        public void run() {
            //TMapPoint tmp = tmapgps.getLocation();
            //
            // ADD this data to APP db
            //
        }
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


//        ffusedLocationClient.getLastLocation()
//                .addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        try {
//                            //if (task.isSuccessful() && task.getResult() != null) {
//                            if (task.getResult() != null) {
//                                //currentLocation = task.getResult();
//                                Log.d("service", String.valueOf(task.getResult().getLongitude()));
//                                Log.d("service", String.valueOf(task.getResult().getLatitude()));
//
//                            } else {
//                                Log.w("service", "Failed to get location.");
//                            }
//                        } catch (SecurityException e) {
//                            Log.d("service", "lost location permission" + e);
//                        }
//                    }
//                });

    }
}





