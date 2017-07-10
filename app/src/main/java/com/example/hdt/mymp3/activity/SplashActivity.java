package com.example.hdt.mymp3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.hdt.mymp3.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_txt);
        findViewById(R.id.txt_music_des).startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.anim_img);
        findViewById(R.id.img_splash).startAnimation(animation);
    }
}