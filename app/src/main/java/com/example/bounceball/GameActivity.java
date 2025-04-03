package com.example.bounceball;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
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
    private boolean isFirstLaunch = true; // Track first launch

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

        if (isFirstLaunch) {
            isFirstLaunch = false; // Mark as launched
        } else {
            // Show countdown only when resuming (not the first time)
            countdownTextView.setVisibility(View.VISIBLE);
            startCountdown();
        }
    }

    private void startCountdown() {
        final Handler handler = new Handler();
        final int[] countdown = {3}; // Start from 3 seconds

        // Pause the game before the countdown starts
        gameView.pauseGame(); // Pause game-related activities (e.g., ball movement, physics)

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (countdown[0] > 0) {
                    countdownTextView.setText(String.valueOf(countdown[0]));
                    countdownTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100); // Set font size to 40sp
                    countdown[0]--;
                    handler.postDelayed(this, 1000);
                } else {
                    countdownTextView.setVisibility(View.INVISIBLE);
                    gameView.resumeGame(); // Resume the game where it was paused
                }
            }
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

