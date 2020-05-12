package com.faceunity.pta_art.entity;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 撤销模型
 */
public class RecordEditBean {
    private int type;
    private String bundleName;
    private double bundleValue;
    private String colorName;
    private double colorValus;
    //捏脸数据
    private LinkedHashMap<String, Float> mList;

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

    public LinkedHashMap<String, Float> getList() {
        return mList;
    }

    public void setList(LinkedHashMap<String, Float> mList) {
        this.mList = new LinkedHashMap<>();
        for (Iterator iterator = mList.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            entry.setValue(getValue(mList.get(entry.getKey())));
            this.mList.put(entry.getKey(), entry.getValue());
        }
    }

    public float getValue(Object v) {
        if (v == null || (float) v < 0 || ((Float) v).isNaN()) {
            return 0.0f;
        } else {
            return (float) v;
        }
    }
}
