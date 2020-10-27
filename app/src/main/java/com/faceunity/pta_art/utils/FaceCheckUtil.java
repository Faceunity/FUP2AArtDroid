package com.faceunity.pta_art.utils;

/**
 * Created by tujh on 2018/10/24.
 */
public abstract class FaceCheckUtil {

    public static boolean checkRotation(float[] rotations) {
        double x = rotations[0];
        double y = rotations[1];
        double z = rotations[2];
        double w = rotations[3];
        double yaw = Math.atan2(2 * (w * z + y * z), 1 - 2 * (x * x + y * y)) / Math.PI * 180;
        double pitch = Math.asin(2 * (w * y - z * x)) / Math.PI * 180;
        double roll = Math.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z)) / Math.PI * 180;
        return yaw > 30 || yaw < -30 || pitch > 15 || pitch < -15;
    }

    public static boolean checkFaceRect(float[] faceRect, int width, int height) {
        float centerX = (faceRect[0] + faceRect[2]) / 2;
        float centerY = (faceRect[1] + faceRect[3]) / 2;
        return centerX < width * 0.25 || centerX > width * 0.55 ||
                centerY < height * 0.35 || centerY > height * 0.65;
    }

    public static boolean checkExpression(float[] expressionData) {
        for (float e : expressionData) {
            if (e > 0.8) {
                return true;
            }
        }
        return false;
    }

}
