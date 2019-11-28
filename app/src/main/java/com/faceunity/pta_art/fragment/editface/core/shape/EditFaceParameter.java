package com.faceunity.pta_art.fragment.editface.core.shape;

import android.util.Log;

import com.faceunity.pta_art.core.AvatarHandle;
import com.faceunity.pta_art.entity.RecordBean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by tujh on 2019/3/7.
 */
public class EditFaceParameter {
    private static final String TAG = EditFaceParameter.class.getSimpleName();

    private AvatarHandle mAvatarHandle;
    private LinkedHashMap<String, Float> mMap;
    private LinkedHashMap<String, Float> mDefaultMap;
    private LinkedHashMap<String, Float> tempMap;//记录刚进入的捏脸信息
    private Stack<RecordBean> recordBackStack;
    private Stack<RecordBean> recordGoHeadStack;
    private LinkedHashMap<String, Float> mLastMap;//更改捏脸前的信息

    public static final String HeadBone_stretch = "HeadBone_stretch";
    public static final String HeadBone_shrink = "HeadBone_shrink";
    public static final String HeadBone_wide = "HeadBone_wide";
    public static final String HeadBone_narrow = "HeadBone_narrow";
    public static final String Head_wide = "Head_wide";
    public static final String Head_narrow = "Head_narrow";
    public static final String head_shrink = "head_shrink";
    public static final String head_stretch = "head_stretch";
    private String[] jsons = new String[]{
            "HeadBone_stretch",
            "HeadBone_shrink",
            "HeadBone_wide",
            "HeadBone_narrow",
            "Head_wide",
            "Head_narrow",
            "head_shrink",
            "head_stretch",
            "head_fat",
            "head_thin",
            "cheek_wide",
            "cheekbone_narrow",
            "jawbone_Wide",
            "jawbone_Narrow",
            "jaw_m_wide",
            "jaw_M_narrow",
            "jaw_wide",
            "jaw_narrow",
            "jaw_up",
            "jaw_lower",
            "upperLip_Thick",
            "upperLipSide_Thick",
            "lowerLip_Thick",
            "lowerLipSide_Thin",
            "lowerLipSide_Thick",
            "upperLip_Thin",
            "lowerLip_Thin",
            "mouth_magnify",
            "mouth_shrink",
            "lipCorner_Out",
            "lipCorner_In",
            "lipCorner_up",
            "lipCorner_down",
            "mouth_m_down",
            "mouth_m_up",
            "mouth_Up",
            "mouth_Down",
            "nostril_Out",
            "nostril_In",
            "noseTip_Up",
            "noseTip_Down",
            "nose_Up",
            "nose_tall",
            "nose_low",
            "nose_Down",
            "Eye_wide",
            "Eye_shrink",
            "Eye_up",
            "Eye_down",
            "Eye_in",
            "Eye_out",
            "Eye_close",
            "Eye_open",
            "Eye_upper_up",
            "Eye_upper_down",
            "Eye_upperBend_in",
            "Eye_upperBend_out",
            "Eye_downer_up",
            "Eye_downer_dn",
            "Eye_downerBend_in",
            "Eye_downerBend_out",
            "Eye_outter_in",
            "Eye_outter_out",
            "Eye_outter_up",
            "Eye_outter_down",
            "Eye_inner_in",
            "Eye_inner_out",
            "Eye_inner_up",
            "Eye_inner_down"
    };

