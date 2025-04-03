package com.example.bounceball;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.widget.TextView;

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

    private View startTapArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        currentColorIndex = sharedPreferences.getInt("ballColorIndex", 0); // Load saved color

        TextView tapToStart = findViewById(R.id.tapToStart);
        btnChangeColor = findViewById(R.id.btnChangeColor);
        startTapArea = findViewById(R.id.startTapArea);

        // Set initial color of the button
        btnChangeColor.setBackgroundColor(colors[currentColorIndex]);

        Animation waveAnimation = AnimationUtils.loadAnimation(this, R.anim.wave_anim);
        tapToStart.startAnimation(waveAnimation);



        // Handle color change button click
        btnChangeColor.setOnClickListener(view -> {
            currentColorIndex = (currentColorIndex + 1) % colors.length; // Cycle colors
            sharedPreferences.edit().putInt("ballColorIndex", currentColorIndex).apply(); // Save choice
            btnChangeColor.setBackgroundColor(colors[currentColorIndex]); // Update button color
            view.setClickable(true);
        });

        startTapArea.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);

        });


    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss(); // Close the dialog
                    super.onBackPressed(); // Now call the default back action
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

}