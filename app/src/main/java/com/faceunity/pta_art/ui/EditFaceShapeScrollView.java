package com.faceunity.pta_art.ui;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by tujh on 2018/11/9.
 */
public class EditFaceShapeScrollView extends HorizontalScrollView {

    private ScrollViewListener scrollViewListener = null;

    private int screenWidth;
    private boolean isEnableScroll = false;
    private int mScrollX;

    private GestureDetectorCompat mGestureDetector;

    public EditFaceShapeScrollView(Context context) {
        this(context, null);
    }

    public EditFaceShapeScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditFaceShapeScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isEnableScroll) {
                    if (mScrollX + distanceX > screenWidth)
                        scrollBy((int) distanceX, 0);
                    else if (mScrollX > screenWidth) {
                        scrollTo(screenWidth, 0);
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        mScrollX = x;
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return true;
    }

    public void setEnableScroll(boolean enableScroll) {
        isEnableScroll = enableScroll;
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public interface ScrollViewListener {
        void onScrollChanged(EditFaceShapeScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}
