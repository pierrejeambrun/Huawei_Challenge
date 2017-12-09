package com.huaweichallenge.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huaweichallenge.app.services.SocketService;

import static com.huaweichallenge.app.services.SocketService.ACTION_CONNECTION_SOCKET;

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
        Intent socketIntent = new Intent(this, SocketService.class);
        socketIntent.setAction(ACTION_CONNECTION_SOCKET);
        startService(socketIntent);

    }
}
