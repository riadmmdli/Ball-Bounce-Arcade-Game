package com.example.bounceball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
public class Ball {
    private int x, y, radius;
    private int dx = 10, dy = 10;
    private Paint paint;

    public Ball(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public void update(Platform platform) {
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
        } else {
            // If no collision, update the position normally
            x = nextX;
            y = nextY;
        }


        // Bounce off the left and right edges of the screen
        if (x <= 0 || x >= 1080) dx = -dx;

        // Bounce off the top edge of the screen
        if (y <= 0) dy = -dy;

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
}