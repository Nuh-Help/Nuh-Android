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
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
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
 * Activity for logging in.
 * @see RegistrationActivity for registration of new accounts
 * @author Hamza Muric
 */
public class LoginActivity extends AppCompatActivity implements SPController {

    /*
     * Package-private class fields for referencing views inside activity.
     */
    TextView toRegisterText;
    Button loginBtn;
    TextInputEditText usernameText;
    TextInputEditText passwordText;


    /**
     * Called when view is getting created.
     * Initializes view references.
     * @param savedInstanceState saved previous state of views in activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.username_login);
        passwordText = findViewById(R.id.password_login);

        loginBtn = findViewById(R.id.button_login);
        loginBtn.setOnClickListener(view -> loginRequest());

        toRegisterText = findViewById(R.id.register_text);
        toRegisterText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /*
     *  Called when loginBtn is clicked.
     *  Gets data from text fields and makes login request to server.
     */
    private void loginRequest() {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        boolean usernameEmpty = TextUtils.isEmpty(username);
        boolean passwordEmpty = TextUtils.isEmpty(password);

        if (usernameEmpty) {
            usernameText.setError("username empty");
        }

        if (passwordEmpty) {
            passwordText.setError("password empty");
        }

        if (usernameEmpty || passwordEmpty) {
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    SharedPreferences.Editor editor = getSharedPreferences(LOGIN, MODE_PRIVATE).edit();
                    editor.putBoolean(IS_LOGGED_IN, true);
                    editor.putString(USERNAME, username);
                    editor.putString(PASSWORD, password);
                    editor.apply();
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
