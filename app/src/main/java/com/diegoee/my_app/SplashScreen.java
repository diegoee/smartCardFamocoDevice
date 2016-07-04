package com.diegoee.my_app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

public class SplashScreen extends Activity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 50;

    ProgressBar progressBar;
    int progressStatus ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        progressStatus = 0;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //finish();

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus = progressStatus + (int) (Math.random()*5+1);
                    progressBar.setProgress(progressStatus);
                    try {
                        Thread.sleep(SPLASH_SCREEN_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Log.v(LOG_TAG,"Progress=" + String.format("%d",progressStatus));
                }

                Intent mainIntent = new Intent().setClass(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);

                Log.v(MainActivity.LOG_TAG,"finish Splash");
                progressStatus = 0;
                finish();
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

}