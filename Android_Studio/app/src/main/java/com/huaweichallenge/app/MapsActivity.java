package com.huaweichallenge.app;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.addMarker(new MarkerOptions().position(point));
            }
        });

        LatLng parisPosition = new LatLng(48.866667, 2.33333333);
        MarkerOptions parisOptions = new MarkerOptions().position(parisPosition).title("Marker in Paris");
        mMap.addMarker(parisOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(parisPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));*/

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://10.0.2.2:3000/markers.json";
        // This endpoint is custom, and returns:
        /*[
            {
                "id": 1,
                "lat": 48.858053,
                "lng": 2.294289,
                "title": "Tour Eiffel",
                "created_at": "2017-12-06T11:51:10.748Z",
                "updated_at": "2017-12-06T11:51:10.748Z",
                "url": "http://localhost:3000/markers/1.json"
            }
        ]*/

        // Request a string response from the provided URL.
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the first 500 characters of the response string.
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject elem = response.getJSONObject(i);
                                LatLng location = new LatLng(elem.getDouble("lat"), elem.getDouble("lng"));
                                mMap.addMarker(new MarkerOptions().position(location).title(elem.getString("title")));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Shit", "didnt work");
            }
        });
// Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }
}
