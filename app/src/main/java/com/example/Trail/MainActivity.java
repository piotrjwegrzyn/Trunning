package com.example.Trail;

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
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    public static final int FASTEST_UPDATE_INTERVAL = 2;
    private static final int PERMISSIONS_FINE_LOCATION = 87;


    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_updates, tv_address;
    Button bt_openMaps, bt_openList;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sw_locationsupdates;

    boolean update = false;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    Dane dane = Dane.getInstance();

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
            tv_address = findViewById(R.id.tv_address);
            sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
            bt_openMaps = findViewById(R.id.bt_openMaps);
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
            locationRequest.setFastestInterval(1000 * FASTEST_UPDATE_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                Location location = locationResult.getLastLocation();
                UpdateTeksty(location);
                dane.addPoint(location);
            }
        };

        bt_openList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TestowaLista.class);
                startActivity(i);
            }
        });
        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationsupdates.isChecked()) {
                    updating();
                } else {
                    notUpdating();
                }
            }
        });
        bt_openMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Mapsy.class);
                startActivity(i);
            }
        });
    } // END of OnCreate

    @SuppressLint("ShowToast")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Potrzebne uprawnienia", Toast.LENGTH_SHORT);
                    finish();
                }
                break;
        }
    }

    public void updating() {

        tv_updates.setText("N");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
            else{
                tv_updates.setText("SDK ERR");
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        tv_updates.setText("ON");
    }
    public void notUpdating(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        tv_updates.setText("OFF");
        tv_lat.setText("NaN");
        tv_lon.setText("NaN");
        tv_altitude.setText("NaN");
        tv_accuracy.setText("NaN");
        tv_speed.setText("NaN");
        dane.clearLocations();
    }

    private void UpdateTeksty(Location location){
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        String a = location.hasAltitude() ? String.valueOf(location.getAltitude()) : "0";
        tv_altitude.setText(a);
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        a = location.hasSpeed() ? String.valueOf(location.getSpeed()) : "0";
        tv_speed.setText(a);
    }

} // END of MainClass