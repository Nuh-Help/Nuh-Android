package org.isa.nuh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Main Activity of the app.
 * Shows {@link MapFragment}, {@link NeedHelpFragment} and {@link GiveHelpFragment}
 * in a {@link BottomNavigationView}.
 * Provides navigation for most (if not all) parts of the app.
 * @author Hamza Muric
 */
public class MainActivity extends AppCompatActivity implements SPController {

    // Key for storing current showing fragment information in savedInstanceState.
    private static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";

    /*
     * Packege-private class fields for view references.
     */
    BottomNavigationView navigation;
    FragmentManager manager;
    MapFragment mMapFragment;
    NeedHelpFragment mNeedHelpFragment;
    GiveHelpFragment mGiveHelpFragment;
    // id of currently showing fragment.
    int showingFragmentID;

    // Listener for BottomNavigationView.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> setShowingFragment(item.getItemId());

    // Sets currently showing fragment ID.
    private boolean setShowingFragment(int id) {
        FragmentTransaction transaction = manager.beginTransaction();

        switch (id) {
            case R.id.navigation_home:
                transaction.replace(R.id.frame, mMapFragment);
                transaction.commit();
                return true;
            case R.id.navigation_need_help:
                transaction.replace(R.id.frame, mNeedHelpFragment);
                transaction.commit();
                return true;
            case R.id.navigation_give_help:
                transaction.replace(R.id.frame, mGiveHelpFragment);
                transaction.commit();
                return true;
        }
        return false;
    }

