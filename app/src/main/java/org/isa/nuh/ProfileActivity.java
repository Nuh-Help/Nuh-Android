package org.isa.nuh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Activity for displaying profile info and providing password changing functionality.
 * (Currently empty)
 * @author Hamza Muric
 */
public class ProfileActivity extends AppCompatActivity {

    /**
     * Called when view is getting created.
     * Initializes view references.
     * @param savedInstanceState saved previous state of views in activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
