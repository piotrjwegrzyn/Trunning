package com.example.Trail;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Dane extends Application {
    private static Dane INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        points = new ArrayList<>();
    }

    public static Dane getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Dane();
        }
        return INSTANCE;
    }

    public List<Location> getPoints() {
        return points;
    }

    public Location getPoint(){
        return points.get(points.size()-1);
    }

    public void addPoint(Location point) {
        points.add(point);
    }

    private ArrayList<Location> points;

    public void clearLocations(){
        points.clear();
    }

}
