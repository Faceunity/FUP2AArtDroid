package com.faceunity.pta_art.ui.seekbar.internal.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.NonNull;

/**
 * Simple {@link StateDrawable} implementation
 * to draw rectangles
 *
 * @hide
 */
public class TrackRectDrawable extends StateDrawable {
    private int startColor, endColor;
    private int[] colorArray;
    private LinearGradient backGradient;
    private int width;
    private int height;

    public TrackRectDrawable(@NonNull ColorStateList tintStateList) {
        this(tintStateList, 0, 0);
    }

    public TrackRectDrawable(@NonNull ColorStateList tintStateList, int startColor, int endColor) {
        super(tintStateList);
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }


    @Override
    void doDraw(Canvas canvas, Paint paint) {
        //获取View的宽高
        if (backGradient == null) {
            width = getBounds().right - getBounds().left;
            height = getBounds().bottom - getBounds().top;
            if (colorArray == null) {
                backGradient = new LinearGradient(0, 0, width, 0, new int[]{startColor, endColor}, null, Shader.TileMode.CLAMP);
            } else {
                backGradient = new LinearGradient(0, 0, width, 0, colorArray, null, Shader.TileMode.CLAMP);
            }
            paint.setShader(backGradient);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(height);
        }
        canvas.drawLine(getBounds().left, getBounds().top + height / 2, getBounds().right, getBounds().bottom - height / 2, paint);


//        RectF rectF = new RectF(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom);
//        canvas.drawRoundRect(rectF, height/2, height/2, paint);
    }

    public void setStartColorArray(int[] colorArray) {
        this.colorArray = colorArray;
    }
}
