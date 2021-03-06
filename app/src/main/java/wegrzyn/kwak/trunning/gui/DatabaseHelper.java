package wegrzyn.kwak.trunning.gui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Data data = Data.getInstance();

    DatabaseHelper(Context context) {
        super(context, "database_name", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE Tracks" +
                "(track_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " track_name TEXT," +
                " track_location TEXT," +
                " track_date TEXT," +
                " track_time INTEGER," +
                " track_distance INTEGER," +
                " track_minimap BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Tracks");
        onCreate(db);
    }

    public void createTrackTable(String name){
        String createTable = "CREATE TABLE " + name +
                "(point_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " point_latitude REAL," +
                " point_longitude REAL)";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(createTable);

        insertPointsToTrackTable(sqLiteDatabase, name);
        insertToTracks(sqLiteDatabase);
    }

    private void insertPointsToTrackTable(SQLiteDatabase sqLiteDatabase, String name){
        for (Location location : data.getPoints()){
            String insert =  "INSERT INTO " + name + "(point_latitude, point_longitude) VALUES (" +
                    location.getLatitude() + "," + location.getLongitude() + ")";
            sqLiteDatabase.execSQL(insert);
        }
    }

    private void insertToTracks(SQLiteDatabase sqLiteDatabase){
        String insert = "INSERT INTO Tracks(track_name, track_location, track_date, track_time," +
                " track_distance, track_minimap) VALUES ('" + data.getTrack_name() + "', '" + getTrack_location() + "', '" +
                getTrack_date() + "', '" + data.getTrack_time() + "', '" + data.getTrack_distance() +"', '" + "null')";
                sqLiteDatabase.execSQL(insert);
    }

    private String getTrack_location(){
        String track_location;
        try {
            Geocoder geocoder = new Geocoder(Data.getContext(), Locale.getDefault());
            Location location = data.getFirstPoint();
            track_location = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10).get(0).getLocality();
        }
        catch (IOException e){
            track_location = "";
        }
        return track_location;
    }

    private String getTrack_date(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    public void readTrackFromDatabase(String table_name){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table_name, null);
        while(cursor.moveToNext()){
            Location temp = new Location("");
            temp.setLatitude(cursor.getDouble(1));
            temp.setLongitude(cursor.getDouble(2));
            data.addPoint(temp);
        }
        cursor.close();
    }

    public Cursor getCursorToTracksTable(){
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            return sqLiteDatabase.rawQuery("SELECT * FROM Tracks", null);
    }

    public int getLastTrackId(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT track_id FROM Tracks", null);
        if(cursor.moveToLast()) {
            return cursor.getInt(0);
        }
        else {
            return 0;
        }
    }
} // End of DatabaseHelper