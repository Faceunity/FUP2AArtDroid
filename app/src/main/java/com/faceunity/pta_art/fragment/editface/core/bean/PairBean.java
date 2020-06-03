package com.faceunity.pta_art.fragment.editface.core.bean;

/**
 * Created by hyj on 2020-05-15.
 * 存储各个类别的位置信息
 */
public class PairBean {
    private int frontLength;  //当前道具之前的长度
    private int selectItemPos;//选择的道具位置
    private int selectColorPos;//选择的道具颜色位置

    public PairBean(int frontLength, int selectItemPos) {
        this(frontLength, selectItemPos, -1);
    }

    public PairBean(int frontLength, int selectItemPos, int selectColorPos) {
        this.frontLength = frontLength;
        this.selectItemPos = selectItemPos;
        this.selectColorPos = selectColorPos;
    }

    public int getFrontLength() {
        return frontLength;
    }

    public void setFrontLength(int frontLength) {
        this.frontLength = frontLength;
    }

    public int getSelectItemPos() {
        return selectItemPos;
    }

    public void setSelectItemPos(int selectItemPos) {
        this.selectItemPos = selectItemPos;
    }

    public int getSelectColorPos() {
        return selectColorPos;
    }

    public void setSelectColorPos(int selectColorPos) {
        this.selectColorPos = selectColorPos;
    }
}
