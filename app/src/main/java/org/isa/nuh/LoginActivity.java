package org.isa.nuh;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements SPController {

    private static final String TAG = "LoginActivity";

    TextView toRegisterText;
    Button loginBtn;
    TextInputEditText usernameText;
    TextInputEditText passwordText;

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

    private String getCSRF() {
        Log.d(TAG, "getCSRF: started");
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
                        for (Cookie cookie : cookies) {
                            if (cookie.name().equals("X-CSRFToken")) {
                                Log.d(TAG, "getCSRF: loadForRequest: token: " + cookie.value());
                            }
                        }
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();

//        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.13:8000/nuh/index")
                .build();

//        client.cookieJar().loadForRequest("http://192.168.1.13:8000/nuh/index")

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Response successful");
                    String cookieToken = response.header("Set-Cookie");
                    String token = cookieToken.substring(cookieToken.indexOf('=') + 1, cookieToken.indexOf(';'));
                    Log.d(TAG, "onResponse: token: " + token);
                }
            }
        });

        return null;
    }

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

                HttpUrl url = HttpUrl.parse("http://192.168.1.19:8000/nuh/login")
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
                        Log.d(TAG, "loginRequest: cookie value: " + token);
                    }
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username", username)
                        .addFormDataPart("password", password)
                        .build();

                Request request = new Request.Builder()
                        .header("X-CSRFToken", token)
                        .url("http://192.168.1.19:8000/nuh/login")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getSharedPreferences(LOGIN, MODE_PRIVATE).edit().putBoolean(IS_LOGGED_IN, true).apply();
                    startActivity(intent);
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
