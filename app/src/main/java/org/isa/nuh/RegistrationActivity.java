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
 * @see LoginActivity for login with existing accounts.
 * @author Hamza Muric
 */
public class RegistrationActivity extends AppCompatActivity implements SPController {

    /*
     * Package-private class fields for referencing views inside activity.
     */
    TextView toLoginText;
    Button registerBtn;
    TextInputEditText usernameText;
    TextInputEditText passwordText;
    TextInputEditText repeatPasswordText;
    TextInputEditText firstNameText;
    TextInputEditText lastNameText;
    TextInputEditText emailText;

    /**
     * Called when view is getting created.
     * Initializes view references.
     * @param savedInstanceState saved previous state of views in activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameText = findViewById(R.id.username_register);
        passwordText = findViewById(R.id.password_register);
        repeatPasswordText = findViewById(R.id.repeat_password_register);
        firstNameText = findViewById(R.id.firstname_register);
        lastNameText = findViewById(R.id.lastname_register);
        emailText = findViewById(R.id.email_register);

        registerBtn = findViewById(R.id.button_register_registration);
        registerBtn.setOnClickListener(view -> registerRequest());

        toLoginText = findViewById(R.id.login_text);
        toLoginText.setOnClickListener(view -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /*
     *  Called when registerBtn is clicked.
     *  Gets data from text fields and makes registration request to server, making new user.
     */
    private void registerRequest() {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String repeatPassword = repeatPasswordText.getText().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String email = emailText.getText().toString();

        boolean usernameEmpty = TextUtils.isEmpty(username);
        boolean passwordEmpty = TextUtils.isEmpty(password);
        boolean firstNameEmpty = TextUtils.isEmpty(firstName);
        boolean lastNameEmpty = TextUtils.isEmpty(lastName);
        boolean emailEmpty = TextUtils.isEmpty(email);

        if (usernameEmpty) {
            usernameText.setError("username empty");
        }

        if (passwordEmpty) {
            passwordText.setError("password empty");
        }

        if (firstNameEmpty) {
            firstNameText.setError("first name empty");
        }

        if (lastNameEmpty) {
            lastNameText.setError("last name empty");
        }

        if (emailEmpty) {
            emailText.setError("email empty");
        }

        if (!password.equals(repeatPassword)) {
            repeatPasswordText.setError("passwords don't match");
        }

        if (usernameEmpty || passwordEmpty || firstNameEmpty || lastNameEmpty || emailEmpty
                || !password.equals(repeatPassword)) {
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

                HttpUrl url = HttpUrl.parse(URL + "/nuh/register")
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
                        .addFormDataPart("first_name", firstName)
                        .addFormDataPart("last_name", lastName)
                        .addFormDataPart("email", email)
                        .build();

                Request request = new Request.Builder()
                        .header("X-CSRFToken", token)
                        .url(URL + "/nuh/register")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
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
