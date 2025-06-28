package com.example.music_tttaaayyyx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2秒延迟
    private static final String PREF_NAME = "music_app_prefs";
    private static final String KEY_AGREEMENT_ACCEPTED = "agreement_accepted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 检查用户是否已同意协议
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isAgreementAccepted = prefs.getBoolean(KEY_AGREEMENT_ACCEPTED, false);

        // 延迟跳转
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (isAgreementAccepted) {
                    // 已同意协议，直接跳转到主界面
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // 未同意协议，跳转到协议页面
                    intent = new Intent(SplashActivity.this, AgreementActivity.class);
                }
                
                // 添加转场动画
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                    SplashActivity.this, 
                    android.R.anim.fade_in, 
                    android.R.anim.fade_out
                );
                startActivity(intent, options.toBundle());
                finish();
            }
        }, SPLASH_DELAY);
    }
} 