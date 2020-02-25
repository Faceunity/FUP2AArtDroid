package com.faceunity.pta_art.entity;

import android.text.TextUtils;

import java.util.Arrays;

/**
 * Created by tujh on 2018/12/17.
 */
public class BundleRes extends FURes {

    public String path;
    public int gender;
    public String name;

    public Integer[] labels;
    public boolean isSupport;
    public int bodyLevel;

    public String[] others;

    private String getNameByPath(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        String[] ret = path.split("/");
        return ret[ret.length - 1];
    }

    public BundleRes(String path) {
        this(AvatarPTA.gender_mid, path, 0, null, true, null);
    }

    public BundleRes(String path, String[] others) {
        this(AvatarPTA.gender_mid, path, 0, null, true, others);
    }

    public BundleRes(String path, int resId) {
        this(AvatarPTA.gender_mid, path, resId, null, true, null);
    }

    public BundleRes(int gender, String path, int resId) {
        this(gender, path, resId, null, true, null);
    }

    public BundleRes(int gender, String path) {
        this(gender, path, 0, null, true, null);
    }

    public BundleRes(int gender, String path, String[] others) {
        this(gender, path, 0, null, true, others);
    }

    public BundleRes(String path, int resId, Integer[] labels) {
        this(AvatarPTA.gender_mid, path, resId, labels, true, null);
    }

    public BundleRes(int gender, String path, int resId, Integer[] labels) {
        this(gender, path, resId, labels, true, null);
    }

    public BundleRes(String path, int resId, Integer[] labels, boolean isSupport) {
        this(AvatarPTA.gender_mid, path, resId, labels, isSupport, null);
    }

    public BundleRes(int gender, String path, int resId, Integer[] labels, boolean isSupport) {
        this(gender, path, resId, labels, isSupport, null);
    }


    public BundleRes(int gender, String path, int resId, Integer[] labels, boolean isSupport, String[] others) {
        this(gender, path, resId, labels, isSupport, 0, others);
    }

    public BundleRes(int gender, String path, int resId, Integer[] labels, boolean isSupport, int bodyLevel) {
        this(gender, path, resId, labels, isSupport, bodyLevel, null);
    }

    public BundleRes(int gender, String path, int resId, Integer[] labels, boolean isSupport, int bodyLevel, String[] others) {
        this.gender = gender;
        this.resId = resId;
        this.path = path;
        this.name = getNameByPath(path);
        this.labels = labels;
        this.isSupport = isSupport;
        this.bodyLevel = bodyLevel;
        this.others = others;
    }

    @Override
    public String toString() {
        return "BundleRes{" +
                "path='" + path + '\'' +
                ", gender=" + gender +
                ", name='" + name + '\'' +
                ", labels=" + Arrays.toString(labels) +
                ", isSupport=" + isSupport +
                ", others=" + Arrays.toString(others) +
                '}';
    }
}
