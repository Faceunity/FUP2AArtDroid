package com.faceunity.pta_art.fragment.editface.core.shape;

import android.util.Log;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.JsonUtils;
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

    public static String HeadBone_stretch = "";
    public static String HeadBone_shrink = "";
    public static String HeadBone_wide = "";
    public static String HeadBone_narrow = "";
    public static String Head_wide = "";
    public static String Head_narrow = "";
    public static String head_shrink = "";
    public static String head_stretch = "";
    private String[] jsons;

    public EditFaceParameter(AvatarHandle avatarHandle) {
        mAvatarHandle = avatarHandle;
        mMap = new LinkedHashMap<>();
        mDefaultMap = new LinkedHashMap<>();

        JsonUtils jsonUtils = new JsonUtils();
        if (Constant.style == Constant.style_art) {
            jsons = jsonUtils.readFacePupJson("art/facepup.json");
        } else {
            jsons = jsonUtils.readFacePupJson("new/facepup.json");
        }
        HeadBone_stretch = jsons[2];
        HeadBone_shrink = jsons[3];
        HeadBone_wide = jsons[4];
        HeadBone_narrow = jsons[5];
        Head_wide = jsons[6];
        Head_narrow = jsons[7];
        head_shrink = jsons[8];
        head_stretch = jsons[9];
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
        if (mLastMap == null) {
            mLastMap = new LinkedHashMap<>();
        }
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

    public Float getDefaultParamByKey(String key) {
        return mDefaultMap.get(key);
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
        //        float[] values = new float[jsons.length];
//        for (int i = 0; i < jsons.length; i++) {
//            values[i] = getValue(mMap.get(jsons[i]));
//            Log.i(TAG, "key:" + jsons[i] + "--value:" + values[i]);
//        }
        return mAvatarHandle.fuItemGetParamFaceShape();
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
        reset(tempMap);
    }

    public void reset(LinkedHashMap<String, Float> mList) {
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            String key = entry.getKey();
            float v = getValue(mList.get(key));
            entry.setValue(v);
            if (key.contains("_L")) {
                String rightKey = key.replace("_L", "_R");
                if (mMap.containsKey(rightKey) && getValue(mMap.get(rightKey)) == 0.0f) {
                    mList.put(rightKey, v);
                }
            }
            mAvatarHandle.fuItemSetParamFaceShape(key, v);
        }
    }


    public LinkedHashMap getTemp() {
        LinkedHashMap mList = new LinkedHashMap<>();
        for (Iterator iterator = tempMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            entry.setValue(getValue(tempMap.get(entry.getKey())));
            mList.put(entry.getKey(), entry.getValue());
        }
        return mList;
    }

    public LinkedHashMap getMap() {
        LinkedHashMap mList = new LinkedHashMap<>();
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            entry.setValue(getValue(mMap.get(entry.getKey())));
            mList.put(entry.getKey(), entry.getValue());
        }
        return mList;
    }

    public float getValue(Object v) {
        if (v == null || (float) v < 0 || ((Float) v).isNaN()) {
            return 0.0f;
        } else {
            return (float) v;
        }
    }


    public void release() {
        HeadBone_stretch = "";
        HeadBone_shrink = "";
        HeadBone_wide = "";
        HeadBone_narrow = "";
        Head_wide = "";
        Head_narrow = "";
        head_shrink = "";
        head_stretch = "";
        if (mMap != null) {
            mMap.clear();
        }
        if (mDefaultMap != null) {
            mDefaultMap.clear();
        }
        if (tempMap != null) {
            tempMap.clear();
        }
        if (mLastMap != null) {
            mLastMap.clear();
        }
    }
}
