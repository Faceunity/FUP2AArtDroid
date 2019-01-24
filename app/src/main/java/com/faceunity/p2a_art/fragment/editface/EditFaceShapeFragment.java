package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faceunity.p2a.P2AClientInterface;
import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.core.AvatarHandle;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.p2a_art.ui.EditFaceRadio;
import com.faceunity.p2a_art.ui.EditFaceRadioGroup;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.ui.seekbar.DiscreteSeekBar;

import java.util.Arrays;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceShapeFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceShapeFragment.class.getSimpleName();

    private static final String COLOR_SELECT = "color_select";
    private static final String[] KEY_SHAPE_FACE = {AvatarHandle.key_shape_Head, AvatarHandle.key_shape_forehead, AvatarHandle.key_shape_cheek, AvatarHandle.key_shape_jawbone, AvatarHandle.key_shape_jaw};
    private static final int[] RES_NORMAL_SHAPE_FACE = {R.drawable.edit_face_shape_face_normal_head, R.drawable.edit_face_shape_face_normal_forehead, R.drawable.edit_face_shape_face_normal_cheek, R.drawable.edit_face_shape_face_normal_jawbone, R.drawable.edit_face_shape_face_normal_jaw};
    private static final int[] RES_CHECKED_SHAPE_FACE = {R.drawable.edit_face_shape_face_checked_head, R.drawable.edit_face_shape_face_checked_forehead, R.drawable.edit_face_shape_face_checked_cheek, R.drawable.edit_face_shape_face_checked_jawbone, R.drawable.edit_face_shape_face_checked_jaw};

    private static final String[] KEY_SHAPE_EYE = {AvatarHandle.key_shape_Eye_down_up, AvatarHandle.key_shape_Eye_outter, AvatarHandle.key_shape_Eye_close_open, AvatarHandle.key_shape_Eye_both, COLOR_SELECT};
    private static final int[] RES_NORMAL_SHAPE_EYE = {R.drawable.edit_face_shape_eye_normal_down_up, R.drawable.edit_face_shape_eye_normal_outter, R.drawable.edit_face_shape_eye_normal_close_open, R.drawable.edit_face_shape_eye_normal_both, R.drawable.edit_face_shape_color_normal};
    private static final int[] RES_CHECKED_SHAPE_EYE = {R.drawable.edit_face_shape_eye_checked_down_up, R.drawable.edit_face_shape_eye_checked_outter, R.drawable.edit_face_shape_eye_checked_close_open, R.drawable.edit_face_shape_eye_checked_both, R.drawable.edit_face_shape_color_checked};

    private static final String[] KEY_SHAPE_NOSE = {AvatarHandle.key_shape_nose, AvatarHandle.key_shape_nostril, AvatarHandle.key_shape_noseTip};
    private static final int[] RES_NORMAL_SHAPE_NOSE = {R.drawable.edit_face_shape_nose_normal_nose, R.drawable.edit_face_shape_nose_normal_nostril, R.drawable.edit_face_shape_nose_normal_nose_tip};
    private static final int[] RES_CHECKED_SHAPE_NOSE = {R.drawable.edit_face_shape_nose_checked_nose, R.drawable.edit_face_shape_nose_checked_nostril, R.drawable.edit_face_shape_nose_checked_nose_tip};

    private static final String[] KEY_SHAPE_MOUTH = {AvatarHandle.key_shape_mouth, AvatarHandle.key_shape_upperLip, AvatarHandle.key_shape_lowerLip, AvatarHandle.key_shape_lipCorner, COLOR_SELECT};
    private static final int[] RES_NORMAL_SHAPE_MOUTH = {R.drawable.edit_face_shape_mouth_normal_mouth, R.drawable.edit_face_shape_mouth_normal_upper_lip, R.drawable.edit_face_shape_mouth_normal_lower_lip, R.drawable.edit_face_shape_mouth_normal_lip_corner, R.drawable.edit_face_shape_color_normal};
    private static final int[] RES_CHECKED_SHAPE_MOUTH = {R.drawable.edit_face_shape_mouth_checked_mouth, R.drawable.edit_face_shape_mouth_checked_upper_lip, R.drawable.edit_face_shape_mouth_checked_lower_lip, R.drawable.edit_face_shape_mouth_checked_lip_corner, R.drawable.edit_face_shape_color_checked};

    private LinearLayout mSeekBarLayout;
    private TextView mSeekBarText;
    private DiscreteSeekBar mSeekBar;
    private HorizontalScrollView mScrollView;
    private EditFaceRadioGroup mShapeRadioGroup;
    private ColorSelectView mColorSelectView;

    private float[] mDefaultShapeValues;
    private float[] mShapeValues;
    private String mShapeTag;
    private ColorValuesChangeListener mColorValuesChangeListener;

    private ShapeValuesChaneListener mShapeValuesChaneListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_shape, container, false);

        mSeekBarLayout = view.findViewById(R.id.shape_seek_bar_layout);
        mSeekBarText = view.findViewById(R.id.shape_seek_bar_text);
        mSeekBar = view.findViewById(R.id.shape_seek_bar);

        mColorSelectView = view.findViewById(R.id.shape_color_recycler);
        if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_EYE_INDEX) {
            mColorSelectView.init(ColorConstant.iris_color, (int) mAvatarP2A.getIrisColorValue());
        } else if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_LIP_INDEX) {
            mColorSelectView.init(ColorConstant.lip_color, (int) mAvatarP2A.getLipColorValue());
        }
        mColorSelectView.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                mColorValuesChangeListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, position);
            }
        });

        view.findViewById(R.id.edit_face_shape_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            resetDefaultDeformParam();
                        }
                    });
                }
            }
        });

        mScrollView = view.findViewById(R.id.edit_face_radio_shape_scroll);
        mShapeRadioGroup = view.findViewById(R.id.edit_face_radio_shape);
        mShapeRadioGroup.setOnCheckedChangeListener(new EditFaceRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(EditFaceRadioGroup group, int checkedId) {
                EditFaceRadio radio = group.findViewById(checkedId);
                mScrollView.smoothScrollTo(radio.getLeft() + radio.getWidth() / 2 - mScrollView.getWidth() / 2, 0);
                updateSeekBarLayout(mShapeTag = (checkedId == View.NO_ID) ? "" : (String) radio.getTag());
            }
        });
        mSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            public void onProgressChanged(DiscreteSeekBar seekBar, int v, boolean fromUser) {
                if (!fromUser) return;
                float value = 1.0f * v / 100;
                setFaceShape(mShapeTag, value);
            }
        });

        if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_FACE_INDEX) {
            updateShapeRadioGroup(KEY_SHAPE_FACE, RES_NORMAL_SHAPE_FACE, RES_CHECKED_SHAPE_FACE);
        } else if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_EYE_INDEX) {
            updateShapeRadioGroup(KEY_SHAPE_EYE, RES_NORMAL_SHAPE_EYE, RES_CHECKED_SHAPE_EYE);
        } else if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_LIP_INDEX) {
            updateShapeRadioGroup(KEY_SHAPE_MOUTH, RES_NORMAL_SHAPE_MOUTH, RES_CHECKED_SHAPE_MOUTH);
        } else if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_NOSE_INDEX) {
            updateShapeRadioGroup(KEY_SHAPE_NOSE, RES_NORMAL_SHAPE_NOSE, RES_CHECKED_SHAPE_NOSE);
        }
        return view;
    }

    public void initDate(float[] values, ColorValuesChangeListener colorValuesChangeListener, ShapeValuesChaneListener shapeValuesChaneListener) {
        mShapeValues = values;
        mDefaultShapeValues = Arrays.copyOf(values, values.length);
        mColorValuesChangeListener = colorValuesChangeListener;
        mShapeValuesChaneListener = shapeValuesChaneListener;
    }

    private void updateShapeRadioGroup(String[] tags, int[] resNormals, int[] resChecked) {
        mShapeRadioGroup.removeAllViews();
        for (int i = 0; i < tags.length; i++) {
            EditFaceRadio radio = new EditFaceRadio(getContext());
            radio.setChecked(false);
            radio.setDrawable(getResources().getDrawable(resNormals[i]), getResources().getDrawable(resChecked[i]));
            radio.setTag(tags[i]);
            mShapeRadioGroup.addView(radio);
        }
    }

    private void updateSeekBarLayout(String tag) {
        if (TextUtils.isEmpty(tag)) {
            mSeekBarLayout.setVisibility(View.GONE);
            mColorSelectView.setVisibility(View.GONE);
        } else if (COLOR_SELECT.equals(tag)) {
            mSeekBarLayout.setVisibility(View.GONE);
            mColorSelectView.setVisibility(View.VISIBLE);
        } else {
            mSeekBarLayout.setVisibility(View.VISIBLE);
            mColorSelectView.setVisibility(View.GONE);
            mSeekBarText.setText(tag);
            mSeekBar.setProgress((int) (getShapeValues(mShapeValues, tag) * 100));
        }
    }

    private void setFaceShape(final String key, final float values) {
        switch (key) {
            case AvatarHandle.key_shape_Head:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Head_shrink, mShapeValues[P2AClientInterface.index_Head_shrink] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Head_stretch, mShapeValues[P2AClientInterface.index_Head_stretch] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_HeadBone:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_HeadBone_stretch, mShapeValues[P2AClientInterface.index_HeadBone_stretch] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_HeadBone_shrink, mShapeValues[P2AClientInterface.index_HeadBone_shrink] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_forehead:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_forehead_Wide, mShapeValues[P2AClientInterface.index_Forehead_Wide] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_forehead_Narrow, mShapeValues[P2AClientInterface.index_Forehead_Narrow] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_cheek:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_cheek_narrow, mShapeValues[P2AClientInterface.index_cheek_narrow] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Head_fat, mShapeValues[P2AClientInterface.index_Head_fat] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_jawbone:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_jawbone_Narrow, mShapeValues[P2AClientInterface.index_jawbone_Narrow] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_jawbone_Wide, mShapeValues[P2AClientInterface.index_jawbone_Wide] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_jaw:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_jaw_lower, mShapeValues[P2AClientInterface.index_jaw_lower] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_jaw_up, mShapeValues[P2AClientInterface.index_jaw_up] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_Eye_down_up:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_up, mShapeValues[P2AClientInterface.index_Eye_up] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_down, mShapeValues[P2AClientInterface.index_Eye_down] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_Eye_outter:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_outter_up, mShapeValues[P2AClientInterface.index_Eye_outter_up] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_outter_down, mShapeValues[P2AClientInterface.index_Eye_outter_down] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_Eye_close_open:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_close, mShapeValues[P2AClientInterface.index_Eye_close] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_open, mShapeValues[P2AClientInterface.index_Eye_open] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_Eye_both:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_both_in, mShapeValues[P2AClientInterface.index_Eye_both_in] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_Eye_both_out, mShapeValues[P2AClientInterface.index_Eye_both_out] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_nose:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_nose_UP, mShapeValues[P2AClientInterface.index_nose_UP] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_nose_Down, mShapeValues[P2AClientInterface.index_nose_Down] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_nostril:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_nostril_In, mShapeValues[P2AClientInterface.index_nostril_In] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_nostril_Out, mShapeValues[P2AClientInterface.index_nostril_Out] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_noseTip:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_noseTip_Up, mShapeValues[P2AClientInterface.index_noseTip_Up] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_noseTip_Down, mShapeValues[P2AClientInterface.index_noseTip_Down] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_mouth:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_mouth_Up, mShapeValues[P2AClientInterface.index_mouth_Up] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_mouth_Down, mShapeValues[P2AClientInterface.index_mouth_Down] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_upperLip:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_upperLip_Thick, mShapeValues[P2AClientInterface.index_upperLip_Thick] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_upperLip_Thin, mShapeValues[P2AClientInterface.index_upperLip_Thin] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_lowerLip:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_lowerLip_Thick, mShapeValues[P2AClientInterface.index_lowerLip_Thick] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_lowerLip_Thin, mShapeValues[P2AClientInterface.index_lowerLip_Thin] = values > 0 ? 0 : Math.abs(values));
                break;
            case AvatarHandle.key_shape_lipCorner:
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_lipCorner_In, mShapeValues[P2AClientInterface.index_lipCorner_In] = values > 0 ? Math.abs(values) : 0);
                mAvatarHandle.fuItemSetParamFaceShape(AvatarHandle.PARAM_KEY_lipCorner_Out, mShapeValues[P2AClientInterface.index_lipCorner_Out] = values > 0 ? 0 : Math.abs(values));
                break;
        }
        if (mShapeValuesChaneListener != null) {
            mShapeValuesChaneListener.shapeValuesChaneListener(mEditFaceBaseFragmentId, values);
        }
    }

    private float getShapeValues(float[] shapeValues, String tag) {
        switch (tag) {
            case AvatarHandle.key_shape_Head:
                return shapeValues[P2AClientInterface.index_Head_shrink] > 0 ? shapeValues[P2AClientInterface.index_Head_shrink] : -shapeValues[P2AClientInterface.index_Head_stretch];
            case AvatarHandle.key_shape_HeadBone:
                return shapeValues[P2AClientInterface.index_HeadBone_stretch] > 0 ? shapeValues[P2AClientInterface.index_HeadBone_stretch] : -shapeValues[P2AClientInterface.index_HeadBone_shrink];
            case AvatarHandle.key_shape_forehead:
                return shapeValues[P2AClientInterface.index_Forehead_Wide] > 0 ? shapeValues[P2AClientInterface.index_Forehead_Wide] : -shapeValues[P2AClientInterface.index_Forehead_Narrow];
            case AvatarHandle.key_shape_cheek:
                return shapeValues[P2AClientInterface.index_cheek_narrow] > 0 ? shapeValues[P2AClientInterface.index_cheek_narrow] : -shapeValues[P2AClientInterface.index_Head_fat];
            case AvatarHandle.key_shape_jawbone:
                return shapeValues[P2AClientInterface.index_jawbone_Narrow] > 0 ? shapeValues[P2AClientInterface.index_jawbone_Narrow] : -shapeValues[P2AClientInterface.index_jawbone_Wide];
            case AvatarHandle.key_shape_jaw:
                return shapeValues[P2AClientInterface.index_jaw_lower] > 0 ? shapeValues[P2AClientInterface.index_jaw_lower] : -shapeValues[P2AClientInterface.index_jaw_up];
            case AvatarHandle.key_shape_Eye_down_up:
                return shapeValues[P2AClientInterface.index_Eye_up] > 0 ? shapeValues[P2AClientInterface.index_Eye_up] : -shapeValues[P2AClientInterface.index_Eye_down];
            case AvatarHandle.key_shape_Eye_outter:
                return shapeValues[P2AClientInterface.index_Eye_outter_up] > 0 ? shapeValues[P2AClientInterface.index_Eye_outter_up] : -shapeValues[P2AClientInterface.index_Eye_outter_down];
            case AvatarHandle.key_shape_Eye_close_open:
                return shapeValues[P2AClientInterface.index_Eye_close] > 0 ? shapeValues[P2AClientInterface.index_Eye_close] : -shapeValues[P2AClientInterface.index_Eye_open];
            case AvatarHandle.key_shape_Eye_both:
                return shapeValues[P2AClientInterface.index_Eye_both_in] > 0 ? shapeValues[P2AClientInterface.index_Eye_both_in] : -shapeValues[P2AClientInterface.index_Eye_both_out];
            case AvatarHandle.key_shape_nose:
                return shapeValues[P2AClientInterface.index_nose_UP] > 0 ? shapeValues[P2AClientInterface.index_nose_UP] : -shapeValues[P2AClientInterface.index_nose_Down];
            case AvatarHandle.key_shape_nostril:
                return shapeValues[P2AClientInterface.index_nostril_In] > 0 ? shapeValues[P2AClientInterface.index_nostril_In] : -shapeValues[P2AClientInterface.index_nostril_Out];
            case AvatarHandle.key_shape_noseTip:
                return shapeValues[P2AClientInterface.index_noseTip_Up] > 0 ? shapeValues[P2AClientInterface.index_noseTip_Up] : -shapeValues[P2AClientInterface.index_noseTip_Down];
            case AvatarHandle.key_shape_mouth:
                return shapeValues[P2AClientInterface.index_mouth_Up] > 0 ? shapeValues[P2AClientInterface.index_mouth_Up] : -shapeValues[P2AClientInterface.index_mouth_Down];
            case AvatarHandle.key_shape_upperLip:
                return shapeValues[P2AClientInterface.index_upperLip_Thick] > 0 ? shapeValues[P2AClientInterface.index_upperLip_Thick] : -shapeValues[P2AClientInterface.index_upperLip_Thin];
            case AvatarHandle.key_shape_lowerLip:
                return shapeValues[P2AClientInterface.index_lowerLip_Thick] > 0 ? shapeValues[P2AClientInterface.index_lowerLip_Thick] : -shapeValues[P2AClientInterface.index_lowerLip_Thin];
            case AvatarHandle.key_shape_lipCorner:
                return shapeValues[P2AClientInterface.index_lipCorner_In] > 0 ? shapeValues[P2AClientInterface.index_lipCorner_In] : -shapeValues[P2AClientInterface.index_lipCorner_Out];
            default:
                return 0;
        }
    }

    public boolean isChangeDeformParam() {
        if (mDefaultShapeValues == null || mShapeValues == null) return false;
        String[] tags = new String[0];
        switch (mEditFaceBaseFragmentId) {
            case EditFaceFragment.TITLE_FACE_INDEX:
                tags = KEY_SHAPE_FACE;
                break;
            case EditFaceFragment.TITLE_EYE_INDEX:
                tags = KEY_SHAPE_EYE;
                break;
            case EditFaceFragment.TITLE_LIP_INDEX:
                tags = KEY_SHAPE_MOUTH;
                break;
            case EditFaceFragment.TITLE_NOSE_INDEX:
                tags = KEY_SHAPE_NOSE;
                break;
        }
        for (String tag : tags) {
            if (getShapeValues(mDefaultShapeValues, tag) != getShapeValues(mShapeValues, tag)) {
                return true;
            }
        }
        return false;
    }

    public void resetDefaultDeformParam() {
        mShapeRadioGroup.clearCheck();
        switch (mEditFaceBaseFragmentId) {
            case EditFaceFragment.TITLE_FACE_INDEX:
                for (String tag : KEY_SHAPE_FACE) {
                    setFaceShape(tag, getShapeValues(mDefaultShapeValues, tag));
                }
                break;
            case EditFaceFragment.TITLE_EYE_INDEX:
                for (String tag : KEY_SHAPE_EYE) {
                    setFaceShape(tag, getShapeValues(mDefaultShapeValues, tag));
                }
                break;
            case EditFaceFragment.TITLE_LIP_INDEX:
                for (String tag : KEY_SHAPE_MOUTH) {
                    setFaceShape(tag, getShapeValues(mDefaultShapeValues, tag));
                }
                break;
            case EditFaceFragment.TITLE_NOSE_INDEX:
                for (String tag : KEY_SHAPE_NOSE) {
                    setFaceShape(tag, getShapeValues(mDefaultShapeValues, tag));
                }
                break;
        }
    }

    public interface ShapeValuesChaneListener {
        void shapeValuesChaneListener(int id, float value);
    }
}
