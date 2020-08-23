package com.example.golddate;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private ImageView goldDateIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        goldDateIcon = findViewById(R.id.golddate_icon_imageView);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(goldDateIcon, "alpha", 1, 0.3f);
        fadeOut.setDuration(2000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(goldDateIcon, "alpha", 0.3f, 1);
        fadeIn.setDuration(2000);

        final AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(fadeIn).after(fadeOut);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorSet.start();
            }
        });
        mAnimatorSet.start();

        new CountDownTimer(4000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }.start();

    }
}
