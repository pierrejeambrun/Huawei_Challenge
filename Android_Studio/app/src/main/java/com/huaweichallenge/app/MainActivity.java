package com.huaweichallenge.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.huaweichallenge.app.services.SocketService;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

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
