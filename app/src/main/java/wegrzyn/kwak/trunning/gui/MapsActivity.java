package wegrzyn.kwak.trunning.gui;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;

import wegrzyn.kwak.trunning.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {

        Data dane = Data.getInstance();
        PolylineOptions polylineOptions = new PolylineOptions();
        for (Location location : dane.getPoints()) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            polylineOptions.add(point);
        }
        polylineOptions.color(Color.rgb(0,120,200));
        googleMap.addPolyline(polylineOptions);

        Location start = dane.getFirstPoint();
        Location end = dane.getLastPoint();
        googleMap.addMarker(new MarkerOptions().position(new LatLng(start.getLatitude(), start.getLongitude())));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(end.getLatitude(), end.getLongitude())));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(end.getLatitude(), end.getLongitude()), 15f));
    }
}