    /**
     * Called when view is getting created.
     * Initializes view references.
     * @param savedInstanceState saved previous state of views in activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();
        mNeedHelpFragment = new NeedHelpFragment();
        mGiveHelpFragment = new GiveHelpFragment();
        mMapFragment = new MapFragment();

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showingFragmentID = R.id.navigation_home;
        if (savedInstanceState != null) {
            showingFragmentID = savedInstanceState.getInt(CURRENT_FRAGMENT);
        }

        setShowingFragment(showingFragmentID);
        getProfileInfo();
    }

    /**
     * Stores activity's current state.
     * Saves currently showing fragment ID in storage.
     * @param outState Bundle containing current activity state info.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, navigation.getSelectedItemId());
    }

    /**
     * Inflates menu from XML into main menu of the app.
     * @param menu Main menu of the app.
     * @return has menu inflating been successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_logged_in, menu);
        return true;
    }

    /**
     * Called when menu item is clicked.
     * Implements menu interaction logic.
     * Currently has logout and profile options.
     * @param item clicked menu item.
     * @return has logic for clicking item been handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.logout_menu:
                logout();
                intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile_menu:
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Logs out user.
     * Puts false value as IS_LOGGED_IN parameter in SharedPreferences storage
     * and makes logout request to server.
     */
    private void logout() {
        getSharedPreferences(HELP_CATEGORIES, MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences(LOGIN, MODE_PRIVATE).edit().putBoolean(IS_LOGGED_IN, false).apply();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL + "/nuh/logout")
                .build();
        AsyncTask.execute(() -> {
            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Called from {@link NeedHelpFragment} and {@link GiveHelpFragment}.
     * Gets data from storage and makes request to server, updating online profile info.
     */
    void submitChangesToServer() {
        SharedPreferences sp = getSharedPreferences(HELP_CATEGORIES, MODE_PRIVATE);
        boolean needAccomodation = sp.getBoolean(ACCOMODATION_NEED, false);
        boolean needFood = sp.getBoolean(FOOD_NEED, false);
        boolean needClothes = sp.getBoolean(CLOTHES_NEED, false);
        boolean needMedicine = sp.getBoolean(MEDICINE_NEED, false);
        boolean needOther = sp.getBoolean(OTHER_NEED, false);

        String needAccomodationReason = sp.getString(ACCOMODATION_NEED_TEXT, "");
        String needFoodReason = sp.getString(FOOD_NEED_TEXT, "");
        String needClothesReason = sp.getString(CLOTHES_NEED_TEXT, "");
        String needMedicineReason = sp.getString(MEDICINE_NEED_TEXT, "");
        String needOtherReason = sp.getString(OTHER_NEED_TEXT, "");

        boolean giveAccomodation = sp.getBoolean(ACCOMODATION_GIVE, false);
        boolean giveFood = sp.getBoolean(FOOD_GIVE, false);
        boolean giveClothes = sp.getBoolean(CLOTHES_GIVE, false);
        boolean giveMedicine = sp.getBoolean(MEDICINE_GIVE, false);
        boolean giveOther = sp.getBoolean(OTHER_GIVE, false);

        String giveAccomodationReason = sp.getString(ACCOMODATION_GIVE_TEXT, "");
        String giveFoodReason = sp.getString(FOOD_GIVE_TEXT, "");
        String giveClothesReason = sp.getString(CLOTHES_GIVE_TEXT, "");
        String giveMedicineReason = sp.getString(MEDICINE_GIVE_TEXT, "");
        String giveOtherReason = sp.getString(OTHER_GIVE_TEXT, "");

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

                HttpUrl urlGet = HttpUrl.parse(URL + "/nuh/save_checkboxes")
                        .newBuilder()
                        .build();

                Request requestToken = new Request.Builder()
                        .url(urlGet)
                        .build();

                client.newCall(requestToken).execute();
                String token = "";

                List<Cookie> cookies = client.cookieJar().loadForRequest(urlGet);
                for (Cookie cookie : cookies) {
                    if (cookie.name().equals("csrftoken")) {
                        token = cookie.value();
                    }
                }

                HttpUrl url = HttpUrl.parse(URL + "/nuh/save_checkboxes")
                        .newBuilder()
                        .build();

                String queryData = "does_gf=" + boolToIntString(giveFood)
                        + "&does_ga=" + boolToIntString(giveAccomodation)
                        + "&does_gc=" + boolToIntString(giveClothes)
                        + "&does_gm=" + boolToIntString(giveMedicine)
                        + "&does_go=" + boolToIntString(giveOther)
                        + "&does_nf=" + boolToIntString(needFood)
                        + "&does_na=" + boolToIntString(needAccomodation)
                        + "&does_nc=" + boolToIntString(needClothes)
                        + "&does_nm=" + boolToIntString(needMedicine)
                        + "&does_no=" + boolToIntString(needOther)
                        + "&give_food=" + giveFoodReason
                        + "&give_accommodation=" + giveAccomodationReason
                        + "&give_clothes=" + giveClothesReason
                        + "&give_medicine=" + giveMedicineReason
                        + "&give_other=" + giveOtherReason
                        + "&get_food=" + needFoodReason
                        + "&get_accommodation=" + needAccomodationReason
                        + "&get_clothes=" + needClothesReason
                        + "&get_medicine=" + needMedicineReason
                        + "&get_other=" + needOtherReason;



                RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), queryData);
                Request request = new Request.Builder()
                        .header("X-CSRFToken", token)
                        .url(url)
                        .method("POST", requestBody)
//                        .post(requestBody)
                        .build();


                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(this,
                            getResources().getString(R.string.successful_submit),
                            Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    // Method for converting boolean value into 1/0 string.
    private String boolToIntString(boolean bool) {
        return bool ? "1" : "0";
    }

    /**
     * Called on activity start.
     * Gets profile data from server and updates the local storage.
     */
    private void getProfileInfo() {
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

                HttpUrl url = HttpUrl.parse(URL + "/nuh/myprofile")
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

                Request request = new Request.Builder()
                        .header("X-CSRFToken", token)
                        .url(URL + "/nuh/myprofile")
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String JSON = response.body().string();
                    Log.d("JSON", "getProfileInfo: JSON: " + JSON);
                    JSONArray array = new JSONArray(JSON);
                    JSONObject usernameObj = array.getJSONObject(0);
                    String username = usernameObj.getString("username");
                    JSONObject firstNameObj = array.getJSONObject(1);
                    String firstName = firstNameObj.getString("first_name");
                    JSONObject lastNameObj = array.getJSONObject(2);
                    String lastName = lastNameObj.getString("last_name");
                    JSONObject emailObj = array.getJSONObject(3);
                    String email = emailObj.getString("e_mail");

                    JSONObject categoriesObj = array.getJSONObject(4);
                    String needFood = categoriesObj.getString("get_food");
                    String needAccomodation = categoriesObj.getString("get_accommodation");
                    String needClothes = categoriesObj.getString("get_clothes");
                    String needMedicine = categoriesObj.getString("get_medicine");
                    String needOther = categoriesObj.getString("get_other");
                    String giveFood = categoriesObj.getString("give_food");
                    String giveAccomodation = categoriesObj.getString("give_accommodation");
                    String giveClothes = categoriesObj.getString("give_clothes");
                    String giveMedicine = categoriesObj.getString("give_medicine");
                    String giveOther = categoriesObj.getString("give_other");
                    double latitude = categoriesObj.getDouble("latitude");
                    double longitude = categoriesObj.getDouble("longitude");

                    SharedPreferences.Editor editor = getSharedPreferences(HELP_CATEGORIES, MODE_PRIVATE).edit();
                    editor.putString(USERNAME, username);
                    editor.putString(FIRST_NAME, firstName);
                    editor.putString(LAST_NAME, lastName);
                    editor.putString(EMAIL, email);
                    editor.putBoolean(FOOD_NEED, needFood != null);
                    editor.putBoolean(ACCOMODATION_NEED, needAccomodation != null);
                    editor.putBoolean(CLOTHES_NEED, needClothes != null);
                    editor.putBoolean(MEDICINE_NEED, needMedicine != null);
                    editor.putBoolean(OTHER_NEED, needOther != null);
                    editor.putBoolean(FOOD_GIVE, giveFood != null);
                    editor.putBoolean(ACCOMODATION_GIVE, giveAccomodation != null);
                    editor.putBoolean(CLOTHES_GIVE, giveClothes != null);
                    editor.putBoolean(MEDICINE_GIVE, giveMedicine != null);
                    editor.putBoolean(OTHER_GIVE, giveOther != null);
                    editor.putString(FOOD_NEED_TEXT, needFood);
                    editor.putString(ACCOMODATION_NEED_TEXT, needAccomodation);
                    editor.putString(CLOTHES_NEED_TEXT, needClothes);
                    editor.putString(MEDICINE_NEED_TEXT, needMedicine);
                    editor.putString(OTHER_NEED_TEXT, needOther);
                    editor.putString(FOOD_GIVE_TEXT, giveFood);
                    editor.putString(ACCOMODATION_GIVE_TEXT, giveAccomodation);
                    editor.putString(CLOTHES_GIVE_TEXT, giveClothes);
                    editor.putString(MEDICINE_GIVE_TEXT, giveMedicine);
                    editor.putString(OTHER_GIVE_TEXT, giveOther);
                    editor.apply();
                    getSharedPreferences(LOGIN, MODE_PRIVATE)
                            .edit()
                            .putString(LATITUDE, String.valueOf(latitude))
                            .putString(LONGITUDE, String.valueOf(longitude))
                            .apply();

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
