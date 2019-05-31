package com.faceunity.p2a_art.entity;

import android.text.TextUtils;

/**
 * Created by tujh on 2018/12/17.
 */
public class BundleRes extends FURes{

    public String path;
    public int gender;
    public String name;

    public Integer[] labels;
    public boolean isSupport;

    public String[] others;

    private String getNameByPath(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        String[] ret = path.split("/");
        return ret[ret.length - 1];
    }

    public BundleRes(String path) {
        this(AvatarP2A.gender_mid, path, 0, null, true, null);
    }

    public BundleRes(String path, String[] others) {
        this(AvatarP2A.gender_mid, path, 0, null, true, others);
    }

    public BundleRes(String path, int resId) {
        this(AvatarP2A.gender_mid, path, resId, null, true, null);
    }

    public BundleRes(int gender, String path, int resId) {
        this(gender, path, resId, null, true, null);
    }

    public BundleRes(int gender, String path) {
        this(gender, path, 0, null, true, null);
    }

    public BundleRes(String path, int resId, Integer[] labels) {
        this(AvatarP2A.gender_mid, path, resId, labels, true, null);
    }

    public BundleRes(int gender, String path, int resId, Integer[] labels) {
        this(gender, path, resId, labels, true, null);
    }

    public BundleRes(String path, int resId, Integer[] labels, boolean isSupport) {
        this(AvatarP2A.gender_mid, path, resId, labels, isSupport, null);
    }

    public BundleRes(int gender, String path, int resId, Integer[] labels, boolean isSupport) {
        this(gender, path, resId, labels, isSupport, null);
    }

    public BundleRes(int gender, String path, int resId, Integer[] labels, boolean isSupport, String[] others) {
        this.gender = gender;
        this.resId = resId;
        this.path = path;
        this.name = getNameByPath(path);
        this.labels = labels;
        this.isSupport = isSupport;
        this.others = others;
    }
}
