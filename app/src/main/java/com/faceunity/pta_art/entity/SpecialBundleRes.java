package com.faceunity.pta_art.entity;

/**
 * Created by hyj on 2020-05-18.
 * 特殊道具资源（美妆、配饰）
 */
public class SpecialBundleRes extends FURes {
    /**
     * type:     美妆类型、配饰道具类型
     * path:     道具路径
     * name:     道具名称
     * hasColor: 是否有颜色
     */
    public int type;
    public String path;
    public String name;
    public boolean hasColor;

    public SpecialBundleRes(int resId,int type, String path) {
        this.resId = resId;
        this.type = type;
        this.path = path;
    }

    public SpecialBundleRes(int resId, int type, String path, String name) {
        this(resId, type, path, name, true);
    }

    public SpecialBundleRes(int resId, int type, String path, String name, boolean hasColor) {
        this.resId = resId;
        this.type = type;
        this.path = path;
        this.name = name;
        this.hasColor = hasColor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasColor() {
        return hasColor;
    }

    public void setHasColor(boolean hasColor) {
        this.hasColor = hasColor;
    }
}