    public EditFaceParameter(AvatarHandle avatarHandle) {
        mAvatarHandle = avatarHandle;
        mMap = new LinkedHashMap<>();
        mDefaultMap = new LinkedHashMap<>();
        for (String s : jsons) {
            mMap.put(s, 0F);
        }

        recordBackStack = new Stack<>();
        recordGoHeadStack = new Stack<>();
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            entry.setValue(mAvatarHandle.fuItemGetParamShape(entry.getKey()));
            mDefaultMap.put(entry.getKey(), getValue(entry.getValue()));
        }
    }

    public void setParamMap(HashMap<String, Float> map) {
        boolean isNeedCopy = false;
        if (mLastMap == null) {
            mLastMap = new LinkedHashMap<>();
            isNeedCopy = true;
        }
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            if (isNeedCopy)
                mLastMap.put(entry.getKey(), mAvatarHandle.fuItemGetParamShape(entry.getKey()));
            mMap.put(entry.getKey(), entry.getValue() == null ? 0F : entry.getValue());
            mAvatarHandle.fuItemSetParamFaceShape(entry.getKey(), entry.getValue() == null ? 0F : entry.getValue());
        }
    }

    public void resetParamMap() {
        for (Map.Entry<String, Float> entry : mLastMap.entrySet()) {
            mMap.put(entry.getKey(), entry.getValue() == null ? 0F : entry.getValue());
            mAvatarHandle.fuItemSetParamFaceShape(entry.getKey(), entry.getValue() == null ? 0F : entry.getValue());
        }
        mLastMap.clear();
        mLastMap = null;
    }

    public Float getParamByKey(String key) {
        return mMap.get(key);
    }

    private void setParamFaceShape(String positiveKey, String negativeKey, float distance) {
        if (mMap.get(positiveKey) == null || mMap.get(negativeKey) == null) {
            Log.e(TAG, "setParamFaceShape error " + positiveKey + ":" + mMap.get(positiveKey) + " " + negativeKey + ":" + mMap.get(negativeKey));
            return;
        }
        float positiveValue = mMap.get(positiveKey);
        float negativeValue = mMap.get(negativeKey);
        float value = positiveValue > 0 ? positiveValue : -negativeValue;
        value += distance;
        value = value > 1 ? 1 : (value < -1 ? -1 : value);
        mMap.put(positiveKey, positiveValue = (value > 0 ? value : 0));
        mMap.put(negativeKey, negativeValue = (value > 0 ? 0 : Math.abs(value)));
        mAvatarHandle.fuItemSetParamFaceShape(positiveKey, positiveValue);
        mAvatarHandle.fuItemSetParamFaceShape(negativeKey, negativeValue);
//        Log.i("ssss", "setParamFaceShape: mMap: positiveKey:" + positiveKey + "--positiveValue:" +
//                mMap.get(positiveKey) + "--negativeKey=" + negativeKey + "--negativeValue:"
//                + mMap.get(negativeKey));
    }

    public void setParamFaceShape(EditFacePoint point, float distanceX, float distanceY) {
        switch (point.direction) {
            case EditFacePoint.DIRECTION_HORIZONTAL:
                setParamFaceShape(point.leftKey, point.rightKey, distanceX);
                break;
            case EditFacePoint.DIRECTION_VERTICAL:
                setParamFaceShape(point.upKey, point.downKey, distanceY);
                break;
            case EditFacePoint.DIRECTION_ALL:
                setParamFaceShape(point.leftKey, point.rightKey, distanceX);
                setParamFaceShape(point.upKey, point.downKey, distanceY);
                break;
        }
    }

    public boolean isShapeChangeValues() {
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            float mDefault = getValue(mDefaultMap.get(entry.getKey()));
            float mValues = getValue(entry.getValue());
            if (mDefault != mValues) {
                return true;
            }
        }
        return false;
    }

    public boolean isHeadShapeChangeValues() {
        return !mDefaultMap.get(HeadBone_stretch).equals(getValue(mMap.get(HeadBone_stretch)))
                || !mDefaultMap.get(HeadBone_shrink).equals(getValue(mMap.get(HeadBone_shrink)))
                || !mDefaultMap.get(HeadBone_wide).equals(getValue(mMap.get(HeadBone_wide)))
                || !mDefaultMap.get(HeadBone_narrow).equals(getValue(mMap.get(HeadBone_narrow)))
                || !mDefaultMap.get(Head_wide).equals(getValue(mMap.get(Head_wide)))
                || !mDefaultMap.get(Head_narrow).equals(getValue(mMap.get(Head_narrow)))
                || !mDefaultMap.get(head_shrink).equals(getValue(mMap.get(head_shrink)))
                || !mDefaultMap.get(head_stretch).equals(getValue(mMap.get(head_stretch)));
    }

    public float[] getEditFaceParameters() {
        float[] ret = new float[mMap.size()];
        int i = 0;
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); i++) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            ret[i] = getValue(entry.getValue());
        }
        return ret;
    }

    public void resetDefaultDeformParam() {
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            float v = mDefaultMap.get(entry.getKey());
            entry.setValue(v);
            mAvatarHandle.fuItemSetParamFaceShape(entry.getKey(), v);
        }
    }

    //撤销数据保存
    private String leftKey, rightKey;
    private String upKey, downKey;
    private float leftValue, rightValue;
    private float upValue, downValue;
    private int direction;

    public void copyLast(EditFacePoint point) {
        this.direction = point.direction;
        switch (point.direction) {
            case EditFacePoint.DIRECTION_HORIZONTAL:
                this.leftKey = point.leftKey;
                this.rightKey = point.rightKey;
                this.leftValue = mMap.get(leftKey) == null ? 0F : mMap.get(leftKey);
                this.rightValue = mMap.get(rightKey) == null ? 0F : mMap.get(rightKey);
                break;
            case EditFacePoint.DIRECTION_VERTICAL:
                this.upKey = point.upKey;
                this.downKey = point.downKey;
                this.upValue = mMap.get(upKey) == null ? 0F : mMap.get(upKey);
                this.downValue = mMap.get(downKey) == null ? 0F : mMap.get(downKey);
                break;
            case EditFacePoint.DIRECTION_ALL:
                this.leftKey = point.leftKey;
                this.rightKey = point.rightKey;
                this.leftValue = mMap.get(leftKey) == null ? 0F : mMap.get(leftKey);
                this.rightValue = mMap.get(rightKey) == null ? 0F : mMap.get(rightKey);
                this.upKey = point.upKey;
                this.downKey = point.downKey;
                this.upValue = mMap.get(upKey) == null ? 0F : mMap.get(upKey);
                this.downValue = mMap.get(downKey) == null ? 0F : mMap.get(downKey);
                break;
        }
    }

    /**
     * 保存回退操作的数据
     */
    public void recordBack() {
        RecordBean tempRecord = new RecordBean();
        tempRecord.setDirection(direction);
        switch (direction) {
            case EditFacePoint.DIRECTION_HORIZONTAL:
                tempRecord.setLeftKey(leftKey);
                tempRecord.setLeftValue(leftValue);
                tempRecord.setRightKey(rightKey);
                tempRecord.setRightValue(rightValue);
                break;
            case EditFacePoint.DIRECTION_VERTICAL:
                tempRecord.setUpKey(upKey);
                tempRecord.setUpValue(upValue);
                tempRecord.setDownKey(downKey);
                tempRecord.setDownValue(downValue);
                break;
            case EditFacePoint.DIRECTION_ALL:
                tempRecord.setLeftKey(leftKey);
                tempRecord.setLeftValue(leftValue);
                tempRecord.setRightKey(rightKey);
                tempRecord.setRightValue(rightValue);
                tempRecord.setUpKey(upKey);
                tempRecord.setUpValue(upValue);
                tempRecord.setDownKey(downKey);
                tempRecord.setDownValue(downValue);
                break;
        }
        recordBackStack.push(tempRecord);
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
        RecordBean goAheadBean = new RecordBean();//当前数据
        RecordBean recordBean = isRevoke == true ? recordBackStack.peek() : recordGoHeadStack.peek();
        goAheadBean.setDirection(recordBean.getDirection());
        switch (recordBean.getDirection()) {
            case EditFacePoint.DIRECTION_HORIZONTAL:
                goAheadBean.setLeftKey(recordBean.getLeftKey());
                goAheadBean.setLeftValue(mMap.get(recordBean.getLeftKey()) == null ? 0F : mMap.get(recordBean.getLeftKey()));
                goAheadBean.setRightKey(recordBean.getRightKey());
                goAheadBean.setRightValue(mMap.get(recordBean.getRightKey()) == null ? 0F : mMap.get(recordBean.getRightKey()));
                mMap.put(recordBean.getLeftKey(), recordBean.getLeftValue());
                mMap.put(recordBean.getRightKey(), recordBean.getRightValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getLeftKey(), recordBean.getLeftValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getRightKey(), recordBean.getRightValue());
                break;
            case EditFacePoint.DIRECTION_VERTICAL:
                goAheadBean.setUpKey(recordBean.getUpKey());
                goAheadBean.setUpValue(mMap.get(recordBean.getUpKey()) == null ? 0F : mMap.get(recordBean.getUpKey()));
                goAheadBean.setDownKey(recordBean.getDownKey());
                goAheadBean.setDownValue(mMap.get(recordBean.getDownKey()) == null ? 0F : mMap.get(recordBean.getDownKey()));
                mMap.put(recordBean.getUpKey(), recordBean.getUpValue());
                mMap.put(recordBean.getDownKey(), recordBean.getDownValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getUpKey(), recordBean.getUpValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getDownKey(), recordBean.getDownValue());
                break;
            case EditFacePoint.DIRECTION_ALL:
                goAheadBean.setLeftKey(recordBean.getLeftKey());
                goAheadBean.setLeftValue(mMap.get(recordBean.getLeftKey()) == null ? 0F : mMap.get(recordBean.getLeftKey()));
                goAheadBean.setRightKey(recordBean.getRightKey());
                goAheadBean.setRightValue(mMap.get(recordBean.getRightKey()) == null ? 0F : mMap.get(recordBean.getRightKey()));
                goAheadBean.setUpKey(recordBean.getUpKey());
                goAheadBean.setUpValue(mMap.get(recordBean.getUpKey()) == null ? 0F : mMap.get(recordBean.getUpKey()));
                goAheadBean.setDownKey(recordBean.getDownKey());
                goAheadBean.setDownValue(mMap.get(recordBean.getDownKey()) == null ? 0F : mMap.get(recordBean.getDownKey()));
                mMap.put(recordBean.getLeftKey(), recordBean.getLeftValue());
                mMap.put(recordBean.getRightKey(), recordBean.getRightValue());
                mMap.put(recordBean.getUpKey(), recordBean.getUpValue());
                mMap.put(recordBean.getDownKey(), recordBean.getDownValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getLeftKey(), recordBean.getLeftValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getRightKey(), recordBean.getRightValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getUpKey(), recordBean.getUpValue());
                mAvatarHandle.fuItemSetParamFaceShape(recordBean.getDownKey(), recordBean.getDownValue());
                break;
        }
        if (isRevoke) {
            recordBackStack.pop();
            recordGoHeadStack.push(goAheadBean);
        } else {
            recordGoHeadStack.pop();
            recordBackStack.push(goAheadBean);
        }
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

    /**
     * 进入捏脸时保持上次的捏脸数据
     */
    public void copy() {
        if (tempMap == null) {
            tempMap = new LinkedHashMap<>();
        }
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            entry.setValue(getValue(mMap.get(entry.getKey())));
            tempMap.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 重置会上次的捏脸状态
     */
    public void resetToTemp() {
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            float v = getValue(tempMap.get(entry.getKey()));
            entry.setValue(v);
            mAvatarHandle.fuItemSetParamFaceShape(entry.getKey(), v);
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