package com.example.bounceball;


import android.app.AlertDialog;
import android.content.res.Configuration;
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

import java.util.Locale;

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

        // 🔧 FIXED: Initialize sharedPreferences first
        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        // ✅ Load and apply saved language
        String langCode = sharedPreferences.getString("lang", "en");
        setLocale(langCode);

        // ✅ Set layout AFTER setting locale
        setContentView(R.layout.activity_main);

        currentColorIndex = sharedPreferences.getInt("ballColorIndex", 0);

        TextView tapToStart = findViewById(R.id.tapToStart);
        btnChangeColor = findViewById(R.id.btnChangeColor);
        startTapArea = findViewById(R.id.startTapArea);

        btnChangeColor.setBackgroundColor(colors[currentColorIndex]);

        Animation waveAnimation = AnimationUtils.loadAnimation(this, R.anim.wave_anim);
        tapToStart.startAnimation(waveAnimation);

        btnChangeColor.setOnClickListener(view -> {
            currentColorIndex = (currentColorIndex + 1) % colors.length;
            sharedPreferences.edit().putInt("ballColorIndex", currentColorIndex).apply();
            btnChangeColor.setBackgroundColor(colors[currentColorIndex]);
            view.setClickable(true);
        });

        startTapArea.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        ImageButton btnLanguage = findViewById(R.id.btnLanguage);

// Set initial flag icon
        if (langCode.equals("tr")) {
            btnLanguage.setImageResource(R.drawable.flag_tr);
        } else {
            btnLanguage.setImageResource(R.drawable.flag_en);
        }

// Toggle language + icon
        btnLanguage.setOnClickListener(v -> {
            String currentLang = sharedPreferences.getString("lang", "en");
            String newLang = currentLang.equals("tr") ? "en" : "tr";
            sharedPreferences.edit().putString("lang", newLang).apply();
            setLocale(newLang);
            recreate(); // Apply language change

            // Update icon after recreation (optional)


    });



    }
private void setLocale(String langCode) {
    Locale locale = new Locale(langCode);
    Locale.setDefault(locale);

    Configuration config = new Configuration();
    config.setLocale(locale);
    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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