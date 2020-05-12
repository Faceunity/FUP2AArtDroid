package com.faceunity.pta_art.utils;

import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by jiangyongxing on 2020/4/13.
 * 描述：
 */
public class SurfaceViewOutlineProvider extends ViewOutlineProvider {

    private float mRadius;

    public SurfaceViewOutlineProvider(float mRadius) {
        this.mRadius = mRadius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int leftMargin = 0;
        int topMargin = 0;
        Rect selfRect = new Rect(leftMargin, topMargin,
                                 rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
        outline.setRoundRect(selfRect, mRadius);
    }
}
