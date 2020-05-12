package com.faceunity.pta_art.entity;

/**
 * Created by tujh on 2018/12/18.
 */
public class Scenes {

    public int resId;
    public BundleRes[] bundles;
    public ScenesBg scenesBg;
    public String camera;
    public boolean isAnimte;

    public Scenes(int resId, BundleRes[] bundles) {
        this(resId, bundles, null, "", false);
    }

    public Scenes(int resId, BundleRes[] bundles, ScenesBg bg) {
        this(resId, bundles, bg, "", false);
    }

    public Scenes(int resId, BundleRes[] bundles, ScenesBg bg, boolean isAnimte) {
        this(resId, bundles, bg, "", isAnimte);
    }

    public Scenes(int resId, BundleRes[] bundles, ScenesBg scenesBg, String camera, boolean isAnimte) {
        this.resId = resId;
        this.bundles = bundles;
        this.scenesBg = scenesBg;
        this.camera = camera;
        this.isAnimte = isAnimte;
    }

    public static class ScenesBg {
        public int bg_2d;
        public int bg_3d;
        public int bg_ani;

        public ScenesBg(int bg_2d, int bg_3d, int bg_ani) {
            this.bg_2d = bg_2d;
            this.bg_3d = bg_3d;
            this.bg_ani = bg_ani;
        }

        public ScenesBg clone() {
            return new ScenesBg(this.bg_2d, this.bg_3d, this.bg_ani);
        }

    }
}
