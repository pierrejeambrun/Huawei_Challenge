package com.huaweichallenge.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huaweichallenge.app.services.MapsService;
import com.huaweichallenge.app.services.SocketService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    /*public class HActivityReceiver extends BroadcastReceiver {
        public static final String ACTION_GET_HACTIVITY = "action_get_hactivity";

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject receivedIntentData = intent.getSerializableExtra()

            for (LatLng location : markers) {
                //TODO Deal with the name on the marker
                mTextView.
            }
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SocketService.startConnectWebSocket(this);

    }

    public void onMapButtonClick(View view) {
        setContentView(R.layout.activity_maps);
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }

    public void onHTTPButtonClick(View view) {
        final TextView mTextView = (TextView) findViewById(R.id.textView2);

        HashMap<String, Float> data = new HashMap<>();
        data.put("accelerometer", 0f);
        SocketService.startSendMessage(this, data);
    }
}
