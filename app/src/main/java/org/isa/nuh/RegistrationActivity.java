package org.isa.nuh;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class RegistrationActivity extends AppCompatActivity implements SPController {

    private static final String TAG = "RegistrationActivity";

    TextView toLoginText;
    Button registerBtn;
    TextInputEditText usernameText;
    TextInputEditText passwordText;
    TextInputEditText repeatPasswordText;
    TextInputEditText firstNameText;
    TextInputEditText lastNameText;
    TextInputEditText emailText;

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
                        Log.d(TAG, "loginRequest: cookie value: " + token);
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
