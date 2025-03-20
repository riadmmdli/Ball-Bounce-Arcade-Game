package com.example.bounceball;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private SharedPreferences prefs;
    private int highScore = 0;
    private TextView countdownTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get ball color index
        int ballColorIndex = getIntent().getIntExtra("ballColorIndex", 0);

        // Load high score
        prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        highScore = prefs.getInt("high_score", 0);

        // Initialize game view
        gameView = new GameView(this, ballColorIndex);

        // Create a layout to hold both game view and countdown text
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(gameView);

        // Initialize countdown text overlay
        countdownTextView = new TextView(this);
        countdownTextView.setTextSize(50);
        countdownTextView.setVisibility(View.INVISIBLE); // Initially hidden
        countdownTextView.setGravity(Gravity.CENTER);

        // Add countdown to layout
        frameLayout.addView(countdownTextView);

        setContentView(frameLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pauseGame(); // Pause the game when the screen turns off
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Show countdown when resuming
        countdownTextView.setVisibility(View.VISIBLE);
        startCountdown();
    }

    private void startCountdown() {
        final Handler handler = new Handler();
        final int[] countdown = {3}; // Start from 3 seconds

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (countdown[0] > 0) {
                    countdownTextView.setText(String.valueOf(countdown[0]));
                    countdown[0]--;
                    handler.postDelayed(this, 1000);
                } else {
                    countdownTextView.setVisibility(View.INVISIBLE);
                    gameView.resumeGame(); // Resume the game
                }
            }
        });
    }
}
