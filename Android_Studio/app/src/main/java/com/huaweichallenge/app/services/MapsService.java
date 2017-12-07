package com.huaweichallenge.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.huaweichallenge.app.MapsActivity.MarkerReceiver;

public class MapsService extends IntentService {

    private static final String ACTION_GET_MARKERS = "GET_MARKERS";
    private static final String ACTION_POST_MARKER = "POST_MARKER";

    // TODO: Rename parameters
    private static final String LATLGN = "Latitude_longitude";

    public static final String MARKERS = "markers_received";

    public static final String urlServer = "http://35.177.151.54/";
    public static final String urlGetMarkers = "markers.json";

    public MapsService() {
        super("MapsService");
    }

    public static void startActionGetMarkers(Context context) {
        Intent intent = new Intent(context, MapsService.class);
        intent.setAction(ACTION_GET_MARKERS);

        context.startService(intent);
    }

    public static void startActionPostMarker(Context context, LatLng l) {
        Intent intent = new Intent(context, MapsService.class);
        intent.setAction(ACTION_POST_MARKER);

        intent.putExtra(LATLGN, l);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_GET_MARKERS.equals(action)) {

                handleActionGetMarkers();
            } else if (ACTION_POST_MARKER.equals(action)) {

                final LatLng l = intent.getParcelableExtra(LATLGN);

                handleActionPostMarker(l);
            }
        }
    }

    private void handleActionGetMarkers() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = urlServer + urlGetMarkers;


        // Request a string response from the provided URL.
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<LatLng> markers = new ArrayList<LatLng>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject elem = response.getJSONObject(i);

                                markers.add(new LatLng(elem.getDouble("lat"), elem.getDouble("lng")));

                                Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction(MarkerReceiver.ACTION_GET_MARKERS);
                                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                broadcastIntent.putExtra(MARKERS, markers);
                                sendBroadcast(broadcastIntent);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("GET", "Can't get markers");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }

    private void handleActionPostMarker(LatLng l) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urlServer + "login.php";

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("lat", String.valueOf(l.latitude));
            jsonBody.put("lng", String.valueOf(l.longitude));

        } catch (org.json.JSONException e) {
            Log.e("JSON", "Unable to create JSON.");
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("POST", "JSON sent");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("POST", "Trouble sending JSON");
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }
}
