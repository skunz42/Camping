package com.example.sean.camping;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class LocationChooser extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {


    private GoogleMap mMap;
    private LatLng mPos;
    private Marker mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_chooser);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Binghamton and move the camera
        mPos = new LatLng(42.05, -76.0);
        mark = null;
        
        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mPos));
        mMap.setOnMapLongClickListener(this);
    }

    public double getLatitude() {
        return mPos.latitude;
    }

    public double getLongitude() {
        return mPos.longitude;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("mapLat", mPos.latitude);
        intent.putExtra("mapLong", mPos.longitude);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onMarkerClick(Marker marker) {
        mPos = marker.getPosition();
        Toast.makeText(LocationChooser.this, "Point coordinates: " + mPos.latitude
                + ", " + mPos.longitude, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mark != null) {
            mark.remove();
        }
        mPos = latLng;
        mark = mMap.addMarker(new MarkerOptions()
                .position(mPos));
    }
}
