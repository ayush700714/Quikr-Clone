package com.example.quikrclone.onboarding;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.quikrclone.MainActivity;
import com.example.quikrclone.R;


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