    package com.example.bounceball;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.content.Context;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Typeface;
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

        private int[] backgroundColors = {
                Color.rgb(173, 216, 230), // Light Blue
                Color.rgb(144, 238, 144), // Light Green
                Color.rgb(255, 182, 193), // Light Pink
                Color.rgb(224, 255, 255), // Light Cyan
                Color.rgb(255, 250, 205), // Light Yellow
                Color.rgb(255, 218, 185)  // Peach
        };
        private int currentColorIndex = 0;
        private int backgroundColor = backgroundColors[currentColorIndex]; // Initial color


        // Add a constructor with color parameter for the ball
        public GameView(Context context) {
            super(context);
            getHolder().addCallback(this);
            init();
        }

        private void init() {
            scorePaint = new Paint();
            scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            scorePaint.setAntiAlias(true);

            // Set the initial score color based on the first background color
            scorePaint.setColor(getDarkerColor(backgroundColor));

            // Initialize the ball with the passed color
            ball = new Ball(300, 300, 40); // Set ball color from MainActivity
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (gameThread == null) {
                gameThread = new GameThread(getHolder(), this);
                gameThread.setRunning(true);
                gameThread.start();
            }

            // Now, get correct height
            int screenHeight = getHeight();
            int platformY = screenHeight - 200; // Place the platform 200 pixels above bottom
            platform = new Platform(200, platformY, 200, 50, 25);
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
                int prevScore = score;
                ball.update(platform); // Update the ball's position

                // If the ball hits the platform, update the background color every 5 hits
                if (score > prevScore && score % 5 == 0) {
                    changeBackgroundColor();
                }

                // If the ball falls below the screen, end the game
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
                // Set dynamic background color
                canvas.drawColor(backgroundColor);

                // Score text style
                scorePaint.setTextSize(500);
                scorePaint.setFakeBoldText(true);

                String scoreText = "" + score;
                float textWidth = scorePaint.measureText(scoreText);
                float xPos = (getWidth() - textWidth) / 2;  // Center horizontally
                float yPos = getHeight() / 2;  // Center higher

                canvas.drawText(scoreText, xPos, yPos, scorePaint);

                ball.draw(canvas);
                platform.draw(canvas);

                if (isGameOver) {
                    drawGameOver(canvas); // Draw the game over screen
                }
            }
        }

        private void changeBackgroundColor() {
            currentColorIndex = (currentColorIndex + 1) % backgroundColors.length;
            backgroundColor = backgroundColors[currentColorIndex];

            // Update score color to be a darker version of the background
            scorePaint.setColor(getDarkerColor(backgroundColor));
        }


        private int getDarkerColor(int color) {
            return Color.rgb(
                    (int) (Color.red(color) * 0.6),
                    (int) (Color.green(color) * 0.6),
                    (int) (Color.blue(color) * 0.6)
            );
        }

        private void drawGameOver(Canvas canvas) {
            // Create a modern paint style for the "GAME OVER" text
            Paint gameOverTextPaint = new Paint();
            gameOverTextPaint.setColor(Color.WHITE); // White color for contrast
            gameOverTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            gameOverTextPaint.setAntiAlias(true);
            gameOverTextPaint.setShadowLayer(15, 0, 0, Color.RED); // Subtle glow effect

// Calculate elapsed time for animation
            long elapsedTime = System.currentTimeMillis() - animationStartTime;

// Smooth scaling effect (from 1.0x to 2.5x)
            float scaleFactor = Math.min(2.5f, 1.0f + (elapsedTime / 800.0f)); // Faster growth

// Smooth fade-out effect (disappears after 3s)
            float alpha = Math.max(0, 1 - (elapsedTime / 2500.0f)); // Gradual fade-out
            gameOverTextPaint.setAlpha((int) (alpha * 255)); // Set transparency dynamically

// Set base text size (will be scaled dynamically)
            gameOverTextPaint.setTextSize(140);

// Calculate text width dynamically for perfect centering
            String gameOverText = "GAME OVER";
            float textWidth = gameOverTextPaint.measureText(gameOverText);
            float xPos = (getWidth() - textWidth) / 2;  // Center horizontally
            float yPos = getHeight() / 2;  // Center vertically

// Apply transformation and draw text
            canvas.save();
            canvas.scale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2); // Centered scaling
            canvas.drawText(gameOverText, xPos, yPos, gameOverTextPaint);
            canvas.restore();

// Stop animation after 3 seconds
            if (elapsedTime > 3000) {
                isAnimating = false;
            }

            // After animation, display the score and high score
            if (!isAnimating) {
                // Create a modern-style paint for the score and high score
                Paint modernTextPaint = new Paint();
                modernTextPaint.setColor(Color.WHITE);
                modernTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                modernTextPaint.setTextSize(150);
                modernTextPaint.setAntiAlias(true); // Smooth edges
                modernTextPaint.setShadowLayer(10, 0, 0, Color.BLACK); // Add a soft glow effect

// Draw the score
                String scoreText = "Score: " + score;
                float scoreWidth = modernTextPaint.measureText(scoreText);
                canvas.drawText(scoreText, (getWidth() - scoreWidth) / 2, getHeight() / 2, modernTextPaint);

// Draw the high score
                modernTextPaint.setTextSize(150); // Slightly smaller
                String highScoreText = "High Score: " + highScore;
                float highScoreWidth = modernTextPaint.measureText(highScoreText);
                canvas.drawText(highScoreText, (getWidth() - highScoreWidth) / 2, getHeight() / 2 + 150, modernTextPaint);

// Create a glowing effect for "Tap to Restart"
                Paint restartPaint = new Paint();
                restartPaint.setColor(Color.CYAN);
                restartPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                restartPaint.setTextSize(150);
                restartPaint.setAntiAlias(true);
                restartPaint.setShadowLayer(15, 0, 0, Color.BLUE); // Strong glow effect

// Draw "Tap to Restart"
                String restartText = "Tap to Restart";
                float restartWidth = restartPaint.measureText(restartText);
                canvas.drawText(restartText, (getWidth() - restartWidth) / 2, getHeight() / 2 + 300, restartPaint);

            }
        }

        private void restartGame() {
            score = 0; // Reset score when restarting
            isGameOver = false;
            ball = new Ball(300, 300, 40 ); // Reset ball position
            platform.setX(getWidth() / 2 - platform.getWidth() / 2); // Center the platform
            ballBouncedThisFrame = false; // Reset bounce flag

        }
    }
