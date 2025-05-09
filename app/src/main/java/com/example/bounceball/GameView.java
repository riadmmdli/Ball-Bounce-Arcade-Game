package com.example.bounceball;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;


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

    private SharedPreferences prefs;


    private boolean isCountingDown = false;
    private int countdownValue = 3;

    private SoundPool soundPool;
    private int bounceSoundId;

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
    public GameView(Context context , int ballColorIndex) {
        super(context);
        getHolder().addCallback(this);
        prefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("high_score", 0); // Load saved high score

        init();
    }

    public void vibrateOnCollision(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) { // Check if device supports vibration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    private void init() {
        scorePaint = new Paint();
        scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        scorePaint.setAntiAlias(true);

        // Set the initial score color based on the first background color
        scorePaint.setColor(getDarkerColor(backgroundColor));

        // Initialize the ball with the passed color
        ball = new Ball(this ,300, 300, 40); // Set ball color from MainActivity

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        bounceSoundId = soundPool.load(getContext(), R.raw.bounce, 1);

    }

    public void playBounceSound() {
        soundPool.play(bounceSoundId, 1, 1, 0, 0, 1);
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
        platform = new Platform(200, platformY, 250, 100, 50);
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

    public void pauseGame() {
        if (gameThread != null) {
            gameThread.setRunning(false); // Stop the thread
        }
    }

    public void resumeGame() {
        isCountingDown = false; // Disable countdown flag
        if (gameThread == null || !gameThread.isRunning()) {
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
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
        if (isCountingDown) {
            return; // Skip game updates during countdown
        }

        if (!isGameOver) {
            int prevScore = score;
            ball.update(platform, this);

            if (score > highScore) {
                // <-- Move here so toast happens immediately
            }

            if (score > prevScore && score % 5 == 0) {
                changeBackgroundColor();
            }

            if (ball.getY() > getHeight()) {
                isGameOver = true;
                updateHighScore(score);  // ✅ Only update high score here
                animationStartTime = System.currentTimeMillis();
                isAnimating = true;
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
            float xPos = (getWidth() - textWidth) / 2;
            float yPos = getHeight() / 2;

            canvas.drawText(scoreText, xPos, yPos, scorePaint);

            ball.draw(canvas);
            platform.draw(canvas);
            drawHighScore(canvas);

            if (isGameOver) {
                drawGameOver(canvas);
            }
            // Draw countdown when resuming
            if (isCountingDown) {
                Paint countdownPaint = new Paint();
                countdownPaint.setColor(Color.RED);
                countdownPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                countdownPaint.setTextSize(200);
                countdownPaint.setAntiAlias(true);

                String countdownText = String.valueOf(countdownValue);
                float countdownWidth = countdownPaint.measureText(countdownText);
                float countdownX = (getWidth() - countdownWidth) / 2;
                float countdownY = getHeight() / 2;

                canvas.drawText(countdownText, countdownX, countdownY, countdownPaint);
            }
        }
    }
    private void drawHighScore(Canvas canvas) {
        Paint highScorePaint = new Paint();
        highScorePaint.setColor(Color.BLACK);  // Use a contrasting color for the high score
        highScorePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        highScorePaint.setTextSize(80);  // Set a reasonable size for the high score text
        highScorePaint.setAntiAlias(true);

        String highScoreText = "\uD83D\uDC51 " + highScore;
        float highScoreWidth = highScorePaint.measureText(highScoreText);
        float highScoreX = (getWidth() - highScoreWidth) / 2; // Center horizontally
        float highScoreY = 100; // Place it at the top of the screen

        canvas.drawText(highScoreText, highScoreX, highScoreY, highScorePaint);
    }

    private void updateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("high_score", highScore);
            editor.apply();

            ((Activity) getContext()).runOnUiThread(() ->
                    Toast.makeText(getContext(), getResources().getString(R.string.new_high_score) + " : " + highScore, Toast.LENGTH_LONG).show()

            );

            vibrateOnCollision(getContext());
        }
    }

    private void changeBackgroundColor() {
        currentColorIndex = (currentColorIndex + 1) % backgroundColors.length;
        backgroundColor = backgroundColors[currentColorIndex];


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

        Paint gameOverTextPaint = new Paint();
        gameOverTextPaint.setColor(Color.WHITE); // White color for contrast
        gameOverTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        gameOverTextPaint.setAntiAlias(true);
        gameOverTextPaint.setShadowLayer(15, 0, 0, Color.RED); // Subtle glow effect


        long elapsedTime = System.currentTimeMillis() - animationStartTime;


        float scaleFactor = Math.min(2.5f, 1.0f + (elapsedTime / 800.0f)); // Faster growth


        float alpha = Math.max(0, 1 - (elapsedTime / 2500.0f)); // Gradual fade-out
        gameOverTextPaint.setAlpha((int) (alpha * 255)); // Set transparency dynamically

        gameOverTextPaint.setTextSize(70);


        String gameOverText = getResources().getString(R.string.game_over);

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
            modernTextPaint.setTextSize(100);
            modernTextPaint.setAntiAlias(true); // Smooth edges
            modernTextPaint.setShadowLayer(10, 0, 0, Color.BLACK); // Add a soft glow effect

// Draw the score
            String scoreText = getResources().getString(R.string.score) + " " + score;

            float scoreWidth = modernTextPaint.measureText(scoreText);
            canvas.drawText(scoreText, (getWidth() - scoreWidth) / 2, getHeight() / 2, modernTextPaint);

// Draw the high score
            modernTextPaint.setTextSize(100); // Slightly smaller
            String highScoreText = getResources().getString(R.string.high_score) + " " + highScore;

            float highScoreWidth = modernTextPaint.measureText(highScoreText);
            canvas.drawText(highScoreText, (getWidth() - highScoreWidth) / 2, getHeight() / 2 + 150, modernTextPaint);

//"Tap to Restart"
            Paint restartPaint = new Paint();
            restartPaint.setColor(Color.CYAN);
            restartPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            restartPaint.setTextSize(100);
            restartPaint.setAntiAlias(true);
            restartPaint.setShadowLayer(15, 0, 0, Color.BLUE);


            String restartText = getResources().getString(R.string.tap_to_restart);

// Use smaller size
            restartPaint.setTextSize(60);  // Adjusted from 100 to 60 or less

            float restartWidth = restartPaint.measureText(restartText);
            canvas.drawText(restartText, (getWidth() - restartWidth) / 2, getHeight() / 2 + 700, restartPaint);

        }
    }

    private void restartGame() {
        score = 0; // Reset score when restarting
        isGameOver = false;
        ball = new Ball(this , 300, 300, 40 ); // Reset ball position
        platform.setX(getWidth() / 2 - platform.getWidth() / 2); // Center the platform


    }
}