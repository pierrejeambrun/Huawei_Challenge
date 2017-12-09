package com.huaweichallenge.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaweichallenge.app.services.SensorService;
import com.huaweichallenge.app.services.SocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import static com.huaweichallenge.app.services.SocketService.ACTION_CONNECTION_SOCKET;
import static com.huaweichallenge.app.services.SocketService.ACTION_SEND_MESSAGE;
import static com.huaweichallenge.app.services.SocketService.EXTRA_PARAM;

public class SensorActivity extends AppCompatActivity {

    public class SensorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            HashMap<String, Float> sensors = (HashMap<String, Float>) intent.getSerializableExtra("sensorDataMap");
            Intent socketIntent = new Intent(context, SocketService.class);
            socketIntent.setAction(ACTION_SEND_MESSAGE);
            socketIntent.putExtra(EXTRA_PARAM, sensors);
            context.startService(socketIntent);
        }
    }

    public class SocketReceiver extends BroadcastReceiver {
        public static final String GET_ACTIVITY = "GET_ACTIVITY";
        @Override
        public void onReceive(Context context, Intent intent) {
            String sensors =  intent.getStringExtra("activityResponse");

            JSONObject jsonObject;
            String uri = "";
            try {
                jsonObject = new JSONObject(sensors);
                int label = (int) jsonObject.get("data");

                handleCounter(label);

                switch (label) {
                    case 1:
                        uri = "@drawable/sit";
                        break;
                    case 2:
                        uri = "@drawable/walk";
                        break;
                    case 3:
                        uri = "@drawable/run";
                        break;
                    case 4:
                        uri = "@drawable/bike";
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());

            Drawable res = getResources().getDrawable(imageResource);
            mImageView.setImageDrawable(res);
        }
    }

    private int[] counter;

    private ImageView mImageView;
    private TextView mTextView;
    private SensorReceiver sensorReceiver;
    private SocketReceiver socketReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SocketService.startConnectWebSocket(this);

        //SensorService.startActionGetSensorValues(this);

        sensorReceiver = new SensorReceiver();
        socketReceiver = new SocketReceiver();

        counter = new int[4];
        Arrays.fill(counter, 0);

        mImageView = (ImageView) findViewById(R.id.imageView2);
        mTextView = (TextView) findViewById(R.id.textView4);

        Intent sensorIntent = new Intent(this, SensorService.class);
        startService(sensorIntent);

        Intent socketIntent = new Intent(this, SocketService.class);
        socketIntent.setAction(ACTION_CONNECTION_SOCKET);
        startService(socketIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the receiver
        IntentFilter filter = new IntentFilter(SensorService.SEND_DATA);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(sensorReceiver, filter);

        IntentFilter filter2 = new IntentFilter(SocketReceiver.GET_ACTIVITY);
        filter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(socketReceiver, filter2);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(socketReceiver);
        unregisterReceiver(sensorReceiver);
    }

    protected void handleCounter(int label) {
        int prev = counter[label-1] +1;
        Arrays.fill(counter, 0);
        counter[label-1] = prev;

        if (counter[label - 1] >= Constants.TOO_MUCH_COUNT && label == 1 ) { //STILL
            mTextView.setText(getString(R.string.toomuch_still));
        } else if (counter[label - 1] >= Constants.TOO_MUCH_COUNT && label == 2 ) { //WALK
            mTextView.setText(getString(R.string.toomuch_walk));
        } else if (counter[label - 1] >= Constants.TOO_MUCH_COUNT && label == 3 ) { //RUN
            mTextView.setText(getString(R.string.toomuch_run));
        } else if (counter[label - 1] >= Constants.TOO_MUCH_COUNT && label == 4 ) { //BIKE
            mTextView.setText(getString(R.string.toomuch_bike));
        } else {
            mTextView.setText("");
        }
    }
}
