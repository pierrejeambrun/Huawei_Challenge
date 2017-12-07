package com.huaweichallenge.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huaweichallenge.app.services.MapsService;

import java.util.ArrayList;

import static com.huaweichallenge.app.MapsActivity.MarkerReceiver.ACTION_GET_MARKERS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public class MarkerReceiver extends BroadcastReceiver {
        public static final String ACTION_GET_MARKERS = "action_get_markers";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<LatLng> markers = intent.getParcelableArrayListExtra(MapsService.MARKERS);

            for (LatLng location : markers) {
                //TODO Deal with the name on the marker
                mMap.addMarker(new MarkerOptions().position(location).title("Mon marker"));
            }
        }
    }

    private GoogleMap mMap;
    private MarkerReceiver markerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // on initialise notre broadcast
        markerReceiver = new MarkerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on déclare notre Broadcast Receiver
        IntentFilter filter = new IntentFilter(ACTION_GET_MARKERS);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(markerReceiver, filter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng l) {
                mMap.addMarker(new MarkerOptions().position(l));
                MapsService.startActionPostMarker(MapsActivity.this, l);
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        MapsService.startActionGetMarkers(this);

    }
}
