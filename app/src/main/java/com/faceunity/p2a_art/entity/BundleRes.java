package com.faceunity.p2a_art.entity;

/**
 * Created by tujh on 2018/12/17.
 */
public class BundleRes {

    public int resId;
    public String path;
    public int gender;
    public Integer[] labels;
    public boolean isSupport = true;

    public BundleRes(String path) {
        this.path = path;
    }

    public BundleRes(String path, int resId) {
        this.path = path;
        this.resId = resId;
    }

    public BundleRes(int gender, String path) {
        this.path = path;
        this.gender = gender;
    }

    public BundleRes(String path, int resId, Integer[] labels) {
        this.path = path;
        this.resId = resId;
        this.labels = labels;
    }

    public BundleRes(String path, int resId, Integer[] labels, boolean isSupport) {
        this.resId = resId;
        this.path = path;
        this.labels = labels;
        this.isSupport = isSupport;
    }
}
