package com.example.quikrclone;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;


public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent dashboardIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(dashboardIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}