package com.example.bounceball;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private boolean isAnimating = false;
    private long animationStartTime;

    private boolean ballBouncedThisFrame = false; // Flag to track if the ball bounced this frame

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        init();
    }

    private void init() {


        ball = new Ball(300, 300, 40);

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
                animationStartTime = System.currentTimeMillis(); // Start animation timing
                isAnimating = true; // Start animation
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // Set background color to grey
            canvas.drawColor(Color.GRAY);  // Set background color to grey

            scorePaint.setTextSize(500);  // Make the font larger
            scorePaint.setColor(Color.YELLOW);  // Change text color to yellow
            scorePaint.setFakeBoldText(true);  // Make the text bold

            String scoreText = "" + score;
            float textWidth = scorePaint.measureText(scoreText);
            float xPos = (getWidth() - textWidth) / 2;  // Center horizontally
            float yPos = getHeight() / 2;  // Center vertically

            canvas.drawText(scoreText, xPos, yPos, scorePaint);

            ball.draw(canvas);
            platform.draw(canvas);

            if (isGameOver) {
                drawGameOver(canvas); // Draw the game over screen
            }
        }
    }

    private void drawGameOver(Canvas canvas) {
        // Display "GAME OVER" text with animation
        Paint gameOverTextPaint = new Paint();
        gameOverTextPaint.setColor(Color.BLACK);
        gameOverTextPaint.setFakeBoldText(true);

        long elapsedTime = System.currentTimeMillis() - animationStartTime;

        if (isAnimating) {
            // Animate "GAME OVER" text: enlarge and fade it out
            float scaleFactor = Math.min(2.0f, 1.0f + (elapsedTime /     1000.0f)); // Max size is double the original size
            float alpha = Math.max(0, 1 - (elapsedTime / 2000.0f)); // Fade out after scaling

            gameOverTextPaint.setAlpha((int) (alpha * 255)); // Set transparency

            // Draw the scaled text in the center of the screen
            String gameOverText = "GAME OVER";
            gameOverTextPaint.setTextSize(120); // Set the base size of the text
            float textWidth = gameOverTextPaint.measureText(gameOverText);
            float xPos = (getWidth() - textWidth) / 2;  // Center horizontally
            float yPos = getHeight() / 4;  // Position vertically a bit higher than center
            canvas.save();
            canvas.scale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 4); // Apply scaling
            canvas.drawText(gameOverText, xPos, yPos, gameOverTextPaint);
            canvas.restore();



            if (elapsedTime > 2500) {
                isAnimating = false;  // Stop animation after 2.5 seconds
            }
        }

        // After animation, display the score and high score
        if (!isAnimating) {
            Paint gameOverPaint = new Paint();
            gameOverPaint.setColor(Color.BLACK);
            gameOverPaint.setTextSize(80);

            // Draw the score
            canvas.drawText("Score: " + score, getWidth() / 2 - 150, getHeight() / 2 + 20, gameOverPaint);

            // Draw the high score
            canvas.drawText("High Score: " + highScore, getWidth() / 2 - 220, getHeight() / 2 + 100, gameOverPaint);

            // Draw the restart message
            canvas.drawText("Tap to Restart", getWidth() / 2 - 220, getHeight() / 2 + 180, gameOverPaint);
        }
    }

    private void restartGame() {
        score = 0; // Reset score when restarting
        isGameOver = false;
        ball = new Ball(300, 300, 40); // Reset ball position
        platform.setX(getWidth() / 2 - platform.getWidth() / 2); // Center the platform
        ballBouncedThisFrame = false; // Reset bounce flag
    }
}
