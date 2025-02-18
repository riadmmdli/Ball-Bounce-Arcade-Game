package com.example.bounceball;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
public class Platform {
    private int x, y, width, height;
    private Paint paint;
    private int cornerRadius;

    public Platform(int x, int y, int width, int height , int cornerRadius) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cornerRadius=cornerRadius;
        paint = new Paint();
        paint.setColor(Color.BLACK);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public void setX(int x) {
        this.x = x;
    }
    public int getHeight(){
        return height;
    }

    public void draw(Canvas canvas) {
        RectF rect = new RectF(x, y, x + width, y + height);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
    }
}
