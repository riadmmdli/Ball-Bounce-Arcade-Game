package com.example.bounceball;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class Ball {
    private int x, y, radius;
    private int dx = 15, dy = 10;
    private Paint paint;

    private SharedPreferences sharedPreferences;

    private GameView gameView;
    public Ball(GameView gameView, int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        paint = new Paint();

        // Load saved color
        sharedPreferences = gameView.getContext().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int savedColorIndex = sharedPreferences.getInt("ballColorIndex", 0);
        int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN};
        paint.setColor(colors[savedColorIndex]);

    }

    public void update(Platform platform , GameView gameView) {
        // Temporarily store the ball's next position
        int nextX = x + dx;
        int nextY = y + dy;

        // Check if the ball's next position is colliding with the platform
        if (nextY + radius >= platform.getY() && nextY - radius <= platform.getY() + platform.getHeight() &&
                nextX >= platform.getX() && nextX <= platform.getX() + platform.getWidth()) {
            // Correct the ball's Y position to just above the platform
            y = platform.getY() - radius;

            // If collision is detected, handle bounce
            bounce();

            GameView.score++;

            // 🛑 **Trigger Vibration on Collision**
            if (gameView != null) {
                gameView.vibrateOnCollision(gameView.getContext());
            }
        } else {
            // If no collision, update the position normally
            x = nextX;
            y = nextY;
        }


        // 🛑 **Fix for Ball Sticking to Walls**
        if (x - radius <= 0) {  // Left wall collision
            x = radius; // Push away from wall
            dx = Math.abs(dx); // Move right
            gameView.playBounceSound();
        } else if (x + radius >= 1080) {  // Right wall collision
            x = 1080 - radius; // Push away from wall
            dx = -Math.abs(dx); // Move left
            gameView.playBounceSound();
        }

        // Bounce off the top edge of the screen
        if (y - radius <= 0) {
            y = radius; // Push away from the top edge
            dy = -dy;
            gameView.playBounceSound();
        }




    }


    public void bounce() {
        dy = -dy - 1; // Increase speed slightly on bounce
    }

    public boolean collidesWith(Platform platform) {
        return y + radius >= platform.getY() && y - radius <= platform.getY() + platform.getHeight() &&
                x >= platform.getX() && x <= platform.getX() + platform.getWidth();
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    // Getter for Y coordinate
    public int getY() {
        return y;
    }

    // Getter for X coordinate
    public int getX() {
        return x;
    }

    // Getter for radius
    public int getRadius() {
        return radius;
    }

    // Optional setters if needed
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Method to change the ball color
    public void setColor(int color) {
        paint.setColor(color);  // Update the ball's color
    }
}