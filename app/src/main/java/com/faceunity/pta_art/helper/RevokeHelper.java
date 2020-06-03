package com.faceunity.pta_art.helper;

import android.util.Log;

import com.faceunity.pta_art.entity.RecordEditBean;
import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 撤销操作辅助类
 */
public class RevokeHelper {
    private Stack<RecordEditBean> recordBackStack;
    private Stack<RecordEditBean> recordGoHeadStack;
    private volatile static RevokeHelper helper = null;
    private WeakReference<RevokeHelperListener> listener;

    public RevokeHelper() {
        recordBackStack = new Stack<>();
        recordGoHeadStack = new Stack<>();
    }

    public static RevokeHelper getInstance() {
        if (helper == null) {
            synchronized (RevokeHelper.class) {
                if (helper == null) {
                    helper = new RevokeHelper();
                }
            }
        }
        return helper;
    }

    public void setListener(RevokeHelperListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    /**
     * 记录操作的步骤
     *
     * @param type
     * @param bundleName
     * @param bundleValue
     * @param colorName
     * @param colorValue
     */
    public RecordEditBean record(int type, String bundleName, double bundleValue,
                                 String colorName, double colorValue) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordEditBean.setColorName(colorName);
        recordEditBean.setColorValus(colorValue);
        recordBackStack.push(recordEditBean);
        playLog(recordEditBean, "record:");
        return recordEditBean;
    }

    /**
     * 记录操作的步骤
     *
     * @param type
     * @param bundleName
     * @param bundleValue
     * @param colorName
     * @param colorValue
     */
    public RecordEditBean recordWithDontPush(int type, String bundleName, double bundleValue,
                                             String colorName, double colorValue) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordEditBean.setColorName(colorName);
        recordEditBean.setColorValus(colorValue);
        playLog(recordEditBean, "record:");
        return recordEditBean;
    }

    /**
     * @param type
     * @param bundleName
     * @param bundleValue
     * @param isSel       美妆是否选择
     * @param colorName
     * @param colorValue
     */
    public RecordEditBean record(int type, String bundleName, double bundleValue, boolean isSel,
                                 String colorName, double colorValue) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordEditBean.setSel(isSel);
        recordEditBean.setColorName(colorName);
        recordEditBean.setColorValus(colorValue);
        recordBackStack.push(recordEditBean);
        playLog(recordEditBean, "record:");
        return recordEditBean;
    }


    public RecordEditBean recordWithDontPush(int type, String bundleName, double bundleValue, boolean isSel,
                                             String colorName, double colorValue) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordEditBean.setSel(isSel);
        recordEditBean.setColorName(colorName);
        recordEditBean.setColorValus(colorValue);
        playLog(recordEditBean, "record:");
        return recordEditBean;
    }

    /**
     * @param type
     * @param bundleName
     * @param bundleValue
     * @param isSel       美妆是否选择
     * @param pairBeanMap 当前美妆道具选择的列表
     */
    public void record(int type, String bundleName, double bundleValue, boolean isSel,
                       Map<Integer, PairBean> pairBeanMap) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordEditBean.setSel(isSel);
        recordEditBean.setPairBeanMap(pairBeanMap);
        recordEditBean.setColorName("");
        recordBackStack.push(recordEditBean);
        playLog(recordEditBean, "record:");
    }

    /**
     * 记录操作的步骤
     *
     * @param type
     * @param bundleName
     * @param bundleValue
     * @param colorName
     * @param colorValue
     * @param mMap        当前的捏脸系数
     */
    public void record(int type, String bundleName, double bundleValue,
                       String colorName, double colorValue, LinkedHashMap<String, Float> mMap) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordEditBean.setColorName(colorName);
        recordEditBean.setColorValus(colorValue);
        recordEditBean.setList(mMap);
        recordBackStack.push(recordEditBean);
        playLog(recordEditBean, "record:");
    }

    /**
     * 记录捏脸
     *
     * @param type
     * @param mMap
     */
    public void record(int type, LinkedHashMap<String, Float> mMap, String bundleName, int bundleValue) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setList(mMap);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordBackStack.push(recordEditBean);
    }

    /**
     * 撤销
     */
    public void revokeLast() {
        if (recordBackStack.size() < 1)
            return;
        operateRevoke(true);
    }

    public boolean getRecordBackStackIsEmpty() {
        return recordBackStack.size() < 1;
    }

    /**
     * 取消撤销
     */
    public void goAheadLast() {
        if (recordGoHeadStack.size() < 1)
            return;
        operateRevoke(false);
    }

    /**
     * 撤销操作
     *
     * @param isRevoke         是否为回退操作  true 为回退操作  false为取消回退操作
     * @param automaticTrigger 是否为代码触发   true为代码takeTheNextStep字段触发  false为用户手动点击触发
     */
    private void operateRevoke(boolean isRevoke, boolean automaticTrigger) {
        RecordEditBean goAheadBean = new RecordEditBean();//当前数据
        RecordEditBean recordBean = isRevoke ? recordBackStack.peek() : recordGoHeadStack.peek();
        playLog(recordBean, "operateRevoke:");
        if (listener != null && listener.get() != null) {
            listener.get().Revoke(recordBean, goAheadBean);
        }
        if (isRevoke) {
            recordBackStack.pop();
            recordGoHeadStack.push(goAheadBean);
        } else {
            recordGoHeadStack.pop();
            recordBackStack.push(goAheadBean);
        }

//        if (recordBean.isTakeTheNextStep()) {
//            recordBean.setTakeTheNextStep(false);
//            operateRevoke(isRevoke, true);
//        }
//
//        // 代码自动触发回退操作
//        if (automaticTrigger) {
//            goAheadBean.setTakeTheNextStep(true);
//        }

        Log.e("jiang", "operateRevoke:" + recordBackStack.size() + "---" + recordGoHeadStack.size());
    }

    /**
     * 撤销操作
     *
     * @param isRevoke
     */
    private void operateRevoke(boolean isRevoke) {
        operateRevoke(isRevoke, false);
    }

    private void playLog(RecordEditBean recordBean, String tag) {
        Log.i("tag", tag + " bundleName:" + recordBean.getBundleName()
                + "--bundleValue=" + recordBean.getBundleValue()
                + "--colorName=" + recordBean.getColorName()
                + "--colorValue=" + recordBean.getColorValus());
    }

    public boolean getRecordGoAheadStackIsEmpty() {
        return recordGoHeadStack.size() < 1;
    }

    /**
     * 清空撤销堆栈
     */
    public void clearRevoke() {
        while (!recordBackStack.isEmpty()) {
            recordBackStack.pop();
        }
        while (!recordGoHeadStack.isEmpty()) {
            recordGoHeadStack.pop();
        }
    }

    public interface RevokeHelperListener {
        void Revoke(RecordEditBean recordEditBean, RecordEditBean goAheadBean);
    }
}
