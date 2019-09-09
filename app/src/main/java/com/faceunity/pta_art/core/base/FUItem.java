package com.faceunity.pta_art.core.base;

/**
 * 道具封装
 * Created by tujh on 2018/12/17.
 */
public class FUItem {

    public String name = "";//道具名（bundle名）
    public int handle;//道具句柄

    public FUItem() {
    }

    public FUItem(String name, int handle) {
        this.name = name;
        this.handle = handle;
    }

    public void clear() {
        name = "";
        handle = 0;
    }
}
