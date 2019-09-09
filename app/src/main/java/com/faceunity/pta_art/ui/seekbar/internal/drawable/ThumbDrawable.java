package com.faceunity.pta_art.ui.seekbar.internal.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.os.SystemClock;
import android.support.annotation.NonNull;


/**
 * <h1>HACK</h1>
 * <p>
 * Special {@link  StateDrawable} implementation
 * to draw the Thumb circle.
 * </p>
 * <p>
 * It's special because it will stop drawing once the state is pressed/focused BUT only after a small delay.
 * </p>
 * <p>
 * This special delay is meant to help avoiding frame glitches while the {@link  com.faceunity.pta_art.ui.seekbar.internal.Marker} is added to the Window
 * </p>
 *
 * @hide
 */
public class ThumbDrawable extends StateDrawable implements Animatable {
    //The current size for this drawable. Must be converted to real DPs
    public static final int DEFAULT_SIZE_DP = 12;
    private final int mSize, mSizeBg;
    private boolean mOpen;
    private boolean mRunning;
    private int bgColor;
    private int color;

    public ThumbDrawable(@NonNull ColorStateList tintStateList, int size) {
        this(tintStateList, size, 0);
    }

    public ThumbDrawable(@NonNull ColorStateList tintStateList, int size, int sizebg) {
        this(tintStateList, 0, size, sizebg, 0);
    }

    public ThumbDrawable(@NonNull ColorStateList tintStateList, int color, int size, int sizebg, int bgColor) {
        super(tintStateList);
        this.color = color;
        mSize = size;
        this.bgColor = bgColor;
        mSizeBg = sizebg;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getIntrinsicWidth() {
        return mSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return mSize;
    }

    @Override
    public void doDraw(Canvas canvas, Paint paint) {
        if (!mOpen) {
            Rect bounds = getBounds();
            float radius = (mSize / 2);
            float radiusBg = (mSizeBg / 2);
            if (radiusBg > 0) {
                paint.setColor(bgColor);
                canvas.drawCircle(bounds.centerX(), bounds.centerY(), radiusBg, paint);
            }
            if (color != 0)
                paint.setColor(color);
            else
                paint.setColor(mCurrentColor);
            int alpha = modulateAlpha(Color.alpha(mCurrentColor));
            paint.setAlpha(alpha);
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, paint);
        }

    }

    public void animateToPressed() {
        scheduleSelf(opener, SystemClock.uptimeMillis() + 100);
        mRunning = true;
    }

    public void animateToNormal() {
        mOpen = false;
        mRunning = false;
        unscheduleSelf(opener);
        invalidateSelf();
    }

    private Runnable opener = new Runnable() {
        @Override
        public void run() {
            mOpen = true;
            invalidateSelf();
            mRunning = false;
        }
    };

    @Override
    public void start() {
        //NOOP
    }

    @Override
    public void stop() {
        animateToNormal();
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }
}
