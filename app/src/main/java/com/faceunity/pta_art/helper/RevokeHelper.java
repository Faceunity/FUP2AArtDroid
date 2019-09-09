package com.faceunity.pta_art.helper;

import com.faceunity.pta_art.entity.RecordEditBean;
import java.lang.ref.WeakReference;
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
    public void record(int type, String bundleName, double bundleValue,
                       String colorName, double colorValue) {
        RecordEditBean recordEditBean = new RecordEditBean();
        recordEditBean.setType(type);
        recordEditBean.setBundleName(bundleName);
        recordEditBean.setBundleValue(bundleValue);
        recordEditBean.setColorName(colorName);
        recordEditBean.setColorValus(colorValue);
        recordBackStack.push(recordEditBean);
        playLog(recordEditBean, "record:");
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
     * @param isRevoke
     */
    private void operateRevoke(boolean isRevoke) {
        RecordEditBean goAheadBean = new RecordEditBean();//当前数据
        RecordEditBean recordBean = isRevoke == true ? recordBackStack.peek() : recordGoHeadStack.peek();

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
    }

    private void playLog(RecordEditBean recordBean, String tag) {
//        Log.i("ssss", tag + " bundleName:" + recordBean.getBundleName()
//                + "--bundleValue=" + recordBean.getBundleValue()
//                + "--colorName=" + recordBean.getColorName()
//                + "--colorValue=" + recordBean.getColorValus());
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
