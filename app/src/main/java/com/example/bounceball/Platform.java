package com.example.bounceball;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
public class Platform {
    private int x, y, width, height;
    private Paint paint;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        paint = new Paint();
        paint.setColor(Color.BLUE);
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
        canvas.drawRect(x, y, x + width, y + height, paint);
    }
}
