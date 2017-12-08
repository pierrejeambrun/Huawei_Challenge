package com.huaweichallenge.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
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

import static com.huaweichallenge.app.Constants.REGISTER_URL;

public class RegisterService extends IntentService {

    private static final String REGISTER = "REGISTER";

    private static final String USERNAME = "USERNAME";
    private static final String MAIL = "MAIL";
    private static final String ADDRESS = "ADDRESS";
    private static final String SEX = "SEX";
    private static final String PASSWORD = "PASSWORD";

    public RegisterService() {
        super("RegisterService");
    }

    public static void startActionRegister(Context context,
                                           String username,
                                           String mail,
                                           String address,
                                           String sex,
                                           String password) {
        Intent intent = new Intent(context, RegisterService.class);
        intent.setAction(REGISTER);
        intent.putExtra(USERNAME, username);
        intent.putExtra(MAIL, mail);
        intent.putExtra(ADDRESS, address);
        intent.putExtra(SEX, sex);
        intent.putExtra(PASSWORD, password);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (REGISTER.equals(action)) {
                final String username = intent.getStringExtra(USERNAME);
                final String mail = intent.getStringExtra(MAIL);
                final String address = intent.getStringExtra(ADDRESS);
                final String sex = intent.getStringExtra(SEX);
                final String password = intent.getStringExtra(PASSWORD);
                handleActionRegister(username, mail, address, sex, password);
            }
        }
    }

    private void handleActionRegister(String username,
                                      String mail,
                                      String address,
                                      String sex,
                                      String password) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = Constants.SERVER_IP + REGISTER_URL;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("mail", mail);
            jsonBody.put("address", address);
            jsonBody.put("sex", sex);
            jsonBody.put("password", password);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    sendRegisterBroadCast(true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    sendRegisterBroadCast(false);
                    Log.e("VOLLEY", "Register Failed!");
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

    private void sendRegisterBroadCast(boolean value) {
        Intent intent = new Intent(REGISTER);
        Bundle bundle = new Bundle();
        bundle.putBoolean("registerSuccess", value);
        intent.putExtras(bundle);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(intent);
    }

}
