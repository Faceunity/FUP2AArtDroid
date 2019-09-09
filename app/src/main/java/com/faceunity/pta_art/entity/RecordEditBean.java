package com.faceunity.pta_art.entity;

public class RecordEditBean {
    private int type;
    private String bundleName;
    private double bundleValue;
    private String colorName;
    private double colorValus;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public double getBundleValue() {
        return bundleValue;
    }

    public void setBundleValue(double bundleValue) {
        this.bundleValue = bundleValue;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public double getColorValus() {
        return colorValus;
    }

    public void setColorValus(double colorValus) {
        this.colorValus = colorValus;
    }
}
