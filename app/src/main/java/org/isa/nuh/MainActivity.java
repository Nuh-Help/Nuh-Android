package org.isa.nuh;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";

    BottomNavigationView navigation;
    FragmentManager manager;
    MapFragment mMapFragment;
    NeedHelpFragment mNeedHelpFragment;
    GiveHelpFragment mGiveHelpFragment;
    int showingFragmentID;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> setShowingFragment(item.getItemId());

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, navigation.getSelectedItemId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_logged_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.logout_menu:
                // TODO: logout logic
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
}
