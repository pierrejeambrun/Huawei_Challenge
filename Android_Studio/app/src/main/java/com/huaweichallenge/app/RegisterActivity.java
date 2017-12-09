package com.huaweichallenge.app;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{4,8}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter("REGISTER");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void startButtonClicked(View view) {
        startActivity(new Intent(RegisterActivity.this, SensorActivity.class));
    }

    private String getStringFromEditText(EditText editText) {
        return editText.getText().toString();
    }


}
