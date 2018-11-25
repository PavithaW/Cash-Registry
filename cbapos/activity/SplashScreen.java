package com.cbasolutions.cbapos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.service.AppKillDetector;

/**
 * Created by Dell on 10/12/2017.
 */

public class SplashScreen extends FragmentActivity {

    /// Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        startService(new Intent(getBaseContext(), AppKillDetector.class));
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                String token = loginPreferences.getString("syncId", null);

                if(token != null){

                    Application application = (Application) getApplication();
                    application.login(token,getApplicationContext());

                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(SplashScreen.this, SignInActivity.class);
                    startActivity(i);

                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
