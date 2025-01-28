package com.example.bounceball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private Ball ball;
    private Platform platform;
    private Paint scorePaint;
    public static int score = 0;
    private int highScore = 0;
    private boolean isGameOver = false;
    private boolean ballBouncedThisFrame = false; // Flag to track if the ball bounced this frame

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        init();
    }

    private void init() {
        ball = new Ball(300, 300, 20);

        int screenHeight = getHeight(); // This returns the height of the GameView
        int platformY = screenHeight - 200; // Place the platform 100 pixels above the bottom edge
        platform = new Platform(200, platformY, 200, 30);

        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(50);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (gameThread == null) {
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        }
        init();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface size or format changes if needed
        // Currently unused, but included to satisfy the SurfaceHolder.Callback contract
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                gameThread.setRunning(false);
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            restartGame();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // Get the x-coordinate of the touch
            int newX = (int) event.getX();

            // Ensure platform stays within screen bounds
            int maxX = getWidth() - platform.getWidth();
            platform.setX(Math.max(0, Math.min(newX, maxX))); // Prevent platform from going out of bounds
        }
        return true;
    }

    public void update() {
        if (!isGameOver) {
            ball.update(platform); // Update the ball's position

            // If the ball goes off the screen, end the game
            if (ball.getY() > getHeight()) {
                isGameOver = true;
                highScore = Math.max(highScore, score);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            ball.draw(canvas);
            platform.draw(canvas);

            // Display the current score
            canvas.drawText("" + score, 50, 100, scorePaint);

            if (isGameOver) {
                drawGameOver(canvas); // Draw the game over screen
            }
        }
    }

    private void drawGameOver(Canvas canvas) {
        Paint gameOverPaint = new Paint();
        gameOverPaint.setColor(Color.RED);
        gameOverPaint.setTextSize(80);

        // Display game over message and scores
        canvas.drawText("Game Over!", getWidth() / 2 - 200, getHeight() / 2 - 50, gameOverPaint);
        canvas.drawText("Score: " + score, getWidth() / 2 - 150, getHeight() / 2 + 20, gameOverPaint);
        canvas.drawText("High Score: " + highScore, getWidth() / 2 - 220, getHeight() / 2 + 100, gameOverPaint);
        canvas.drawText("Tap to Restart", getWidth() / 2 - 220, getHeight() / 2 + 180, gameOverPaint);
    }

    private void restartGame() {
        score = 0; // Reset score when restarting
        isGameOver = false;
        ball = new Ball(300, 300, 20); // Reset ball position
        platform.setX(getWidth() / 2 - platform.getWidth() / 2); // Center the platform
        ballBouncedThisFrame = false; // Reset bounce flag
    }
}
