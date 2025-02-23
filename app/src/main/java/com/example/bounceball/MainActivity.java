package com.example.bounceball;


import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton startButton = findViewById(R.id.startButton);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        startButton.startAnimation(pulseAnimation);
        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });


    }

}