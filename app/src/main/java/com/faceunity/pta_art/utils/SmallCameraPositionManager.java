package com.faceunity.pta_art.utils;

import com.faceunity.pta_art.FUApplication;
import com.faceunity.pta_art.R;

/**
 * Created by jiangyongxing on 2020/4/10.
 * 描述：用于管理渲染模型的时候，显示相机小窗口的位置
 */
public class SmallCameraPositionManager {

    private int mViewHeight;
    private int mViewWidth;

    private volatile int startX;

    private volatile int startY;

    private volatile int previewCameraHeight;
    private volatile int previewCameraWidth;

    private boolean bodyDrivenScene = false;
    private int bottomMargin;

    public SmallCameraPositionManager(int viewHeight, int viewWidth) {
        this.mViewHeight = viewHeight;
        this.mViewWidth = viewWidth;
    }

    public void updataWH(int viewHeight, int viewWidth) {
        this.mViewHeight = viewHeight;
        this.mViewWidth = viewWidth;

        setBodyDrivenScene(bodyDrivenScene);
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getPreviewCameraHeight() {
        return previewCameraHeight;
    }

    public int getPreviewCameraWidth() {
        return previewCameraWidth;
    }

    public boolean isBodyDrivenScene() {
        return bodyDrivenScene;
    }

    public void setBodyDrivenScene(boolean bodyDrivenScene) {
        this.bodyDrivenScene = bodyDrivenScene;
        if (bodyDrivenScene) {

            previewCameraWidth = FUApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.x180);

            previewCameraHeight = previewCameraWidth * mViewHeight / mViewWidth;

            startX = mViewWidth - FUApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.x20) - previewCameraWidth;
            startY = bottomMargin;
        } else {
            startX = 0;
            startY = mViewHeight * 2 / 3;

            previewCameraHeight = mViewHeight / 3;
            previewCameraWidth = mViewWidth / 3;

        }
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
        startY = bottomMargin;
    }
}
