package com.huaweichallenge.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huaweichallenge.app.services.MapsService;
import com.huaweichallenge.app.services.SensorService;
import com.huaweichallenge.app.services.SocketService;

import java.util.ArrayList;
import java.util.HashMap;

public class SensorActivity extends AppCompatActivity {

    public class SensorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            HashMap<String, Float> sensors = (HashMap<String, Float>) intent.getSerializableExtra("sensorDataMap");
            SocketService.startSendMessage(context, sensors);
        }
    }

    public class SocketReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sensors =  intent.getStringExtra("activityResponse");

            Log.i("Stuff", "Happened");
            mTextView.setText(sensors);
        }
    }

    private TextView mTextView;
    private SensorReceiver sensorReceiver;
    private SocketReceiver socketReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sensorReceiver = new SensorReceiver();
        socketReceiver = new SocketReceiver();
        mTextView = (TextView) findViewById(R.id.activityResponse);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent sensorIntent = new Intent(this, SensorService.class);
        startService(sensorIntent);
        Intent socketIntent = new Intent(this, SocketService.class);
        startService(socketIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the receiver
        IntentFilter filter = new IntentFilter(SensorService.SEND_DATA);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(sensorReceiver, filter);

        IntentFilter filter2 = new IntentFilter(SocketService.GET_ACTIVITY);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(socketReceiver, filter2);
    }
}
