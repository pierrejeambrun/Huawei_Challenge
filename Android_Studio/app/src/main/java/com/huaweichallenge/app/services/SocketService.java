package com.huaweichallenge.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.huaweichallenge.app.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


public class SocketService extends IntentService {

    private static WebSocketClient mWebSocketClient;

    private static final String ACTION_CONNECTION_SOCKET = "CONNECTION_SOCKET";
    private static final String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    private static final String ACTION_CLOSE_SOCKET = "CLOSE_SOCKET";


    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "PARAM1";

    public SocketService() {
        super("SocketService");
    }

    public static void startConnectWebSocket(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        intent.setAction(ACTION_CONNECTION_SOCKET);
        context.startService(intent);
    }

    public static void startSendMessage(Context context, String param1) {
        Intent intent = new Intent(context, SocketService.class);
        intent.setAction(ACTION_SEND_MESSAGE);
        intent.putExtra(EXTRA_PARAM1, param1);
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
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleConnectWebSocket();
            } else if (ACTION_SEND_MESSAGE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleSendMessage(param1);
            } else if (ACTION_CLOSE_SOCKET.equals(action)) {
                handleCloseSocket();
            }
        }
    }

    private void handleConnectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://10.0.2.2:8888");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.w("Socket","Received " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void handleSendMessage(String param1) {

        Log.i("Websocket", "J'ENVOIE");
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("Coucou", "Je suis al");
            jsonBody.put("Toto", "Titi");

        } catch (org.json.JSONException e) {
            Log.e("JSON", "Unable to create JSON.");
        }

        mWebSocketClient.send(jsonBody.toString());
    }

    private void handleCloseSocket() {
        mWebSocketClient.close();
    }
}
