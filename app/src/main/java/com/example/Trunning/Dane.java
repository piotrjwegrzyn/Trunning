package com.example.Trunning;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Dane extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Dane INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private ArrayList<Location> points;
    private int track_distance;
    private int track_time;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        points = new ArrayList<>();
        Dane.context = getApplicationContext();
    }

    public static Dane getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Dane();
        }
        return INSTANCE;
    }

    public static Context getContext(){return Dane.context;}


    public List<Location> getPoints() {
        return points;
    }

    public Location getLastPoint(){
        if (!points.isEmpty()) {
            return points.get(points.size() - 1);
        }
        else{ return null; }
    }

    public Location getFirstPoint(){
        if (!points.isEmpty()) {
            return points.get(0);
        }
        else{ return null; }
    }

    public void addPoint(Location point) {
        points.add(point);
    }

    public void clearLocations(){
        points.clear();
    }

    public void setTrack_time(int time){ track_time = time; }

    public int getTrack_time(){ return track_time; }

    public void setTrack_distance(int distance){ track_distance = distance; }

    public int getTrack_distance(){ return track_distance; }
}
