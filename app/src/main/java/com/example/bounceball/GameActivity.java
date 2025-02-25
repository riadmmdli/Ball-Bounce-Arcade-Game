package com.example.bounceball;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {


    private GameView gameView;
    private SharedPreferences prefs;
    private int highScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the color index passed from MainActivity
        int ballColorIndex = getIntent().getIntExtra("ballColorIndex", 0);

        // Initialize SharedPreferences
        prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        highScore = prefs.getInt("high_score", 0); // Load the saved high score

        gameView = new GameView(this , ballColorIndex);
        setContentView(gameView);
    }

    // Method to update high score
    public void updateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;

            // Save the new high score
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("high_score", highScore);
            editor.apply();
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to quit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed(); // Call the default back button behavior
                })
                .setNegativeButton("No", null)
                .show();
    }
}
