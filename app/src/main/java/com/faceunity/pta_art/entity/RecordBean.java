package com.faceunity.pta_art.entity;

/**
 * 捏脸撤销数据体
 */

public class RecordBean {
    private String leftKey, rightKey;
    private String upKey, downKey;

    private float leftValue, rightValue;
    private float upValue, downValue;
    private int direction;

    public String getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(String leftKey) {
        this.leftKey = leftKey;
    }

    public String getRightKey() {
        return rightKey;
    }

    public void setRightKey(String rightKey) {
        this.rightKey = rightKey;
    }

    public String getUpKey() {
        return upKey;
    }

    public void setUpKey(String upKey) {
        this.upKey = upKey;
    }

    public String getDownKey() {
        return downKey;
    }

    public void setDownKey(String downKey) {
        this.downKey = downKey;
    }

    public float getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(float leftValue) {
        this.leftValue = leftValue;
    }

    public float getRightValue() {
        return rightValue;
    }

    public void setRightValue(float rightValue) {
        this.rightValue = rightValue;
    }

    public float getUpValue() {
        return upValue;
    }

    public void setUpValue(float upValue) {
        this.upValue = upValue;
    }

    public float getDownValue() {
        return downValue;
    }

    public void setDownValue(float downValue) {
        this.downValue = downValue;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
