package com.faceunity.pta_art.fragment.editface.core.shape;

import android.graphics.Point;

/**
 * Created by tujh on 2019/3/5.
 */
public class EditFacePoint extends Point {
    private static final String TAG = EditFacePoint.class.getSimpleName();

    public static final int DIRECTION_HORIZONTAL = 0;  // 左右
    public static final int DIRECTION_VERTICAL = 1;   // 上下
    public static final int DIRECTION_ALL = 2;   // 0 && 1

    public final int index;
    public final int direction;

    public final String leftKey;
    public final String rightKey;
    public final String upKey;
    public final String downKey;

    public EditFacePoint(int index, int direction, String leftKey, String rightKey, String upKey, String downKey) {
        this.index = index;
        this.direction = direction;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.downKey = downKey;
    }
}
