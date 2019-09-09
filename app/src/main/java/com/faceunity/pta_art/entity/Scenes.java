package com.faceunity.pta_art.entity;

/**
 * Created by tujh on 2018/12/18.
 */
public class Scenes {

    public int resId;
    public BundleRes[] bundles;
    public String bg = "";

    public Scenes(int resId, BundleRes[] bundles) {
        this.resId = resId;
        this.bundles = bundles;
    }

    public Scenes(int resId, BundleRes[] bundles, String bg) {
        this.resId = resId;
        this.bundles = bundles;
        this.bg = bg;
    }
}
