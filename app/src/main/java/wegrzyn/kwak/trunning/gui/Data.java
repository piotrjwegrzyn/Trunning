package wegrzyn.kwak.trunning.gui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Data extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Data INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private ArrayList<Location> points;
    private int track_distance;
    private int track_time;
    private DatabaseHelper databaseHelper;

    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        points = new ArrayList<>();
        Data.context = getApplicationContext();
        databaseHelper = new DatabaseHelper(context);
    }

    public static Data getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Data();
        }
        return INSTANCE;
    }

    public static Context getContext(){return Data.context;}

    public List<Location> getPoints(){ return points; }

    public Location getLastPoint(){ return points.isEmpty() ? null : points.get(points.size() - 1); }

    public Location getFirstPoint(){ return points.isEmpty() ? null : points.get(0); }

    public void addPoint(Location point){ points.add(point); }

    public void clearLocations(){ points.clear(); }

    public void setTrack_time(int time){ track_time = time; }

    public int getTrack_time(){ return track_time; }

    public void setTrack_distance(int distance){ track_distance = distance; }

    public int getTrack_distance() { return track_distance; }

    public DatabaseHelper getDatabaseHelper() { return databaseHelper; }
}
