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
import android.view.View;
import android.widget.EditText;

import com.huaweichallenge.app.services.RegisterService;

public class RegisterActivity extends AppCompatActivity {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{4,8}$";
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        receiver = new RegisterReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter("REGISTER");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, intentFilter);
    }

    public void registerButtonClicked(View view) {
        boolean wrongInput = false;

        String username = getStringFromEditText((EditText) findViewById(R.id.username));
        String mail = getStringFromEditText((EditText) findViewById(R.id.email_address));
        String address = getStringFromEditText((EditText) findViewById(R.id.address));
        String sex = getStringFromEditText((EditText) findViewById(R.id.sex));
        String password = getStringFromEditText((EditText) findViewById(R.id.password));
        String confirmPassword = getStringFromEditText((EditText) findViewById(R.id.confirm_password));

        if (mail.isEmpty() || !mail.matches(EMAIL_REGEX)) {
            ((EditText) findViewById(R.id.email_address)).setError("Enter a valid email address.");
            wrongInput = true;
        }

        if (password.isEmpty() || !password.matches(PASSWORD_REGEX)) {
            ((EditText) findViewById(R.id.password)).setError("Password must contain one upper case letter and one digit.");
            wrongInput = true;
        } else if (password.isEmpty() || !password.equals(confirmPassword)) {
            ((EditText) findViewById(R.id.password)).setError("Passwords did not match.");
            wrongInput = true;
        }

        if (!wrongInput) {
            RegisterService.startActionRegister(this,
                    username,
                    mail,
                    address,
                    sex,
                    password);
        }
    }

    private String getStringFromEditText(EditText editText) {
        return editText.getText().toString();
    }

    public class RegisterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            boolean success = bundle.getBoolean("registerSuccess");
            if (success) {
                setContentView(R.layout.activity_login);
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        }
    }

}
