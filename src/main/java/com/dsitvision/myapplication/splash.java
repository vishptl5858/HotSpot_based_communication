package com.dsitvision.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * Created by vishptl on 11/23/2017.
 */

public class splash extends AppCompatActivity {
    ImageView iv;
    private static boolean splashLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iv=(ImageView)findViewById(R.id.splash);

        if (!splashLoaded) {
            setContentView(R.layout.splash);
            int secondsDelayed = 10;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(splash.this, HomeScreen.class));
                    finish();
                }
            }, secondsDelayed * 500);

            splashLoaded = true;
        }
        else {
            Intent goToMainActivity = new Intent(splash.this, HomeScreen.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }



    }
}

