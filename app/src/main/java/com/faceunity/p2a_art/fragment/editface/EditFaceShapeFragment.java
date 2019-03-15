package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFaceParameter;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFacePoint;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFacePointFactory;
import com.faceunity.p2a_art.ui.EditFaceRadioGroup;
import com.faceunity.p2a_art.ui.NormalDialog;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceShapeFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceShapeFragment.class.getSimpleName();

    private EditFaceRadioGroup mShapeRadioGroup;
    private int mShapeRadioId = R.id.edit_face_radio_shape_face;
    private CheckBox mIsFrontBox;
    private boolean isFront = true;

    private EditFaceParameter mEditFaceParameter;
    private EditFaceStatusChaneListener mEditFaceStatusChaneListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_shape, container, false);

        view.findViewById(R.id.edit_face_shape_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditFaceParameter.isShapeChangeValues()) {
                    NormalDialog normalDialog = new NormalDialog();
                    normalDialog.setNormalDialogTheme(R.style.FullScreenTheme);
                    normalDialog.setMessageStr("确认将所有参数恢复默认吗？");
                    normalDialog.setNegativeStr("取消");
                    normalDialog.setPositiveStr("确认");
                    normalDialog.show(getChildFragmentManager(), NormalDialog.TAG);
                    normalDialog.setOnClickListener(new NormalDialog.OnSimpleClickListener() {
                        @Override
                        public void onPositiveListener() {
                            mEditFaceParameter.resetDefaultDeformParam();
                            mEditFaceStatusChaneListener.resetDefaultDeformParam();
                        }
                    });
                }
            }
        });

        mShapeRadioGroup = view.findViewById(R.id.edit_face_radio_shape);
        mShapeRadioGroup.setOnCheckedChangeListener(new EditFaceRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(EditFaceRadioGroup group, int checkedId) {
                mShapeRadioId = checkedId;
                updateEditPoint();
            }
        });

        mIsFrontBox = view.findViewById(R.id.edit_face_shape_position);
        mIsFrontBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFront = isChecked;
                updateEditPoint();
            }
        });

        updateEditPoint();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            updateEditPoint();
    }

    public void initDate(EditFaceParameter parameter, EditFaceStatusChaneListener editFaceStatusChaneListener) {
        mEditFaceParameter = parameter;
        mEditFaceStatusChaneListener = editFaceStatusChaneListener;
    }

    private void updateEditPoint() {
        if (mAvatarP2A.getGender() == AvatarP2A.gender_boy) {
            switch (mShapeRadioId) {
                case R.id.edit_face_radio_shape_face:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mMaleFaceFrontPoints : EditFacePointFactory.mMaleFaceSidePoints);
                    break;
                case R.id.edit_face_radio_shape_eye:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mMaleEyeFrontPoints : EditFacePointFactory.mMaleEyeSidePoints);
                    break;
                case R.id.edit_face_radio_shape_mouth:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mMaleMouthFrontPoints : EditFacePointFactory.mMaleMouthSidePoints);
                    break;
                case R.id.edit_face_radio_shape_nose:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mMaleNoseFrontPoints : EditFacePointFactory.mMaleNoseSidePoints);
                    break;
            }
        } else {
            switch (mShapeRadioId) {
                case R.id.edit_face_radio_shape_face:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mFemaleFaceFrontPoints : EditFacePointFactory.mFemaleFaceSidePoints);
                    break;
                case R.id.edit_face_radio_shape_eye:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mFemaleEyeFrontPoints : EditFacePointFactory.mFemaleEyeSidePoints);
                    break;
                case R.id.edit_face_radio_shape_mouth:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mFemaleMouthFrontPoints : EditFacePointFactory.mFemaleMouthSidePoints);
                    break;
                case R.id.edit_face_radio_shape_nose:
                    mEditFaceStatusChaneListener.editFacePointChaneListener(isFront ? EditFacePointFactory.mFemaleNoseFrontPoints : EditFacePointFactory.mFemaleNoseSidePoints);
                    break;
            }
        }
        if (isFront) {
            mAvatarHandle.resetAllFront();
        } else {
            mAvatarHandle.resetAllSide();
        }
    }

    public interface EditFaceStatusChaneListener {
        void editFacePointChaneListener(EditFacePoint[] point);

        void resetDefaultDeformParam();
    }
}
