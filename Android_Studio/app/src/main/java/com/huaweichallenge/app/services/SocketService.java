package com.huaweichallenge.app.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huaweichallenge.app.Constants;
import com.huaweichallenge.app.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


public class SocketService extends IntentService {

    private static WebSocketClient mWebSocketClient;

    private static final String ACTION_CONNECTION_SOCKET = "CONNECTION_SOCKET";
    private static final String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    private static final String ACTION_CLOSE_SOCKET = "CLOSE_SOCKET";

    public static final String GET_ACTIVITY = "GET_ACTIVITY";


    // TODO: Rename parameters
    private static final String EXTRA_PARAM = "PARAM";

    public SocketService() {
        super("SocketService");
    }

    public static void startConnectWebSocket(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        intent.setAction(ACTION_CONNECTION_SOCKET);
        context.startService(intent);
    }

    public static void startSendMessage(Context context, HashMap<String, Float> param1) {
        Intent intent = new Intent(context, SocketService.class);
        intent.setAction(ACTION_SEND_MESSAGE);
        intent.putExtra(EXTRA_PARAM, param1);
        context.startService(intent);
    }

    public static void startCloseSocket(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        intent.setAction(ACTION_CLOSE_SOCKET);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CONNECTION_SOCKET.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM);
                handleConnectWebSocket();
            } else if (ACTION_SEND_MESSAGE.equals(action)) {
                final HashMap<String, Float> param = (HashMap<String, Float>) intent.getSerializableExtra(EXTRA_PARAM) ;
                handleSendMessage(param);
            } else if (ACTION_CLOSE_SOCKET.equals(action)) {
                handleCloseSocket();
            }
        }
    }


    private void handleConnectWebSocket() {
        URI uri;

        try {
            uri = new URI(Constants.WS_SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                //toastMessage("Connection established.");
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                try {
                    final JSONObject parsedData = new JSONObject(s);

                    // ON MESSAGE BROADCAST IT
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("activityResponse", s); //TODO PARSE THAT
                    intent.setAction(GET_ACTIVITY);
                    intent.putExtras(bundle);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(intent);


                } catch (JSONException e) {
                    Log.i("WebSocket", "Error parsing response");
                }

                Log.w("Socket","Received " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                //toastMessage("Established Connection.");
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void handleSendMessage(HashMap<String, Float> data) {

        JSONObject jsonBody = new JSONObject(data);

        mWebSocketClient.send(jsonBody.toString());
    }

    private void handleCloseSocket() {
        mWebSocketClient.close();
    }

    //TODO: Make this work

/*    private void toastMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }*/
}
