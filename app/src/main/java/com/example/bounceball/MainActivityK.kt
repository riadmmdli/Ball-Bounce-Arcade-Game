package com.example.bounceball

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class   MainActivityK : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: ImageButton = findViewById(R.id.startButton)

        // Load and apply animation
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        startButton.startAnimation(pulseAnimation)

        startButton.setOnClickListener {

            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}
