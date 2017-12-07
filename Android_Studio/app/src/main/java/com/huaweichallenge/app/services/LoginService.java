package com.huaweichallenge.app.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.huaweichallenge.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginService extends IntentService {

    private static final String LOGIN = "LOGIN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    public LoginService() {
        super("LoginService");
    }

    public static void startActionLogin(Context context, String username, String password) {
        Intent intent = new Intent(context, LoginService.class);
        intent.setAction(LOGIN);
        intent.putExtra(USERNAME, username);
        intent.putExtra(PASSWORD, password);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (LOGIN.equals(action)) {
                final String username = intent.getStringExtra(USERNAME);
                final String password = intent.getStringExtra(PASSWORD);
                handleActionLogin(username, password);
            } else if (REFRESH_TOKEN.equals(action)) {
                handleActionRefreshToken();
            }
        }
    }

    private void handleActionRefreshToken() {
        throw new UnsupportedOperationException("Refresh Token action not implemented yet");
    }

    private void handleActionLogin(String username, String password) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = Constants.SERVER_IP + "login.php";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.i(String.format("VOLLEY: Login succeeded, token: %s, user_id: %s", response.getString("token"), response.getInt("user_id")), response.getString("token"));
                        Intent intent = new Intent("LOGIN");
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("success", true);
                        intent.putExtras(bundle);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        sendBroadcast(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Intent intent = new Intent("LOGIN");
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("success", false);
                    intent.putExtras(bundle);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(intent);
                    Log.e("VOLLEY", "Login Failed");
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };
            requestQueue.add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
