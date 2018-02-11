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

import android.support.v7.app.AppCompatActivity;
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
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Binghamton and move the camera
        mPos = new LatLng(42.05, -76.0);
        mark = null;
        
        //mMap.setOnMarkerDragListener(this);
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

    /*@Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        for (int i = 0; i < 100; i++) {
            mPos = marker.getPosition();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mPos));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mPos = marker.getPosition();
        Toast.makeText(LocationChooser.this, "Point coordinates: " + mPos.latitude
                + ", " + mPos.longitude, Toast.LENGTH_LONG).show();
    }*/

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("mapLat", mPos.latitude);
        intent.putExtra("mapLong", mPos.longitude);
        setResult(RESULT_OK, intent);
        finish();
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
                .position(mPos)
                .title("Camping Location")
                .snippet("Location: " + mPos.latitude + ", " + mPos.longitude));
    }
}
