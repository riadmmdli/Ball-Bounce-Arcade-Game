package com.example.bounceball;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class BouncingBallsView extends View {
    private static final int BALL_COUNT = 100;
    private static final int BALL_RADIUS = 60;
    private static final int BALL_SPEED = 15;
    private static final int COLOR_CHANGE_INTERVAL = 50; // Slower color change (higher = slower)

    private final Paint paint = new Paint();
    private final Ball[] balls = new Ball[BALL_COUNT];
    private final Random random = new Random();
    private int frameCounter = 0;

    public BouncingBallsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBalls();
    }

    private void initBalls() {
        for (int i = 0; i < BALL_COUNT; i++) {
            balls[i] = new Ball(random.nextInt(800), random.nextInt(1500), BALL_SPEED, BALL_SPEED);
            balls[i].color = getRandomColor();
        }
    }

    private int getRandomColor() {
        return 0xFF000000 | random.nextInt(0xFFFFFF);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        frameCounter++;
        if (frameCounter % COLOR_CHANGE_INTERVAL == 0) { // Change color every X frames
            for (Ball ball : balls) {
                ball.color = getRandomColor();
            }
        }

        for (Ball ball : balls) {
            ball.move(getWidth(), getHeight());
            paint.setColor(ball.color);
            canvas.drawOval(new RectF(ball.x, ball.y, ball.x + BALL_RADIUS, ball.y + BALL_RADIUS), paint);
        }

        invalidate(); // Redraw for animation
    }

    private static class Ball {
        float x, y, dx, dy;
        int color;

        Ball(float x, float y, float dx, float dy) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }

        void move(int width, int height) {
            x += dx;
            y += dy;

            if (x <= 0 || x + BALL_RADIUS >= width) dx = -dx;
            if (y <= 0 || y + BALL_RADIUS >= height) dy = -dy;
        }
    }
}