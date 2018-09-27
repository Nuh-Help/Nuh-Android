package org.isa.nuh;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity implements SPController {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        boolean loggedIn = getSharedPreferences(LOGIN, MODE_PRIVATE).getBoolean(IS_LOGGED_IN, false);
        if (loggedIn) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }

        MapFragment mapFragment = new MapFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map_fragment_welcome, mapFragment);
        transaction.commit();

        findViewById(R.id.login_welcome).setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.register_welcome).setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }
}
