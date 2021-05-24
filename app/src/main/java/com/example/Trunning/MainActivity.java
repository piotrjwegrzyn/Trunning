package com.example.Trunning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_UPDATE_INTERVAL = 2;
    private static final int FASTEST_UPDATE_INTERVAL = 1;
    private static final int PERMISSIONS_FINE_LOCATION_CODE = 1;
    private static final int INTERNET_PERMISSION_GRANTED_CODE = 2;
    private static final int MAX_ACCEPTABLE_ACCURACY = 18;

    private Boolean isRunning = false;
    private int hour;
    private int min;
    private int sec;

    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_updates;
    Button bt_openMaps, bt_openList;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sw_locationsupdates;

    private DatabaseHelper databaseHelper;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private final Dane dane = Dane.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        {
            bt_openList = findViewById(R.id.bt_openList);
            tv_lat = findViewById(R.id.tv_lat);
            tv_lon = findViewById(R.id.tv_lon);
            tv_altitude = findViewById(R.id.tv_altitude);
            tv_accuracy = findViewById(R.id.tv_accuracy);
            tv_speed = findViewById(R.id.tv_speed);
            tv_updates = findViewById(R.id.tv_updates);
            sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
            bt_openMaps = findViewById(R.id.bt_openMaps);
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
            locationRequest.setFastestInterval(1000 * FASTEST_UPDATE_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            databaseHelper = new DatabaseHelper(this);
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if(location.getAccuracy() < MAX_ACCEPTABLE_ACCURACY) {
                    UpdateTeksty(location);
                    dane.addPoint(location);
                }
            }
        };

        bt_openList.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, TestowaLista.class);
            startActivity(i);
        });
        sw_locationsupdates.setOnClickListener(view -> {
            if (sw_locationsupdates.isChecked()) {
                updating();
            } else {
                notUpdating();
            }
        });
        bt_openMaps.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, Mapsy.class);
            startActivity(i);
        });
    } // END of onCreate

    @SuppressLint("ShowToast")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION_CODE:
            case INTERNET_PERMISSION_GRANTED_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Potrzebne uprawnienia", Toast.LENGTH_SHORT);
                    finish();
                }
                break;
        }
    }

    private void updating() {

        tv_updates.setText("N");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION_CODE);
            }
            else{
                tv_updates.setText("SDK ERR");
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                requestPermissions(new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_GRANTED_CODE);
            }
        }
        time();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        tv_updates.setText("ON");
    } // END of updating

    private void notUpdating(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        tv_updates.setText("OFF");
        tv_lat.setText("NaN");
        tv_lon.setText("NaN");
        tv_altitude.setText("NaN");
        tv_accuracy.setText("NaN");
        tv_speed.setText("NaN");
        dane.setTrack_distance(countDistance());
        time();
        Random random = new Random();
        int x = random.nextInt(100000);
        databaseHelper.createTrackTable("Track" + x);
        dane.clearLocations();
    } // END of notUpdating

    private void UpdateTeksty(Location location){
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        String a = location.hasAltitude() ? String.valueOf(location.getAltitude()) : "0";
        tv_altitude.setText(a);
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        a = location.hasSpeed() ? String.valueOf(location.getSpeed()) : "0";
        tv_speed.setText(a);
    } // END of UpdateTeksty

    private int countDistance(){
        double deltaLatitude = 0;
        double deltaLongtitude = 0;
        double actualLatitude = dane.getFirstPoint().getLatitude();
        double actualLongtitude = dane.getFirstPoint().getLongitude();
        for (Location location : dane.getPoints()){
            deltaLatitude += (location.getLatitude() - actualLatitude);
            deltaLongtitude += (location.getLongitude() - actualLongtitude);

            actualLatitude = location.getLatitude();
            actualLongtitude = location.getLongitude();
        }
        double deltaLatitudeInMeters = deltaLatitude * 111320;
        double deltaLongtitudeInMeters = deltaLongtitude * 40075000 * Math.cos(dane.getFirstPoint().getLatitude())/360;
        double distance = Math.sqrt(Math.pow(deltaLatitudeInMeters, 2) + Math.pow(deltaLongtitudeInMeters, 2));
        return (int)distance;
    } // END of countDistance

    public void time(){
        if (isRunning) {
            Calendar calendar = Calendar.getInstance();
            int _hour = calendar.get(Calendar.HOUR_OF_DAY);
            int _min = calendar.get(Calendar.MINUTE);
            int _sec = calendar.get(Calendar.SECOND);
            dane.setTrack_time(3600 * (_hour - hour) + 60 * (_min - min) + _sec - sec);
            isRunning = false;
        }
        else{
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            isRunning = true;
        }
    } // END of time
} // END of MainClass