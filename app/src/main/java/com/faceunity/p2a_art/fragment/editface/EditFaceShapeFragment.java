package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faceunity.p2a.P2AClientInterface;
import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.ui.EditFaceRadio;
import com.faceunity.p2a_art.ui.EditFaceRadioGroup;
import com.faceunity.p2a_art.ui.EditFaceShapeScrollView;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.ui.seekbar.DiscreteSeekBar;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceShapeFragment extends EditFaceBaseFragment implements View.OnClickListener {
    public static final String TAG = EditFaceShapeFragment.class.getSimpleName();
    private int screenWidth;

    private LinearLayout mSeekBarLayout;
    private TextView mSeekBarText;
    private DiscreteSeekBar mSeekBar;
    private EditFaceShapeScrollView mShapeScrollView;
    private EditFaceRadioGroup mShapeRadioGroup;

    private float[] mDefaultShapeValues;
    private float[] mShapeValues;
    private String mShapeTag;

    private static final String[] KEY_SHAPE_FACE = {FUP2ARenderer.key_shape_Head, FUP2ARenderer.key_shape_cheek, FUP2ARenderer.key_shape_jawbone, FUP2ARenderer.key_shape_jaw};
    private static final int[] RES_NORMAL_SHAPE_FACE = {R.drawable.edit_face_shape_face_normal_head, R.drawable.edit_face_shape_face_normal_cheek, R.drawable.edit_face_shape_face_normal_jawbone, R.drawable.edit_face_shape_face_normal_jaw};
    private static final int[] RES_CHECKED_SHAPE_FACE = {R.drawable.edit_face_shape_face_checked_head, R.drawable.edit_face_shape_face_checked_cheek, R.drawable.edit_face_shape_face_checked_jawbone, R.drawable.edit_face_shape_face_checked_jaw};
    private static final String[] KEY_SHAPE_EYE = {FUP2ARenderer.key_shape_Eye_down_up, FUP2ARenderer.key_shape_Eye_outter, FUP2ARenderer.key_shape_Eye_close_open, FUP2ARenderer.key_shape_Eye_both};
    private static final int[] RES_NORMAL_SHAPE_EYE = {R.drawable.edit_face_shape_eye_normal_down_up, R.drawable.edit_face_shape_eye_normal_outter, R.drawable.edit_face_shape_eye_normal_close_open, R.drawable.edit_face_shape_eye_normal_both};
    private static final int[] RES_CHECKED_SHAPE_EYE = {R.drawable.edit_face_shape_eye_checked_down_up, R.drawable.edit_face_shape_eye_checked_outter, R.drawable.edit_face_shape_eye_checked_close_open, R.drawable.edit_face_shape_eye_checked_both};
    private static final String[] KEY_SHAPE_NOESE = {FUP2ARenderer.key_shape_nose, FUP2ARenderer.key_shape_nostril, FUP2ARenderer.key_shape_noseTip};
    private static final int[] RES_NORMAL_SHAPE_NOESE = {R.drawable.edit_face_shape_nose_normal_nose, R.drawable.edit_face_shape_nose_normal_nostril, R.drawable.edit_face_shape_nose_normal_nose_tip};
    private static final int[] RES_CHECKED_SHAPE_NOESE = {R.drawable.edit_face_shape_nose_checked_nose, R.drawable.edit_face_shape_nose_checked_nostril, R.drawable.edit_face_shape_nose_checked_nose_tip};
    private static final String[] KEY_SHAPE_MOUTH = {FUP2ARenderer.key_shape_mouth, FUP2ARenderer.key_shape_upperLip, FUP2ARenderer.key_shape_lowerLip, FUP2ARenderer.key_shape_lipCorner};
    private static final int[] RES_NORMAL_SHAPE_MOUTH = {R.drawable.edit_face_shape_mouth_normal_mouth, R.drawable.edit_face_shape_mouth_normal_upper_lip, R.drawable.edit_face_shape_mouth_normal_lower_lip, R.drawable.edit_face_shape_mouth_normal_lip_corner};
    private static final int[] RES_CHECKED_SHAPE_MOUTH = {R.drawable.edit_face_shape_mouth_checked_mouth, R.drawable.edit_face_shape_mouth_checked_upper_lip, R.drawable.edit_face_shape_mouth_checked_lower_lip, R.drawable.edit_face_shape_mouth_checked_lip_corner};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_shape, container, false);
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        mSeekBarLayout = view.findViewById(R.id.shape_seek_bar_layout);
        mSeekBarText = view.findViewById(R.id.shape_seek_bar_text);
        mSeekBar = view.findViewById(R.id.shape_seek_bar);

        mShapeScrollView = view.findViewById(R.id.edit_face_shape_scroll_layout);

        view.findViewById(R.id.edit_face_shape_layout).setLayoutParams(new LinearLayout.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        view.findViewById(R.id.edit_face_shape_reset).setOnClickListener(this);
        view.findViewById(R.id.edit_face_shape_face).setOnClickListener(this);
        view.findViewById(R.id.edit_face_shape_eye).setOnClickListener(this);
        view.findViewById(R.id.edit_face_shape_mouth).setOnClickListener(this);
        view.findViewById(R.id.edit_face_shape_nose).setOnClickListener(this);
        view.findViewById(R.id.edit_face_shape_back).setOnClickListener(this);

        mShapeRadioGroup = view.findViewById(R.id.edit_face_radio_shape);
        mShapeRadioGroup.setMinimumWidth(screenWidth - getResources().getDimensionPixelSize(R.dimen.x130));
        mShapeRadioGroup.setOnCheckedChangeListener(new EditFaceRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(EditFaceRadioGroup group, int checkedId) {
                updateSeekBarLayout(mShapeTag = (checkedId == View.NO_ID) ? "" : (String) group.findViewById(checkedId).getTag());
            }
        });
        mSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            public void onProgressChanged(DiscreteSeekBar seekBar, int v, boolean fromUser) {
                if (!fromUser) return;
                float value = 1.0f * v / 100;
                setFaceShape(mShapeTag, value);
            }
        });

        setDefaultShapeValues();
        return view;
    }

    private void setDefaultShapeValues() {
        mDefaultShapeValues = new float[P2AClientInterface.index_count];
        mShapeValues = new float[P2AClientInterface.index_count];
        mDefaultShapeValues[P2AClientInterface.index_cheek_narrow] = mShapeValues[P2AClientInterface.index_cheek_narrow] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_cheek_narrow);
        mDefaultShapeValues[P2AClientInterface.index_Head_fat] = mShapeValues[P2AClientInterface.index_Head_fat] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Head_fat);
        mDefaultShapeValues[P2AClientInterface.index_Head_shrink] = mShapeValues[P2AClientInterface.index_Head_shrink] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Head_shrink);
        mDefaultShapeValues[P2AClientInterface.index_Head_stretch] = mShapeValues[P2AClientInterface.index_Head_stretch] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Head_stretch);
        mDefaultShapeValues[P2AClientInterface.index_HeadBone_shrink] = mShapeValues[P2AClientInterface.index_HeadBone_shrink] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_HeadBone_shrink);
        mDefaultShapeValues[P2AClientInterface.index_HeadBone_stretch] = mShapeValues[P2AClientInterface.index_HeadBone_stretch] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_HeadBone_stretch);
        mDefaultShapeValues[P2AClientInterface.index_jaw_lower] = mShapeValues[P2AClientInterface.index_jaw_lower] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_jaw_up);
        mDefaultShapeValues[P2AClientInterface.index_jawbone_Narrow] = mShapeValues[P2AClientInterface.index_jawbone_Narrow] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_jawbone_Narrow);
        mDefaultShapeValues[P2AClientInterface.index_jawbone_Wide] = mShapeValues[P2AClientInterface.index_jawbone_Wide] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_jawbone_Wide);
        mDefaultShapeValues[P2AClientInterface.index_lipCorner_In] = mShapeValues[P2AClientInterface.index_lipCorner_In] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_lipCorner_In);
        mDefaultShapeValues[P2AClientInterface.index_lipCorner_Out] = mShapeValues[P2AClientInterface.index_lipCorner_Out] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_lipCorner_Out);
        mDefaultShapeValues[P2AClientInterface.index_lowerLip_Thick] = mShapeValues[P2AClientInterface.index_lowerLip_Thick] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_lowerLip_Thick);
        mDefaultShapeValues[P2AClientInterface.index_lowerLip_Thin] = mShapeValues[P2AClientInterface.index_lowerLip_Thin] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_lowerLip_Thin);
        mDefaultShapeValues[P2AClientInterface.index_lowerLipSide_Thick] = mShapeValues[P2AClientInterface.index_lowerLipSide_Thick] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_lowerLipSide_Thick);
        mDefaultShapeValues[P2AClientInterface.index_mouth_Down] = mShapeValues[P2AClientInterface.index_mouth_Down] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_mouth_Down);
        mDefaultShapeValues[P2AClientInterface.index_mouth_Up] = mShapeValues[P2AClientInterface.index_mouth_Up] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_mouth_Up);
        mDefaultShapeValues[P2AClientInterface.index_nose_Down] = mShapeValues[P2AClientInterface.index_nose_Down] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_nose_Down);
        mDefaultShapeValues[P2AClientInterface.index_nose_UP] = mShapeValues[P2AClientInterface.index_nose_UP] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_nose_UP);
        mDefaultShapeValues[P2AClientInterface.index_noseTip_Down] = mShapeValues[P2AClientInterface.index_noseTip_Down] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_noseTip_Down);
        mDefaultShapeValues[P2AClientInterface.index_noseTip_Up] = mShapeValues[P2AClientInterface.index_noseTip_Up] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_noseTip_Up);
        mDefaultShapeValues[P2AClientInterface.index_nostril_In] = mShapeValues[P2AClientInterface.index_nostril_In] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_nostril_In);
        mDefaultShapeValues[P2AClientInterface.index_nostril_Out] = mShapeValues[P2AClientInterface.index_nostril_Out] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_nostril_Out);
        mDefaultShapeValues[P2AClientInterface.index_upperLip_Thick] = mShapeValues[P2AClientInterface.index_upperLip_Thick] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_upperLip_Thick);
        mDefaultShapeValues[P2AClientInterface.index_upperLip_Thin] = mShapeValues[P2AClientInterface.index_upperLip_Thin] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_upperLip_Thin);
        mDefaultShapeValues[P2AClientInterface.index_upperLipSide_Thick] = mShapeValues[P2AClientInterface.index_upperLipSide_Thick] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_upperLipSide_Thick);
        mDefaultShapeValues[P2AClientInterface.index_Eye_both_in] = mShapeValues[P2AClientInterface.index_Eye_both_in] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_both_in);
        mDefaultShapeValues[P2AClientInterface.index_Eye_both_out] = mShapeValues[P2AClientInterface.index_Eye_both_out] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_both_out);
        mDefaultShapeValues[P2AClientInterface.index_Eye_close] = mShapeValues[P2AClientInterface.index_Eye_close] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_close);
        mDefaultShapeValues[P2AClientInterface.index_Eye_down] = mShapeValues[P2AClientInterface.index_Eye_down] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_down);
        mDefaultShapeValues[P2AClientInterface.index_Eye_inner_down] = mShapeValues[P2AClientInterface.index_Eye_inner_down] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_inner_down);
        mDefaultShapeValues[P2AClientInterface.index_Eye_inner_up] = mShapeValues[P2AClientInterface.index_Eye_inner_up] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_inner_up);
        mDefaultShapeValues[P2AClientInterface.index_Eye_open] = mShapeValues[P2AClientInterface.index_Eye_open] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_open);
        mDefaultShapeValues[P2AClientInterface.index_Eye_outter_down] = mShapeValues[P2AClientInterface.index_Eye_outter_down] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_outter_down);
        mDefaultShapeValues[P2AClientInterface.index_Eye_outter_up] = mShapeValues[P2AClientInterface.index_Eye_outter_up] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_outter_up);
        mDefaultShapeValues[P2AClientInterface.index_Eye_up] = mShapeValues[P2AClientInterface.index_Eye_up] = mFUP2ARenderer.fuItemGetParamShape(FUP2ARenderer.PARAM_KEY_Eye_up);
    }

    @Override
    public void resetDefaultDeformParam() {
        System.arraycopy(mDefaultShapeValues, 0, mShapeValues, 0, P2AClientInterface.index_count);
    }

    @Override
    public boolean isChangeDeformParam() {
        if (mDefaultShapeValues == null || mShapeValues == null) return false;
        for (int i = 0; i < P2AClientInterface.index_count; i++) {
            if (mShapeValues[i] != mDefaultShapeValues[i])
                return true;
        }
        return false;
    }

    private void updateShapeRadioGroup(String[] tags, int[] resNormals, int[] resCheckeds) {
        mShapeRadioGroup.removeAllViews();
        for (int i = 0; i < tags.length; i++) {
            EditFaceRadio radio = new EditFaceRadio(getContext());
            radio.setChecked(false);
            radio.setDrawable(getResources().getDrawable(resNormals[i]), getResources().getDrawable(resCheckeds[i]));
            radio.setTag(tags[i]);
            mShapeRadioGroup.addView(radio);
        }
    }

    private void updateSeekBarLayout(String tag) {
        mSeekBarLayout.setVisibility(View.VISIBLE);
        switch (tag) {
            case FUP2ARenderer.key_shape_Head:
                mSeekBarText.setText("脸型长度");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_Head_shrink] > 0 ? mShapeValues[P2AClientInterface.index_Head_shrink] * 100 : -mShapeValues[P2AClientInterface.index_Head_stretch] * 100));
                break;
            case FUP2ARenderer.key_shape_HeadBone:
                mSeekBarText.setText("额头高低");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_HeadBone_stretch] > 0 ? mShapeValues[P2AClientInterface.index_HeadBone_stretch] * 100 : -mShapeValues[P2AClientInterface.index_HeadBone_shrink] * 100));
                break;
            case FUP2ARenderer.key_shape_cheek:
                mSeekBarText.setText("脸颊宽度");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_cheek_narrow] > 0 ? mShapeValues[P2AClientInterface.index_cheek_narrow] * 100 : -mShapeValues[P2AClientInterface.index_Head_fat] * 100));
                break;
            case FUP2ARenderer.key_shape_jawbone:
                mSeekBarText.setText("下颚宽度");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_jawbone_Narrow] > 0 ? mShapeValues[P2AClientInterface.index_jawbone_Narrow] * 100 : -mShapeValues[P2AClientInterface.index_jawbone_Wide] * 100));
                break;
            case FUP2ARenderer.key_shape_jaw:
                mSeekBarText.setText("下巴高低");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_jaw_lower] > 0 ? mShapeValues[P2AClientInterface.index_jaw_lower] * 100 : -mShapeValues[P2AClientInterface.index_jaw_up] * 100));
                break;
            case FUP2ARenderer.key_shape_Eye_down_up:
                mSeekBarText.setText("眼睛位置");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_Eye_up] > 0 ? mShapeValues[P2AClientInterface.index_Eye_up] * 100 : -mShapeValues[P2AClientInterface.index_Eye_down] * 100));
                break;
            case FUP2ARenderer.key_shape_Eye_outter:
                mSeekBarText.setText("眼角高低");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_Eye_outter_up] > 0 ? mShapeValues[P2AClientInterface.index_Eye_outter_up] * 100 : -mShapeValues[P2AClientInterface.index_Eye_outter_down] * 100));
                break;
            case FUP2ARenderer.key_shape_Eye_close_open:
                mSeekBarText.setText("眼睛高低");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_Eye_close] > 0 ? mShapeValues[P2AClientInterface.index_Eye_close] * 100 : -mShapeValues[P2AClientInterface.index_Eye_open] * 100));
                break;
            case FUP2ARenderer.key_shape_Eye_both:
                mSeekBarText.setText("眼睛宽窄");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_Eye_both_in] > 0 ? mShapeValues[P2AClientInterface.index_Eye_both_in] * 100 : -mShapeValues[P2AClientInterface.index_Eye_both_out] * 100));
                break;
            case FUP2ARenderer.key_shape_nose:
                mSeekBarText.setText("鼻子位置");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_nose_UP] > 0 ? mShapeValues[P2AClientInterface.index_nose_UP] * 100 : -mShapeValues[P2AClientInterface.index_nose_Down] * 100));
                break;
            case FUP2ARenderer.key_shape_nostril:
                mSeekBarText.setText("鼻翼宽窄");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_nostril_In] > 0 ? mShapeValues[P2AClientInterface.index_nostril_In] * 100 : -mShapeValues[P2AClientInterface.index_nostril_Out] * 100));
                break;
            case FUP2ARenderer.key_shape_noseTip:
                mSeekBarText.setText("鼻头高低");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_noseTip_Up] > 0 ? mShapeValues[P2AClientInterface.index_noseTip_Up] * 100 : -mShapeValues[P2AClientInterface.index_noseTip_Down] * 100));
                break;
            case FUP2ARenderer.key_shape_mouth:
                mSeekBarText.setText("嘴部位置");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_mouth_Up] > 0 ? mShapeValues[P2AClientInterface.index_mouth_Up] * 100 : -mShapeValues[P2AClientInterface.index_mouth_Down] * 100));
                break;
            case FUP2ARenderer.key_shape_upperLip:
                mSeekBarText.setText("上唇厚度");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_upperLip_Thick] > 0 ? mShapeValues[P2AClientInterface.index_upperLip_Thick] * 100 : -mShapeValues[P2AClientInterface.index_upperLip_Thin] * 100));
                break;
            case FUP2ARenderer.key_shape_lowerLip:
                mSeekBarText.setText("下唇厚度");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_lowerLip_Thick] > 0 ? mShapeValues[P2AClientInterface.index_lowerLip_Thick] * 100 : -mShapeValues[P2AClientInterface.index_lowerLip_Thin] * 100));
                break;
            case FUP2ARenderer.key_shape_lipCorner:
                mSeekBarText.setText("嘴唇宽度");
                mSeekBar.setProgress((int) (mShapeValues[P2AClientInterface.index_lipCorner_In] > 0 ? mShapeValues[P2AClientInterface.index_lipCorner_In] * 100 : -mShapeValues[P2AClientInterface.index_lipCorner_Out] * 100));
                break;
            default:
                mSeekBarLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void setFaceShape(final String key, final float values) {
        switch (key) {
            case FUP2ARenderer.key_shape_Head:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Head_shrink, mShapeValues[P2AClientInterface.index_Head_shrink] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Head_stretch, mShapeValues[P2AClientInterface.index_Head_stretch] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_HeadBone:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_HeadBone_stretch, mShapeValues[P2AClientInterface.index_HeadBone_stretch] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_HeadBone_shrink, mShapeValues[P2AClientInterface.index_HeadBone_shrink] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_cheek:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_cheek_narrow, mShapeValues[P2AClientInterface.index_cheek_narrow] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Head_fat, mShapeValues[P2AClientInterface.index_Head_fat] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_jawbone:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_jawbone_Narrow, mShapeValues[P2AClientInterface.index_jawbone_Narrow] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_jawbone_Wide, mShapeValues[P2AClientInterface.index_jawbone_Wide] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_jaw:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_jaw_lower, mShapeValues[P2AClientInterface.index_jaw_lower] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_jaw_up, mShapeValues[P2AClientInterface.index_jaw_up] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_Eye_down_up:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_up, mShapeValues[P2AClientInterface.index_Eye_up] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_down, mShapeValues[P2AClientInterface.index_Eye_down] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_Eye_outter:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_outter_up, mShapeValues[P2AClientInterface.index_Eye_outter_up] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_outter_down, mShapeValues[P2AClientInterface.index_Eye_outter_down] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_Eye_close_open:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_close, mShapeValues[P2AClientInterface.index_Eye_close] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_open, mShapeValues[P2AClientInterface.index_Eye_open] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_Eye_both:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_both_in, mShapeValues[P2AClientInterface.index_Eye_both_in] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_Eye_both_out, mShapeValues[P2AClientInterface.index_Eye_both_out] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_nose:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_nose_UP, mShapeValues[P2AClientInterface.index_nose_UP] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_nose_Down, mShapeValues[P2AClientInterface.index_nose_Down] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_nostril:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_nostril_In, mShapeValues[P2AClientInterface.index_nostril_In] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_nostril_Out, mShapeValues[P2AClientInterface.index_nostril_Out] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_noseTip:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_noseTip_Up, mShapeValues[P2AClientInterface.index_noseTip_Up] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_noseTip_Down, mShapeValues[P2AClientInterface.index_noseTip_Down] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_mouth:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_mouth_Up, mShapeValues[P2AClientInterface.index_mouth_Up] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_mouth_Down, mShapeValues[P2AClientInterface.index_mouth_Down] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_upperLip:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_upperLip_Thick, mShapeValues[P2AClientInterface.index_upperLip_Thick] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_upperLip_Thin, mShapeValues[P2AClientInterface.index_upperLip_Thin] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_lowerLip:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_lowerLip_Thick, mShapeValues[P2AClientInterface.index_lowerLip_Thick] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_lowerLip_Thin, mShapeValues[P2AClientInterface.index_lowerLip_Thin] = values > 0 ? 0 : Math.abs(values));
                break;
            case FUP2ARenderer.key_shape_lipCorner:
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_lipCorner_In, mShapeValues[P2AClientInterface.index_lipCorner_In] = values > 0 ? Math.abs(values) : 0);
                mFUP2ARenderer.fuItemSetParamFaceShape(FUP2ARenderer.PARAM_KEY_lipCorner_Out, mShapeValues[P2AClientInterface.index_lipCorner_Out] = values > 0 ? 0 : Math.abs(values));
                break;
        }
        if (colorControllerListener != null) {
            colorControllerListener.colorValuesChangeListener(values);
        }
    }

    public float[] getShapeValues() {
        return mShapeValues;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.edit_face_shape_reset) {
            if (isChangeDeformParam()) {
                NormalDialog normalDialog = new NormalDialog();
                normalDialog.setNormalDialogTheme(R.style.FullScreenTheme);
                normalDialog.setMessageStr("确认将所有参数恢复默认吗？");
                normalDialog.setNegativeStr("取消");
                normalDialog.setPositiveStr("确认");
                normalDialog.show(getChildFragmentManager(), NormalDialog.TAG);
                normalDialog.setOnClickListener(new NormalDialog.OnSimpleClickListener() {
                    @Override
                    public void onPositiveListener() {
                        for (String tag : KEY_SHAPE_FACE) {
                            setFaceShape(tag, 0);
                        }
                        for (String tag : KEY_SHAPE_EYE) {
                            setFaceShape(tag, 0);
                        }
                        for (String tag : KEY_SHAPE_MOUTH) {
                            setFaceShape(tag, 0);
                        }
                        for (String tag : KEY_SHAPE_NOESE) {
                            setFaceShape(tag, 0);
                        }
                        resetDefaultDeformParam();
                    }
                });
            }
        } else if (id == R.id.edit_face_shape_back) {
            mShapeScrollView.setEnableScroll(false);
            if (mSeekBarLayout.getVisibility() != View.GONE) {
                mSeekBarLayout.setVisibility(View.GONE);
                mShapeRadioGroup.clearCheck();
                mShapeScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mShapeScrollView.smoothScrollTo(0, 0);
                    }
                }, 300);
            } else {
                mShapeScrollView.smoothScrollTo(0, 0);
            }
        } else {
            mShapeScrollView.setEnableScroll(true);
            mShapeScrollView.smoothScrollTo(screenWidth, 0);
            if (id == R.id.edit_face_shape_face) {
                updateShapeRadioGroup(KEY_SHAPE_FACE, RES_NORMAL_SHAPE_FACE, RES_CHECKED_SHAPE_FACE);
            } else if (id == R.id.edit_face_shape_eye) {
                updateShapeRadioGroup(KEY_SHAPE_EYE, RES_NORMAL_SHAPE_EYE, RES_CHECKED_SHAPE_EYE);
            } else if (id == R.id.edit_face_shape_mouth) {
                updateShapeRadioGroup(KEY_SHAPE_MOUTH, RES_NORMAL_SHAPE_MOUTH, RES_CHECKED_SHAPE_MOUTH);
            } else if (id == R.id.edit_face_shape_nose) {
                updateShapeRadioGroup(KEY_SHAPE_NOESE, RES_NORMAL_SHAPE_NOESE, RES_CHECKED_SHAPE_NOESE);
            }
        }
    }

}
