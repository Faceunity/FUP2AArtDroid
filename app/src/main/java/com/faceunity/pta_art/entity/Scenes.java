package com.faceunity.pta_art.entity;

/**
 * Created by tujh on 2018/12/18.
 */
public class Scenes {

    public int resId;
    public BundleRes[] bundles;
    public String bg;
    public String camera;
    public boolean isAnimte;

    public Scenes(int resId, BundleRes[] bundles) {
        this(resId, bundles, "", "", false);
    }

    public Scenes(int resId, BundleRes[] bundles, String bg) {
        this(resId, bundles, bg, "", false);
    }

    public Scenes(int resId, BundleRes[] bundles, String bg, boolean isAnimte) {
        this(resId, bundles, bg, "", isAnimte);
    }

    public Scenes(int resId, BundleRes[] bundles, String bg, String camera, boolean isAnimte) {
        this.resId = resId;
        this.bundles = bundles;
        this.bg = bg;
        this.camera = camera;
        this.isAnimte = isAnimte;
    }
}
