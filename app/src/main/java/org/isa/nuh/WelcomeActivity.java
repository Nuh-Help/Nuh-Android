package org.isa.nuh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WelcomeActivity extends AppCompatActivity implements SPController {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        boolean loggedIn = getSharedPreferences(LOGIN, MODE_PRIVATE).getBoolean(IS_LOGGED_IN, false);
        if (loggedIn) {
            login();
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

    private void login() {
        SharedPreferences sp = getSharedPreferences(LOGIN, MODE_PRIVATE);
        String username = sp.getString(USERNAME, null);
        String password = sp.getString(PASSWORD, null);

        if (username == null || password == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }


        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();

        AsyncTask.execute(() -> {
            try {

                HttpUrl url = HttpUrl.parse(URL + "/nuh/login")
                        .newBuilder()
                        .build();

                Request requestToken = new Request.Builder()
                        .url(url)
                        .build();

                client.newCall(requestToken).execute();
                String token = "";

                List<Cookie> cookies = client.cookieJar().loadForRequest(url);
                for (Cookie cookie : cookies) {
                    if (cookie.name().equals("csrftoken")) {
                        token = cookie.value();
                    }
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username", username)
                        .addFormDataPart("password", password)
                        .build();

                Request request = new Request.Builder()
                        .header("X-CSRFToken", token)
                        .url(URL + "/nuh/login")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
