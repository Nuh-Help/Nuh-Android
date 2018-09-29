/*===========================================================================\
 * Copyright 2018. WANAT                                                     *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 * http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 \==========================================================================*/

package org.isa.nuh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

/**
 * Launching activity.
 * Providing user with welcome screen with some map functionality
 * and Login and Register options.
 * This activity is shown to user only if the user is not logged in.
 * @author Hamza Muric
 */
public class WelcomeActivity extends AppCompatActivity implements SPController {

    /**
     * Called when view is getting created.
     * Initializes view references.
     * @param savedInstanceState saved previous state of views in activity.
     */
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

    /*
     * Logs the user in and updates the local storage with correct login info.
     * It is only called when the user has already logged in,
     * bypassing the welcome screen and navigating the user directly into the MainActivity.
     */
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
