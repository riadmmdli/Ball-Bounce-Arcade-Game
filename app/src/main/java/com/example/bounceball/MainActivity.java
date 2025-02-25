package com.example.bounceball;


import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private int[] colors = {
            Color.rgb(139, 0, 0),    // Dark Red
            Color.rgb(0, 0, 139),    // Dark Blue
            Color.rgb(0, 100, 0),    // Dark Green
            Color.rgb(204, 204, 0),  // Dark Yellow
            Color.rgb(0, 139, 139)   // Dark Cyan
    };
    private int currentColorIndex = 0;
    private SharedPreferences sharedPreferences;
    private ImageButton btnChangeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        currentColorIndex = sharedPreferences.getInt("ballColorIndex", 0); // Load saved color

        ImageButton startButton = findViewById(R.id.startButton);
        btnChangeColor = findViewById(R.id.btnChangeColor);

        // Set initial color of the button
        btnChangeColor.setBackgroundColor(colors[currentColorIndex]);

        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        startButton.startAnimation(pulseAnimation);



        // Handle color change button click
        btnChangeColor.setOnClickListener(view -> {
            currentColorIndex = (currentColorIndex + 1) % colors.length; // Cycle colors
            sharedPreferences.edit().putInt("ballColorIndex", currentColorIndex).apply(); // Save choice
            btnChangeColor.setBackgroundColor(colors[currentColorIndex]); // Update button color
        });

        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });


    }

}