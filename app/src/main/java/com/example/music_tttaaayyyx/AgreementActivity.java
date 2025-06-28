package com.example.music_tttaaayyyx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

public class AgreementActivity extends AppCompatActivity {

    private static final String PREF_NAME = "music_app_prefs";
    private static final String KEY_AGREEMENT_ACCEPTED = "agreement_accepted";
    
    private Button btnAgree;
    private Button btnExit;
    private TextView tvUserAgreement;
    private TextView tvPrivacyAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnAgree = findViewById(R.id.btn_agree);
        btnExit = findViewById(R.id.btn_exit);
        tvUserAgreement = findViewById(R.id.tv_user_agreement);
        tvPrivacyAgreement = findViewById(R.id.tv_privacy_agreement);
    }

    private void setupListeners() {
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存用户同意状态
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                prefs.edit().putBoolean(KEY_AGREEMENT_ACCEPTED, true).apply();
                
                // 用户同意协议，跳转到主界面
                Intent intent = new Intent(AgreementActivity.this, MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                    AgreementActivity.this, 
                    android.R.anim.slide_in_left, 
                    android.R.anim.slide_out_right
                );
                startActivity(intent, options.toBundle());
                finish();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用户退出应用
                finish();
            }
        });

        // 用户协议链接
        tvUserAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mi.com"));
                startActivity(intent);
            }
        });

        // 隐私协议链接
        tvPrivacyAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xiaomiev.com/"));
                startActivity(intent);
            }
        });
    }
} 