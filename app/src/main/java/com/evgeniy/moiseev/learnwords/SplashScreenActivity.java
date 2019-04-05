package com.evgeniy.moiseev.learnwords;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3500;
    private TextView textViewT;
    private TextView textViewR;
    private TextView textViewA;
    private TextView textViewI;
    private TextView textViewN;

    private TextView textViewW;
    private TextView textViewO;
    private TextView textViewRb;
    private TextView textViewD;
    private TextView textViewS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        textViewT = findViewById(R.id.textViewT);
        textViewR = findViewById(R.id.textViewR);
        textViewA = findViewById(R.id.textViewA);
        textViewI = findViewById(R.id.textViewI);
        textViewN = findViewById(R.id.textViewN);

        textViewW = findViewById(R.id.textViewW);
        textViewO = findViewById(R.id.textViewO);
        textViewRb = findViewById(R.id.textViewRb);
        textViewD = findViewById(R.id.textViewD);
        textViewS = findViewById(R.id.textViewS);

        ObjectAnimator o1 = ObjectAnimator.ofFloat(textViewT, "translationY", 0.0f).setDuration(3000);
        o1.setInterpolator(new BounceInterpolator());
        o1.start();
        ObjectAnimator o2 = ObjectAnimator.ofFloat(textViewR, "translationY", 0.0f).setDuration(2500);
        o2.setInterpolator(new BounceInterpolator());
        o2.start();
        ObjectAnimator o3 = ObjectAnimator.ofFloat(textViewA, "translationY", 0.0f).setDuration(1700);
        o3.setInterpolator(new BounceInterpolator());
        o3.start();
        ObjectAnimator o4 = ObjectAnimator.ofFloat(textViewI, "translationY", 0.0f).setDuration(2300);
        o4.setInterpolator(new BounceInterpolator());
        o4.start();
        ObjectAnimator o5 = ObjectAnimator.ofFloat(textViewN, "translationY", 0.0f).setDuration(2600);
        o5.setInterpolator(new BounceInterpolator());
        o5.start();

        ObjectAnimator o6 = ObjectAnimator.ofFloat(textViewW, "alpha", 1.0f).setDuration(1000);
        o6.setStartDelay(1300);
        o6.start();
        ObjectAnimator o7 = ObjectAnimator.ofFloat(textViewO, "alpha", 1.0f).setDuration(1000);
        o7.setStartDelay(2500);
        o7.start();
        ObjectAnimator o8 = ObjectAnimator.ofFloat(textViewRb, "alpha", 1.0f).setDuration(1000);
        o8.setStartDelay(2000);
        o8.start();
        ObjectAnimator o9 = ObjectAnimator.ofFloat(textViewD, "alpha", 1.0f).setDuration(1000);
        o9.setStartDelay(2600);
        o9.start();
        ObjectAnimator o10 = ObjectAnimator.ofFloat(textViewS, "alpha", 1.0f).setDuration(1000);
        o10.setStartDelay(1700);
        o10.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
