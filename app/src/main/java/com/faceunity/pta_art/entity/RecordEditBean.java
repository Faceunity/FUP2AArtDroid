package com.faceunity.pta_art.entity;

import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
    /**
     * 美妆相关
     */
    private boolean isSel;//美妆是否选择
    private Map<Integer, PairBean> pairBeanMap;//存储当前美妆选中的列表
    //捏脸数据
    private LinkedHashMap<String, Float> mList;
    /**
     * 是否需要立即执行下一步操作
     */
    private boolean takeTheNextStep;

    /**
     * 绑定操作，当前撤销的项目可能不止一种
     */
    private RecordEditBean bindOperation;


    public RecordEditBean getBindOperation() {
        return bindOperation;
    }

    public void setBindOperation(RecordEditBean bindOperation) {
        this.bindOperation = bindOperation;
    }

    public boolean isTakeTheNextStep() {
        return takeTheNextStep;
    }

    public void setTakeTheNextStep(boolean takeTheNextStep) {
        this.takeTheNextStep = takeTheNextStep;
    }

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

    public boolean isSel() {
        return isSel;
    }

    public void setSel(boolean sel) {
        isSel = sel;
    }

    public void setPairBeanMap(Map<Integer, PairBean> pairBeanMap) {
        this.pairBeanMap = new HashMap<>();
        for (Integer key : pairBeanMap.keySet()) {
            PairBean pairBean = new PairBean(pairBeanMap.get(key).getFrontLength(),
                                             pairBeanMap.get(key).getSelectItemPos(), pairBeanMap.get(key).getSelectColorPos());
            this.pairBeanMap.put(key, pairBean);
        }
    }

    public Map<Integer, PairBean> getPairBeanMap() {
        return pairBeanMap;
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
