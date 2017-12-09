package com.huaweichallenge.app.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.huaweichallenge.app.Constants;
import com.huaweichallenge.app.SensorActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


public class SocketService extends Service {

    private static WebSocketClient mWebSocketClient;

    public static final String ACTION_CONNECTION_SOCKET = "CONNECTION_SOCKET";
    public static final String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String ACTION_CLOSE_SOCKET = "CLOSE_SOCKET";


    // TODO: Rename parameters
    public static final String EXTRA_PARAM = "PARAM";

    public SocketService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startCloseSocket(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        intent.setAction(ACTION_CLOSE_SOCKET);
        context.startService(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CONNECTION_SOCKET.equals(action)) {
                handleConnectWebSocket();
            } else if (ACTION_SEND_MESSAGE.equals(action)) {
                final HashMap<String, Float> param = (HashMap<String, Float>) intent.getSerializableExtra(EXTRA_PARAM);
                try {
                    handleSendMessage(param);
                } catch (RuntimeException e) {
                    Log.e("ERREUR SOCKET", "coucou");
                    handleConnectWebSocket();
                }
            } else if (ACTION_CLOSE_SOCKET.equals(action)) {
                handleCloseSocket();
            }
        }
        return START_STICKY;
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

                } catch (JSONException e) {
                    Log.i("WebSocket", "Error parsing response");
                }
                
                broadcastResponse(s);

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

    private void broadcastResponse(String s) {
        Intent bIntent = new Intent();
        bIntent.setAction(SensorActivity.SocketReceiver.GET_ACTIVITY);
        bIntent.putExtra("activityResponse", s); //TODO PARSE THAT
        bIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(bIntent);
    }

    //TODO: Make this work

/*    private void toastMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }*/
}
