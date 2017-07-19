package org.androidtown.schedule;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Created by ohji1 on 2017-07-13.
 */

public class MyCustomSpan implements LineBackgroundSpan
{
    private int color;
    private float radius;
    private int group_person_position;
    public MyCustomSpan() {
    }

    public MyCustomSpan(float radius, int color,int group_person_position) {
        this.color = color;
        this.radius = radius;
        this.group_person_position = group_person_position;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline,
                               int bottom, CharSequence text, int start, int end, int lnum) {


        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }

        int position = group_person_position/2;

        canvas.drawCircle((left + right) / 2-20*position* (int)Math.pow(-1,group_person_position), (float) (bottom + 1.5*radius), radius, paint);
        // x-coordinate , y-coordinate ,   radius ,  The paint used to draw the circle
        paint.setColor(oldColor);
    }
}
