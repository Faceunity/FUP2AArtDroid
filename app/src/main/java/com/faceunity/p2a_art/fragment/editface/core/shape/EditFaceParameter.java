package com.faceunity.p2a_art.fragment.editface.core.shape;

import android.util.Log;

import com.faceunity.p2a_art.core.AvatarHandle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tujh on 2019/3/7.
 */
public class EditFaceParameter {
    private static final String TAG = EditFaceParameter.class.getSimpleName();

    private AvatarHandle mAvatarHandle;
    private LinkedHashMap<String, Float> mMap;
    private LinkedHashMap<String, Float> mDefaultMap;

    public static final String HeadBone_stretch = "HeadBone_stretch";
    public static final String HeadBone_shrink = "HeadBone_shrink";
    public static final String HeadBone_wide = "HeadBone_wide";
    public static final String HeadBone_narrow = "HeadBone_narrow";
    public static final String Head_wide = "Head_wide";
    public static final String Head_narrow = "Head_narrow";
    public static final String head_shrink = "head_shrink";
    public static final String head_stretch = "head_stretch";

    public EditFaceParameter(AvatarHandle avatarHandle) {
        mAvatarHandle = avatarHandle;
        mMap = new LinkedHashMap<>();
        mDefaultMap = new LinkedHashMap<>();
        mMap.put(HeadBone_stretch, 0F);
        mMap.put(HeadBone_shrink, 0F);
        mMap.put(HeadBone_wide, 0F);
        mMap.put(HeadBone_narrow, 0F);
        mMap.put(Head_wide, 0F);
        mMap.put(Head_narrow, 0F);
        mMap.put(head_shrink, 0F);
        mMap.put(head_stretch, 0F);
        mMap.put("head_fat", 0F);
        mMap.put("head_thin", 0F);
        mMap.put("cheek_wide", 0F);
        mMap.put("cheekbone_narrow", 0F);
        mMap.put("jawbone_Wide", 0F);
        mMap.put("jawbone_Narrow", 0F);
        mMap.put("jaw_m_wide", 0F);
        mMap.put("jaw_M_narrow", 0F);
        mMap.put("jaw_wide", 0F);
        mMap.put("jaw_narrow", 0F);
        mMap.put("jaw_up", 0F);
        mMap.put("jaw_lower", 0F);
        mMap.put("upperLip_Thick", 0F);
        mMap.put("upperLipSide_Thick", 0F);
        mMap.put("lowerLip_Thick", 0F);
        mMap.put("lowerLipSide_Thin", 0F);
        mMap.put("lowerLipSide_Thick", 0F);
        mMap.put("upperLip_Thin", 0F);
        mMap.put("lowerLip_Thin", 0F);
        mMap.put("mouth_magnify", 0F);
        mMap.put("mouth_shrink", 0F);
        mMap.put("lipCorner_Out", 0F);
        mMap.put("lipCorner_In", 0F);
        mMap.put("lipCorner_up", 0F);
        mMap.put("lipCorner_down", 0F);
        mMap.put("mouth_m_down", 0F);
        mMap.put("mouth_m_up", 0F);
        mMap.put("mouth_Up", 0F);
        mMap.put("mouth_Down", 0F);
        mMap.put("nostril_Out", 0F);
        mMap.put("nostril_In", 0F);
        mMap.put("noseTip_Up", 0F);
        mMap.put("noseTip_Down", 0F);
        mMap.put("nose_Up", 0F);
        mMap.put("nose_tall", 0F);
        mMap.put("nose_low", 0F);
        mMap.put("nose_Down", 0F);
        mMap.put("Eye_wide", 0F);
        mMap.put("Eye_shrink", 0F);
        mMap.put("Eye_up", 0F);
        mMap.put("Eye_down", 0F);
        mMap.put("Eye_in", 0F);
        mMap.put("Eye_out", 0F);
        mMap.put("Eye_close", 0F);
        mMap.put("Eye_open", 0F);
        mMap.put("Eye_upper_up", 0F);
        mMap.put("Eye_upper_down", 0F);
        mMap.put("Eye_upperBend_in", 0F);
        mMap.put("Eye_upperBend_out", 0F);
        mMap.put("Eye_downer_up", 0F);
        mMap.put("Eye_downer_dn", 0F);
        mMap.put("Eye_downerBend_in", 0F);
        mMap.put("Eye_downerBend_out", 0F);
        mMap.put("Eye_outter_in", 0F);
        mMap.put("Eye_outter_out", 0F);
        mMap.put("Eye_outter_up", 0F);
        mMap.put("Eye_outter_down", 0F);
        mMap.put("Eye_inner_in", 0F);
        mMap.put("Eye_inner_out", 0F);
        mMap.put("Eye_inner_up", 0F);
        mMap.put("Eye_inner_down", 0F);

        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            entry.setValue(mAvatarHandle.fuItemGetParamShape(entry.getKey()));
            mDefaultMap.put(entry.getKey(), entry.getValue());
        }
    }

    public void setParamMap(HashMap<String, Float> map) {
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            mMap.put(entry.getKey(), entry.getValue());
            mAvatarHandle.fuItemSetParamFaceShape(entry.getKey(), entry.getValue());
        }
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
            if (!mDefaultMap.get(entry.getKey()).equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public boolean isHeadShapeChangeValues() {
        return !mDefaultMap.get(HeadBone_stretch).equals(mMap.get(HeadBone_stretch))
                || !mDefaultMap.get(HeadBone_shrink).equals(mMap.get(HeadBone_shrink))
                || !mDefaultMap.get(HeadBone_wide).equals(mMap.get(HeadBone_wide))
                || !mDefaultMap.get(HeadBone_narrow).equals(mMap.get(HeadBone_narrow))
                || !mDefaultMap.get(Head_wide).equals(mMap.get(Head_wide))
                || !mDefaultMap.get(Head_narrow).equals(mMap.get(Head_narrow))
                || !mDefaultMap.get(head_shrink).equals(mMap.get(head_shrink))
                || !mDefaultMap.get(head_stretch).equals(mMap.get(head_stretch));
    }

    public float[] getEditFaceParameters() {
        float[] ret = new float[mMap.size()];
        int i = 0;
        for (Iterator iterator = mMap.entrySet().iterator(); iterator.hasNext(); i++) {
            Map.Entry<String, Float> entry = (Map.Entry<String, Float>) iterator.next();
            ret[i] = entry.getValue();
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
}
